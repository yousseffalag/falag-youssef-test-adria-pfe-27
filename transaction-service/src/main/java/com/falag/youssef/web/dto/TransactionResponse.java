package com.falag.youssef.web.dto;

import com.falag.youssef.domain.Transaction;
import com.falag.youssef.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, UUID accountId, BigDecimal amount, String currency,
                                  String merchantCategory, String country, TransactionType type,
                                  UUID transferId, Instant createdAt) {
    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getAccountId(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getMerchantCategory(), transaction.getCountry(),
                transaction.getType(), transaction.getTransferId(), transaction.getCreatedAt());
    }
}
