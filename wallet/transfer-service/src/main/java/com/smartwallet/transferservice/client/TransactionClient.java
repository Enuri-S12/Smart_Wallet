package com.smartwallet.transferservice.client;

import com.smartwallet.common.dto.CreateTransactionRequest;
import com.smartwallet.common.dto.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TransactionClient {

    private static final Logger log = LoggerFactory.getLogger(TransactionClient.class);
    private final RestClient restClient;

    public TransactionClient(@Value("${transaction.service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Retryable(retryFor = RestClientException.class, maxAttempts = 3,
            backoff = @Backoff(delay = 500, multiplier = 2))
    public TransactionResponse recordTransaction(CreateTransactionRequest request) {
        return restClient.post()
                .uri("/api/transactions")
                .body(request)
                .retrieve()
                .body(TransactionResponse.class);
    }

    @Recover
    public TransactionResponse recoverRecordTransaction(RestClientException ex,
                                                         CreateTransactionRequest request) {
        log.error("transaction-service unreachable after retries - txId={}",
                request.transactionId(), ex);
        return null;
    }
}