package com.trackit.views.student;

import com.trackit.dao.SubjectDAO;
import com.trackit.models.AttendanceRecord;
import com.trackit.models.Student;
import com.trackit.models.Subject;
import com.trackit.services.AttendanceService;
import com.trackit.services.MinAttendanceTracker;
import com.trackit.utils.Constants;
import com.trackit.utils.ColorUtils;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class AttendanceTrackerPanel extends JPanel {
    private final Student student;
    private final AttendanceService attendanceService;
    private final MinAttendanceTracker tracker;
    private final SubjectDAO subjectDAO = new SubjectDAO();

    public AttendanceTrackerPanel(Student student, AttendanceService attendanceService, MinAttendanceTracker tracker) {
        this.student = student;
        this.attendanceService = attendanceService;
        this.tracker = tracker;
        setLayout(new BorderLayout());
        setBackground(ColorUtils.BACKGROUND_COLOR);
        buildUI();
    }

    private void buildUI() {
        JPanel container = new JPanel();
        container.setBackground(ColorUtils.BACKGROUND_COLOR);
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));

        container.add(sectionTitle("Subject-wise Tracker"));
        container.add(subjectTracker());
        container.add(Box.createVerticalStrut(16));
        container.add(sectionTitle("Overall Tracker"));
        container.add(overallTracker());

        JScrollPane scroll = new JScrollPane(container);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);
    }

    private JComponent sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setBorder(BorderFactory.createEmptyBorder(10,10,6,10));
        return l;
    }

    private JPanel subjectTracker() {
        JPanel card = cardPanel();
        JComboBox<String> subjectBox = new JComboBox<>();
        JTextField minPct = new JTextField("75", 4);
        JLabel result = new JLabel(" ");

        try {
            java.util.Set<String> codes = new TreeSet<>();
            for (String day : new String[]{"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"}) {
                student.getTimetable().getDaySchedule(day).forEach(s -> codes.add(s.getSubjectCode()));
            }
            for (String code : codes) {
                Subject s = subjectDAO.getSubjectByCode(code);
                subjectBox.addItem(code + " - " + (s != null ? s.getName() : code));
            }
        } catch (Exception ignored) {}

        JButton calc = new JButton("Calculate");
        calc.addActionListener(e -> {
            String sel = (String) subjectBox.getSelectedItem();
            if (sel == null) return;
            String code = sel.contains(" - ") ? sel.substring(0, sel.indexOf(" - ")) : sel;
            try {
                List<AttendanceRecord> all = attendanceService.getAttendanceByRollNo(student.getRollNo());
                List<AttendanceRecord> filtered = new ArrayList<>();
                for (AttendanceRecord r : all) if (code.equals(r.getSubjectCode())) filtered.add(r);
                Stats stats = computeStats(filtered);
                int bunk = tracker.calculateBunkableClasses(stats.attended, stats.total, parsePct(minPct.getText()));
                int must = tracker.calculateRequiredClasses(stats.attended, stats.total, parsePct(minPct.getText()));
                result.setText(advice(stats, parsePct(minPct.getText()), bunk, must));
                result.setForeground(colorForAdvice(stats, parsePct(minPct.getText())));
            } catch (Exception ex) {
                result.setText("No data available.");
            }
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.add(new JLabel("Subject:"));
        row.add(subjectBox);
        row.add(new JLabel("Minimum %:"));
        row.add(minPct);
        row.add(calc);

        card.add(row, BorderLayout.NORTH);
        card.add(result, BorderLayout.CENTER);
        return card;
    }

    private JPanel overallTracker() {
        JPanel card = cardPanel();
        JTextField minPct = new JTextField("75", 4);
        JLabel result = new JLabel(" ");
        JButton calc = new JButton("Calculate");
        calc.addActionListener(e -> {
            try {
                List<AttendanceRecord> all = attendanceService.getAttendanceByRollNo(student.getRollNo());
                Stats stats = computeStats(all);
                int bunk = tracker.calculateBunkableClasses(stats.attended, stats.total, parsePct(minPct.getText()));
                int must = tracker.calculateRequiredClasses(stats.attended, stats.total, parsePct(minPct.getText()));
                result.setText(advice(stats, parsePct(minPct.getText()), bunk, must));
                result.setForeground(colorForAdvice(stats, parsePct(minPct.getText())));
            } catch (Exception ex) {
                result.setText("No data available.");
            }
        });

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setOpaque(false);
        row.add(new JLabel("Minimum %:"));
        row.add(minPct);
        row.add(calc);
        card.add(row, BorderLayout.NORTH);
        card.add(result, BorderLayout.CENTER);
        return card;
    }

    private JPanel cardPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(10,10,10,10)
        ));
        p.setBackground(Color.WHITE);
        return p;
    }

    private static class Stats {
        int total; int attended;
    }

    private Stats computeStats(List<AttendanceRecord> records) {
        Stats s = new Stats();
        for (AttendanceRecord r : records) {
            s.total++;
            if (Constants.STATUS_PRESENT.equals(r.getStatus()) || Constants.STATUS_ON_DUTY.equals(r.getStatus())) s.attended++;
        }
        return s;
    }

    private double parsePct(String txt) {
        try { return Double.parseDouble(txt); } catch (Exception e) { return 75; }
    }

    private String advice(Stats s, double min, int bunk, int must) {
        double pct = s.total == 0 ? 0 : (s.attended * 100.0 / s.total);
        if (pct >= min) return String.format("Current: %.2f%%. You can safely bunk %d class(es).", pct, bunk);
        if (must == -1) return "Warning: It may be impossible to reach minimum attendance.";
        return String.format("Current: %.2f%%. You must attend next %d class(es).", pct, must);
    }

    private Color colorForAdvice(Stats s, double min) {
        double pct = s.total == 0 ? 0 : (s.attended * 100.0 / s.total);
        if (pct >= min) return new Color(76, 175, 80);
        if (pct >= min - 5) return new Color(255, 193, 7);
        return new Color(244, 67, 54);
    }
}





