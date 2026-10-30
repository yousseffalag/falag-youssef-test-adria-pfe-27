package com.falag.youssef.accountservice.repository;

import com.falag.youssef.accountservice.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByCustomerReference(String customerReference);
}
