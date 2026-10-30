package com.falag.youssef.accountservice.web.dto;

import com.falag.youssef.accountservice.domain.Account;
import com.falag.youssef.accountservice.domain.AccountStatus;

import java.util.UUID;

public record AccountResponse(UUID id, String customerReference, AccountStatus status, String currency, double balance) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getCustomerReference(), account.getStatus(),
                account.getCurrency(), account.getBalance());
    }
}
