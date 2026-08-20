package com.smartwallet.transferservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EntityScan(basePackages = "com.smartwallet.common.entity")
@EnableJpaRepositories(basePackages = "com.smartwallet.transferservice.repository")
@EnableRetry
public class TransferserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransferserviceApplication.class, args);
    }
}