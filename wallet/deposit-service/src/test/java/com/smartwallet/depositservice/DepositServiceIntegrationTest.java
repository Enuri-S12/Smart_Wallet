package com.smartwallet.depositservice;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.common.enums.TransactionType;
import com.smartwallet.depositservice.client.TransactionClient;
import com.smartwallet.depositservice.repository.WalletRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DepositServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @MockitoBean
    private TransactionClient transactionClient;

    @Test
    void depositCreditsBalanceAndRecordsTransaction() throws Exception {
        Wallet wallet = walletRepository.save(
                new Wallet("WAL-TEST01", "user-1", BigDecimal.ZERO, "USD")
        );

        mockMvc.perform(post("/api/deposits/{walletId}", wallet.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(50.00));

        Wallet updated = walletRepository.findById(wallet.getId()).orElseThrow();
        assertThat(updated.getBalance()).isEqualByComparingTo("50.00");

        verify(transactionClient).recordTransaction(argThat(request ->
                request.type() == TransactionType.DEPOSIT
                        && request.destinationWalletId().equals(wallet.getId())
                        && request.amount().compareTo(new BigDecimal("50.00")) == 0
        ));
    }

    @Test
    void depositReturnsNotFoundForUnknownWallet() throws Exception {
        mockMvc.perform(post("/api/deposits/{walletId}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 10.00}"))
                .andExpect(status().isNotFound());
    }
}
