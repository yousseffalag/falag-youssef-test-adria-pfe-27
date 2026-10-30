package com.falag.youssef.client;

import com.falag.youssef.client.dto.AccountDto;
import com.falag.youssef.client.dto.OperationRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${services.account.url:http://localhost:8081}",
        path = "/api/accounts", fallbackFactory = AccountClientFallbackFactory.class,
        configuration = com.falag.youssef.config.FeignConfig.class)
public interface AccountClient {

    @GetMapping("/{id}")
    AccountDto getAccount(@PathVariable("id") UUID id);

    @PostMapping("/{id}/debit")
    AccountDto debit(@PathVariable("id") UUID id, @RequestBody OperationRequest request);

    @PostMapping("/{id}/credit")
    AccountDto credit(@PathVariable("id") UUID id, @RequestBody OperationRequest request);
}
