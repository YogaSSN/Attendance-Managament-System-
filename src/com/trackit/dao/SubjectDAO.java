package com.trackit.dao;

import com.trackit.models.Subject;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.util.*;

public class SubjectDAO {
    
    public Subject getSubjectByCode(String code) throws FileIOException {
        List<String> lines = FileHandler.readFile(Constants.SUBJECT_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6 && parts[0].equals(code)) {
                return new Subject(parts[0], parts[1], parts[2], parts[3], 
                                 parts[4], Integer.parseInt(parts[5]));
            }
        }
        return null;
    }
    
    public List<Subject> getAllSubjects() throws FileIOException {
        List<Subject> subjects = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.SUBJECT_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6) {
                subjects.add(new Subject(parts[0], parts[1], parts[2], parts[3], 
                                       parts[4], Integer.parseInt(parts[5])));
            }
        }
        return subjects;
    }
    
    public List<Subject> getSubjectsByFaculty(String facultyId) throws FileIOException {
        List<Subject> subjects = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.SUBJECT_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6 && parts[2].equals(facultyId)) {
                subjects.add(new Subject(parts[0], parts[1], parts[2], parts[3], 
                                       parts[4], Integer.parseInt(parts[5])));
            }
        }
        return subjects;
    }
    
    public List<Subject> getSubjectsByClass(String className, String section) throws FileIOException {
        List<Subject> subjects = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.SUBJECT_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6 && parts[3].equals(className) && parts[4].equals(section)) {
                subjects.add(new Subject(parts[0], parts[1], parts[2], parts[3], 
                                       parts[4], Integer.parseInt(parts[5])));
            }
        }
        return subjects;
    }

    // New API using department terminology
    public List<Subject> getSubjectsByDepartment(String department, String section) throws FileIOException {
        List<Subject> subjects = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.SUBJECT_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6 && parts[3].equalsIgnoreCase(department) && parts[4].equalsIgnoreCase(section)) {
                subjects.add(new Subject(parts[0], parts[1], parts[2], parts[3], parts[4], Integer.parseInt(parts[5])));
            }
        }
        return subjects;
    }
}