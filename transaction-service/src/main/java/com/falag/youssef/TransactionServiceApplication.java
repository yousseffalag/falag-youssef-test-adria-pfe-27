package com.falag.youssef;

import com.falag.youssef.domain.Transaction;
import com.falag.youssef.domain.TransactionType;
import com.falag.youssef.repository.TransactionRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
@EnableFeignClients
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner seedDemoTransactions(TransactionRepository transactionRepository) {
        return args -> {
            if (transactionRepository.count() == 0) {
                Instant now = Instant.now();
                transactionRepository.saveAll(List.of(
                        Transaction.builder()
                                .id(UUID.randomUUID())
                                .accountId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                                .amount(new BigDecimal("125.00"))
                                .currency("EUR")
                                .merchantCategory("GROCERY")
                                .country("MA")
                                .type(TransactionType.DEBIT)
                                .createdAt(now.minusSeconds(3600))
                                .build(),
                        Transaction.builder()
                                .id(UUID.randomUUID())
                                .accountId(UUID.fromString("11111111-1111-1111-1111-111111111111"))
                                .amount(new BigDecimal("45.00"))
                                .currency("EUR")
                                .merchantCategory("RESTAURANT")
                                .country("MA")
                                .type(TransactionType.DEBIT)
                                .createdAt(now)
                                .build()));
            }
        };
    }
}
