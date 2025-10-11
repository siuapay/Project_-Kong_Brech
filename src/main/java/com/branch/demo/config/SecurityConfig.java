package com.branch.demo.config;

import com.branch.demo.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    

    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Static resources
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/uploads/**").permitAll()
                
                // Auth pages (public)
                .requestMatchers("/login", "/register").permitAll()
                
                // Public client pages
                .requestMatchers("/", "/about", "/contact", "/news/**", "/tin-tuc/**").permitAll()
                
                // Profile pages - require authentication
                .requestMatchers("/profile", "/change-password").authenticated()
                
                // Block access to /admin/nhom (commented out functionality)
                .requestMatchers("/admin/nhom/**").denyAll()
                
                // Admin pages - require ADMIN role
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // All other pages are public (no authentication required)
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform-login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecret") // Secret key for remember-me token
                .tokenValiditySeconds(86400 * 7) // 7 days
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me") // Parameter name from form
                .rememberMeCookieName("remember-me-cookie")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me-cookie")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Disable for development
            
        return http.build();
    }
}