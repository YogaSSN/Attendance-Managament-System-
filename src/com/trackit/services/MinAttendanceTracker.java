package com.trackit.services;

public class MinAttendanceTracker {
    
    public int calculateBunkableClasses(int attended, int total, double minPercentage) {
        if (total == 0) return 0;
        
        double currentPercentage = (attended * 100.0) / total;
        if (currentPercentage < minPercentage) {
            return 0;
        }
        
        int bunkable = 0;
        int tempTotal = total;
        int tempAttended = attended;
        
        while (true) {
            tempTotal++;
            double newPercentage = (tempAttended * 100.0) / tempTotal;
            if (newPercentage < minPercentage) {
                break;
            }
            bunkable++;
        }
        
        return bunkable;
    }
    
    public int calculateRequiredClasses(int attended, int total, double minPercentage) {
        if (total == 0) return 0;
        
        double currentPercentage = (attended * 100.0) / total;
        if (currentPercentage >= minPercentage) {
            return 0;
        }
        
        int required = 0;
        int tempTotal = total;
        int tempAttended = attended;
        
        while (true) {
            tempTotal++;
            tempAttended++;
            required++;
            
            double newPercentage = (tempAttended * 100.0) / tempTotal;
            if (newPercentage >= minPercentage) {
                break;
            }
            
            if (required > 1000) {
                return -1;
            }
        }
        
        return required;
    }
    
    public String getAttendanceAdvice(int attended, int total, double minPercentage) {
        if (total == 0) {
            return "No attendance records yet.";
        }
        
        double currentPercentage = (attended * 100.0) / total;
        
        if (currentPercentage >= minPercentage) {
            int bunkable = calculateBunkableClasses(attended, total, minPercentage);
            return String.format("You're doing great! You can safely miss %d more class(es).", bunkable);
        } else {
            int required = calculateRequiredClasses(attended, total, minPercentage);
            if (required == -1) {
                return "Warning: It may be impossible to reach minimum attendance.";
            }
            return String.format("You need to attend %d more class(es) to reach minimum attendance.", required);
        }
    }
}
