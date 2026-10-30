package com.falag.youssef.client;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte introuvable");
            case 409, 422 -> new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Opération refusée par account-service (HTTP " + response.status() + ")");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
