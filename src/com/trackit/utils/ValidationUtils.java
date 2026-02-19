package com.trackit.utils;

public class ValidationUtils {
    
    public static boolean isValidRollNo(String rollNo) {
        if (rollNo == null) return false;
        return rollNo.matches("\\d{4} \\d{2} \\d{4} \\d{3}");
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 3;
    }
    
    public static boolean isValidAttendanceStatus(String status) {
        return status != null && 
               (status.equals(Constants.STATUS_PRESENT) || 
                status.equals(Constants.STATUS_ABSENT) || 
                status.equals(Constants.STATUS_ON_DUTY));
    }
    
    public static boolean isValidPeriod(int period) {
        return period >= 1 && period <= Constants.PERIODS_PER_DAY;
    }
    
    public static boolean isValidDay(String day) {
        if (day == null) return false;
        for (String validDay : Constants.DAYS) {
            if (validDay.equalsIgnoreCase(day)) return true;
        }
        return false;
    }
    
    public static boolean isValidPercentage(double percentage) {
        return percentage >= 0 && percentage <= 100;
    }
    
    public static boolean isValidDepartment(String department) {
        if (department == null) return false;
        for (String d : Constants.DEPARTMENTS) {
            if (d.equalsIgnoreCase(department)) return true;
        }
        return false;
    }
    
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}