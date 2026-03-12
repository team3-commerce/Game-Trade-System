package com.example.tradedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaAuditing
public class TradeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeDemoApplication.class, args);
    }
}
