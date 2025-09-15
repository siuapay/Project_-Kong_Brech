package com.branch.demo.config;

import com.branch.demo.domain.Account;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {
    
    @Bean
    public AuditorAware<Account> auditorProvider() {
        return new AuditorAwareImpl();
    }
    
    public static class AuditorAwareImpl implements AuditorAware<Account> {
        
        @Override
        public Optional<Account> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated() || 
                authentication.getPrincipal().equals("anonymousUser")) {
                return Optional.empty();
            }
            
            if (authentication.getPrincipal() instanceof Account) {
                return Optional.of((Account) authentication.getPrincipal());
            }
            
            return Optional.empty();
        }
    }
}