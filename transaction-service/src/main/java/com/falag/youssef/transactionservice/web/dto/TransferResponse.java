package com.falag.youssef.transactionservice.web.dto;

import java.util.UUID;

public record TransferResponse(UUID transferId, TransactionResponse debit, TransactionResponse credit) {
}
