package com.smartwallet.transactionservice.repository;

import com.smartwallet.common.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}

