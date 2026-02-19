package com.trackit.utils;

import java.awt.Color;

public class ColorUtils {
    public static final Color BACKGROUND_COLOR = new Color(240, 248, 255); // AliceBlue
    
    public static Color getAttendanceColor(double percentage, double minPercentage) {
        if (percentage >= minPercentage) {
            return new Color(76, 175, 80);
        } else if (percentage >= minPercentage - 5) {
            return new Color(255, 152, 0);
        } else {
            return new Color(244, 67, 54);
        }
    }
    
    public static Color getPrimaryColor() {
        return new Color(25, 118, 210);
    }
    
    public static Color getSuccessColor() {
        return new Color(76, 175, 80);
    }
    
    public static Color getDangerColor() {
        return new Color(244, 67, 54);
    }
    
    public static Color getWarningColor() {
        return new Color(255, 152, 0);
    }
    
    public static Color getLightGray() {
        return new Color(245, 245, 245);
    }
    
    public static Color getDarkGray() {
        return new Color(66, 66, 66);
    }

    public static Color getHoverLightBlue() {
        // Light blue used for subtle hover effect on buttons
        return new Color(227, 242, 253);
    }
}