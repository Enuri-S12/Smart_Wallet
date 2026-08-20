package com.smartwallet.depositservice.service;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.depositservice.repository.WalletRepository;
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
    public Wallet credit(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
        wallet.credit(amount);
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet create(String userId, String currency) {
        String walletNumber = "WAL-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Wallet wallet = new Wallet(walletNumber, userId, BigDecimal.ZERO, currency.toUpperCase());
        return walletRepository.save(wallet);
    }
}
