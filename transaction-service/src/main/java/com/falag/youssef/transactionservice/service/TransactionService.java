package com.falag.youssef.transactionservice.service;

import com.falag.youssef.transactionservice.client.AccountClient;
import com.falag.youssef.transactionservice.client.dto.AccountDto;
import com.falag.youssef.transactionservice.client.dto.OperationRequest;
import com.falag.youssef.transactionservice.config.BankTransactionProperties;
import com.falag.youssef.transactionservice.domain.Transaction;
import com.falag.youssef.transactionservice.domain.TransactionType;
import com.falag.youssef.transactionservice.exception.BusinessRuleViolationException;
import com.falag.youssef.transactionservice.exception.InvalidTransactionException;
import com.falag.youssef.transactionservice.exception.TransactionNotFoundException;
import com.falag.youssef.transactionservice.repository.TransactionRepository;
import com.falag.youssef.transactionservice.service.event.TransactionRecorded;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountClient accountClient;
    private final BankTransactionProperties properties;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionService(TransactionRepository transactionRepository, AccountClient accountClient,
                              BankTransactionProperties properties, ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.accountClient = accountClient;
        this.properties = properties;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findByAccount(UUID accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    @Transactional(readOnly = true)
    public Transaction findById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction create(CreateTransactionCommand command) {
        BigDecimal amount = validateAmount(command.amount());
        AccountDto account = accountClient.getAccount(command.accountId());
        checkOperable(account, command.currency());
        checkFunds(account, amount);

        Transaction transaction = transactionRepository.save(Transaction.record(
                TransactionType.DEBIT, command.accountId(), amount, command.currency(),
                command.merchantCategory(), command.country(), null));
        accountClient.debit(command.accountId(), new OperationRequest(amount, command.currency()));
        eventPublisher.publishEvent(TransactionRecorded.from(transaction));
        return transaction;
    }

    public TransferResult transfer(TransferCommand command) {
        BigDecimal amount = validateAmount(command.amount());
        if (command.sourceAccountId().equals(command.targetAccountId())) {
            throw new InvalidTransactionException("Le compte source et le compte cible doivent être différents");
        }

        AccountDto source = accountClient.getAccount(command.sourceAccountId());
        AccountDto target = accountClient.getAccount(command.targetAccountId());
        checkOperable(source, command.currency());
        checkOperable(target, command.currency());
        checkFunds(source, amount);

        UUID transferId = UUID.randomUUID();
        Transaction debit = transactionRepository.save(Transaction.record(TransactionType.DEBIT,
                command.sourceAccountId(), amount, command.currency(), "TRANSFER", null, transferId));
        Transaction credit = transactionRepository.save(Transaction.record(TransactionType.CREDIT,
                command.targetAccountId(), amount, command.currency(), "TRANSFER", null, transferId));

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

        eventPublisher.publishEvent(TransactionRecorded.from(debit));
        eventPublisher.publishEvent(TransactionRecorded.from(credit));
        return new TransferResult(transferId, debit, credit);
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new InvalidTransactionException("Le montant doit être strictement positif");
        }
        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        if (normalized.compareTo(amount) != 0 || normalized.signum() <= 0) {
            throw new InvalidTransactionException("Le montant doit comporter au plus deux décimales");
        }
        if (normalized.compareTo(properties.maxAmount()) > 0) {
            throw new InvalidTransactionException("Le montant dépasse le plafond autorisé (" + properties.maxAmount() + ")");
        }
        return normalized;
    }

    private void checkOperable(AccountDto account, String currency) {
        if (!"ACTIVE".equals(account.status())) {
            throw new BusinessRuleViolationException("Le compte " + account.id() + " n'est pas actif");
        }
        if (!account.currency().equalsIgnoreCase(currency)) {
            throw new BusinessRuleViolationException("Devise du compte (" + account.currency()
                    + ") différente de la devise de l'opération (" + currency + ")");
        }
    }

    private void checkFunds(AccountDto account, BigDecimal amount) {
        if (BigDecimal.valueOf(account.balance()).compareTo(amount) < 0) {
            throw new BusinessRuleViolationException("Solde insuffisant pour le compte " + account.id());
        }
    }
}
