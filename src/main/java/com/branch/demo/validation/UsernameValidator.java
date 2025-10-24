package com.branch.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    
    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        // No initialization needed
    }
    
    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null || username.trim().isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tên đăng nhập không được để trống")
                   .addConstraintViolation();
            return false;
        }
        
        // Check length
        if (username.length() < 3 || username.length() > 50) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tên đăng nhập phải từ 3-50 ký tự")
                   .addConstraintViolation();
            return false;
        }
        
        // Check for spaces
        if (username.contains(" ")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tên đăng nhập không được chứa dấu cách")
                   .addConstraintViolation();
            return false;
        }
        
        // Check for valid characters (alphanumeric, underscore, hyphen, dot)
        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Tên đăng nhập chỉ được chứa chữ cái, số, dấu gạch dưới, gạch ngang và dấu chấm")
                   .addConstraintViolation();
            return false;
        }
        
        return true;
    }
}