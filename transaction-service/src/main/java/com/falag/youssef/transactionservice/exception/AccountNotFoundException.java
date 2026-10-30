package com.falag.youssef.transactionservice.exception;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
