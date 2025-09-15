package com.branch.demo.service;

import com.branch.demo.domain.Account;
import com.branch.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));
        
        return account;
    }
    
    @Transactional
    public void updateLastLogin(String username) {
        Account account = accountRepository.findByUsernameOrEmail(username)
                .orElse(null);
        if (account != null) {
            account.setLastLogin(LocalDateTime.now());
            account.setLoginAttempts(0); // Reset login attempts on successful login
            accountRepository.save(account);
        }
    }
    
    @Transactional
    public void incrementLoginAttempts(String username) {
        Account account = accountRepository.findByUsernameOrEmail(username)
                .orElse(null);
        if (account != null) {
            int attempts = account.getLoginAttempts() + 1;
            account.setLoginAttempts(attempts);
            
            // Lock account after 5 failed attempts for 30 minutes
            if (attempts >= 5) {
                account.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                account.setStatus(Account.AccountStatus.LOCKED);
            }
            
            accountRepository.save(account);
        }
    }
}