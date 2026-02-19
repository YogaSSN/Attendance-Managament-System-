package com.trackit.views.faculty;

import com.trackit.components.CustomButton;
import com.trackit.models.Faculty;
import com.trackit.utils.*;
import com.trackit.dao.TimetableDAO;
import com.trackit.views.common.LoginFrame;
import javax.swing.*;
import java.awt.*;

public class FacultyDashboard extends JFrame {
    private Faculty faculty;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private TimetableDAO timetableDAO;
    
    public FacultyDashboard(Faculty faculty) {
        this.faculty = faculty;
        this.timetableDAO = new TimetableDAO();
        loadFacultyTimetable();
        initializeUI();
    }
    
    private void loadFacultyTimetable() {
        try {
            faculty.setTimetable(timetableDAO.loadFacultyTimetable(faculty.getId()));
        } catch (Exception e) {
            ErrorHandler.logError("Error loading timetable", e);
        }
    }
    
    private void initializeUI() {
        setTitle("Track IT! - Faculty Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1366, 768);
        setLocationRelativeTo(null);
        setResizable(true);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        
        JPanel topPanel = createTopPanel();
        JPanel sidePanel = createSidePanel();
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.add(new WelcomePanel(faculty), "welcome");
        contentPanel.add(new TimetableConfigPanel(faculty, timetableDAO), "timetable_config");
        contentPanel.add(new ViewTimetablePanel(faculty), "view_timetable");
        contentPanel.add(new MarkAttendancePanel(faculty), "mark_attendance");
        contentPanel.add(new ClassDetailsPanel(faculty), "class_details");
        contentPanel.add(new GenerateReportPanel(), "reports");
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        add(mainPanel);
        cardLayout.show(contentPanel, "welcome");
    }
    
    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ColorUtils.getPrimaryColor());
        panel.setPreferredSize(new Dimension(0, 70));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Track It !! - Faculty Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel("Welcome, " + faculty.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(nameLabel, BorderLayout.SOUTH);
        
        CustomButton logoutBtn = new CustomButton("Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> logout());
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(logoutBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorUtils.getLightGray());
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        addMenuButton(panel, "Home", "welcome");
        addMenuButton(panel, "Configure Timetable", "timetable_config");
        addMenuButton(panel, "View Timetable", "view_timetable");
        addMenuButton(panel, "Mark Attendance", "mark_attendance");
        addMenuButton(panel, "Department Details", "class_details");
        addMenuButton(panel, "Generate Reports", "reports");
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addMenuButton(JPanel panel, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 45));
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> cardLayout.show(contentPanel, cardName));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorUtils.getHoverLightBlue());
                btn.setForeground(Color.BLACK);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
            }
        });
        
        panel.add(btn);
        panel.add(Box.createVerticalStrut(5));
    }
    
    private void logout() {
        if (ErrorHandler.confirm(this, "Are you sure you want to logout?")) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
    
    static class WelcomePanel extends JPanel {
        public WelcomePanel(Faculty faculty) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
            
            JLabel welcomeLabel = new JLabel("Welcome to Faculty Dashboard");
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel(faculty.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            nameLabel.setForeground(ColorUtils.getPrimaryColor());
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel deptLabel = new JLabel("Department of " + faculty.getDepartment());
            deptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            deptLabel.setForeground(Color.GRAY);
            deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JTextArea infoArea = new JTextArea(
                "Quick Actions:\n\n" +
                "• Configure your weekly timetable\n" +
                "• View today's scheduled classes\n" +
                "• Mark student attendance\n" +
                "• View class details and attendance statistics\n" +
                "• Generate comprehensive reports\n\n" +
                "Use the menu on the left to navigate through different features."
            );
            infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            infoArea.setEditable(false);
            infoArea.setOpaque(false);
            infoArea.setLineWrap(true);
            infoArea.setWrapStyleWord(true);
            
            centerPanel.add(welcomeLabel);
            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(nameLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(deptLabel);
            centerPanel.add(Box.createVerticalStrut(40));
            centerPanel.add(infoArea);
            
            add(centerPanel, BorderLayout.CENTER);
        }
    }
}