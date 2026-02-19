package com.trackit.views.student;

import com.trackit.models.AttendanceRecord;
import com.trackit.models.Student;
import com.trackit.services.AttendanceService;
import com.trackit.dao.SubjectDAO;
import com.trackit.models.Subject;
import com.trackit.utils.Constants;
import com.trackit.utils.ErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ViewAttendancePanel extends JPanel {
    private final Student student;
    private final AttendanceService attendanceService;
    private final SubjectDAO subjectDAO = new SubjectDAO();

    public ViewAttendancePanel(Student student, AttendanceService attendanceService) {
        this.student = student;
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        buildUI();
    }

    private void buildUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Subject-wise", buildSubjectWise());
        tabs.addTab("Overall", buildOverall());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildSubjectWise() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        try {
            Map<String, Double> map = attendanceService.getSubjectWiseAttendance(student.getRollNo());
            java.util.List<String> codes = new ArrayList<>(map.keySet());
            Collections.sort(codes);
            for (String code : codes) {
                Subject s = subjectDAO.getSubjectByCode(code);
                String name = s != null ? s.getName() : code;
                SubjectStats stats = computeStatsForSubject(code);
                panel.add(subjectCard(code + " - " + name, stats));
                panel.add(Box.createVerticalStrut(8));
            }
        } catch (Exception e) {
            panel.add(new JLabel("No attendance records found."));
        }
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        JPanel container = new JPanel(new BorderLayout());
        container.add(scroll, BorderLayout.CENTER);
        return container;
    }

    private JPanel buildOverall() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        try {
            java.util.List<AttendanceRecord> records = attendanceService.getAttendanceByRollNo(student.getRollNo());
            SubjectStats stats = computeStats(records);

            JPanel center = new JPanel(new GridLayout(1,2,10,10));
            center.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            center.add(overallCard(stats));
            center.add(pieChart(stats));
            panel.add(center, BorderLayout.CENTER);
        } catch (Exception e) {
            panel.add(new JLabel("No attendance records found."), BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel buildHistory() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(new JLabel("History view coming soon."), BorderLayout.CENTER);
        return panel;
    }

    private JPanel subjectCard(String title, SubjectStats stats) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(stats.percentage >= 75 ? new Color(76, 175, 80) : new Color(244, 67, 54), 2),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        JLabel header = new JLabel(title);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        card.add(header, BorderLayout.NORTH);

        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int)Math.round(stats.percentage));
        bar.setStringPainted(true);
        bar.setString(String.format("%.2f%%", stats.percentage));
        card.add(bar, BorderLayout.CENTER);

        JPanel meta = new JPanel(new GridLayout(2,3,8,8));
        meta.setOpaque(false);
        meta.add(new JLabel("Total: " + stats.total));
        meta.add(new JLabel("Present+OD: " + stats.presentPlusOD));
        meta.add(new JLabel("Absent: " + stats.absent));
        meta.add(new JLabel("OD: " + stats.od));
        meta.add(new JLabel("NC: " + stats.nc));
        meta.add(new JLabel(" "));
        card.add(meta, BorderLayout.SOUTH);
        return card;
    }

    private JPanel overallCard(SubjectStats stats) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(stats.percentage >= 75 ? new Color(76, 175, 80) : new Color(244, 67, 54), 2),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        JLabel pct = new JLabel(String.format("Overall: %.2f%%", stats.percentage));
        pct.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pct.setAlignmentX(Component.LEFT_ALIGNMENT);
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue((int)Math.round(stats.percentage));
        bar.setStringPainted(true);
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel breakdown = new JLabel(String.format("Total: %d  Present+OD: %d  Absent: %d  OD: %d  NC: %d",
            stats.total, stats.presentPlusOD, stats.absent, stats.od, stats.nc));
        breakdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(pct);
        card.add(Box.createVerticalStrut(8));
        card.add(bar);
        card.add(Box.createVerticalStrut(8));
        card.add(breakdown);
        return card;
    }

    private JPanel pieChart(SubjectStats stats) {
        // Calculate sum with temporary variable first
        int tempSum = stats.presentPlusOD + stats.absent + stats.od;
        if (tempSum == 0) tempSum = 1;
        
        // Now make final copies for use in inner class
        final int sum = tempSum;
        final int p = stats.presentPlusOD;
        final int a = stats.absent;
        final int od = stats.od;
        
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int w = getWidth();
                int h = getHeight();
                int size = Math.min(w, h) - 20;
                int x = (w - size) / 2;
                int y = (h - size) / 2;
                int start = 0;
                
                // Calculate angles using final variables
                final int pAngle = Math.round(360f * p / sum);
                final int aAngle = Math.round(360f * a / sum);
                
                // Draw pie chart
                g.setColor(new Color(76, 175, 80)); // Green for present
                g.fillArc(x, y, size, size, start, pAngle);
                start += pAngle;
                
                g.setColor(new Color(244, 67, 54)); // Red for absent
                g.fillArc(x, y, size, size, start, aAngle);
                start += aAngle;
                
                g.setColor(new Color(33, 150, 243)); // Blue for OD
                g.fillArc(x, y, size, size, start, 360 - start);
            }
        };
    }

    private SubjectStats computeStatsForSubject(String code) {
        try {
            List<AttendanceRecord> all = attendanceService.getAttendanceByRollNo(student.getRollNo());
            List<AttendanceRecord> filtered = new ArrayList<>();
            for (AttendanceRecord r : all) {
                if (code.equals(r.getSubjectCode())) {
                    filtered.add(r);
                }
            }
            return computeStats(filtered);
        } catch (Exception e) {
            return new SubjectStats();
        }
    }

    private SubjectStats computeStats(List<AttendanceRecord> records) {
        SubjectStats s = new SubjectStats();
        for (AttendanceRecord r : records) {
            s.total++;
            if (Constants.STATUS_PRESENT.equals(r.getStatus()) || Constants.STATUS_ON_DUTY.equals(r.getStatus())) {
                s.presentPlusOD++;
                if (Constants.STATUS_ON_DUTY.equals(r.getStatus())) {
                    s.od++;
                }
            } else if (Constants.STATUS_ABSENT.equals(r.getStatus())) {
                s.absent++;
            } else {
                s.nc++;
            }
        }
        s.percentage = s.total == 0 ? 0 : (s.presentPlusOD * 100.0 / s.total);
        return s;
    }

    private static class SubjectStats {
        int total = 0;
        int presentPlusOD = 0;
        int absent = 0;
        int od = 0;
        int nc = 0;
        double percentage = 0;
    }
}