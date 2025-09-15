package com.branch.demo.util;

import java.security.SecureRandom;

public class PasswordGenerator {
    
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS;
    
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a random password with specified length
     * @param length Password length (minimum 6)
     * @return Random password containing uppercase, lowercase and digits
     */
    public static String generateRandomPassword(int length) {
        if (length < 6) {
            length = 6;
        }
        
        StringBuilder password = new StringBuilder();
        
        // Ensure at least one character from each type
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        
        // Fill the rest with random characters
        for (int i = 3; i < length; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString());
    }
    
    /**
     * Generate a random password with default length of 8 characters
     * @return Random password
     */
    public static String generateRandomPassword() {
        return generateRandomPassword(8);
    }
    
    private static String shuffleString(String string) {
        char[] chars = string.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}