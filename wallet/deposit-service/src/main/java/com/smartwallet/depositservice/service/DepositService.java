package com.smartwallet.depositservice.service;

import com.smartwallet.common.dto.AmountRequest;
import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.CreateWalletRequest;
import com.smartwallet.common.dto.WalletResponse;
import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.depositservice.client.TransactionClient;
import com.smartwallet.depositservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.UUID;

@Service
public class DepositService {

    private final WalletRepository walletRepository;
    private final TransactionClient transactionClient;
    private final WalletBalanceService walletBalanceService;

    public DepositService(
            WalletRepository walletRepository,
            TransactionClient transactionClient,
            WalletBalanceService walletBalanceService
    ) {
        this.walletRepository = walletRepository;
        this.transactionClient = transactionClient;
        this.walletBalanceService = walletBalanceService;
    }

    public WalletResponse createWallet(CreateWalletRequest request) {
        Wallet wallet = walletBalanceService.create(request.userId(), request.currency());
        return mapWallet(wallet);
    }

    @Transactional
    public WalletResponse deposit(Long walletId, AmountRequest request) {
        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        wallet.credit(request.amount());
        walletRepository.save(wallet);

        String txId = UUID.randomUUID().toString();
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                txId,
                TransactionType.DEPOSIT,
                null,
                walletId,
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
