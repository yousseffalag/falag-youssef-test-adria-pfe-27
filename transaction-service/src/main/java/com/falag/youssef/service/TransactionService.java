package com.falag.youssef.service;

import com.falag.youssef.feign.AccountClient;
import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.client.dto.OperationRequest;
import com.falag.youssef.domain.Transaction;
import com.falag.youssef.domain.TransactionType;
import com.falag.youssef.event.TransactionCreatedMessage;
import com.falag.youssef.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final BigDecimal maxAmount;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionService(TransactionRepository transactionRepository, AccountClient accountClient,
                              @Value("${bank.transaction.max-amount}") BigDecimal maxAmount,
                              ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
        this.maxAmount = maxAmount;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<TransactionView> findByAccount(UUID accountId) {
        AccountDto account = accountClient.getAccount(accountId);
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(transaction -> new TransactionView(transaction, account))
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionView findById(UUID id) {
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction introuvable : " + id));
        return new TransactionView(transaction, accountClient.getAccount(transaction.getAccountId()));
    }

    public TransactionView create(UUID accountId, BigDecimal requestedAmount, String currency,
                                  String merchantCategory, String country) {
        BigDecimal amount = validateAmount(requestedAmount);
        AccountDto account = accountClient.getAccount(accountId);
        checkOperable(account, currency);
        checkFunds(account, amount);

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .amount(amount)
                .currency(currency.toUpperCase(Locale.ROOT))
                .merchantCategory(merchantCategory)
                .country(country)
                .type(TransactionType.DEBIT)
                .createdAt(Instant.now())
                .build());
        AccountDto updatedAccount = accountClient.debit(accountId, new OperationRequest(amount, currency));
        publishCreated(transaction);
        return new TransactionView(transaction, updatedAccount);
    }

    public TransferResult transfer(UUID sourceAccountId, UUID targetAccountId, BigDecimal requestedAmount,
                                   String currency) {
        BigDecimal amount = validateAmount(requestedAmount);
        if (sourceAccountId.equals(targetAccountId)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le compte source et le compte cible doivent être différents");
        }

        AccountDto source = accountClient.getAccount(sourceAccountId);
        AccountDto target = accountClient.getAccount(targetAccountId);
        checkOperable(source, currency);
        checkOperable(target, currency);
        checkFunds(source, amount);

        UUID transferId = UUID.randomUUID();
        Transaction debit = transactionRepository.save(createTransferTransaction(
                sourceAccountId, amount, currency, TransactionType.DEBIT, transferId));
        Transaction credit = transactionRepository.save(createTransferTransaction(
                targetAccountId, amount, currency, TransactionType.CREDIT, transferId));

        OperationRequest operation = new OperationRequest(amount, currency);
        AccountDto updatedSource = accountClient.debit(sourceAccountId, operation);
        AccountDto updatedTarget;
        try {
            updatedTarget = accountClient.credit(targetAccountId, operation);
        } catch (RuntimeException transferFailure) {
            try {
                accountClient.credit(sourceAccountId, operation);
            } catch (RuntimeException compensationFailure) {
                transferFailure.addSuppressed(compensationFailure);
            }
            throw transferFailure;
        }

        publishCreated(debit);
        publishCreated(credit);
        return new TransferResult(transferId, new TransactionView(debit, updatedSource),
                new TransactionView(credit, updatedTarget));
    }

    private void publishCreated(Transaction transaction) {
        eventPublisher.publishEvent(TransactionCreatedMessage.from(transaction));
    }

    private Transaction createTransferTransaction(UUID accountId, BigDecimal amount, String currency,
                                                   TransactionType type, UUID transferId) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(accountId)
                .amount(amount)
                .currency(currency.toUpperCase(Locale.ROOT))
                .merchantCategory("TRANSFER")
                .type(type)
                .transferId(transferId)
                .createdAt(Instant.now())
                .build();
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le montant doit être strictement positif");
        }
        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        if (normalized.compareTo(amount) != 0 || normalized.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le montant doit comporter au plus deux décimales");
        }
        if (normalized.compareTo(maxAmount) > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le montant dépasse le plafond autorisé (" + maxAmount + ")");
        }
        return normalized;
    }

    private void checkOperable(AccountDto account, String currency) {
        if (!"ACTIVE".equals(account.status())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le compte " + account.id() + " n'est pas actif");
        }
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Devise du compte (" + account.currency()
                    + ") différente de la devise de l'opération (" + currency + ")");
        }
    }

    private void checkFunds(AccountDto account, BigDecimal amount) {
        if (BigDecimal.valueOf(account.balance()).compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Solde insuffisant pour le compte " + account.id());
        }
    }
}
