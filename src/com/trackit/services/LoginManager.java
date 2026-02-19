package com.trackit.services;

import com.trackit.dao.*;
import com.trackit.models.*;
import com.trackit.exceptions.*;

public class LoginManager {
    private FacultyDAO facultyDAO;
    private StudentDAO studentDAO;
    
    public LoginManager() {
        this.facultyDAO = new FacultyDAO();
        this.studentDAO = new StudentDAO();
    }
    
    public User login(String id, String password, String role) throws InvalidLoginException {
        try {
            final String cleanId = id == null ? "" : id.trim();
            final String cleanPwd = password == null ? "" : password.trim();
            System.out.println("[DEBUG] Login requested for role=" + role + ", id='" + cleanId + "'");
            if (role.equals("FACULTY")) {
                if (facultyDAO.validateCredentials(cleanId, cleanPwd)) {
                    Faculty faculty = facultyDAO.getFacultyById(cleanId);
                    if (faculty != null) {
                        return faculty;
                    }
                }
            } else if (role.equals("STUDENT")) {
                if (studentDAO.validateCredentials(cleanId, cleanPwd)) {
                    Student student = studentDAO.getStudentByRollNo(cleanId);
                    if (student != null) {
                        return student;
                    }
                }
            }
            throw new InvalidLoginException("Invalid credentials");
        } catch (FileIOException e) {
            throw new InvalidLoginException("Error accessing data: " + e.getMessage());
        }
    }
}