package com.smartwallet.walletservice.exception;


public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {
        super("Insufficient wallet balance");
    }
}
