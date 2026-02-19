package com.trackit.utils;

import javax.swing.JOptionPane;
import java.awt.Component;

public class ErrorHandler {
    
    public static void displayWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }
    
    public static void displayError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    public static void displayInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void displaySuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static boolean confirm(Component parent, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, "Confirm", 
            JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }
    
    public static void checkLowAttendance(Component parent, double percentage, 
                                         double minPercentage) {
        if (percentage < minPercentage) {
            displayWarning(parent, 
                String.format("Attendance is below minimum threshold!\nCurrent: %.2f%% | Required: %.2f%%", 
                percentage, minPercentage));
        }
    }
    
    public static void logError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }

    // Added simple helpers per requirement
    public static void showErrorMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void showSuccessMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
