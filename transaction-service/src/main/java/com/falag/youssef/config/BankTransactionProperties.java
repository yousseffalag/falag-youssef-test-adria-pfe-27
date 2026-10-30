package com.falag.youssef.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "bank.transaction")
public record BankTransactionProperties(BigDecimal maxAmount) {
}
