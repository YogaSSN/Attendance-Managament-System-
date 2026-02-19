package com.trackit.components;

import javax.swing.*;
import java.awt.*;

public class CustomPasswordField extends JPasswordField {
    public CustomPasswordField(int columns) {
        super(columns);
        setupUI();
    }

    private void setupUI() {
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
        setPreferredSize(new Dimension(250, 40));
        setMinimumSize(new Dimension(250, 40));
        setForeground(Color.BLACK);
        setBackground(Color.WHITE);
        setCaretColor(Color.BLACK);
        setEchoChar('•');
        setSelectionColor(new Color(184, 207, 229));
        setSelectedTextColor(Color.BLACK);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(250, 40);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(250, 40);
    }
}