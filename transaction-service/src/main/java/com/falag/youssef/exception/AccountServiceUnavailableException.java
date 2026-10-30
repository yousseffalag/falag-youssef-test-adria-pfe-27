package com.falag.youssef.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class AccountServiceUnavailableException extends RuntimeException {
    public AccountServiceUnavailableException(Throwable cause) {
        super("account-service est indisponible, réessayez plus tard", cause);
    }
}
