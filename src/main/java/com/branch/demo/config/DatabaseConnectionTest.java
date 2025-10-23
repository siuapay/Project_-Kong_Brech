package com.branch.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;

@Component
public class DatabaseConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            System.out.println("=== DATABASE CONNECTION TEST ===");
            System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
            System.out.println("Database Product Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Database URL: " + metaData.getURL());
            System.out.println("Database User: " + metaData.getUserName());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            
            // Check if it's MySQL
            if (metaData.getDatabaseProductName().toLowerCase().contains("mysql")) {
                System.out.println("✅ MySQL Connection successful!");
            } else {
                System.out.println("✅ Database Connection successful!");
            }
            System.out.println("===============================");
            
        } catch (Exception e) {
            System.err.println("=== DATABASE CONNECTION FAILED ===");
            System.err.println("Error: " + e.getMessage());
            System.err.println("Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "Unknown"));
            System.err.println("==================================");
            throw e;
        }
    }
}