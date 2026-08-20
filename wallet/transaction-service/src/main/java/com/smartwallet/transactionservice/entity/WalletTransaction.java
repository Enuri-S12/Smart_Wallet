package com.smartwallet.transactionservice.entity;

import com.smartwallet.common.enums.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
public class WalletTransaction {

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    private Long sourceWalletId;

    private Long destinationWalletId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WalletTransaction() {
    }

    public WalletTransaction(
            String id,
            TransactionType type,
            Long sourceWalletId,
            Long destinationWalletId,
            BigDecimal amount
    ) {
        this.id = id;
        this.type = type;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.status = "SUCCESS";
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public Long getSourceWalletId() {
        return sourceWalletId;
    }

    public Long getDestinationWalletId() {
        return destinationWalletId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

