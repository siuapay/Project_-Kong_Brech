package com.branch.demo.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import jakarta.servlet.MultipartConfigElement;

@Configuration
public class MultipartConfig {

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        
        // Set max file size (100MB)
        factory.setMaxFileSize(DataSize.ofMegabytes(100));
        
        // Set max request size (200MB) - for multiple files + rich content
        factory.setMaxRequestSize(DataSize.ofMegabytes(200));
        
        // Set file size threshold (2KB)
        factory.setFileSizeThreshold(DataSize.ofKilobytes(2));
        
        return factory.createMultipartConfig();
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}