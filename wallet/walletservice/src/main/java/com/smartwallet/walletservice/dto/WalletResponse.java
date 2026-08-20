package com.smartwallet.walletservice.dto;



import java.math.BigDecimal;

public record WalletResponse(

        Long id,
        String walletNumber,
        String userId,
        BigDecimal balance,
        String currency

) {
}
