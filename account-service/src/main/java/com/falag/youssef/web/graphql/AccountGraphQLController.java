package com.falag.youssef.web.graphql;

import com.falag.youssef.domain.AccountStatus;
import com.falag.youssef.service.AccountService;
import com.falag.youssef.web.dto.AccountResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class AccountGraphQLController {

    private final AccountService accountService;

    public AccountGraphQLController(AccountService accountService) {
        this.accountService = accountService;
    }

    public record CreateAccountInput(String customerReference, String currency, Double initialBalance) {
    }

    @QueryMapping
    public List<AccountResponse> accounts() {
        return accountService.findAll().stream().map(AccountResponse::from).toList();
    }

    @QueryMapping
    public AccountResponse accountById(@Argument UUID id) {
        return AccountResponse.from(accountService.findById(id));
    }

    @QueryMapping
    public List<AccountResponse> accountsByCustomer(@Argument String customerReference) {
        return accountService.findByCustomerReference(customerReference).stream().map(AccountResponse::from).toList();
    }

    @MutationMapping
    public AccountResponse createAccount(@Argument CreateAccountInput input) {
        BigDecimal initialBalance = input.initialBalance() == null
                ? BigDecimal.ZERO : BigDecimal.valueOf(input.initialBalance());
        return AccountResponse.from(accountService.create(input.customerReference(), input.currency(), initialBalance));
    }

    @MutationMapping
    public AccountResponse changeAccountStatus(@Argument UUID id, @Argument AccountStatus status) {
        return AccountResponse.from(accountService.changeStatus(id, status));
    }
}
