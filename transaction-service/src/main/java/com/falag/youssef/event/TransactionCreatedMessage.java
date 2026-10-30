package com.falag.youssef.event;

import com.falag.youssef.domain.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionCreatedMessage(
        UUID eventId,
        String eventType,
        UUID transactionId,
        UUID accountId,
        BigDecimal amount,
        String currency,
        Instant occurredAt) {

    public static TransactionCreatedMessage from(Transaction transaction) {
        return new TransactionCreatedMessage(
                UUID.randomUUID(),
                "TransactionCreated",
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getCreatedAt());
    }
}
