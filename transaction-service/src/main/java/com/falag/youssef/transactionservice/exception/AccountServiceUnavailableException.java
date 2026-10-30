package com.falag.youssef.transactionservice.exception;

public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(Throwable cause) {
        super("account-service est indisponible, réessayez plus tard", cause);
    }
}
