package com.trackit.models;

public class Subject {
    private String subjectCode;
    private String name;
    private String facultyId;
    private String department;
    private String section;
    private int totalHours;
    
    public Subject(String subjectCode, String name, String facultyId, 
                   String department, String section, int totalHours) {
        this.subjectCode = subjectCode;
        this.name = name;
        this.facultyId = facultyId;
        this.department = department;
        this.section = section;
        this.totalHours = totalHours;
    }
    
    public String getSubjectCode() { return subjectCode; }
    public String getName() { return name; }
    public String getFacultyId() { return facultyId; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSection() { return section; }
    public int getTotalHours() { return totalHours; }
    
    // Added for compatibility with views expecting these names
    public String getCode() { return subjectCode; }
    public String getClassYear() { return department; }
    
    @Override
    public String toString() {
        return String.format("%s - %s", subjectCode, name);
    }
}