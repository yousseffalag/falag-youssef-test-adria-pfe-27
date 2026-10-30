package com.falag.youssef;

import com.falag.youssef.domain.Account;
import com.falag.youssef.domain.AccountStatus;
import com.falag.youssef.repository.AccountRepository;
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
                accountRepository.save(Account.builder()
                        .id(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                        .customerReference("CUST-001").status(AccountStatus.ACTIVE).currency("EUR").balance(5_000.00).build());
                accountRepository.save(Account.builder()
                        .id(UUID.fromString("22222222-2222-2222-2222-222222222222"))
                        .customerReference("CUST-002").status(AccountStatus.ACTIVE).currency("EUR").balance(2_500.00).build());
                accountRepository.save(Account.builder()
                        .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                        .customerReference("CUST-003").status(AccountStatus.BLOCKED).currency("EUR").balance(1_000.00).build());
                accountRepository.save(Account.builder()
                        .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                        .customerReference("CUST-004").status(AccountStatus.ACTIVE).currency("USD").balance(1_000.00).build());
            }
        };
    }
}
