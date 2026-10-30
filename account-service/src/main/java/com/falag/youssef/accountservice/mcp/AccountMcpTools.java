package com.falag.youssef.accountservice.mcp;

import com.falag.youssef.accountservice.domain.Account;
import com.falag.youssef.accountservice.domain.AccountStatus;
import com.falag.youssef.accountservice.service.AccountService;
import com.falag.youssef.accountservice.web.dto.AccountResponse;
import org.springframework.ai.mcp.server.annotation.McpTool;
import org.springframework.ai.mcp.server.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class AccountMcpTools {

    private final AccountService accountService;

    public AccountMcpTools(AccountService accountService) {
        this.accountService = accountService;
    }

    @McpTool(name = "list_accounts",
            description = "Liste tous les comptes bancaires avec leur statut, leur devise et leur solde.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
                    idempotentHint = true, openWorldHint = false))
    public List<AccountResponse> listAccounts() {
        return accountService.findAll().stream().map(AccountResponse::from).toList();
    }

    @McpTool(name = "get_account",
            description = "Retourne un compte et son solde à partir de son UUID.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
                    idempotentHint = true, openWorldHint = false))
    public AccountResponse getAccount(@McpToolParam(description = "UUID du compte", required = true) String accountId) {
        return AccountResponse.from(accountService.findById(parse(accountId)));
    }

    @McpTool(name = "list_accounts_by_customer",
            description = "Liste les comptes d'un client à partir de sa référence, par exemple CUST-001.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = true, destructiveHint = false,
                    idempotentHint = true, openWorldHint = false))
    public List<AccountResponse> listAccountsByCustomer(
            @McpToolParam(description = "Référence client, par exemple CUST-001", required = true) String customerReference) {
        return accountService.findByCustomerReference(customerReference).stream().map(AccountResponse::from).toList();
    }

    @McpTool(name = "create_account",
            description = "Ouvre un compte actif. N'utiliser qu'après confirmation explicite de l'utilisateur.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = false,
                    idempotentHint = false, openWorldHint = false))
    public AccountResponse createAccount(
            @McpToolParam(description = "Référence client", required = true) String customerReference,
            @McpToolParam(description = "Code devise ISO à trois lettres", required = true) String currency,
            @McpToolParam(description = "Solde initial, positif ou nul", required = true) BigDecimal initialBalance) {
        return AccountResponse.from(accountService.create(customerReference, currency, initialBalance));
    }

    @McpTool(name = "change_account_status",
            description = "Modifie le statut d'un compte. N'utiliser qu'après confirmation explicite de l'utilisateur.",
            annotations = @McpTool.McpAnnotations(readOnlyHint = false, destructiveHint = true,
                    idempotentHint = true, openWorldHint = false))
    public AccountResponse changeStatus(
            @McpToolParam(description = "UUID du compte", required = true) String accountId,
            @McpToolParam(description = "Nouveau statut : ACTIVE ou BLOCKED", required = true) String status) {
        return AccountResponse.from(accountService.changeStatus(parse(accountId), AccountStatus.valueOf(status.toUpperCase())));
    }

    private UUID parse(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("UUID de compte invalide : " + id, exception);
        }
    }
}
