package com.smartwallet.withdrawalservice.service;

import com.smartwallet.common.dto.AmountRequest;
import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.WalletResponse;
import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.common.exception.InsufficientFundsException;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.withdrawalservice.client.TransactionClient;
import com.smartwallet.withdrawalservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class WithdrawalService {

    private final WalletRepository walletRepository;
    private final TransactionClient transactionClient;

    public WithdrawalService(
            WalletRepository walletRepository,
            TransactionClient transactionClient
    ) {
        this.walletRepository = walletRepository;
        this.transactionClient = transactionClient;
    }

    @Transactional
    public WalletResponse withdraw(Long walletId, AmountRequest request) {
        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        if (wallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException();
        }

        wallet.debit(request.amount());
        walletRepository.save(wallet);

        String txId = UUID.randomUUID().toString();
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                txId,
                TransactionType.WITHDRAWAL,
                walletId,
                null,
                request.amount()
        );

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                transactionClient.recordTransaction(txRequest);
            }
        });

        return mapWallet(wallet);
    }

    private WalletResponse mapWallet(Wallet wallet) {
        return new WalletResponse(
                wallet.getId(),
                wallet.getWalletNumber(),
                wallet.getUserId(),
                wallet.getBalance(),
                wallet.getCurrency()
        );
    }
}
