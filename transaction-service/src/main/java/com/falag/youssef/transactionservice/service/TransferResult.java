package com.falag.youssef.transactionservice.service;

import com.falag.youssef.transactionservice.domain.Transaction;

import java.util.UUID;

public record TransferResult(UUID transferId, Transaction debit, Transaction credit) {
}
