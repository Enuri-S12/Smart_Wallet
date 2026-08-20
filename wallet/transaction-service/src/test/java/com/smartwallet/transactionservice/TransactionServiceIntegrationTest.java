package com.smartwallet.transactionservice;

import com.smartwallet.common.entity.Wallet;
import com.smartwallet.transactionservice.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void recordsAndListsTransactionsForWallet() throws Exception {
        Wallet wallet = walletRepository.save(
                new Wallet("WAL-TEST04", "user-1", BigDecimal.ZERO, "USD")
        );

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactionId": "tx-deposit-1",
                                  "type": "DEPOSIT",
                                  "sourceWalletId": null,
                                  "destinationWalletId": %d,
                                  "amount": 25.00
                                }
                                """.formatted(wallet.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-deposit-1"))
                .andExpect(jsonPath("$.type").value("DEPOSIT"));

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "transactionId": "tx-deposit-1",
                                  "type": "DEPOSIT",
                                  "sourceWalletId": null,
                                  "destinationWalletId": %d,
                                  "amount": 25.00
                                }
                                """.formatted(wallet.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value("tx-deposit-1"));

        mockMvc.perform(get("/api/wallets/{walletId}/transactions", wallet.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].transactionId").value("tx-deposit-1"));
    }
}
