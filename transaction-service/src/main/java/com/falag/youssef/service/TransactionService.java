package com.falag.youssef.service;

import com.falag.youssef.client.AccountClient;
import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.client.dto.OperationRequest;
import com.falag.youssef.config.BankTransactionProperties;
import com.falag.youssef.domain.Transaction;
import com.falag.youssef.domain.TransactionType;
import com.falag.youssef.repository.TransactionRepository;
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
    private final BankTransactionProperties properties;

    public TransactionService(TransactionRepository transactionRepository, AccountClient accountClient,
                              BankTransactionProperties properties) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
        this.properties = properties;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByAccount(UUID accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @Transactional(readOnly = true)
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction introuvable : " + id));
    }

    public Transaction create(CreateTransactionCommand command) {
        BigDecimal amount = validateAmount(command.amount());
        AccountDto account = accountClient.getAccount(command.accountId());
        checkOperable(account, command.currency());
        checkFunds(account, amount);

        Transaction transaction = transactionRepository.save(Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(command.accountId())
                .amount(amount)
                .currency(command.currency().toUpperCase(Locale.ROOT))
                .merchantCategory(command.merchantCategory())
                .country(command.country())
                .type(TransactionType.DEBIT)
                .createdAt(Instant.now())
                .build());
        accountClient.debit(command.accountId(), new OperationRequest(amount, command.currency()));
        return transaction;
    }

    public TransferResult transfer(TransferCommand command) {
        BigDecimal amount = validateAmount(command.amount());
        if (command.sourceAccountId().equals(command.targetAccountId())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le compte source et le compte cible doivent être différents");
        }

        AccountDto source = accountClient.getAccount(command.sourceAccountId());
        AccountDto target = accountClient.getAccount(command.targetAccountId());
        checkOperable(source, command.currency());
        checkOperable(target, command.currency());
        checkFunds(source, amount);

        UUID transferId = UUID.randomUUID();
        Transaction debit = transactionRepository.save(createTransferTransaction(
                command.sourceAccountId(), amount, command.currency(), TransactionType.DEBIT, transferId));
        Transaction credit = transactionRepository.save(createTransferTransaction(
                command.targetAccountId(), amount, command.currency(), TransactionType.CREDIT, transferId));

        OperationRequest operation = new OperationRequest(amount, command.currency());
        accountClient.debit(command.sourceAccountId(), operation);
        try {
            accountClient.credit(command.targetAccountId(), operation);
        } catch (RuntimeException transferFailure) {
            try {
                accountClient.credit(command.sourceAccountId(), operation);
            } catch (RuntimeException compensationFailure) {
                transferFailure.addSuppressed(compensationFailure);
            }
            throw transferFailure;
        }

        return new TransferResult(transferId, debit, credit);
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
        if (normalized.compareTo(properties.maxAmount()) > 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le montant dépasse le plafond autorisé (" + properties.maxAmount() + ")");
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
