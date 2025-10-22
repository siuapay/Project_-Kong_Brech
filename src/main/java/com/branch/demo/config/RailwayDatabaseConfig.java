package com.branch.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@Configuration
public class RailwayDatabaseConfig {

    /**
     * Railway DataSource Configuration
     * This will override default datasource when DATABASE_URL is present
     */
    @Bean
    @Primary
    @ConditionalOnProperty(name = "DATABASE_URL")
    public DataSource railwayDataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl == null || databaseUrl.isEmpty()) {
            throw new RuntimeException("DATABASE_URL environment variable is not set");
        }
        
        System.out.println("Using Railway DATABASE_URL: " + databaseUrl.substring(0, Math.min(50, databaseUrl.length())) + "...");
        
        return DataSourceBuilder.create()
                .url(databaseUrl)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}