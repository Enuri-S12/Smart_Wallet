package com.smartwallet.depositservice.controller;

import com.smartwallet.common.dto.AmountRequest;
import com.smartwallet.common.dto.CreateWalletRequest;
import com.smartwallet.common.dto.WalletResponse;
import com.smartwallet.depositservice.service.DepositService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DepositController {

    private final DepositService depositService;

    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/wallets")
    @ResponseStatus(HttpStatus.CREATED)
    public WalletResponse createWallet(@Valid @RequestBody CreateWalletRequest request) {
        return depositService.createWallet(request);
    }

    @PostMapping("/deposits/{walletId}")
    public WalletResponse deposit(
            @PathVariable("walletId") Long walletId,
            @Valid @RequestBody AmountRequest request
    ) {
        return depositService.deposit(walletId, request);
    }
}
