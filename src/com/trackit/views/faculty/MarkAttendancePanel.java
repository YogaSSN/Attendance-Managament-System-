package com.trackit.views.faculty;

import com.trackit.components.*;
import com.trackit.models.*;
import com.trackit.dao.*;
import com.trackit.services.AttendanceService;
import com.trackit.utils.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.*;
import java.util.List;

public class MarkAttendancePanel extends JPanel {
    private Faculty faculty;
    private StudentDAO studentDAO;
    private AttendanceService attendanceService;
    private JComboBox<String> dayCombo;
    private JComboBox<TimeSlot> classCombo;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> periodCombo;
    private Map<String, String> studentStatus;
    
    public MarkAttendancePanel(Faculty faculty) {
        this.faculty = faculty;
        this.studentDAO = new StudentDAO();
        this.attendanceService = new AttendanceService();
        this.studentStatus = new HashMap<>();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorUtils.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Mark Attendance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.CENTER);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Select Department"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Day:"), gbc);
        dayCombo = new JComboBox<>(Constants.DAYS);
        dayCombo.setSelectedItem(DateUtils.getCurrentDay());
        dayCombo.addActionListener(e -> loadClasses());
        gbc.gridx = 1;
        panel.add(dayCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(new JLabel("Department:"), gbc);
        classCombo = new JComboBox<>();
        classCombo.setPreferredSize(new Dimension(300, 30));
        classCombo.addActionListener(e -> loadPeriods());
        gbc.gridx = 3;
        panel.add(classCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Period:"), gbc);
        periodCombo = new JComboBox<>();
        periodCombo.setPreferredSize(new Dimension(120, 30));
        gbc.gridx = 1;
        panel.add(periodCombo, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        CustomButton loadBtn = new CustomButton("Load Students");
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loadBtn.setForeground(Color.WHITE);
        loadBtn.addActionListener(e -> loadStudents());
        panel.add(loadBtn, gbc);
        
        loadClasses();
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        String[] columns = {"Roll No", "Name", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // only Status column is editable
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setRowHeight(40);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        studentTable.getColumn("Status").setCellRenderer(new ButtonsStatusRenderer());
        studentTable.getColumn("Status").setCellEditor(new ButtonsStatusEditor());
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        
        CustomButton markAllPresentBtn = new CustomButton("Mark All Present");
        markAllPresentBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        markAllPresentBtn.setForeground(Color.WHITE);
        markAllPresentBtn.addActionListener(e -> markAllStatus(Constants.STATUS_PRESENT));
        
        CustomButton saveBtn = new CustomButton("Save Attendance");
        saveBtn.setBackground(ColorUtils.getSuccessColor());
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveAttendance());
        
        buttonPanel.add(markAllPresentBtn);
        buttonPanel.add(saveBtn);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadClasses() {
        classCombo.removeAllItems();
        String day = (String) dayCombo.getSelectedItem();
        if (day == null) return;
        
        List<TimeSlot> slots = faculty.getTimetable().getDaySchedule(day);
        for (TimeSlot slot : slots) {
            classCombo.addItem(slot);
        }
    }
    
    private void loadPeriods() {
        periodCombo.removeAllItems();
        TimeSlot selectedSlot = (TimeSlot) classCombo.getSelectedItem();
        if (selectedSlot != null) {
            periodCombo.addItem(selectedSlot.getPeriod());
        }
    }
    
    private void loadStudents() {
        TimeSlot selectedSlot = (TimeSlot) classCombo.getSelectedItem();
        if (selectedSlot == null) {
            ErrorHandler.displayError(this, "Please select a department");
            return;
        }
        
        try {
            List<Student> students = studentDAO.getStudentsByDepartment(
                selectedSlot.getDepartment(), selectedSlot.getSection());
            
            tableModel.setRowCount(0);
            studentStatus.clear();

            for (Student student : students) {
                // default to PRESENT
                studentStatus.put(student.getRollNo(), Constants.STATUS_PRESENT);
                tableModel.addRow(new Object[]{
                    student.getRollNo(),
                    student.getName(),
                    Constants.STATUS_PRESENT
                });
            }
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Error loading students: " + e.getMessage());
        }
    }
    
    private void markAllStatus(String status) {
        for (int r = 0; r < tableModel.getRowCount(); r++) {
            String roll = (String) tableModel.getValueAt(r, 0);
            studentStatus.put(roll, status);
            tableModel.setValueAt(status, r, 2);
        }
        studentTable.repaint();
    }
    
    private void saveAttendance() {
        TimeSlot selectedSlot = (TimeSlot) classCombo.getSelectedItem();
        Integer period = (Integer) periodCombo.getSelectedItem();
        
        if (selectedSlot == null || period == null) {
            ErrorHandler.displayError(this, "Please select class and period");
            return;
        }
        
        if (studentStatus.isEmpty()) {
            ErrorHandler.displayError(this, "No students to mark attendance");
            return;
        }
        
        try {
            List<AttendanceRecord> records = new ArrayList<>();
            LocalDate today = LocalDate.now();
            
            for (Map.Entry<String, String> entry : studentStatus.entrySet()) {
                String rollNo = entry.getKey();
                String status = entry.getValue();
                
                AttendanceRecord record = new AttendanceRecord(
                    rollNo, selectedSlot.getSubjectCode(), today, period, status
                );
                records.add(record);
            }
            
            attendanceService.markMultipleAttendance(records);
            ErrorHandler.displaySuccess(this, "Attendance saved successfully!");
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Error saving attendance: " + e.getMessage());
        }
    }
    
    private static class StyledButton extends CustomButton {
        private final Color baseColor;
        public StyledButton(String text, Color bg) {
            super(text, bg);
            this.baseColor = bg;
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setPreferredSize(new Dimension(100, 35));
            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 3));
        }
        public void setSelectedVisual(boolean selected) {
            if (selected) {
                setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
            } else {
                setBorder(BorderFactory.createLineBorder(new Color(0,0,0,0), 3));
            }
        }
    }

    private class ButtonsStatusRenderer implements javax.swing.table.TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = value == null ? Constants.STATUS_PRESENT : value.toString();
            JPanel panel = createButtonsPanel(status, false, null);
            return panel;
        }
    }

    private class ButtonsStatusEditor extends AbstractCellEditor implements TableCellEditor {
        private String currentStatus;
        private JPanel panel;
        private String rollNo;
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentStatus = value == null ? Constants.STATUS_PRESENT : value.toString();
            rollNo = (String) table.getValueAt(row, 0);
            panel = createButtonsPanel(currentStatus, true, s -> {
                currentStatus = s;
                studentStatus.put(rollNo, s);
                tableModel.setValueAt(s, row, 2);
                fireEditingStopped();
            });
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentStatus;
        }
    }

    private interface StatusListener { void onChange(String status); }

    private JPanel createButtonsPanel(String selected, boolean editable, StatusListener listener) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(true);
        panel.setBackground(Color.WHITE);

        StyledButton present = new StyledButton("Present", new Color(76, 175, 80));
        StyledButton absent = new StyledButton("Absent", new Color(244, 67, 54));
        StyledButton onDuty = new StyledButton("On Duty", new Color(158, 158, 158));

        present.setSelectedVisual(Constants.STATUS_PRESENT.equals(selected));
        absent.setSelectedVisual(Constants.STATUS_ABSENT.equals(selected));
        onDuty.setSelectedVisual(Constants.STATUS_ON_DUTY.equals(selected));

        if (editable && listener != null) {
            present.addActionListener(e -> {
                listener.onChange(Constants.STATUS_PRESENT);
            });
            absent.addActionListener(e -> {
                listener.onChange(Constants.STATUS_ABSENT);
            });
            onDuty.addActionListener(e -> {
                listener.onChange(Constants.STATUS_ON_DUTY);
            });
        } else {
            present.setEnabled(false);
            absent.setEnabled(false);
            onDuty.setEnabled(false);
        }

        panel.add(present);
        panel.add(absent);
        panel.add(onDuty);
        return panel;
    }
}
