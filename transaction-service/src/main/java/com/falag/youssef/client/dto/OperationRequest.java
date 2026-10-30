package com.falag.youssef.client.dto;

import java.math.BigDecimal;

public record OperationRequest(BigDecimal amount, String currency) {
}
