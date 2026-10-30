package com.falag.youssef.transactionservice.exception;

import java.util.UUID;

public class TransactionNotFoundException extends BusinessException {
    public TransactionNotFoundException(UUID id) {
        super("Transaction introuvable : " + id);
    }
}
