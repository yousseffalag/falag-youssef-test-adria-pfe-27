package com.falag.youssef.service;

import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.domain.Transaction;

public record TransactionView(Transaction transaction, AccountDto account) {
}
