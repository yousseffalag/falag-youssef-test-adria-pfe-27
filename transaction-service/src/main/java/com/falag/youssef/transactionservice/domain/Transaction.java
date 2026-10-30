package com.falag.youssef.transactionservice.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID accountId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    private String merchantCategory;
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private UUID transferId;

    @Column(nullable = false)
    private Instant createdAt;

    public static Transaction record(TransactionType type, UUID accountId, BigDecimal amount, String currency,
                                     String merchantCategory, String country, UUID transferId) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .type(type)
                .accountId(accountId)
                .amount(amount.setScale(2, RoundingMode.HALF_UP))
                .currency(currency.toUpperCase())
                .merchantCategory(merchantCategory)
                .country(country)
                .transferId(transferId)
                .createdAt(Instant.now())
                .build();
    }
}
