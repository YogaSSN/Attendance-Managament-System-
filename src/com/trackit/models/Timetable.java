package com.trackit.models;

import java.util.*;

public class Timetable {
    private Map<String, List<TimeSlot>> weeklySchedule;
    
    public Timetable() {
        weeklySchedule = new HashMap<>();
        initializeDays();
    }
    
    private void initializeDays() {
        String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        for (String day : days) {
            weeklySchedule.put(day, new ArrayList<>());
        }
    }
    
    public void addSlot(String day, TimeSlot slot) {
        weeklySchedule.computeIfAbsent(day.toUpperCase(), k -> new ArrayList<>()).add(slot);
    }
    
    public List<TimeSlot> getDaySchedule(String day) {
        return weeklySchedule.getOrDefault(day.toUpperCase(), new ArrayList<>());
    }
    
    public Map<String, List<TimeSlot>> getWeeklySchedule() {
        return weeklySchedule;
    }
    
    public void clearDay(String day) {
        weeklySchedule.getOrDefault(day.toUpperCase(), new ArrayList<>()).clear();
    }
    
    public void clearAll() {
        for (List<TimeSlot> slots : weeklySchedule.values()) {
            slots.clear();
        }
    }
}