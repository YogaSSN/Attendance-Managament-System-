package com.trackit.views.faculty;

import com.trackit.components.*;
import com.trackit.models.*;
import com.trackit.dao.*;
import com.trackit.utils.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class TimetableConfigPanel extends JPanel {
    private Faculty faculty;
    private TimetableDAO timetableDAO;
    private SubjectDAO subjectDAO;
    private JComboBox<String> dayCombo;
    private JComboBox<Integer> periodCombo;
    private JComboBox<String> subjectCombo;
    private JComboBox<String> classCombo;
    private JComboBox<String> sectionCombo;
    private JTable timetableTable;
    private DefaultTableModel tableModel;
    
    public TimetableConfigPanel(Faculty faculty, TimetableDAO timetableDAO) {
        this.faculty = faculty;
        this.timetableDAO = timetableDAO;
        this.subjectDAO = new SubjectDAO();
        initializeUI();
        loadTimetableTable();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorUtils.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Configure Timetable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.CENTER);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Add Department Slot to Timetable"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Day:"), gbc);
        dayCombo = new JComboBox<>(Constants.DAYS);
        dayCombo.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 1;
        panel.add(dayCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Period:"), gbc);
        Integer[] periods = new Integer[Constants.PERIODS_PER_DAY];
        for (int i = 0; i < periods.length; i++) periods[i] = i + 1;
        periodCombo = new JComboBox<>(periods);
        periodCombo.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 3;
        panel.add(periodCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Subject:"), gbc);
        subjectCombo = new JComboBox<>();
        subjectCombo.setPreferredSize(new Dimension(300, 35));
        loadSubjects();
        gbc.gridx = 1;
        panel.add(subjectCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Department:"), gbc);
        classCombo = new JComboBox<>(Constants.DEPARTMENTS);
        classCombo.setPreferredSize(new Dimension(150, 35));
        gbc.gridx = 1;
        panel.add(classCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = row;
        panel.add(new JLabel("Section:"), gbc);
        sectionCombo = new JComboBox<>(new String[]{"A", "B", "C"});
        sectionCombo.setPreferredSize(new Dimension(120, 35));
        gbc.gridx = 3;
        panel.add(sectionCombo, gbc);
        
        row++;
        gbc.gridx = 1; gbc.gridy = row;
        gbc.gridwidth = 2;
        CustomButton addBtn = new CustomButton("Add to Timetable");
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setForeground(Color.WHITE);
        addBtn.addActionListener(e -> addToTimetable());
        panel.add(addBtn, gbc);
        
        gbc.gridx = 3;
        CustomButton clearBtn = new CustomButton("Clear Timetable");
        clearBtn.setBackground(ColorUtils.getDangerColor());
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.addActionListener(e -> clearTimetable());
        panel.add(clearBtn, gbc);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Current Timetable"));
        
        String[] columns = {"Day", "Period", "Subject", "Department", "Section"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        timetableTable = new JTable(tableModel);
        timetableTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        timetableTable.setRowHeight(30);
        timetableTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        JScrollPane scrollPane = new JScrollPane(timetableTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadSubjects() {
        try {
            List<Subject> subjects = subjectDAO.getSubjectsByFaculty(faculty.getId());
            subjectCombo.removeAllItems();
            for (Subject subject : subjects) {
                subjectCombo.addItem(subject.getSubjectCode() + " - " + subject.getName());
            }
        } catch (Exception e) {
            ErrorHandler.logError("Error loading subjects", e);
        }
    }
    
    private void addToTimetable() {
        try {
            String day = (String) dayCombo.getSelectedItem();
            int period = (Integer) periodCombo.getSelectedItem();
            String subjectStr = (String) subjectCombo.getSelectedItem();
            if (subjectStr == null) {
                ErrorHandler.displayError(this, "Please select a subject");
                return;
            }
            
            String subjectCode = subjectStr.split(" - ")[0];
            String subjectName = subjectStr.split(" - ")[1];
            String className = (String) classCombo.getSelectedItem();
            String section = (String) sectionCombo.getSelectedItem();
            
            TimeSlot slot = new TimeSlot(period, subjectCode, subjectName, className, section);
            faculty.getTimetable().addSlot(day, slot);
            
            timetableDAO.saveFacultyTimetable(faculty.getId(), faculty.getTimetable());
            
            loadTimetableTable();
            ErrorHandler.displaySuccess(this, "Added to timetable successfully!");
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Error adding to timetable: " + e.getMessage());
        }
    }
    
    private void clearTimetable() {
        if (ErrorHandler.confirm(this, "Are you sure you want to clear the entire timetable?")) {
            try {
                faculty.getTimetable().clearAll();
                timetableDAO.saveFacultyTimetable(faculty.getId(), faculty.getTimetable());
                loadTimetableTable();
                ErrorHandler.displaySuccess(this, "Timetable cleared!");
            } catch (Exception e) {
                ErrorHandler.displayError(this, "Error clearing timetable: " + e.getMessage());
            }
        }
    }
    
    private void loadTimetableTable() {
        tableModel.setRowCount(0);
        
        for (String day : Constants.DAYS) {
            List<TimeSlot> slots = faculty.getTimetable().getDaySchedule(day);
            for (TimeSlot slot : slots) {
                tableModel.addRow(new Object[]{
                    day,
                    slot.getPeriod(),
                    slot.getSubjectName(),
                    slot.getDepartment(),
                    slot.getSection()
                });
            }
        }
    }
}