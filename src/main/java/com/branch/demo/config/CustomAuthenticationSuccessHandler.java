package com.branch.demo.config;

import com.branch.demo.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // Update last login time
        userDetailsService.updateLastLogin(authentication.getName());

        // Determine redirect URL based on user role
        String redirectUrl = determineRedirectUrl(authentication);

        response.sendRedirect(redirectUrl);
    }

    private String determineRedirectUrl(Authentication authentication) {
        // Log user info for debugging
        logger.info("User '{}' logged in with authorities: {}", 
                   authentication.getName(), 
                   authentication.getAuthorities());
        
        // Check if user has ADMIN role
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        String redirectUrl;
        if (isAdmin) {
            redirectUrl = "/admin"; // Admin goes to admin dashboard
            logger.info("User '{}' is ADMIN, redirecting to: {}", authentication.getName(), redirectUrl);
        } else {
            redirectUrl = "/"; // MODERATOR, USER and others go to homepage
            logger.info("User '{}' is not ADMIN, redirecting to: {}", authentication.getName(), redirectUrl);
        }
        
        return redirectUrl;
    }
}