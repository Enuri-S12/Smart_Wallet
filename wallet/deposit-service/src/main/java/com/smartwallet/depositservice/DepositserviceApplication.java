package com.smartwallet.depositservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EntityScan(basePackages = "com.smartwallet.common.entity")
@EnableJpaRepositories(basePackages = "com.smartwallet.depositservice.repository")
@EnableRetry
public class DepositserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DepositserviceApplication.class, args);
    }
}
