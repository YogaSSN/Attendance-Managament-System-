package com.trackit.services;

import com.trackit.dao.*;
import com.trackit.models.*;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.util.*;

public class ReportService {
    private AttendanceService attendanceService;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;
    
    public ReportService() {
        this.attendanceService = new AttendanceService();
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
    }
    
    public String generateStudentReport(String rollNo) {
        try {
            Student student = studentDAO.getStudentByRollNo(rollNo);
            if (student == null) {
                return "Student not found.";
            }
            
            StringBuilder report = new StringBuilder();
            report.append("========================================\n");
            report.append("         STUDENT ATTENDANCE REPORT      \n");
            report.append("========================================\n\n");
            report.append(String.format("Name: %s\n", student.getName()));
            report.append(String.format("Roll No: %s\n", rollNo));
            report.append(String.format("Department: %s %s\n\n", student.getDepartment(), student.getSection()));
            
            Map<String, Double> attendance = attendanceService.getSubjectWiseAttendance(rollNo);
            
            report.append("Subject-wise Attendance:\n");
            report.append("----------------------------------------\n");
            
            for (Map.Entry<String, Double> entry : attendance.entrySet()) {
                Subject subject = subjectDAO.getSubjectByCode(entry.getKey());
                String subjectName = subject != null ? subject.getName() : entry.getKey();
                report.append(String.format("%-30s: %.2f%%\n", subjectName, entry.getValue()));
            }
            
            double overall = attendanceService.calculateOverallAttendance(rollNo);
            report.append("\n----------------------------------------\n");
            report.append(String.format("Overall Attendance: %.2f%%\n", overall));
            report.append("========================================\n");
            
            return report.toString();
            
        } catch (Exception e) {
            ErrorHandler.logError("Error generating student report", e);
            return "Error generating report: " + e.getMessage();
        }
    }
    
    public String generateDepartmentReport(String department, String section) {
        try {
            List<Student> students = studentDAO.getStudentsByDepartment(department, section);
            
            StringBuilder report = new StringBuilder();
            report.append("========================================================\n");
            report.append("                DEPARTMENT ATTENDANCE REPORT     \n");
            report.append("========================================================\n\n");
            report.append(String.format("Department: %s %s\n", department, section));
            report.append(String.format("Total Students: %d\n\n", students.size()));
            
            report.append(String.format("%-20s %-20s %s\n", "Roll No", "Name", "Attendance %"));
            report.append("--------------------------------------------------------\n");
            
            double totalAttendance = 0;
            for (Student student : students) {
                double attendance = attendanceService.calculateOverallAttendance(student.getRollNo());
                totalAttendance += attendance;
                report.append(String.format("%-20s %-20s %.2f%%\n", 
                    student.getRollNo(), student.getName(), attendance));
            }
            
            double avgAttendance = students.isEmpty() ? 0 : totalAttendance / students.size();
            report.append("\n--------------------------------------------------------\n");
            report.append(String.format("Average Department Attendance: %.2f%%\n", avgAttendance));
            report.append("========================================================\n");
            
            return report.toString();
            
        } catch (Exception e) {
            ErrorHandler.logError("Error generating department report", e);
            return "Error generating report: " + e.getMessage();
        }
    }
    
    public String generateSubjectReport(String subjectCode) {
        try {
            Subject subject = subjectDAO.getSubjectByCode(subjectCode);
            if (subject == null) {
                return "Subject not found.";
            }
            
            List<Student> students = studentDAO.getStudentsByDepartment(
                subject.getDepartment(), subject.getSection());
            
            StringBuilder report = new StringBuilder();
            report.append("========================================================\n");
            report.append("               SUBJECT ATTENDANCE REPORT       \n");
            report.append("========================================================\n\n");
            report.append(String.format("Subject: %s (%s)\n", subject.getName(), subjectCode));
            report.append(String.format("Department: %s %s\n\n", subject.getDepartment(), subject.getSection()));
            
            report.append(String.format("%-20s %-20s %s\n", "Roll No", "Name", "Attendance %"));
            report.append("--------------------------------------------------------\n");
            
            double totalAttendance = 0;
            int count = 0;
            
            for (Student student : students) {
                double attendance = attendanceService.calculateSubjectAttendance(
                    student.getRollNo(), subjectCode);
                if (attendance > 0) {
                    totalAttendance += attendance;
                    count++;
                    report.append(String.format("%-20s %-20s %.2f%%\n", 
                        student.getRollNo(), student.getName(), attendance));
                }
            }
            
            double avgAttendance = count == 0 ? 0 : totalAttendance / count;
            report.append("\n--------------------------------------------------------\n");
            report.append(String.format("Average Subject Attendance: %.2f%%\n", avgAttendance));
            report.append("========================================================\n");
            
            return report.toString();
            
        } catch (Exception e) {
            ErrorHandler.logError("Error generating subject report", e);
            return "Error generating report: " + e.getMessage();
        }
    }
}