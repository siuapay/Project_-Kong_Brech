package com.branch.demo.util;

import org.springframework.stereotype.Component;

@Component("numberFormat")
public class NumberFormatUtil {
    
    /**
     * Format number to compact form (1k, 1.2M, etc.)
     * @param number the number to format
     * @return formatted string
     */
    public String formatCompact(Number number) {
        if (number == null) {
            return "0";
        }
        
        long value = number.longValue();
        
        if (value < 1000) {
            return String.valueOf(value);
        } else if (value < 1000000) {
            if (value % 1000 == 0) {
                return (value / 1000) + "k";
            } else {
                return String.format("%.1fk", value / 1000.0);
            }
        } else if (value < 1000000000) {
            if (value % 1000000 == 0) {
                return (value / 1000000) + "M";
            } else {
                return String.format("%.1fM", value / 1000000.0);
            }
        } else {
            if (value % 1000000000 == 0) {
                return (value / 1000000000) + "B";
            } else {
                return String.format("%.1fB", value / 1000000000.0);
            }
        }
    }
    
    /**
     * Format number with thousand separators (1,000)
     * @param number the number to format
     * @return formatted string with commas
     */
    public String formatWithCommas(Number number) {
        if (number == null) {
            return "0";
        }
        return String.format("%,d", number.longValue());
    }
}