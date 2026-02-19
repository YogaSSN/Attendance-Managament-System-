package com.trackit.views.student;

import com.trackit.models.AttendanceRecord;
import com.trackit.models.Student;
import com.trackit.models.TimeSlot;
import com.trackit.models.Timetable;
import com.trackit.services.AttendanceService;
import com.trackit.utils.Constants;
import com.trackit.utils.ErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MarkAttendancePanel extends JPanel {
    private final Student student;
    private final AttendanceService attendanceService;
    private final String[] timeLabels = {
        "8:00-8:40", "8:45-9:30", "9:50-10:35", "10:35-11:20",
        "12:20-1:05", "1:05-1:50", "2:10-2:55", "2:55-3:40"
    };

    private final List<ClassRow> classRows = new ArrayList<>();
    private JLabel summaryLabel;

    public MarkAttendancePanel(Student student, AttendanceService attendanceService) {
        this.student = student;
        this.attendanceService = attendanceService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        buildUI();
        // initial list load occurs inside buildUI via selected day
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        JLabel title = new JLabel("Mark Attendance");
        title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JComboBox<String> daySelect = new JComboBox<>(new String[]{"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"});
        daySelect.setSelectedItem(java.time.LocalDate.now().getDayOfWeek().name());
        daySelect.addActionListener(e -> loadClassesForDay((String) daySelect.getSelectedItem()));
        left.add(title);
        left.add(daySelect);
        JButton markAll = new JButton("Mark All Present");
        markAll.addActionListener(e -> {
            System.out.println("[DEBUG] Mark All Present clicked");
            markAllPresent();
        });
        JButton saveAll = new JButton("Save / Submit All");
        saveAll.addActionListener(e -> saveAllMarked());
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        rightBtns.setOpaque(false);
        rightBtns.add(markAll);
        rightBtns.add(saveAll);
        top.add(left, BorderLayout.WEST);
        top.add(rightBtns, BorderLayout.EAST);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        summaryLabel = new JLabel(" ");
        summaryLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(summaryLabel, BorderLayout.SOUTH);

        // Store list panel for dynamic population
        putClientProperty("listPanel", listPanel);
        // Initial load for currently selected day
        loadClassesForDay((String) daySelect.getSelectedItem());
    }

    private void loadClassesForDay(String dayKey) {
        JPanel listPanel = (JPanel) getClientProperty("listPanel");
        listPanel.removeAll();
        classRows.clear();

        Timetable tt = student.getTimetable();
        if (tt == null) {
            listPanel.add(new JLabel("Configure your timetable first"));
            revalidate(); repaint();
            return;
        }
        List<TimeSlot> slots = tt.getDaySchedule(dayKey);
        for (int i = 1; i <= 8; i++) {
            final int period = i;
            TimeSlot slot = slots.stream().filter(s -> s.getPeriod() == period).findFirst().orElse(null);
            ClassRow row = new ClassRow(period, timeLabels[period-1], slot == null ? null : slot.getSubjectCode(), slot == null ? "No Class" : slot.getSubjectName());
            classRows.add(row);
            listPanel.add(row.panel);
            listPanel.add(Box.createVerticalStrut(6));
        }
        updateSummary();
        revalidate(); repaint();
    }

    private void updateSummary() {
        long marked = classRows.stream().filter(r -> r.getSelectedStatus() != null).count();
        summaryLabel.setText(String.format("Marked %d out of %d classes", marked, classRows.size()));
    }

    private void markAllPresent() {
        int changed = 0;
        for (ClassRow r : classRows) {
            if (r.subjectCode == null) continue; // No Class rows remain NC
            r.present.setSelected(true);
            r.updateStatusColor();
            changed++;
        }
        System.out.println("[DEBUG] Marked present for periods: " + changed + " out of " + classRows.size());
        updateSummary();
        JOptionPane.showMessageDialog(this, "All applicable periods marked Present.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveAllMarked() {
        System.out.println("[DEBUG] Save All clicked");
        int saved = 0;
        for (ClassRow r : classRows) {
            String status = r.getSelectedStatus();
            if (status == null) continue; // skip unmarked
            if (r.subjectCode == null && !"NC".equals(status)) continue; // skip invalid
            // Reuse per-row save routine
            saveRow(r);
            saved++;
        }
        System.out.println("[DEBUG] Save All processed rows: " + saved);
        JOptionPane.showMessageDialog(this, "Saved updates for " + saved + " period(s).", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void saveRow(ClassRow r) {
        // Allow marking attendance for the selected day
        String status;
        if (r.noClass.isSelected() || r.subjectCode == null) status = "NC";
        else if (r.present.isSelected()) status = Constants.STATUS_PRESENT;
        else if (r.absent.isSelected()) status = Constants.STATUS_ABSENT;
        else { ErrorHandler.showErrorMessage(this, "Select a status first."); return; }

        AttendanceRecord record = new AttendanceRecord(
            student.getRollNo(),
            r.subjectCode == null ? "-" : r.subjectCode,
            LocalDate.now(),
            r.period,
            status
        );
        try {
            attendanceService.markAttendance(record);
            r.updateStatusColor();
            updateSummary();
        } catch (Exception e) {
            ErrorHandler.showErrorMessage(this, "Failed to mark attendance: " + e.getMessage());
        }
    }

    private class ClassRow {
        final JPanel panel = new JPanel(new BorderLayout());
        final int period;
        final String time;
        final String subjectCode;
        final String subjectName;

        final JRadioButton present = new JRadioButton("Present");
        final JRadioButton absent = new JRadioButton("Absent");
        final JRadioButton noClass = new JRadioButton("No Class");

        ClassRow(int period, String time, String subjectCode, String subjectName) {
            this.period = period;
            this.time = time;
            this.subjectCode = subjectCode;
            this.subjectName = subjectName;

            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230,230,230)),
                BorderFactory.createEmptyBorder(8,8,8,8)
            ));
            panel.setBackground(new Color(255, 253, 231)); // yellow default

            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);

            JLabel periodLbl = new JLabel(String.format("P%d", period));
            periodLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JLabel subjLbl = new JLabel(String.format("  %s - %s  (%s)", subjectCode == null ? "-" : subjectCode, subjectName, time));
            subjLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            Dimension btnSize = new Dimension(100, 35);
            present.setPreferredSize(btnSize);
            absent.setPreferredSize(btnSize);
            noClass.setPreferredSize(btnSize);

            // Distinct colors
            present.setBackground(new Color(0x4CAF50));
            present.setOpaque(true);
            absent.setBackground(new Color(0xF44336));
            absent.setOpaque(true);
            noClass.setBackground(new Color(0x9E9E9E));
            noClass.setOpaque(true);

            ButtonGroup group = new ButtonGroup();
            group.add(present); group.add(absent); group.add(noClass);

            row.add(periodLbl);
            row.add(Box.createRigidArea(new Dimension(12, 0)));
            row.add(subjLbl);
            row.add(Box.createRigidArea(new Dimension(20, 0)));
            row.add(present);
            row.add(Box.createRigidArea(new Dimension(10, 0)));
            row.add(absent);
            row.add(Box.createRigidArea(new Dimension(10, 0)));
            row.add(noClass);

            JButton markBtn = new JButton("Mark / Update");
            markBtn.addActionListener(e -> saveRow(this));

            panel.add(row, BorderLayout.CENTER);
            panel.add(markBtn, BorderLayout.EAST);
        }

        String getSelectedStatus() {
            if (noClass.isSelected() || subjectCode == null) return "NC";
            if (present.isSelected()) return Constants.STATUS_PRESENT;
            if (absent.isSelected()) return Constants.STATUS_ABSENT;
            return null;
        }

        void updateStatusColor() {
            String status = getSelectedStatus();
            if (status == null) { panel.setBackground(new Color(255, 253, 231)); return; }
            switch (status) {
                case "P": panel.setBackground(new Color(232, 245, 233)); break; // green
                case "A": panel.setBackground(new Color(255, 235, 238)); break; // red
                default: panel.setBackground(new Color(238, 238, 238)); // gray
            }
            panel.repaint();
        }
    }
}


