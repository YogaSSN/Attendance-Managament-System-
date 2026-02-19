package com.trackit.views.student;

import com.trackit.dao.SubjectDAO;
import com.trackit.models.Student;
import com.trackit.models.Subject;
import com.trackit.models.TimeSlot;
import com.trackit.models.Timetable;
import com.trackit.utils.ErrorHandler;
import com.trackit.utils.ColorUtils;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.print.PrinterException;
import java.util.*;
import java.util.List;

public class ViewTimetablePanel extends JPanel {
    private final Student student;
    private final SubjectDAO subjectDAO;
    private JTable table;
    private final String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    private final String[] periodHeaders = {
        "Period 1\n8:00-8:40", "Period 2\n8:45-9:30", "Period 3\n9:50-10:35",
        "Period 4\n10:35-11:20", "Period 5\n12:20-1:05", "Period 6\n1:05-1:50",
        "Period 7\n2:10-2:55", "Period 8\n2:55-3:40"
    };
    private Map<String, Color> subjectColorMap = new HashMap<>();

    public ViewTimetablePanel(Student student) {
        this.student = student;
        this.subjectDAO = new SubjectDAO();
        setLayout(new BorderLayout());
        setBackground(ColorUtils.BACKGROUND_COLOR);
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(ColorUtils.BACKGROUND_COLOR);
        JLabel info = new JLabel(String.format("%s | Roll: %s | Dept: %s %s", student.getName(), student.getRollNo(), student.getDepartment(), student.getSection()));
        info.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        info.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JButton printBtn = new JButton("Print / Export");
        printBtn.addActionListener(e -> printTable());
        top.add(info, BorderLayout.WEST);
        top.add(printBtn, BorderLayout.EAST);

        TimetableTableModel model = new TimetableTableModel();
        table = new JTable(model);
        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, new SubjectColorRenderer());

        String[] headers = new String[9];
        headers[0] = "Day";
        System.arraycopy(periodHeaders, 0, headers, 1, 8);
        model.setColumnHeaders(headers);

        // Day column renderer
        DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer();
        dayRenderer.setBackground(new Color(245, 245, 245));
        dayRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(dayRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(ColorUtils.BACKGROUND_COLOR);

        JPanel legendPanel = buildLegend();

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.SOUTH);

        loadData((TimetableTableModel) table.getModel());
    }

    private void printTable() {
        try {
            table.print();
        } catch (PrinterException e) {
            ErrorHandler.showErrorMessage(this, "Failed to print/export: " + e.getMessage());
        }
    }

    private JPanel buildLegend() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(ColorUtils.BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        Set<String> codes = new TreeSet<>();
        Timetable tt = student.getTimetable();
        if (tt != null) {
            for (String d : days) {
                for (TimeSlot s : tt.getDaySchedule(d)) {
                    codes.add(s.getSubjectCode());
                }
            }
        }
        for (String code : codes) {
            JLabel chip = new JLabel(code + " - " + getSubjectName(code));
            chip.setOpaque(true);
            chip.setBackground(getColorForSubject(code));
            chip.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            panel.add(chip);
        }
        return panel;
    }

    private void loadData(TimetableTableModel model) {
        for (int r = 0; r < days.length; r++) {
            model.setValueAt(cap(days[r]), r, 0);
            for (int c = 1; c <= 8; c++) model.setValueAt("No Class", r, c);
        }
        Timetable tt = student.getTimetable();
        if (tt == null) return;
        for (int r = 0; r < days.length; r++) {
            for (TimeSlot slot : tt.getDaySchedule(days[r])) {
                int p = slot.getPeriod();
                if (p >= 1 && p <= 8) {
                    String code = slot.getSubjectCode();
                    String name = getSubjectName(code);
                    model.setValueAt(code + " - " + name, r, p);
                }
            }
        }
    }

    private String getSubjectName(String code) {
        try {
            Subject s = subjectDAO.getSubjectByCode(code);
            return s != null ? s.getName() : code;
        } catch (Exception e) {
            return code;
        }
    }

    private Color getColorForSubject(String code) {
        return subjectColorMap.computeIfAbsent(code, k -> randomPastel());
    }

    private Color randomPastel() {
        Random r = new Random();
        int red = (r.nextInt(128) + 127);
        int green = (r.nextInt(128) + 127);
        int blue = (r.nextInt(128) + 127);
        return new Color(red, green, blue);
    }

    private String cap(String s) { return s.substring(0,1) + s.substring(1).toLowerCase(); }

    private class TimetableTableModel extends AbstractTableModel {
        private String[] headers = new String[9];
        private final Object[][] data = new Object[5][9];

        @Override
        public int getRowCount() { return 5; }

        @Override
        public int getColumnCount() { return 9; }

        void setColumnHeaders(String[] h) { this.headers = h; fireTableStructureChanged(); }

        @Override
        public String getColumnName(int column) { return headers[column]; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) { return data[rowIndex][columnIndex]; }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) { data[rowIndex][columnIndex] = aValue; fireTableCellUpdated(rowIndex, columnIndex); }
    }

    private class SubjectColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (column == 0) return c;
            String v = value == null ? "" : value.toString();
            if (v.equals("No Class") || v.trim().isEmpty()) {
                c.setBackground(Color.WHITE);
            } else {
                String code = v.contains(" - ") ? v.substring(0, v.indexOf(" - ")) : v;
                c.setBackground(getColorForSubject(code));
            }
            return c;
        }
    }
}


