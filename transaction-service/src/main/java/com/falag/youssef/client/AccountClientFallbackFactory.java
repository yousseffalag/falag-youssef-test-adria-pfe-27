package com.falag.youssef.client;

import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.client.dto.OperationRequest;
import com.falag.youssef.exception.AccountServiceUnavailableException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Component
public class AccountClientFallbackFactory implements FallbackFactory<AccountClient> {

    @Override
    public AccountClient create(Throwable cause) {
        RuntimeException error = translate(cause);
        return new AccountClient() {
            @Override
            public AccountDto getAccount(UUID id) {
                throw error;
            }

            @Override
            public AccountDto debit(UUID id, OperationRequest request) {
                throw error;
            }

            @Override
            public AccountDto credit(UUID id, OperationRequest request) {
                throw error;
            }
        };
    }

    private RuntimeException translate(Throwable cause) {
        Throwable current = cause;
        while (current != null) {
            if (current instanceof ResponseStatusException responseStatusException) {
                return responseStatusException;
            }
            current = current.getCause();
        }
        return new AccountServiceUnavailableException(cause);
    }
}
