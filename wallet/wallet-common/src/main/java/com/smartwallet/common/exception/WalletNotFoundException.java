package com.smartwallet.common.exception;

public class WalletNotFoundException extends RuntimeException {
    public WalletNotFoundException(Long id) {
        super("Wallet not found: " + id);
    }
}
