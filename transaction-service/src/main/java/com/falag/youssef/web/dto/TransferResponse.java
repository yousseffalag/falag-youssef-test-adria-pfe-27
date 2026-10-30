package com.falag.youssef.web.dto;

import java.util.UUID;

public record TransferResponse(UUID transferId, TransactionResponse debit, TransactionResponse credit) {
}
