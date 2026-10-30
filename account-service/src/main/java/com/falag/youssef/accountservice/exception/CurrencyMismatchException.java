package com.falag.youssef.accountservice.exception;

public class CurrencyMismatchException extends RuntimeException {
    public CurrencyMismatchException(String accountCurrency, String operationCurrency) {
        super("Devise du compte (" + accountCurrency + ") différente de la devise de l'opération (" + operationCurrency + ")");
    }
}
