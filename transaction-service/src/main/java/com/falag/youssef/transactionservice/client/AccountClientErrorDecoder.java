package com.falag.youssef.transactionservice.client;

import com.falag.youssef.transactionservice.exception.AccountNotFoundException;
import com.falag.youssef.transactionservice.exception.BusinessRuleViolationException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class AccountClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new AccountNotFoundException("Compte introuvable (" + methodKey + ")");
            case 409, 422 -> new BusinessRuleViolationException("Opération refusée par account-service (HTTP " + response.status() + ")");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
