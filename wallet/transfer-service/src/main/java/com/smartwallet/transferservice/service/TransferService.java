package com.smartwallet.transferservice.service;

import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.TransactionResponse;
import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.common.exception.InsufficientFundsException;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.transferservice.client.TransactionClient;
import com.smartwallet.transferservice.dto.TransferRequest;
import com.smartwallet.transferservice.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferService {

    private final WalletRepository walletRepository;
    private final TransactionClient transactionClient;

    public TransferService(WalletRepository walletRepository, TransactionClient transactionClient) {
        this.walletRepository = walletRepository;
        this.transactionClient = transactionClient;
    }

    

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.fromWalletId().equals(request.toWalletId())) {
            throw new IllegalArgumentException("Source and destination wallets cannot be the same");
        }

        Long firstId = Math.min(request.fromWalletId(), request.toWalletId());
        Long secondId = Math.max(request.fromWalletId(), request.toWalletId());
        Wallet firstWallet = walletRepository.findByIdForUpdate(firstId)
                .orElseThrow(() -> new WalletNotFoundException(firstId));
        Wallet secondWallet = walletRepository.findByIdForUpdate(secondId)
                .orElseThrow(() -> new WalletNotFoundException(secondId));

        Wallet sourceWallet = firstWallet.getId().equals(request.fromWalletId()) ? firstWallet : secondWallet;
        Wallet destinationWallet = firstWallet.getId().equals(request.toWalletId()) ? firstWallet : secondWallet;

        if (!sourceWallet.getCurrency().equals(destinationWallet.getCurrency())) {
            throw new IllegalArgumentException("Wallet currencies must match");
        }
        if (sourceWallet.getBalance().compareTo(request.amount()) < 0) {
            throw new InsufficientFundsException();
        }

        sourceWallet.debit(request.amount());
        destinationWallet.credit(request.amount());
        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        String transactionId = UUID.randomUUID().toString();
        CreateTransactionRequest transactionRequest = new CreateTransactionRequest(
                transactionId, TransactionType.TRANSFER, sourceWallet.getId(),
                destinationWallet.getId(), request.amount());
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                transactionClient.recordTransaction(transactionRequest);
            }
        });

        return new TransactionResponse(transactionId, TransactionType.TRANSFER,
                sourceWallet.getId(), destinationWallet.getId(), request.amount(),
                "SUCCESS", LocalDateTime.now());
    }
}