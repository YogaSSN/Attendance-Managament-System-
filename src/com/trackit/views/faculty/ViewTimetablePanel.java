package com.trackit.views.faculty;

import com.trackit.models.*;
import com.trackit.utils.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class ViewTimetablePanel extends JPanel {
    private Faculty faculty;
    private JComboBox<String> dayCombo;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    
    public ViewTimetablePanel(Faculty faculty) {
        this.faculty = faculty;
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorUtils.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("View Timetable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.CENTER);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
        
        showTodaySchedule();
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        
        JLabel dayLabel = new JLabel("Select Day:");
        dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        dayCombo = new JComboBox<>(Constants.DAYS);
        dayCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dayCombo.setPreferredSize(new Dimension(150, 35));
        dayCombo.addActionListener(e -> showSchedule());
        
        JButton todayBtn = new JButton("Today's Schedule");
        todayBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        todayBtn.setBackground(Color.WHITE);
        todayBtn.setForeground(Color.BLACK);
        todayBtn.setFocusPainted(false);
        todayBtn.addActionListener(e -> showTodaySchedule());
        todayBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                todayBtn.setBackground(ColorUtils.getHoverLightBlue());
                todayBtn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                todayBtn.setBackground(Color.WHITE);
                todayBtn.setForeground(Color.BLACK);
            }
        });
        
        panel.add(dayLabel);
        panel.add(dayCombo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(todayBtn);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Schedule"));
        
        String[] columns = {"Period", "Subject Code", "Subject Name", "Department", "Section"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        timetableTable = new JTable(tableModel);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timetableTable.setRowHeight(35);
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void showSchedule() {
        String day = (String) dayCombo.getSelectedItem();
        if (day == null) return;
        
        tableModel.setRowCount(0);
        List<TimeSlot> slots = faculty.getTimetable().getDaySchedule(day);
        
        if (slots.isEmpty()) {
            tableModel.addRow(new Object[]{"No classes scheduled", "", "", "", ""});
        } else {
            for (TimeSlot slot : slots) {
                tableModel.addRow(new Object[]{
                    slot.getPeriod(),
                    slot.getSubjectCode(),
                    slot.getSubjectName(),
                    slot.getDepartment(),
                    slot.getSection()
                });
            }
        }
    }
    
    private void showTodaySchedule() {
        String today = DateUtils.getCurrentDay();
        dayCombo.setSelectedItem(today);
        showSchedule();
    }
}