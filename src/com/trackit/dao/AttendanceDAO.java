package com.trackit.dao;

import com.trackit.models.AttendanceRecord;
import com.trackit.utils.*;
import com.trackit.exceptions.*;
import java.time.LocalDate;
import java.util.*;

public class AttendanceDAO {
    
    public void saveAttendance(AttendanceRecord record) throws FileIOException {
        FileHandler.ensureFileExists(Constants.ATTENDANCE_FILE);
        
        List<String> lines = FileHandler.readFile(Constants.ATTENDANCE_FILE);
        boolean updated = false;
        
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split("\\|");
            if (parts.length >= 5 && 
                parts[0].equals(record.getRollNo()) &&
                parts[1].equals(record.getSubjectCode()) &&
                parts[2].equals(DateUtils.formatDate(record.getDate())) &&
                parts[3].equals(String.valueOf(record.getPeriod()))) {
                lines.set(i, record.toString());
                updated = true;
                break;
            }
        }
        
        if (!updated) {
            lines.add(record.toString());
        }
        
        FileHandler.writeFile(Constants.ATTENDANCE_FILE, lines);
    }
    
    public List<AttendanceRecord> getAttendanceByRollNo(String rollNo) throws FileIOException {
        List<AttendanceRecord> records = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.ATTENDANCE_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5 && parts[0].equals(rollNo)) {
                records.add(new AttendanceRecord(
                    parts[0], parts[1], DateUtils.parseDate(parts[2]), 
                    Integer.parseInt(parts[3]), parts[4]
                ));
            }
        }
        return records;
    }
    
    public List<AttendanceRecord> getAttendanceBySubject(String rollNo, String subjectCode) 
            throws FileIOException {
        List<AttendanceRecord> records = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.ATTENDANCE_FILE);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5 && parts[0].equals(rollNo) && parts[1].equals(subjectCode)) {
                records.add(new AttendanceRecord(
                    parts[0], parts[1], DateUtils.parseDate(parts[2]), 
                    Integer.parseInt(parts[3]), parts[4]
                ));
            }
        }
        return records;
    }
    
    public List<AttendanceRecord> getAttendanceByDate(LocalDate date) throws FileIOException {
        List<AttendanceRecord> records = new ArrayList<>();
        List<String> lines = FileHandler.readFile(Constants.ATTENDANCE_FILE);
        String dateStr = DateUtils.formatDate(date);
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length >= 5 && parts[2].equals(dateStr)) {
                records.add(new AttendanceRecord(
                    parts[0], parts[1], DateUtils.parseDate(parts[2]), 
                    Integer.parseInt(parts[3]), parts[4]
                ));
            }
        }
        return records;
    }
    
    public void saveMultipleAttendance(List<AttendanceRecord> records) throws FileIOException {
        for (AttendanceRecord record : records) {
            saveAttendance(record);
        }
    }
}