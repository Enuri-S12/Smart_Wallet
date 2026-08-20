package com.smartwallet.common.dto;

import com.smartwallet.common.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String transactionId,
        TransactionType type,
        Long sourceWalletId,
        Long destinationWalletId,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {
}
