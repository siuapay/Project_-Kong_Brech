package com.branch.demo.config;

import com.branch.demo.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException, ServletException {
        
        String username = request.getParameter("username");
        String errorMessage;
        
        if (exception instanceof LockedException) {
            errorMessage = "Tài khoản đã bị khóa do đăng nhập sai quá nhiều lần. Vui lòng thử lại sau 30 phút.";
        } else if (exception instanceof BadCredentialsException) {
            userDetailsService.incrementLoginAttempts(username);
            errorMessage = "Tên đăng nhập hoặc mật khẩu không đúng.";
        } else {
            errorMessage = "Đăng nhập thất bại. Vui lòng thử lại.";
        }
        
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=true&message=" + encodedMessage);
    }
}