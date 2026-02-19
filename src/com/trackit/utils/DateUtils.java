package com.trackit.utils;

import java.time.*;
import java.time.format.*;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    public static LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DATE_FORMATTER);
        } catch (Exception e) {
            return LocalDate.now();
        }
    }
    
    public static String getCurrentDay() {
        return LocalDate.now().getDayOfWeek().toString();
    }
    
    public static String getCurrentDateString() {
        return formatDate(LocalDate.now());
    }
    
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }
}