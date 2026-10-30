package com.falag.youssef.transactionservice.web.graphql;

import com.falag.youssef.transactionservice.service.CreateTransactionCommand;
import com.falag.youssef.transactionservice.service.TransactionService;
import com.falag.youssef.transactionservice.service.TransferCommand;
import com.falag.youssef.transactionservice.service.TransferResult;
import com.falag.youssef.transactionservice.web.dto.TransactionResponse;
import com.falag.youssef.transactionservice.web.dto.TransferResponse;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    public TransactionGraphQLController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public record CreateTransactionInput(String accountId, Double amount, String currency,
                                         String merchantCategory, String country) {
    }

    public record TransferInput(String sourceAccountId, String targetAccountId, Double amount, String currency) {
    }

    @QueryMapping
    public List<TransactionResponse> transactionsByAccount(@Argument UUID accountId) {
        return transactionService.findByAccount(accountId).stream().map(TransactionResponse::from).toList();
    }

    @QueryMapping
    public TransactionResponse transactionById(@Argument UUID id) {
        return TransactionResponse.from(transactionService.findById(id));
    }

    @MutationMapping
    public TransactionResponse createTransaction(@Argument CreateTransactionInput input) {
        return TransactionResponse.from(transactionService.create(new CreateTransactionCommand(
                UUID.fromString(input.accountId()), BigDecimal.valueOf(input.amount()), input.currency(),
                input.merchantCategory(), input.country())));
    }

    @MutationMapping
    public TransferResponse createTransfer(@Argument TransferInput input) {
        TransferResult result = transactionService.transfer(new TransferCommand(UUID.fromString(input.sourceAccountId()),
                UUID.fromString(input.targetAccountId()), BigDecimal.valueOf(input.amount()), input.currency()));
        return new TransferResponse(result.transferId(), TransactionResponse.from(result.debit()),
                TransactionResponse.from(result.credit()));
    }
}
