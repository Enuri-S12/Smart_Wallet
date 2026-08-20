package com.smartwallet.withdrawalservice.controller;

import com.smartwallet.common.dto.AmountRequest;
import com.smartwallet.common.dto.WalletResponse;
import com.smartwallet.withdrawalservice.service.WithdrawalService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    public WithdrawalController(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    @PostMapping("/withdrawals/{walletId}")
    @ResponseStatus(HttpStatus.OK)
    public WalletResponse withdraw(
            @PathVariable("walletId") Long walletId,
            @Valid @RequestBody AmountRequest request
    ) {
        return withdrawalService.withdraw(walletId, request);
    }
}

