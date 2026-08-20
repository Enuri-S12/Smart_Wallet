package com.smartwallet.withdrawalservice.service;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.exception.InsufficientFundsException;
import com.smartwallet.withdrawalservice.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletBalanceServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletBalanceService walletBalanceService;

    @Test
    void debitRejectsInsufficientFunds() {
        Wallet wallet = new Wallet("WAL-TEST", "user-1", new BigDecimal("5.00"), "USD");
        when(walletRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(wallet));

        assertThatThrownBy(() -> walletBalanceService.debit(1L, new BigDecimal("10.00")))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
