package com.smartwallet.withdrawalservice;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.withdrawalservice.client.TransactionClient;
import com.smartwallet.withdrawalservice.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WithdrawalServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @MockitoBean
    private TransactionClient transactionClient;

    @Test
    void withdrawDebitsBalanceAndRecordsTransaction() throws Exception {
        Wallet wallet = walletRepository.save(
                new Wallet("WAL-TEST02", "user-1", new BigDecimal("100.00"), "USD")
        );

        mockMvc.perform(post("/api/withdrawals/{walletId}", wallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 40.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(60.00));

        Wallet updated = walletRepository.findById(wallet.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo("60.00");

        verify(transactionClient).recordTransaction(argThat(request ->
                request.type() == TransactionType.WITHDRAWAL
                        && request.sourceWalletId().equals(wallet.getId())
                        && request.amount().compareTo(new BigDecimal("40.00")) == 0
        ));
    }

    @Test
    void withdrawRejectsInsufficientFunds() throws Exception {
        Wallet wallet = walletRepository.save(
                new Wallet("WAL-TEST03", "user-1", new BigDecimal("10.00"), "USD")
        );

        mockMvc.perform(post("/api/withdrawals/{walletId}", wallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 25.00}"))
                .andExpect(status().isBadRequest());

        Wallet unchanged = walletRepository.findById(wallet.getId()).orElseThrow();
        assertThat(unchanged.getBalance()).isEqualByComparingTo("10.00");
        verifyNoInteractions(transactionClient);
    }
}
