package com.trackit.views.faculty;

import com.trackit.components.*;
import com.trackit.services.ReportService;
import com.trackit.utils.*;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GenerateReportPanel extends JPanel {
    private ReportService reportService;
    private JComboBox<String> reportTypeCombo;
    private JTextField inputField;
    private JTextArea reportArea;
    private JLabel inputLabel;
    
    public GenerateReportPanel() {
        this.reportService = new ReportService();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(ColorUtils.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Generate Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.CENTER);
        
        JPanel reportPanel = createReportPanel();
        add(reportPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Report Configuration"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Report Type:"), gbc);
        
        String[] reportTypes = {"Student Report", "Department Report", "Subject Report"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTypeCombo.setPreferredSize(new Dimension(200, 35));
        reportTypeCombo.addActionListener(e -> updateInputLabel());
        gbc.gridx = 1;
        panel.add(reportTypeCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        inputLabel = new JLabel("Roll No:");
        inputLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(inputLabel, gbc);
        
        inputField = new CustomTextField(20);
        gbc.gridx = 1;
        panel.add(inputField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        
        CustomButton generateBtn = new CustomButton("Generate Report");
        generateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateBtn.setForeground(Color.WHITE);
        generateBtn.addActionListener(e -> generateReport());
        
        CustomButton saveBtn = new CustomButton("Save to File");
        saveBtn.setBackground(ColorUtils.getSuccessColor());
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.addActionListener(e -> saveReport());
        
        buttonPanel.add(generateBtn);
        buttonPanel.add(saveBtn);
        panel.add(buttonPanel, gbc);
        
        return panel;
    }
    
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder("Report Output"));
        
        reportArea = new JTextArea();
        reportArea.setFont(new Font("Courier New", Font.PLAIN, 12));
        reportArea.setEditable(false);
        reportArea.setLineWrap(false);
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setPreferredSize(new Dimension(0, 350));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateInputLabel() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        
        if (reportType.equals("Student Report")) {
            inputLabel.setText("Roll No:");
            inputField.setToolTipText("Enter student roll number");
        } else if (reportType.equals("Department Report")) {
            inputLabel.setText("Department (e.g., CSE A):");
            inputField.setToolTipText("Enter department and section (e.g., CSE A)");
        } else if (reportType.equals("Subject Report")) {
            inputLabel.setText("Subject Code:");
            inputField.setToolTipText("Enter subject code (e.g., CS301)");
        }
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String input = inputField.getText().trim();
        
        if (ValidationUtils.isEmpty(input)) {
            ErrorHandler.displayError(this, "Please enter the required information");
            return;
        }
        
        String report = "";
        
        try {
            if (reportType.equals("Student Report")) {
                report = reportService.generateStudentReport(input);
            } else if (reportType.equals("Department Report")) {
                String[] parts = input.split(" ");
                if (parts.length != 2) {
                    ErrorHandler.displayError(this, "Format: DEPARTMENT SECTION (e.g., CSE A)");
                    return;
                }
                report = reportService.generateDepartmentReport(parts[0], parts[1]);
            } else if (reportType.equals("Subject Report")) {
                report = reportService.generateSubjectReport(input);
            }
            
            reportArea.setText(report);
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Error generating report: " + e.getMessage());
        }
    }
    
    private void saveReport() {
        String report = reportArea.getText();
        
        if (ValidationUtils.isEmpty(report)) {
            ErrorHandler.displayError(this, "No report to save. Generate a report first.");
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report");
        fileChooser.setSelectedFile(new File("report_" + DateUtils.getCurrentDateString() + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (BufferedWriter writer = new BufferedWriter(
                    new FileWriter(fileChooser.getSelectedFile()))) {
                writer.write(report);
                ErrorHandler.displaySuccess(this, "Report saved successfully!");
            } catch (IOException e) {
                ErrorHandler.displayError(this, "Error saving report: " + e.getMessage());
            }
        }
    }
}