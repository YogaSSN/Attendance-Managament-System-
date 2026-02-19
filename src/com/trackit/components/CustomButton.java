package com.trackit.components;

import javax.swing.*;
import java.awt.*;

public class CustomButton extends JButton {
    private Color originalBackground;
    public CustomButton(String text) {
        super(text);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setFocusPainted(false);
        setBorderPainted(false);
        setBackground(new Color(25, 118, 210));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(150, 40));
        originalBackground = getBackground();
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (getBackground() != null) {
                    setBackground(getBackground().darker());
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (originalBackground != null) {
                    setBackground(originalBackground);
                }
            }
        });
    }
    
    public CustomButton(String text, Color bgColor) {
        this(text);
        setBackground(bgColor);
        originalBackground = bgColor;
    }
}
