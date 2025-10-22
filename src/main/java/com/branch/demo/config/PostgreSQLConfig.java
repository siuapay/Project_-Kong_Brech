package com.branch.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Configuration
@Profile("railway")
public class PostgreSQLConfig {

    /**
     * PostgreSQL specific configuration
     * This will be activated when using Railway profile
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "org.postgresql.Driver")
    public String postgresqlSetup(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            // Enable UUID extension if needed
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\"");
            } catch (SQLException e) {
                // Extension might already exist or not needed
                System.out.println("UUID extension setup: " + e.getMessage());
            }
            
            System.out.println("PostgreSQL configuration completed successfully");
            return "PostgreSQL configured";
        } catch (SQLException e) {
            System.err.println("Error configuring PostgreSQL: " + e.getMessage());
            return "PostgreSQL configuration failed";
        }
    }
}