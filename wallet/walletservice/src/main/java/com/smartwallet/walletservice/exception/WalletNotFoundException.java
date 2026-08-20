package com.smartwallet.walletservice.exception;

public class WalletNotFoundException extends RuntimeException {

    public WalletNotFoundException(Long id) {
        super("Wallet not found: " + id);
    }
}