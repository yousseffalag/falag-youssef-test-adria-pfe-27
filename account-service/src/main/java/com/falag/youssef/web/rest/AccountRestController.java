package com.falag.youssef.web.rest;

import com.falag.youssef.domain.Account;
import com.falag.youssef.service.AccountService;
import com.falag.youssef.web.dto.AccountResponse;
import com.falag.youssef.web.dto.CreateAccountRequest;
import com.falag.youssef.web.dto.OperationRequest;
import com.falag.youssef.web.dto.StatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
public class AccountRestController {

    private final AccountService accountService;

    public AccountRestController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> findAll(@RequestParam(required = false) String customerReference) {
        List<Account> accounts = customerReference == null
                ? accountService.findAll()
                : accountService.findByCustomerReference(customerReference);
        return accounts.stream().map(AccountResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AccountResponse findById(@PathVariable UUID id) {
        return AccountResponse.from(accountService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody CreateAccountRequest request) {
        return AccountResponse.from(accountService.create(request.customerReference(), request.currency(), request.initialBalance()));
    }

    @PatchMapping("/{id}/status")
    public AccountResponse changeStatus(@PathVariable UUID id, @Valid @RequestBody StatusRequest request) {
        return AccountResponse.from(accountService.changeStatus(id, request.status()));
    }

    @PostMapping("/{id}/debit")
    public AccountResponse debit(@PathVariable UUID id, @Valid @RequestBody OperationRequest request) {
        return AccountResponse.from(accountService.debit(id, request.amount(), request.currency()));
    }

    @PostMapping("/{id}/credit")
    public AccountResponse credit(@PathVariable UUID id, @Valid @RequestBody OperationRequest request) {
        return AccountResponse.from(accountService.credit(id, request.amount(), request.currency()));
    }
}
