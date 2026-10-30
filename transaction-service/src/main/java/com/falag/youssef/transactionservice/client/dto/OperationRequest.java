package com.falag.youssef.transactionservice.client.dto;

import java.math.BigDecimal;

public record OperationRequest(BigDecimal amount, String currency) {
}
