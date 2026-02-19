package com.trackit.views.student;

import com.trackit.dao.FacultyDAO;
import com.trackit.dao.SubjectDAO;
import com.trackit.models.Faculty;
import com.trackit.models.Student;
import com.trackit.models.Subject;
import com.trackit.models.TimeSlot;
import com.trackit.models.Timetable;
import com.trackit.utils.ErrorHandler;
import com.trackit.utils.ColorUtils;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.*;

public class TodayClassesPanel extends JPanel {
    private final Student student;
    private final SubjectDAO subjectDAO = new SubjectDAO();
    private final FacultyDAO facultyDAO = new FacultyDAO();
    private JTable table;
    private JLabel statsLabel;
    private JLabel dateBanner;
    private Timer timer;

    private final String[] timeLabels = {
        "8:00-8:40", "8:45-9:30", "9:50-10:35", "10:35-11:20",
        "12:20-1:05", "1:05-1:50", "2:10-2:55", "2:55-3:40"
    };

    public TodayClassesPanel(Student student) {
        this.student = student;
        setLayout(new BorderLayout());
        setBackground(ColorUtils.BACKGROUND_COLOR);
        buildUI();
        loadToday();
        startAutoUpdate();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(ColorUtils.BACKGROUND_COLOR);
        dateBanner = new JLabel("Today", SwingConstants.LEFT);
        dateBanner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dateBanner.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadToday());
        top.add(dateBanner, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        String[] cols = {"Period", "Time", "Subject Code", "Subject Name", "Faculty Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new PeriodHighlightRenderer());

        statsLabel = new JLabel(" ");
        statsLabel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(statsLabel, BorderLayout.SOUTH);
    }

    private void startAutoUpdate() {
        timer = new Timer(60_000, e -> table.repaint());
        timer.start();
    }

    private void loadToday() {
        LocalDate now = LocalDate.now();
        DayOfWeek dow = now.getDayOfWeek();
        String formatted = new SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new Date());
        dateBanner.setText("Today is " + formatted);

        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            showInfo("No classes on weekends");
            return;
        }

        Timetable tt = student.getTimetable();
        if (tt == null) {
            showInfo("Configure your timetable first");
            return;
        }

        String dayKey = dow.name();
        List<TimeSlot> slots = tt.getDaySchedule(dayKey);
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int total = 0, completed = 0, remaining = 0;
        for (int i = 1; i <= 8; i++) {
            String code = null;
            String name = "No Class";
            String facultyName = "-";
            final int period = i;
            Optional<TimeSlot> match = slots.stream().filter(s -> s.getPeriod() == period).findFirst();
            if (match.isPresent()) {
                code = match.get().getSubjectCode();
                name = getSubjectName(code);
                facultyName = getFacultyNameForSubject(code);
                total++;
            }
            model.addRow(new Object[]{i, timeLabels[i-1], code == null ? "-" : code, name, facultyName, statusForPeriod(i)});
        }

        int current = currentPeriodIndex();
        if (current == -1) {
            // all past or none; compute completed/remaining roughly
            completed = (int) Arrays.stream(timeLabels).limit(8).count();
        } else {
            completed = current - 1;
            remaining = 8 - current + 1;
        }
        statsLabel.setText(String.format("Total classes today: %d, Completed: %d, Remaining: %d", total, completed, Math.max(0, total - completed)));
    }

    private void showInfo(String msg) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{"-", "-", "-", msg, "-", "-"});
        statsLabel.setText(" ");
    }

    private String getSubjectName(String code) {
        if (code == null) return "No Class";
        try { Subject s = subjectDAO.getSubjectByCode(code); return s != null ? s.getName() : code; } catch (Exception e) { return code; }
    }

    private String getFacultyNameForSubject(String code) {
        try {
            Subject s = subjectDAO.getSubjectByCode(code);
            if (s == null) return "-";
            Faculty f = facultyDAO.getFacultyById(s.getFacultyId());
            return f != null ? f.getName() : "-";
        } catch (Exception e) { return "-"; }
    }

    private String statusForPeriod(int period) {
        int idx = currentPeriodIndex();
        if (idx == -1) return "Past";
        if (period < idx) return "Past";
        if (period == idx) return "Ongoing";
        return "Upcoming";
    }

    private int currentPeriodIndex() {
        // Rough mapping by index; in real scenario, parse time. Here we highlight based on approximate schedule
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int hm = hour * 100 + minute;
        int[] startTimes = {800, 845, 950, 1035, 1220, 1305, 1410, 1455};
        int[] endTimes = {840, 930, 1035, 1120, 1305, 1350, 1455, 1540};
        for (int i = 0; i < 8; i++) {
            if (hm >= startTimes[i] && hm <= endTimes[i]) return i + 1;
        }
        if (hm < startTimes[0]) return 1;
        if (hm > endTimes[7]) return -1; // all done
        for (int i = 0; i < 7; i++) if (hm > endTimes[i] && hm < startTimes[i+1]) return i + 2;
        return -1;
    }

    private class PeriodHighlightRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            int period = 0;
            try {
                Object v = table.getValueAt(row, 0);
                period = Integer.parseInt(String.valueOf(v));
            } catch (Exception ignored) {}
            int current = currentPeriodIndex();
            if (current == -1) {
                c.setBackground(new Color(240, 240, 240));
            } else if (period < current) {
                c.setBackground(new Color(230, 230, 230)); // past
            } else if (period == current) {
                c.setBackground(new Color(255, 249, 196)); // yellow
            } else {
                c.setBackground(Color.WHITE);
            }
            return c;
        }
    }
}


