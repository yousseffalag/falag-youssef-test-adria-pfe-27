package com.falag.youssef.service;

import java.util.UUID;

public record TransferResult(UUID transferId, TransactionView debit, TransactionView credit) {
}
