package com.trackit.models;

public class Student extends User {
    private String rollNo;
    private String department;
    private String section;
    private Timetable timetable;
    
    public Student(String id, String name, String password, String rollNo, 
                   String department, String section) {
        super(id, name, password);
        this.rollNo = rollNo;
        this.department = department;
        this.section = section;
        this.timetable = new Timetable();
    }
    
    public String getRollNo() { return rollNo; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getSection() { return section; }
    public Timetable getTimetable() { return timetable; }
    public void setTimetable(Timetable timetable) { this.timetable = timetable; }
    
    @Override
    public String getRole() { return "STUDENT"; }
}