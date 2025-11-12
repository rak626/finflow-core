package com.rakesh.finflow.identity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.rakesh.finflow.common", "com.rakesh.finflow.identity"})
@EnableDiscoveryClient
@EnableJpaRepositories(basePackages = {"com.rakesh.finflow.common", "com.rakesh.finflow.identity"})
@EntityScan(basePackages = {"com.rakesh.finflow.common", "com.rakesh.finflow.identity"})
@EnableFeignClients(basePackages = {"com.rakesh.finflow.common.feign.clients"})
public class IdentityServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdentityServiceApplication.class, args);
    }

}
