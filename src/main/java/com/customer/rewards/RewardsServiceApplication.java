package com.customer.rewards;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class RewardsServiceApplication {

    public static void main(String[] args) {

        log.info("Starting Reward Service application");
        SpringApplication.run(RewardsServiceApplication.class, args);
    }

}
