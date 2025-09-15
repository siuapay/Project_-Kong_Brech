package com.branch.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from uploads directory
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");

        // Serve static resources
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
                
        // Serve images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");
                
        // Serve CSS
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                
        // Serve JS
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}