package com.branch.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

@Component
@Profile({"local", "test"})
public class MySQLConnectionTest implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        testDatabaseConnection();
    }

    private void testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            System.out.println("=== MySQL Connection Test ===");
            System.out.println("Database Product Name: " + metaData.getDatabaseProductName());
            System.out.println("Database Product Version: " + metaData.getDatabaseProductVersion());
            System.out.println("Driver Name: " + metaData.getDriverName());
            System.out.println("Driver Version: " + metaData.getDriverVersion());
            System.out.println("URL: " + metaData.getURL());
            System.out.println("Username: " + metaData.getUserName());
            
            // Test if we can execute a simple query
            try (ResultSet rs = connection.createStatement().executeQuery("SELECT 1 as test")) {
                if (rs.next()) {
                    System.out.println("Query Test: SUCCESS - Result: " + rs.getInt("test"));
                }
            }
            
            // Check current database
            try (ResultSet rs = connection.createStatement().executeQuery("SELECT DATABASE() as current_db")) {
                if (rs.next()) {
                    System.out.println("Current Database: " + rs.getString("current_db"));
                }
            }
            
            // Check timezone
            try (ResultSet rs = connection.createStatement().executeQuery("SELECT @@time_zone as timezone")) {
                if (rs.next()) {
                    System.out.println("Database Timezone: " + rs.getString("timezone"));
                }
            }
            
            System.out.println("=== Connection Test Completed Successfully ===");
            
        } catch (Exception e) {
            System.err.println("=== MySQL Connection Test FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}