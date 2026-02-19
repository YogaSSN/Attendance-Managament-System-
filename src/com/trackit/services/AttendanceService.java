package com.trackit.services;

import com.trackit.dao.*;
import com.trackit.models.*;
import com.trackit.utils.Constants;
import com.trackit.exceptions.*;
import java.util.*;

public class AttendanceService {
    private AttendanceDAO attendanceDAO;
    
    public AttendanceService() {
        this.attendanceDAO = new AttendanceDAO();
    }
    
    public void markAttendance(AttendanceRecord record) throws FileIOException {
        attendanceDAO.saveAttendance(record);
    }
    
    public void markMultipleAttendance(List<AttendanceRecord> records) throws FileIOException {
        attendanceDAO.saveMultipleAttendance(records);
    }
    
    public double calculateSubjectAttendance(String rollNo, String subjectCode) 
            throws FileIOException {
        List<AttendanceRecord> records = attendanceDAO.getAttendanceBySubject(rollNo, subjectCode);
        
        if (records.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = records.stream()
            .filter(r -> r.getStatus().equals(Constants.STATUS_PRESENT) || 
                        r.getStatus().equals(Constants.STATUS_ON_DUTY))
            .count();
        
        return (presentCount * 100.0) / records.size();
    }
    
    public double calculateOverallAttendance(String rollNo) throws FileIOException {
        List<AttendanceRecord> records = attendanceDAO.getAttendanceByRollNo(rollNo);
        
        if (records.isEmpty()) {
            return 0.0;
        }
        
        long presentCount = records.stream()
            .filter(r -> r.getStatus().equals(Constants.STATUS_PRESENT) || 
                        r.getStatus().equals(Constants.STATUS_ON_DUTY))
            .count();
        
        return (presentCount * 100.0) / records.size();
    }
    
    public Map<String, Double> getSubjectWiseAttendance(String rollNo) 
            throws FileIOException {
        Map<String, Double> attendance = new HashMap<>();
        List<AttendanceRecord> records = attendanceDAO.getAttendanceByRollNo(rollNo);
        
        Map<String, List<AttendanceRecord>> subjectRecords = new HashMap<>();
        for (AttendanceRecord record : records) {
            subjectRecords.computeIfAbsent(record.getSubjectCode(), k -> new ArrayList<>())
                         .add(record);
        }
        
        for (Map.Entry<String, List<AttendanceRecord>> entry : subjectRecords.entrySet()) {
            String subjectCode = entry.getKey();
            List<AttendanceRecord> subRecords = entry.getValue();
            
            long presentCount = subRecords.stream()
                .filter(r -> r.getStatus().equals(Constants.STATUS_PRESENT) || 
                            r.getStatus().equals(Constants.STATUS_ON_DUTY))
                .count();
            
            double percentage = (presentCount * 100.0) / subRecords.size();
            attendance.put(subjectCode, percentage);
        }
        
        return attendance;
    }
    
    public int getODCount(String rollNo, String subjectCode) throws FileIOException {
        List<AttendanceRecord> records = attendanceDAO.getAttendanceBySubject(rollNo, subjectCode);
        return (int) records.stream()
            .filter(r -> r.getStatus().equals(Constants.STATUS_ON_DUTY))
            .count();
    }
    
    public List<AttendanceRecord> getAttendanceByRollNo(String rollNo) throws FileIOException {
        return attendanceDAO.getAttendanceByRollNo(rollNo);
    }
}
