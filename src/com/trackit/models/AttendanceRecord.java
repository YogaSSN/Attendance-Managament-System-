package com.trackit.models;

import java.time.LocalDate;

public class AttendanceRecord {
    private String rollNo;
    private String subjectCode;
    private LocalDate date;
    private int period;
    private String status;
    
    public AttendanceRecord(String rollNo, String subjectCode, LocalDate date, 
                           int period, String status) {
        this.rollNo = rollNo;
        this.subjectCode = subjectCode;
        this.date = date;
        this.period = period;
        this.status = status;
    }
    
    public String getRollNo() { return rollNo; }
    public String getSubjectCode() { return subjectCode; }
    public LocalDate getDate() { return date; }
    public int getPeriod() { return period; }
    public String getStatus() { return status; }
    
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return String.format("%s|%s|%s|%d|%s", 
            rollNo, subjectCode, date, period, status);
    }
}
