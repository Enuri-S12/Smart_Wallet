package com.smartwallet.transactionservice.service;

import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.TransactionResponse;
import com.smartwallet.common.exception.WalletNotFoundException;
import com.smartwallet.transactionservice.entity.WalletTransaction;
import com.smartwallet.transactionservice.repository.WalletRepository;
import com.smartwallet.transactionservice.repository.WalletTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TransactionService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;

    public TransactionService(
            WalletRepository walletRepository,
            WalletTransactionRepository transactionRepository
    ) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponse recordTransaction(CreateTransactionRequest request) {
        if (request.sourceWalletId() == null && request.destinationWalletId() == null) {
            throw new IllegalArgumentException("Either sourceWalletId or destinationWalletId is required");
        }

        if (request.sourceWalletId() != null && !walletRepository.existsById(request.sourceWalletId())) {
            throw new WalletNotFoundException(request.sourceWalletId());
        }
        if (request.destinationWalletId() != null && !walletRepository.existsById(request.destinationWalletId())) {
            throw new WalletNotFoundException(request.destinationWalletId());
        }

        return transactionRepository.findById(request.transactionId())
                .map(this::mapTransaction)
                .orElseGet(() -> {
                    WalletTransaction tx = new WalletTransaction(
                            request.transactionId(),
                            request.type(),
                            request.sourceWalletId(),
                            request.destinationWalletId(),
                            request.amount()
                    );
                    return mapTransaction(transactionRepository.save(tx));
                });
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException(walletId);
        }

        return transactionRepository
                .findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(walletId, walletId)
                .stream()
                .map(this::mapTransaction)
                .toList();
    }

    private TransactionResponse mapTransaction(WalletTransaction transaction) {
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
