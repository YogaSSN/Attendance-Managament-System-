package com.trackit.models;

public class Faculty extends User {
    private String department;
    private Timetable timetable;
    
    public Faculty(String id, String name, String password, String department) {
        super(id, name, password);
        this.department = department;
        this.timetable = new Timetable();
    }
    
    public String getDepartment() { return department; }
    public Timetable getTimetable() { return timetable; }
    public void setTimetable(Timetable timetable) { this.timetable = timetable; }
    
    @Override
    public String getRole() { return "FACULTY"; }
}