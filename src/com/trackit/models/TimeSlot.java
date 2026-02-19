package com.trackit.models;

public class TimeSlot {
    private int period;
    private String subjectCode;
    private String subjectName;
    private String department;
    private String section;
    
    public TimeSlot(int period, String subjectCode, String subjectName) {
        this.period = period;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
    }
    
    public TimeSlot(int period, String subjectCode, String subjectName, 
                    String department, String section) {
        this(period, subjectCode, subjectName);
        this.department = department;
        this.section = section;
    }
    
    public int getPeriod() { return period; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public String getDepartment() { return department; }
    public String getSection() { return section; }
    
    @Override
    public String toString() {
        if (department != null) {
            return String.format("Period %d: %s (%s) - %s %s", 
                period, subjectName, subjectCode, department, section);
        }
        return String.format("Period %d: %s (%s)", period, subjectName, subjectCode);
    }
}