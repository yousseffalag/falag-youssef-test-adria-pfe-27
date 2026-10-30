package com.falag.youssef.feign;

import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.client.dto.OperationRequest;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@FeignClient(name = "account-service", path = "/api/accounts")
public interface AccountClient {

    @GetMapping("/{id}")
    @CircuitBreaker(name = "account-service", fallbackMethod = "accountFallback")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PostMapping("/{id}/debit")
    @CircuitBreaker(name = "account-service", fallbackMethod = "operationFallback")
    AccountDto debit(@PathVariable("id") UUID id, @RequestBody OperationRequest request);

    @PostMapping("/{id}/credit")
    @CircuitBreaker(name = "account-service", fallbackMethod = "operationFallback")
    AccountDto credit(@PathVariable("id") UUID id, @RequestBody OperationRequest request);

    default AccountDto accountFallback(UUID id, Exception cause) {
        throw mapFailure(cause);
    }

    default AccountDto operationFallback(UUID id, OperationRequest request, Exception cause) {
        throw mapFailure(cause);
    }

    private static ResponseStatusException mapFailure(Exception cause) {
        if (cause instanceof FeignException feignException && feignException.status() == 404) {
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte introuvable", cause);
        }
        if (cause instanceof FeignException feignException && feignException.status() == 422) {
            return new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Opération refusée par account-service", cause);
        }
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                "account-service est indisponible", cause);
    }
}
