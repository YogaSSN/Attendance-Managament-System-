package com.trackit.views.common;

import com.trackit.components.*;
import com.trackit.models.*;
import com.trackit.services.LoginManager;
import com.trackit.utils.*;
import com.trackit.views.faculty.FacultyDashboard;
import com.trackit.views.student.StudentDashboard;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private CustomTextField idField;
    private CustomPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private LoginManager loginManager;
    
    public LoginFrame() {
        this.loginManager = new LoginManager();
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Track It !! - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 600);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(ColorUtils.BACKGROUND_COLOR);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JLabel titleLabel = new JLabel("Track It !!");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        titleLabel.setForeground(ColorUtils.getPrimaryColor());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Smart Attendance Management");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel roleLabel = new JLabel("Login As:");
        roleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(roleLabel, gbc);
        
        String[] roles = {"Faculty", "Student"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleCombo.setPreferredSize(new Dimension(250, 35));
        roleCombo.setFocusable(true);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(roleCombo, gbc);
        
        JLabel idLabel = new JLabel("ID / Roll No:");
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(idLabel, gbc);
        
        idField = new CustomTextField(24);
        idField.setFocusable(true);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(idField, gbc);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(passwordLabel, gbc);
        
        passwordField = new CustomPasswordField(24);
        passwordField.setFocusable(true);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(passwordField, gbc);
        
        formPanel.setMaximumSize(new Dimension(300, 350));
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        
        CustomButton loginButton = new CustomButton("LOGIN");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setPreferredSize(new Dimension(300, 45));
        loginButton.addActionListener(e -> handleLogin());
        
        passwordField.addActionListener(e -> handleLogin());
        
        // Set default focus to ID field
        SwingUtilities.invokeLater(() -> idField.requestFocusInWindow());
        
        mainPanel.add(loginButton);
        mainPanel.add(Box.createVerticalStrut(20));
        
        JLabel helpLabel = new JLabel("<html><center>Default Credentials:<br>" +
            "Faculty: F001 / pass123<br>" +
            "Student: 3122 24 5001 189 / pass123</center></html>");
        helpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        helpLabel.setForeground(Color.GRAY);
        JPanel helpContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        helpContainer.setOpaque(false);
        helpContainer.add(helpLabel);
        mainPanel.add(helpContainer);

        JPanel autofillPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        autofillPanel.setOpaque(false);
        JButton fillFaculty = new JButton("Use Default Faculty");
        JButton fillStudent = new JButton("Use Default Student");
        fillFaculty.addActionListener(e -> {
            roleCombo.setSelectedItem("Faculty");
            idField.setText("F001");
            passwordField.setText("pass123");
            idField.requestFocusInWindow();
        });
        fillStudent.addActionListener(e -> {
            roleCombo.setSelectedItem("Student");
            idField.setText("3122 24 5001 189");
            passwordField.setText("pass123");
            idField.requestFocusInWindow();
        });
        autofillPanel.add(fillFaculty);
        autofillPanel.add(fillStudent);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(autofillPanel);
        
        add(mainPanel);
    }
    
    private void handleLogin() {
        String id = idField.getText() == null ? "" : idField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = roleCombo.getSelectedItem().toString().toUpperCase();
        System.out.println("[DEBUG] UI collected id='" + id + "', role=" + role);
        
        if (ValidationUtils.isEmpty(id) || ValidationUtils.isEmpty(password)) {
            ErrorHandler.displayError(this, "Please enter ID and Password");
            return;
        }
        
        try {
            User user = loginManager.login(id, password, role);
            
            if (user instanceof Faculty) {
                Faculty faculty = (Faculty) user;
                new FacultyDashboard(faculty).setVisible(true);
                dispose();
            } else if (user instanceof Student) {
                Student student = (Student) user;
                new StudentDashboard(student).setVisible(true);
                dispose();
            }
            
        } catch (Exception e) {
            ErrorHandler.displayError(this, "Login failed: " + e.getMessage());
            passwordField.setText("");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginFrame().setVisible(true);
        });
    }
}