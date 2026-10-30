package com.falag.youssef.accountservice.service;

import com.falag.youssef.accountservice.domain.Account;
import com.falag.youssef.accountservice.domain.AccountStatus;
import com.falag.youssef.accountservice.exception.AccountNotFoundException;
import com.falag.youssef.accountservice.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Account> findByCustomerReference(String customerReference) {
        return accountRepository.findByCustomerReference(customerReference);
    }

    public Account create(String customerReference, String currency, BigDecimal initialBalance) {
        double balance = initialBalance == null ? 0.0 : initialBalance.doubleValue();
        if (!Double.isFinite(balance) || balance < 0) {
            throw new IllegalArgumentException("Le solde initial doit être positif ou nul");
        }
        return accountRepository.save(Account.open(customerReference, currency, balance));
    }

    public Account changeStatus(UUID id, AccountStatus status) {
        Account account = findById(id);
        account.setStatus(status);
        return account;
    }

    public Account debit(UUID id, BigDecimal amount, String currency) {
        Account account = findById(id);
        account.assertOperable(currency);
        account.debit(amount.doubleValue());
        return account;
    }

    public Account credit(UUID id, BigDecimal amount, String currency) {
        Account account = findById(id);
        account.assertOperable(currency);
        account.credit(amount.doubleValue());
        return account;
    }
}
