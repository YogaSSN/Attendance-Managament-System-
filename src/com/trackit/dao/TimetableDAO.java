package com.trackit.dao;

import com.trackit.models.*;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.util.*;

public class TimetableDAO {
    
    public void saveFacultyTimetable(String facultyId, Timetable timetable) 
            throws FileIOException {
        FileHandler.ensureFileExists(Constants.FACULTY_TIMETABLE_FILE);
        
        List<String> lines = FileHandler.readFile(Constants.FACULTY_TIMETABLE_FILE);
        lines.removeIf(line -> line.startsWith(facultyId + "|"));
        
        for (Map.Entry<String, List<TimeSlot>> entry : timetable.getWeeklySchedule().entrySet()) {
            String day = entry.getKey();
            for (TimeSlot slot : entry.getValue()) {
                String line = String.format("%s|%s|%d|%s|%s|%s", 
                    facultyId, day, slot.getPeriod(), slot.getSubjectCode(),
                    slot.getDepartment(), slot.getSection());
                lines.add(line);
            }
        }
        
        FileHandler.writeFile(Constants.FACULTY_TIMETABLE_FILE, lines);
    }
    
    public Timetable loadFacultyTimetable(String facultyId) throws FileIOException {
        Timetable timetable = new Timetable();
        List<String> lines = FileHandler.readFile(Constants.FACULTY_TIMETABLE_FILE);
        SubjectDAO subjectDAO = new SubjectDAO();
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 6 && parts[0].equals(facultyId)) {
                try {
                    Subject subject = subjectDAO.getSubjectByCode(parts[3]);
                    String subjectName = subject != null ? subject.getName() : parts[3];
                    TimeSlot slot = new TimeSlot(
                        Integer.parseInt(parts[2]), parts[3], subjectName, 
                        parts[4], parts[5]
                    );
                    timetable.addSlot(parts[1], slot);
                } catch (Exception e) {
                    ErrorHandler.logError("Error loading faculty timetable", e);
                }
            }
        }
        return timetable;
    }
    
    public void saveStudentTimetable(String rollNo, Timetable timetable) 
            throws FileIOException {
        FileHandler.ensureFileExists(Constants.STUDENT_TIMETABLE_FILE);
        
        List<String> lines = FileHandler.readFile(Constants.STUDENT_TIMETABLE_FILE);
        lines.removeIf(line -> line.startsWith(rollNo + "|"));
        
        for (Map.Entry<String, List<TimeSlot>> entry : timetable.getWeeklySchedule().entrySet()) {
            String day = entry.getKey();
            for (TimeSlot slot : entry.getValue()) {
                String line = String.format("%s|%s|%d|%s", 
                    rollNo, day, slot.getPeriod(), slot.getSubjectCode());
                lines.add(line);
            }
        }
        
        FileHandler.writeFile(Constants.STUDENT_TIMETABLE_FILE, lines);
    }
    
    public Timetable loadStudentTimetable(String rollNo) throws FileIOException {
        Timetable timetable = new Timetable();
        List<String> lines = FileHandler.readFile(Constants.STUDENT_TIMETABLE_FILE);
        SubjectDAO subjectDAO = new SubjectDAO();
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 4 && parts[0].equals(rollNo)) {
                try {
                    Subject subject = subjectDAO.getSubjectByCode(parts[3]);
                    String subjectName = subject != null ? subject.getName() : parts[3];
                    TimeSlot slot = new TimeSlot(
                        Integer.parseInt(parts[2]), parts[3], subjectName
                    );
                    timetable.addSlot(parts[1], slot);
                } catch (Exception e) {
                    ErrorHandler.logError("Error loading student timetable", e);
                }
            }
        }
        return timetable;
    }
}