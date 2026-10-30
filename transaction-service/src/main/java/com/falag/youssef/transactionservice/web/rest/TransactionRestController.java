package com.falag.youssef.transactionservice.web.rest;

import com.falag.youssef.transactionservice.service.CreateTransactionCommand;
import com.falag.youssef.transactionservice.service.TransactionService;
import com.falag.youssef.transactionservice.service.TransferCommand;
import com.falag.youssef.transactionservice.service.TransferResult;
import com.falag.youssef.transactionservice.web.dto.CreateTransactionRequest;
import com.falag.youssef.transactionservice.web.dto.TransactionResponse;
import com.falag.youssef.transactionservice.web.dto.TransferRequest;
import com.falag.youssef.transactionservice.web.dto.TransferResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TransactionRestController {

    private final TransactionService transactionService;

    public TransactionRestController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse create(@Valid @RequestBody CreateTransactionRequest request) {
        return TransactionResponse.from(transactionService.create(new CreateTransactionCommand(
                request.accountId(), request.amount(), request.currency(), request.merchantCategory(), request.country())));
    }

    @GetMapping("/transactions")
    public List<TransactionResponse> byAccount(@RequestParam UUID accountId) {
        return transactionService.findByAccount(accountId).stream().map(TransactionResponse::from).toList();
    }

    @GetMapping("/transactions/{id}")
    public TransactionResponse findById(@PathVariable UUID id) {
        return TransactionResponse.from(transactionService.findById(id));
    }

    @PostMapping("/transfers")
    @ResponseStatus(HttpStatus.CREATED)
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = transactionService.transfer(new TransferCommand(request.sourceAccountId(),
                request.targetAccountId(), request.amount(), request.currency()));
        return new TransferResponse(result.transferId(), TransactionResponse.from(result.debit()),
                TransactionResponse.from(result.credit()));
    }
}
