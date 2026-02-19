package com.trackit.dao;

import com.trackit.models.Faculty;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.util.*;

public class FacultyDAO {
    
    public Faculty getFacultyById(String id) throws FileIOException {
        List<String> lines = FileHandler.readFile(Constants.FACULTY_FILE);
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && parts[0].equals(id)) {
                return new Faculty(parts[0], parts[1], parts[2], parts[3]);
            }
        }
        return null;
    }
    
    public List<Faculty> getAllFaculty() throws FileIOException {
        List<Faculty> faculties = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.FACULTY_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4) {
                faculties.add(new Faculty(parts[0], parts[1], parts[2], parts[3]));
            }
        }
        return faculties;
    }
    
    public boolean validateCredentials(String id, String password) throws FileIOException {
        Faculty faculty = getFacultyById(id);
        return faculty != null && faculty.getPassword().equals(password);
    }
}