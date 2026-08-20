package com.smartwallet.walletservice.controller;

import com.smartwallet.walletservice.dto.*;
import com.smartwallet.walletservice.service.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(
            WalletService walletService
    ) {
        this.walletService = walletService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(
            @Valid @RequestBody CreateWalletRequest request
    ) {
        return walletService.createWallet(request);
    }

    @GetMapping("/{walletId}")
    public WalletResponse getWallet(
            @PathVariable Long walletId
    ) {
        return walletService.getWallet(walletId);
    }

    @PostMapping("/{walletId}/deposit")
    public WalletResponse deposit(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request
    ) {
        return walletService.deposit(
                walletId,
                request
        );
    }

    @PostMapping("/{walletId}/withdraw")
    public WalletResponse withdraw(
            @PathVariable Long walletId,
            @Valid @RequestBody AmountRequest request
    ) {
        return walletService.withdraw(
                walletId,
                request
        );
    }

    @PostMapping("/transfer")
    public TransactionResponse transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        return walletService.transfer(request);
    }

    @GetMapping("/{walletId}/transactions")
    public List<TransactionResponse> transactions(
            @PathVariable Long walletId
    ) {
        return walletService.getTransactions(walletId);
    }
}
