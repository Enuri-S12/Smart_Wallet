package com.smartwallet.walletservice.service;


import com.smartwallet.walletservice.dto.*;
import com.smartwallet.walletservice.entity.Wallet;
import com.smartwallet.walletservice.entity.WalletTransaction;
import com.smartwallet.walletservice.enums.TransactionType;
import com.smartwallet.walletservice.exception.InsufficientFundsException;
import com.smartwallet.walletservice.exception.WalletNotFoundException;
import com.smartwallet.walletservice.repository.WalletRepository;
import com.smartwallet.walletservice.repository.WalletTransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public WalletService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletResponse createWallet(CreateWalletRequest request) {

        String walletNumber =
                "WAL-" + UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();

        Wallet wallet = new Wallet(
                walletNumber,
                request.userId(),
                BigDecimal.ZERO,
                request.currency().toUpperCase()
        );

        Wallet savedWallet = walletRepository.save(wallet);

        return mapWallet(savedWallet);
    }

    public WalletResponse getWallet(Long walletId) {

        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(
                        () -> new WalletNotFoundException(walletId)
                );

        return mapWallet(wallet);
    }

    @Transactional
    public WalletResponse deposit(
            Long walletId,
            AmountRequest request
    ) {

        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(
                        () -> new WalletNotFoundException(walletId)
                );

        wallet.credit(request.amount());

        walletRepository.save(wallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        TransactionType.DEPOSIT,
                        null,
                        walletId,
                        request.amount()
                );

        transactionRepository.save(transaction);

        return mapWallet(wallet);
    }

    @Transactional
    public WalletResponse withdraw(
            Long walletId,
            AmountRequest request
    ) {

        Wallet wallet = walletRepository
                .findByIdForUpdate(walletId)
                .orElseThrow(
                        () -> new WalletNotFoundException(walletId)
                );

        if (wallet.getBalance()
                .compareTo(request.amount()) < 0) {

            throw new InsufficientFundsException();
        }

        wallet.debit(request.amount());

        walletRepository.save(wallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        TransactionType.WITHDRAWAL,
                        walletId,
                        null,
                        request.amount()
                );

        transactionRepository.save(transaction);

        return mapWallet(wallet);
    }

    @Transactional
    public TransactionResponse transfer(
            TransferRequest request
    ) {

        if (request.fromWalletId()
                .equals(request.toWalletId())) {

            throw new IllegalArgumentException(
                    "Source and destination wallets cannot be the same"
            );
        }

        /*
         * Lock wallets in a consistent order.
         * This reduces the possibility of database deadlocks.
         */
        Long firstId =
                Math.min(
                        request.fromWalletId(),
                        request.toWalletId()
                );

        Long secondId =
                Math.max(
                        request.fromWalletId(),
                        request.toWalletId()
                );

        Wallet firstWallet = walletRepository
                .findByIdForUpdate(firstId)
                .orElseThrow(
                        () -> new WalletNotFoundException(firstId)
                );

        Wallet secondWallet = walletRepository
                .findByIdForUpdate(secondId)
                .orElseThrow(
                        () -> new WalletNotFoundException(secondId)
                );

        Wallet sourceWallet =
                firstWallet.getId()
                        .equals(request.fromWalletId())
                        ? firstWallet
                        : secondWallet;

        Wallet destinationWallet =
                firstWallet.getId()
                        .equals(request.toWalletId())
                        ? firstWallet
                        : secondWallet;

        if (!sourceWallet.getCurrency()
                .equals(destinationWallet.getCurrency())) {

            throw new IllegalArgumentException(
                    "Wallet currencies must match"
            );
        }

        if (sourceWallet.getBalance()
                .compareTo(request.amount()) < 0) {

            throw new InsufficientFundsException();
        }

        sourceWallet.debit(request.amount());

        destinationWallet.credit(request.amount());

        walletRepository.save(sourceWallet);
        walletRepository.save(destinationWallet);

        WalletTransaction transaction =
                new WalletTransaction(
                        TransactionType.TRANSFER,
                        sourceWallet.getId(),
                        destinationWallet.getId(),
                        request.amount()
                );

        WalletTransaction savedTransaction =
                transactionRepository.save(transaction);

        return mapTransaction(savedTransaction);
    }

    public List<TransactionResponse> getTransactions(
            Long walletId
    ) {

        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        return transactionRepository
                .findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                        walletId,
                        walletId
                )
                .stream()
                .map(this::mapTransaction)
                .toList();
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

    private TransactionResponse mapTransaction(
            WalletTransaction transaction
    ) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getSourceWalletId(),
                transaction.getDestinationWalletId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}