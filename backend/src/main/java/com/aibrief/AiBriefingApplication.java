package com.aibrief;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AiBriefingApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiBriefingApplication.class, args);
    }
}
