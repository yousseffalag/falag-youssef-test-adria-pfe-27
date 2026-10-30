package com.falag.youssef.accountservice;

import com.falag.youssef.accountservice.domain.Account;
import com.falag.youssef.accountservice.domain.AccountStatus;
import com.falag.youssef.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoAccounts(AccountRepository accountRepository) {
        return args -> {
            if (accountRepository.count() == 0) {
                accountRepository.save(Account.open(UUID.fromString("11111111-1111-1111-1111-111111111111"),
                        "CUST-001", "EUR", 5_000.00, AccountStatus.ACTIVE));
                accountRepository.save(Account.open(UUID.fromString("22222222-2222-2222-2222-222222222222"),
                        "CUST-002", "EUR", 2_500.00, AccountStatus.ACTIVE));
                accountRepository.save(Account.open(UUID.fromString("33333333-3333-3333-3333-333333333333"),
                        "CUST-003", "EUR", 1_000.00, AccountStatus.BLOCKED));
                accountRepository.save(Account.open(UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        "CUST-004", "USD", 1_000.00, AccountStatus.ACTIVE));
            }
        };
    }
}
