package com.smartwallet.common.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException() {
        super("Insufficient wallet balance");
    }
}
