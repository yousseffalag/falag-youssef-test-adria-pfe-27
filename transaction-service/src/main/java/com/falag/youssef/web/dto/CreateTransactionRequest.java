package com.falag.youssef.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionRequest(@NotNull UUID accountId, @NotNull @Positive BigDecimal amount,
                                       @NotBlank String currency, String merchantCategory, String country) {
}
