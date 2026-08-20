package com.smartwallet.transactionservice.repository;

import com.smartwallet.transactionservice.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletTransactionRepository
        extends JpaRepository<WalletTransaction, String> {

    List<WalletTransaction>
            findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(
                    Long sourceWalletId,
                    Long destinationWalletId
            );
}

