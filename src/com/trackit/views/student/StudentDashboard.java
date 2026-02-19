package com.trackit.views.student;

import com.trackit.components.CustomButton;
import com.trackit.models.Student;
import com.trackit.utils.*;
import com.trackit.dao.TimetableDAO;
import com.trackit.views.common.LoginFrame;
import com.trackit.services.AttendanceService;
import com.trackit.services.ReportService;
import com.trackit.services.MinAttendanceTracker;
import javax.swing.*;
import java.awt.*;

public class StudentDashboard extends JFrame {
    private Student student;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private TimetableDAO timetableDAO;
    private AttendanceService attendanceService;
    private ReportService reportService;
    private MinAttendanceTracker tracker;
    
    public StudentDashboard(Student student) {
        this.student = student;
        this.timetableDAO = new TimetableDAO();
        this.attendanceService = new AttendanceService();
        this.reportService = new ReportService();
        this.tracker = new MinAttendanceTracker();
        loadStudentTimetable();
        initializeUI();
    }
    
    private void loadStudentTimetable() {
        try {
            student.setTimetable(timetableDAO.loadStudentTimetable(student.getRollNo()));
        } catch (Exception e) {
            ErrorHandler.logError("Error loading timetable", e);
        }
    }
    
    private void initializeUI() {
        setTitle("Track IT! - Student Dashboard");
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
        contentPanel.setBackground(ColorUtils.BACKGROUND_COLOR);
        contentPanel.add(new WelcomePanel(student), "welcome");
        contentPanel.add(new TimetableConfigPanel(student, timetableDAO), "timetable_config");
        contentPanel.add(new ViewTimetablePanel(student), "view_timetable");
        contentPanel.add(new TodayClassesPanel(student), "today_classes");
        contentPanel.add(new MarkAttendancePanel(student, attendanceService), "mark_attendance");
        contentPanel.add(new ViewAttendancePanel(student, attendanceService), "view_attendance");
        contentPanel.add(new AttendanceTrackerPanel(student, attendanceService, tracker), "tracker");
        contentPanel.add(new GenerateReportPanel(student, reportService), "reports");
        
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
        
        JLabel titleLabel = new JLabel("Track It !! - Student Portal");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel nameLabel = new JLabel("Welcome, " + student.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(nameLabel, BorderLayout.SOUTH);
        
        CustomButton logoutBtn = new CustomButton("👤 Logout");
        logoutBtn.setBackground(new Color(244, 67, 54));
        logoutBtn.addActionListener(e -> logout());
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(logoutBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setPreferredSize(new Dimension(220, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        addMenuButton(panel, "Home", "welcome");
        addMenuButton(panel, "Configure Timetable", "timetable_config");
        addMenuButton(panel, "View Timetable", "view_timetable");
        addMenuButton(panel, "Today's Classes", "today_classes");
        addMenuButton(panel, "Mark Attendance", "mark_attendance");
        addMenuButton(panel, "View Attendance", "view_attendance");
        addMenuButton(panel, "Attendance Tracker", "tracker");
        addMenuButton(panel, "Reports", "reports");
        
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    private void addMenuButton(JPanel panel, String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
        public WelcomePanel(Student student) {
            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            
            JPanel centerPanel = new JPanel();
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.setBackground(Color.WHITE);
            centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
            
            JLabel welcomeLabel = new JLabel("Welcome to Student Dashboard");
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel nameLabel = new JLabel(student.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            nameLabel.setForeground(ColorUtils.getPrimaryColor());
            nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel rollLabel = new JLabel("Roll No: " + student.getRollNo());
            rollLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            rollLabel.setForeground(Color.GRAY);
            rollLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JLabel classLabel = new JLabel("Department: " + student.getDepartment() + " - " + student.getSection());
            classLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            classLabel.setForeground(Color.GRAY);
            classLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            JTextArea infoArea = new JTextArea(
                "Quick Actions:\n\n" +
                "• Configure your personal timetable (8 periods × 5 days)\n" +
                "• View today's scheduled classes\n" +
                "• Track your subject-wise attendance\n" +
                "• Check overall attendance percentage\n" +
                "• Use attendance tracker to plan your attendance\n" +
                "• See how many classes you can bunk safely\n" +
                "• Know how many classes you need to attend\n\n" +
                "Use the menu on the left to navigate through different features."
            );
            infoArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            infoArea.setEditable(false);
            infoArea.setOpaque(false);
            infoArea.setLineWrap(true);
            infoArea.setWrapStyleWord(true);
            
            centerPanel.add(welcomeLabel);
            centerPanel.add(Box.createVerticalStrut(10));
            centerPanel.add(nameLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(rollLabel);
            centerPanel.add(Box.createVerticalStrut(5));
            centerPanel.add(classLabel);
            centerPanel.add(Box.createVerticalStrut(40));
            centerPanel.add(infoArea);
            
            add(centerPanel, BorderLayout.CENTER);
        }
    }
}