package com.smartwallet.withdrawalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EntityScan(basePackages = "com.smartwallet.common.entity")
@EnableJpaRepositories(basePackages = "com.smartwallet.withdrawalservice.repository")
@EnableRetry
public class WithdrawalserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WithdrawalserviceApplication.class, args);
    }
}
