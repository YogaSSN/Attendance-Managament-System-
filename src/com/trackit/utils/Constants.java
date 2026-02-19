package com.trackit.utils;

public class Constants {
    // Application Info
    public static final String APP_NAME = "Track IT!";
    public static final String APP_VERSION = "1.0";
    
    // Roles
    public static final String FACULTY_ROLE = "FACULTY";
    public static final String STUDENT_ROLE = "STUDENT";
    
    // Attendance Status
    public static final String STATUS_PRESENT = "P";
    public static final String STATUS_ABSENT = "A";
    public static final String STATUS_ON_DUTY = "OD";
    
    // Default Settings
    public static final double MIN_ATTENDANCE_PERCENTAGE = 75.0;
    public static final int WORKING_DAYS = 5;
    public static final int PERIODS_PER_DAY = 8;
    
    // File Paths
    public static final String DATA_DIR = "data/";
    public static final String FACULTY_FILE = DATA_DIR + "faculty.txt";
    public static final String STUDENT_FILE = DATA_DIR + "students.txt";
    public static final String SUBJECT_FILE = DATA_DIR + "subjects.txt";
    public static final String ATTENDANCE_FILE = DATA_DIR + "attendance.txt";
    public static final String FACULTY_TIMETABLE_FILE = DATA_DIR + "faculty_timetables.txt";
    public static final String STUDENT_TIMETABLE_FILE = DATA_DIR + "student_timetables.txt";
    public static final String REPORTS_DIR = DATA_DIR + "reports/";
    
    // Days of Week
    public static final String[] DAYS = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    
    // Departments
    public static final String[] DEPARTMENTS = {"CSE", "IT", "ECE", "EEE", "MECH", "BME", "CHE"};
    
    // Colors (RGB)
    public static final int[] COLOR_PRIMARY = {25, 118, 210};
    public static final int[] COLOR_SUCCESS = {76, 175, 80};
    public static final int[] COLOR_DANGER = {244, 67, 54};
    public static final int[] COLOR_WARNING = {255, 152, 0};
}