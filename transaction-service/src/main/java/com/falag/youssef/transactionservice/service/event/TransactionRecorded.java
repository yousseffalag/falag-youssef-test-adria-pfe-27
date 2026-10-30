package com.falag.youssef.transactionservice.service.event;

import com.falag.youssef.transactionservice.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionRecorded(UUID transactionId, UUID accountId, BigDecimal amount, String currency,
                                  Instant createdAt) {
    public static TransactionRecorded from(Transaction transaction) {
        return new TransactionRecorded(transaction.getId(), transaction.getAccountId(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getCreatedAt());
    }
}
