package com.smartwallet.transferservice.exception;

import com.smartwallet.common.exception.InsufficientFundsException;
import com.smartwallet.common.exception.WalletNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<?> walletNotFound(WalletNotFoundException ex) {
        return response(HttpStatus.NOT_FOUND, "WALLET_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<?> insufficientFunds(InsufficientFundsException ex) {
        return response(HttpStatus.BAD_REQUEST, "INSUFFICIENT_FUNDS", ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<?> invalidRequest(Exception ex) {
        String message = ex instanceof MethodArgumentNotValidException
                ? "Invalid request data" : ex.getMessage();
        return response(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", message);
    }

    private ResponseEntity<?> response(HttpStatus status, String error, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", LocalDateTime.now(),
                "status", status.value(),
                "error", error,
                "message", message
        ));
    }
}