package com.rakesh.finflow.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.rakesh.finflow")
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = {"com.rakesh.finflow.common", "com.rakesh.finflow.transaction"})
@EntityScan(basePackages = {"com.rakesh.finflow.common", "com.rakesh.finflow.transaction"})
public class TransactionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }

}
