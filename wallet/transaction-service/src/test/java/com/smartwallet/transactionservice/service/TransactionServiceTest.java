package com.smartwallet.transactionservice.service;

import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.TransactionResponse;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.transactionservice.entity.WalletTransaction;
import com.smartwallet.transactionservice.repository.WalletRepository;
import com.smartwallet.transactionservice.repository.WalletTransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletTransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void recordTransactionIsIdempotentForExistingId() {
        WalletTransaction existing = new WalletTransaction(
                "tx-1",
                TransactionType.DEPOSIT,
                null,
                1L,
                new BigDecimal("10.00")
        );
        when(walletRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findById("tx-1")).thenReturn(Optional.of(existing));

        TransactionResponse response = transactionService.recordTransaction(new CreateTransactionRequest(
                "tx-1",
                TransactionType.DEPOSIT,
                null,
                1L,
                new BigDecimal("10.00")
        ));

        assertThat(response.transactionId()).isEqualTo("tx-1");
        verify(transactionRepository, never()).save(any());
    }

    @Test
    void getTransactionsReturnsNewestFirstMapping() {
        when(walletRepository.existsById(1L)).thenReturn(true);
        when(transactionRepository.findBySourceWalletIdOrDestinationWalletIdOrderByCreatedAtDesc(1L, 1L))
                .thenReturn(List.of(new WalletTransaction(
                        "tx-2",
                        TransactionType.WITHDRAWAL,
                        1L,
                        null,
                        new BigDecimal("5.00")
                )));

        List<TransactionResponse> responses = transactionService.getTransactions(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).type()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(responses.get(0).amount()).isEqualByComparingTo("5.00");
    }
}
