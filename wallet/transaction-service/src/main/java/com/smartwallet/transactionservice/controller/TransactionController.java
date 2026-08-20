package com.smartwallet.transactionservice.controller;

import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.TransactionResponse;
import com.smartwallet.transactionservice.service.TransactionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponse recordTransaction(
            @Valid @RequestBody CreateTransactionRequest request
    ) {
        return transactionService.recordTransaction(request);
    }

    @GetMapping("/wallets/{walletId}/transactions")
    public List<TransactionResponse> getTransactions(
            @PathVariable("walletId") Long walletId
    ) {
        return transactionService.getTransactions(walletId);
    }
}

