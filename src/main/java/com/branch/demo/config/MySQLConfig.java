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
@Profile("production")
public class MySQLConfig {

    /**
     * MySQL specific configuration
     * This will be activated when using production profile with MySQL
     */
    @Bean
    @ConditionalOnProperty(name = "spring.datasource.driver-class-name", havingValue = "com.mysql.cj.jdbc.Driver")
    public String mysqlSetup(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            // Set MySQL specific settings
            try (Statement statement = connection.createStatement()) {
                // Set charset and collation for UTF-8 support
                statement.execute("SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci");
                statement.execute("SET CHARACTER SET utf8mb4");
            } catch (SQLException e) {
                // Settings might already be configured
                System.out.println("MySQL charset setup: " + e.getMessage());
            }
            
            System.out.println("MySQL configuration completed successfully");
            return "MySQL configured";
        } catch (SQLException e) {
            System.err.println("Error configuring MySQL: " + e.getMessage());
            return "MySQL configuration failed";
        }
    }
}