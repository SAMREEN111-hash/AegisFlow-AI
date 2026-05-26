package com.aegisflow.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class AegisFlowApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AegisFlowApiApplication.class, args);
    }
}
