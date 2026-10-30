package com.falag.youssef.mcp;

import com.falag.youssef.service.TransactionService;
import com.falag.youssef.service.TransferResult;
import com.falag.youssef.web.dto.TransactionResponse;
import com.falag.youssef.web.dto.TransferResponse;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class TransactionMcpTools {

    private final TransactionService transactionService;

    public TransactionMcpTools(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @McpTool(name = "list_transactions_by_account",
            description = "Liste les transactions d'un compte UUID, de la plus récente à la plus ancienne.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
                    idempotentHint = true, openWorldHint = false))
    public List<TransactionResponse> listByAccount(
            @McpToolParam(description = "UUID du compte", required = true) String accountId) {
        return transactionService.findByAccount(parse(accountId)).stream().map(TransactionResponse::from).toList();
    }

    @McpTool(name = "get_transaction",
            description = "Retourne une transaction à partir de son UUID.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
                    idempotentHint = true, openWorldHint = false))
    public TransactionResponse getTransaction(
            @McpToolParam(description = "UUID de la transaction", required = true) String transactionId) {
        return TransactionResponse.from(transactionService.findById(parse(transactionId)));
    }

    @McpTool(name = "create_transaction",
            description = "Enregistre un paiement (débit). Refusé si montant invalide, plafond dépassé, compte bloqué, devise différente ou solde insuffisant. À utiliser après confirmation explicite.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false,
                    idempotentHint = false, openWorldHint = false))
    public TransactionResponse createTransaction(
            @McpToolParam(description = "UUID du compte", required = true) String accountId,
            @McpToolParam(description = "Montant positif en devise", required = true) BigDecimal amount,
            @McpToolParam(description = "Code devise", required = true) String currency,
            @McpToolParam(description = "Catégorie du commerçant", required = true) String merchantCategory,
            @McpToolParam(description = "Code pays", required = true) String country) {
        return TransactionResponse.from(transactionService.create(parse(accountId), amount, currency,
                merchantCategory, country));
    }

    @McpTool(name = "create_transfer",
            description = "Effectue un virement entre deux comptes distincts après confirmation explicite de l'utilisateur.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false,
                    idempotentHint = false, openWorldHint = false))
    public TransferResponse createTransfer(
            @McpToolParam(description = "UUID du compte source", required = true) String sourceAccountId,
            @McpToolParam(description = "UUID du compte cible", required = true) String targetAccountId,
            @McpToolParam(description = "Montant positif", required = true) BigDecimal amount,
            @McpToolParam(description = "Code devise", required = true) String currency) {
        TransferResult result = transactionService.transfer(parse(sourceAccountId), parse(targetAccountId),
                amount, currency);
        return new TransferResponse(result.transferId(), TransactionResponse.from(result.debit()),
                TransactionResponse.from(result.credit()));
    }

    private UUID parse(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("UUID invalide : " + id, exception);
        }
    }
}
