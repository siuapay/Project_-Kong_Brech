package com.branch.demo.validation;

/**
 * Validation groups for different scenarios
 */
public class ValidationGroups {
    
    /**
     * Validation group for accounts with profile link (NhanSu or ChapSu)
     * Email and fullName are optional
     */
    public interface WithProfileLink {}
    
    /**
     * Validation group for accounts without profile link
     * Email and fullName are required
     */
    public interface WithoutProfileLink {}
    
    /**
     * Common validation group for all accounts
     * Username, role, status are always required
     */
    public interface Common {}
}