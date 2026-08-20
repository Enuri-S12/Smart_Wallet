package com.smartwallet.common.dto;

import com.smartwallet.common.enums.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotBlank
        String transactionId,
        @NotNull
        TransactionType type,
        Long sourceWalletId,
        Long destinationWalletId,
        @NotNull
        @DecimalMin("0.01")
        @Digits(integer = 17, fraction = 2)
        BigDecimal amount
) {
}
