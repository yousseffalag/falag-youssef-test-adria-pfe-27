package com.falag.youssef.service;

import com.falag.youssef.domain.Transaction;

import java.util.UUID;

public record TransferResult(UUID transferId, Transaction debit, Transaction credit) {
}
