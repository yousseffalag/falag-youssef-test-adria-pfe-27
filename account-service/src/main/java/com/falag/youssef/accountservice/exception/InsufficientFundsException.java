package com.falag.youssef.accountservice.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID id, double balance, double amount) {
        super("Solde insuffisant pour le compte " + id + " : solde=" + balance + ", montant=" + amount);
    }
}
