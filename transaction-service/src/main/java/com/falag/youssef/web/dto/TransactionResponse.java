package com.falag.youssef.web.dto;

import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.service.TransactionView;
import com.falag.youssef.domain.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransactionResponse(UUID id, UUID accountId, BigDecimal amount, String currency,
                                  String merchantCategory, String country, TransactionType type,
                                  UUID transferId, Instant createdAt, AccountDto account) {
    public static TransactionResponse from(TransactionView view) {
        var transaction = view.transaction();
        return new TransactionResponse(transaction.getId(), transaction.getAccountId(), transaction.getAmount(),
                transaction.getCurrency(), transaction.getMerchantCategory(), transaction.getCountry(),
                transaction.getType(), transaction.getTransferId(), transaction.getCreatedAt(), view.account());
    }
}
