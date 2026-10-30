package com.falag.youssef.accountservice.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String customerReference,
        @NotBlank @Size(min = 3, max = 3) String currency,
        @PositiveOrZero BigDecimal initialBalance) {
}
