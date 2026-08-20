package com.smartwallet.common.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateWalletRequest(
        @NotBlank
        String userId,
        @NotBlank
        String currency
) {
}
