package com.example.cardmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.example.cardmanagement.entity")
public class CardManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(CardManagementApplication.class, args);
    }
}