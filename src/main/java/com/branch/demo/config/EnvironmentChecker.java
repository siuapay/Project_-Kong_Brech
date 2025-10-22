package com.branch.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentChecker implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== ENVIRONMENT CHECK ===");
        System.out.println("DATABASE_URL: " + (System.getenv("DATABASE_URL") != null ? "SET" : "NOT SET"));
        System.out.println("PORT: " + System.getenv("PORT"));
        System.out.println("RAILWAY_ENVIRONMENT: " + System.getenv("RAILWAY_ENVIRONMENT"));
        System.out.println("SPRING_PROFILES_ACTIVE: " + System.getenv("SPRING_PROFILES_ACTIVE"));
        System.out.println("Active Profile: " + System.getProperty("spring.profiles.active"));
        System.out.println("========================");
    }
}