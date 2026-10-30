package com.falag.youssef.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record OperationRequest(@NotNull @Positive BigDecimal amount, @NotBlank String currency) {
}
