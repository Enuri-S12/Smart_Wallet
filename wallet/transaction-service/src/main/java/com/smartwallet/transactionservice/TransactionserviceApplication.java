package com.smartwallet.transactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {
        "com.smartwallet.common.entity",
        "com.smartwallet.transactionservice.entity"
})
@EnableJpaRepositories(basePackages = "com.smartwallet.transactionservice.repository")
public class TransactionserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionserviceApplication.class, args);
    }
}
