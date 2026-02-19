package com.trackit.main;

import com.trackit.views.common.LoginFrame;
import com.trackit.utils.FileHandler;
import com.trackit.utils.Constants;
import javax.swing.*;

/**
 * Track IT! - Smart Attendance Management System
 * Main Application Entry Point
 * 
 * @author Tushyent N P
 * @version 1.0
 */
public class TrackITApplication {
    public static void main(String[] args) {
        initializeDataFiles();
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
    
    private static void initializeDataFiles() {
        try {
            FileHandler.ensureFileExists(Constants.FACULTY_FILE);
            FileHandler.ensureFileExists(Constants.STUDENT_FILE);
            FileHandler.ensureFileExists(Constants.SUBJECT_FILE);
            FileHandler.ensureFileExists(Constants.ATTENDANCE_FILE);
            FileHandler.ensureFileExists(Constants.FACULTY_TIMETABLE_FILE);
            FileHandler.ensureFileExists(Constants.STUDENT_TIMETABLE_FILE);
            
            java.io.File reportsDir = new java.io.File(Constants.REPORTS_DIR);
            if (!reportsDir.exists()) {
                reportsDir.mkdirs();
            }
        } catch (Exception e) {
            System.err.println("Error initializing data files: " + e.getMessage());
        }
    }
}