package com.aegisqa.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * AegisQA Platform — Enterprise AI-Driven ACS UI Test Platform
 * Main Spring Boot application entry point.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
        "com.aegisqa.gateway",
        "com.aegisqa.scenario",
        "com.aegisqa.orchestration",
        "com.aegisqa.browser",
        "com.aegisqa.mcp",
        "com.aegisqa.ai",
        "com.aegisqa.recovery",
        "com.aegisqa.reporting"
})
public class AegisQaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AegisQaApplication.class, args);
    }
}
