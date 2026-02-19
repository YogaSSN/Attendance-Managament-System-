package com.trackit.views.faculty;

import com.trackit.components.*;
import com.trackit.models.*;
import com.trackit.dao.*;
import com.trackit.services.AttendanceService;
import com.trackit.utils.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ClassDetailsPanel extends JPanel {
    private Faculty faculty;
    private StudentDAO studentDAO;
    private SubjectDAO subjectDAO;
    private AttendanceService attendanceService;
    private JComboBox<String> classCombo;
    private JComboBox<String> sectionCombo;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    public ClassDetailsPanel(Faculty faculty) {
        this.faculty = faculty;
        this.studentDAO = new StudentDAO();
        this.subjectDAO = new SubjectDAO();
        this.attendanceService = new AttendanceService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorUtils.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Department Details & Attendance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.CENTER);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(Color.WHITE);
        
        JLabel classLabel = new JLabel("Department:");
        classLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        classCombo = new JComboBox<>(new String[]{"CSE", "IT", "ECE", "EEE", "MECH", "BME", "CHE"});
        classCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        classCombo.setPreferredSize(new Dimension(100, 35));
        
        JLabel sectionLabel = new JLabel("Section:");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        sectionCombo = new JComboBox<>(new String[]{"A", "B", "C"});
        sectionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sectionCombo.setPreferredSize(new Dimension(100, 35));
        
        CustomButton loadBtn = new CustomButton("Load Students");
        loadBtn.addActionListener(e -> loadStudents());
        
        panel.add(classLabel);
        panel.add(classCombo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(sectionLabel);
        panel.add(sectionCombo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(loadBtn);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Student List"));
        
        String[] columns = {"Roll No", "Name", "Overall Attendance %", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        studentTable.setRowHeight(35);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        studentTable.getColumn("Status").setCellRenderer(new ColorRenderer());
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadStudents() {
        String className = (String) classCombo.getSelectedItem();
        String section = (String) sectionCombo.getSelectedItem();
        
        try {
            List<Student> students = studentDAO.getStudentsByDepartment(className, section);
            tableModel.setRowCount(0);
            
            for (Student student : students) {
                double attendance = attendanceService.calculateOverallAttendance(student.getRollNo());
                String status = attendance >= Constants.MIN_ATTENDANCE_PERCENTAGE ? "Good" : "Low";
                
                tableModel.addRow(new Object[]{
                    student.getRollNo(),
                    student.getName(),
                    String.format("%.2f%%", attendance),
                    status
                });
            }
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Error loading students: " + e.getMessage());
        }
    }
    
    class ColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (value != null && value.equals("Low")) {
                c.setForeground(ColorUtils.getDangerColor());
                setFont(getFont().deriveFont(Font.BOLD));
            } else if (value != null && value.equals("Good")) {
                c.setForeground(ColorUtils.getSuccessColor());
                setFont(getFont().deriveFont(Font.BOLD));
            }
            
            return c;
        }
    }
}
