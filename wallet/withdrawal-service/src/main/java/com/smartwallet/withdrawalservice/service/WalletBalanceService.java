package com.smartwallet.withdrawalservice.service;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.exception.InsufficientFundsException;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.withdrawalservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletBalanceService {

    private final WalletRepository walletRepository;

    public WalletBalanceService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Wallet debit(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException();
        }

        wallet.debit(amount);
        return walletRepository.save(wallet);
    }
}
