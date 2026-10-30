package com.falag.youssef.repository;

import com.falag.youssef.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(UUID accountId);
}
