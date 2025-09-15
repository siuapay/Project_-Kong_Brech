package com.branch.demo.controller;

import com.branch.demo.domain.Account;
import com.branch.demo.repository.AccountRepository;
import com.branch.demo.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Authentication authentication,
                           Model model) {
        
        // Nếu user đã đăng nhập, redirect về admin dashboard
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return "redirect:/admin";
        }
        
        if (error != null) {
            model.addAttribute("error", true);
            model.addAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không đúng");
        }
        
        if (logout != null) {
            model.addAttribute("success", true);
            model.addAttribute("successMessage", "Đăng xuất thành công");
        }
        
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model, Authentication authentication) {
        // Nếu user đã đăng nhập, redirect về admin dashboard
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            return "redirect:/admin";
        }
        
        // Check if there are any admin accounts
        long adminCount = accountRepository.countActiveAdmins();
        if (adminCount > 0) {
            return "redirect:/login?error=true";
        }
        
        model.addAttribute("account", new Account());
        return "auth/register";
    }
    
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute Account account,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        
        // Check if there are any admin accounts
        long adminCount = accountRepository.countActiveAdmins();
        if (adminCount > 0) {
            return "redirect:/login?error=true";
        }
        
        // Validate form
        if (result.hasErrors()) {
            return "auth/register";
        }
        
        // Check if username exists
        if (accountRepository.existsByUsername(account.getUsername())) {
            result.rejectValue("username", "error.account", "Tên đăng nhập đã tồn tại");
            return "auth/register";
        }
        
        // Check if email exists
        if (accountRepository.existsByEmail(account.getEmail())) {
            result.rejectValue("email", "error.account", "Email đã được sử dụng");
            return "auth/register";
        }
        
        // Encode password and save
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setRole(Account.Role.ADMIN);
        account.setStatus(Account.AccountStatus.ACTIVE);
        
        accountRepository.save(account);
        
        return "redirect:/login?success=true";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        model.addAttribute("account", account);
        return "auth/profile";
    }
    
    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute Account accountForm,
                               BindingResult result,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        
        Account currentAccount = (Account) authentication.getPrincipal();
        
        if (result.hasErrors()) {
            return "auth/profile";
        }
        
        // Check if email is changed and already exists
        if (!currentAccount.getEmail().equals(accountForm.getEmail()) && 
            accountRepository.existsByEmail(accountForm.getEmail())) {
            result.rejectValue("email", "error.account", "Email đã được sử dụng");
            return "auth/profile";
        }
        
        // Update account info
        currentAccount.setFullName(accountForm.getFullName());
        currentAccount.setEmail(accountForm.getEmail());
        
        accountRepository.save(currentAccount);
        
        redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công");
        return "redirect:/profile";
    }
    
    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        
        Account account = (Account) authentication.getPrincipal();
        
        // Validate current password
        if (!passwordEncoder.matches(currentPassword, account.getPassword())) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng");
            return "redirect:/profile";
        }
        
        // Validate new password
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải ít nhất 6 ký tự");
            return "redirect:/profile";
        }
        
        // Validate confirm password
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Xác nhận mật khẩu không khớp");
            return "redirect:/profile";
        }
        
        // Update password
        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepository.save(account);
        
        redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công");
        return "redirect:/profile";
    }
}