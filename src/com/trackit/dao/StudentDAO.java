package com.trackit.dao;

import com.trackit.models.Student;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.util.*;

public class StudentDAO {
    
    public Student getStudentByRollNo(String rollNo) throws FileIOException {
        List<String> lines = FileHandler.readFile(Constants.STUDENT_FILE);
        String input = rollNo == null ? "" : rollNo.trim();
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                String fileRoll = parts[0].trim();
                String name = parts[1].trim();
                String password = parts[2].trim();
                String department = parts[3].trim();
                String section = parts[4].trim();
                if (fileRoll.equalsIgnoreCase(input)) {
                    System.out.println("[DEBUG] Matched student record: roll=" + fileRoll + ", name=" + name);
                    return new Student(fileRoll, name, password, fileRoll, department, section);
                }
            }
        }
        return null;
    }
    
    public List<Student> getAllStudents() throws FileIOException {
        List<Student> students = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.STUDENT_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                students.add(new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[0].trim(), parts[3].trim(), parts[4].trim()));
            }
        }
        return students;
    }
    
    public List<Student> getStudentsByDepartment(String department, String section) throws FileIOException {
        List<Student> students = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.STUDENT_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5) {
                String dept = parts[3].trim();
                String sec = parts[4].trim();
                if (dept.equalsIgnoreCase(department) && sec.equalsIgnoreCase(section)) {
                    students.add(new Student(parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[0].trim(), dept, sec));
                }
            }
        }
        return students;
    }
    
    public boolean validateCredentials(String rollNo, String password) throws FileIOException {
        String inputRoll = rollNo == null ? "" : rollNo.trim();
        String inputPwd = password == null ? "" : password.trim();
        System.out.println("[DEBUG] Attempting student login with roll: '" + inputRoll + "'");
        System.out.println("[DEBUG] Password entered length: " + inputPwd.length());
        List<String> lines = FileHandler.readFile(Constants.STUDENT_FILE);
        if (!lines.isEmpty()) {
            System.out.println("[DEBUG] First student record: " + lines.get(0));
        }
        Student student = getStudentByRollNo(inputRoll);
        if (student == null) {
            System.out.println("[DEBUG] No matching student found for roll: '" + inputRoll + "'");
            return false;
        }
        boolean ok = student.getPassword() != null && student.getPassword().trim().equals(inputPwd);
        System.out.println("[DEBUG] Password match: " + ok);
        return ok;
    }
}
