package com.falag.youssef.service;

import com.falag.youssef.domain.Account;
import com.falag.youssef.domain.AccountStatus;
import com.falag.youssef.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public List<Account> findAll() {
        return accountRepository.findAll();
    }

    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Compte introuvable : " + id));
    }

    public List<Account> findByCustomerReference(String customerReference) {
        return accountRepository.findByCustomerReference(customerReference);
    }

    public Account create(String customerReference, String currency, BigDecimal initialBalance) {
        double balance = initialBalance == null ? 0.0 : initialBalance.doubleValue();
        if (!Double.isFinite(balance) || balance < 0) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY,
                    "Le solde initial doit être positif ou nul");
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
        validateOperation(account, amount, currency);
        if (account.getBalance() < amount.doubleValue()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Solde insuffisant");
        }
        account.debit(amount.doubleValue());
        return account;
    }

    public Account credit(UUID id, BigDecimal amount, String currency) {
        Account account = findById(id);
        validateOperation(account, amount, currency);
        if (!Double.isFinite(account.getBalance() + amount.doubleValue())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Solde résultant invalide");
        }
        account.credit(amount.doubleValue());
        return account;
    }

    private void validateOperation(Account account, BigDecimal amount, String currency) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le compte n'est pas actif");
        }
        if (!account.getCurrency().equalsIgnoreCase(currency)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "La devise du compte ne correspond pas");
        }
        if (amount == null || amount.signum() <= 0 || !Double.isFinite(amount.doubleValue())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Le montant doit être strictement positif");
        }
    }
}
