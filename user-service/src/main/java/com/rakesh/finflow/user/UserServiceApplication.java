package com.rakesh.finflow.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.rakesh.finflow.user", "com.rakesh.finflow.common"})
@EnableDiscoveryClient
@EntityScan(basePackages = {"com.rakesh.finflow.user", "com.rakesh.finflow.common.kafka"})
@EnableJpaRepositories(basePackages = {"com.rakesh.finflow.user", "com.rakesh.finflow.common.kafka"})
@EnableFeignClients(basePackages = {"com.rakesh.finflow.common.feign.clients"})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
