package com.trackit.views.student;

import com.trackit.dao.SubjectDAO;
import com.trackit.dao.TimetableDAO;
import com.trackit.models.Student;
import com.trackit.models.Subject;
import com.trackit.models.TimeSlot;
import com.trackit.models.Timetable;
import com.trackit.utils.ColorUtils;
import com.trackit.utils.ErrorHandler;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Student timetable configuration panel.
 * - 5 days (Mon-Fri) x 8 periods grid
 * - Each cell is a JComboBox with subjects filtered by student's class/section
 * - Supports "No Class" option
 * - Loads existing timetable and saves back via TimetableDAO
 * - Allows the same subject to appear multiple times on the same day (each period is independent)
 */
public class TimetableConfigPanel extends JPanel {
    private final Student student;
    private final TimetableDAO timetableDAO;
    private final SubjectDAO subjectDAO;

    private JTable table;
    private TimetableTableModel tableModel;
    private final String[] days = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    private final String[] periodHeaders = {
        "Period 1 \n8:00-8:40", "Period 2 \n8:45-9:30", "Period 3 \n9:50-10:35",
        "Period 4 \n10:35-11:20", "Period 5 \n12:20-1:05", "Period 6 \n1:05-1:50",
        "Period 7 \n2:10-2:55", "Period 8 \n2:55-3:40"
    };

    private List<Subject> availableSubjects;
    private Map<String, Subject> codeToSubject;

    public TimetableConfigPanel(Student student, TimetableDAO timetableDAO) {
        this.student = student;
        this.timetableDAO = timetableDAO;
        this.subjectDAO = new SubjectDAO();
        setLayout(new BorderLayout());
        setBackground(ColorUtils.BACKGROUND_COLOR);

        loadSubjects();
        buildUI();
        loadExistingTimetable();
    }

    private void loadSubjects() {
        try {
            availableSubjects = subjectDAO.getSubjectsByDepartment(student.getDepartment(), student.getSection());
        } catch (Exception e) {
            availableSubjects = new ArrayList<>();
            ErrorHandler.logError("Failed loading subjects for department/section", e);
        }
        codeToSubject = new HashMap<>();
        for (Subject s : availableSubjects) {
            codeToSubject.put(s.getCode(), s);
        }
    }

    private void buildUI() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ColorUtils.BACKGROUND_COLOR);
        JLabel title = new JLabel("Configure Timetable (Mon–Fri × 8 periods)");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(ColorUtils.getPrimaryColor());
        header.add(title, BorderLayout.WEST);

        JButton saveBtn = new JButton("Save Timetable");
        saveBtn.addActionListener(e -> saveTimetable());
        header.add(saveBtn, BorderLayout.EAST);
        header.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tableModel = new TimetableTableModel();
        table = new JTable(tableModel) {
            @Override
            public TableCellRenderer getCellRenderer(int row, int column) {
                if (column == 0) {
                    return super.getCellRenderer(row, column);
                }
                return new SubjectCellRenderer();
            }

            @Override
            public TableCellEditor getCellEditor(int row, int column) {
                if (column == 0) return super.getCellEditor(row, column);
                return new SubjectCellEditor(getSubjectOptions());
            }
        };
        table.setRowHeight(36);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));

        // Set column headers
        String[] headers = new String[9];
        headers[0] = "Day";
        System.arraycopy(periodHeaders, 0, headers, 1, 8);
        tableModel.setColumnHeaders(headers);

        // First column (Day) renderer
        DefaultTableCellRenderer dayRenderer = new DefaultTableCellRenderer();
        dayRenderer.setBackground(new Color(245, 245, 245));
        dayRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(dayRenderer);
        table.getColumnModel().getColumn(0).setPreferredWidth(110);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(header, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JLabel note = new JLabel("Tip: You can use the same subject multiple times on the same day. Use 'No Class' for free periods.");
        note.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        add(note, BorderLayout.SOUTH);
    }

    private String[] getSubjectOptions() {
        // First option is No Class
        List<String> opts = new ArrayList<>();
        opts.add("No Class");
        for (Subject s : availableSubjects) {
            opts.add(s.getCode() + " - " + s.getName());
        }
        return opts.toArray(new String[0]);
    }

    private void loadExistingTimetable() {
        // Initialize table with day labels and empty selections
        for (int r = 0; r < days.length; r++) {
            tableModel.setValueAt(displayDay(days[r]), r, 0);
            for (int c = 1; c <= 8; c++) {
                tableModel.setValueAt("No Class", r, c);
            }
        }

        try {
            Timetable tt = student.getTimetable();
            if (tt == null) return;
            for (int r = 0; r < days.length; r++) {
                List<TimeSlot> slots = tt.getDaySchedule(days[r]);
                for (TimeSlot slot : slots) {
                    int period = slot.getPeriod();
                    if (period >= 1 && period <= 8) {
                        Subject s = codeToSubject.get(slot.getSubjectCode());
                        String label = s != null ? s.getCode() + " - " + s.getName() : slot.getSubjectCode();
                        tableModel.setValueAt(label, r, period);
                    }
                }
            }
        } catch (Exception e) {
            ErrorHandler.logError("Error initializing timetable grid", e);
        }
    }

    private void saveTimetable() {
        // Duplicate subjects on the same day are allowed. Each period is independent so no per-day uniqueness check is performed here.

        Timetable timetable = new Timetable();
        for (int r = 0; r < days.length; r++) {
            for (int c = 1; c <= 8; c++) {
                String selection = String.valueOf(tableModel.getValueAt(r, c));
                String code = parseSubjectCode(selection);
                if (code != null && !code.isEmpty()) {
                    Subject s = codeToSubject.get(code);
                    String name = s != null ? s.getName() : code;
                    TimeSlot slot = new TimeSlot(c, code, name, student.getDepartment(), student.getSection());
                    timetable.addSlot(days[r], slot);
                }
            }
        }

        try {
            timetableDAO.saveStudentTimetable(student.getRollNo(), timetable);
            student.setTimetable(timetable);
            JOptionPane.showMessageDialog(this, "Timetable saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            ErrorHandler.logError("Failed to save timetable", e);
            ErrorHandler.showErrorMessage(this, "Failed to save timetable: " + e.getMessage());
        }
    }

    private String parseSubjectCode(String display) {
        if (display == null) return null;
        String val = display.trim();
        if (val.isEmpty() || val.equalsIgnoreCase("No Class")) return "";
        int idx = val.indexOf(" - ");
        return idx > 0 ? val.substring(0, idx).trim() : val;
    }

    private String displayDay(String dayKey) {
        String d = dayKey.substring(0, 1) + dayKey.substring(1).toLowerCase();
        return d.charAt(0) + d.substring(1);
    }

    private class TimetableTableModel extends AbstractTableModel {
        private String[] columnHeaders = new String[9];
        private final Object[][] data = new Object[5][9];

        public TimetableTableModel() {
            for (int r = 0; r < 5; r++) {
                data[r][0] = days[r];
            }
        }

        void setColumnHeaders(String[] headers) {
            this.columnHeaders = headers;
            fireTableStructureChanged();
        }

        @Override
        public int getRowCount() { return 5; }

        @Override
        public int getColumnCount() { return 9; }

        @Override
        public String getColumnName(int column) { return columnHeaders[column]; }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) { return columnIndex != 0; }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) { return data[rowIndex][columnIndex]; }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            data[rowIndex][columnIndex] = aValue;
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    private static class SubjectCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String v = value == null ? "" : value.toString();
            if (v.trim().isEmpty() || v.equalsIgnoreCase("No Class")) {
                c.setBackground(Color.WHITE);
                setText("No Class");
                setForeground(Color.DARK_GRAY);
            } else {
                c.setBackground(new Color(227, 242, 253)); // light blue
                setForeground(Color.BLACK);
            }
            return c;
        }
    }

    private static class SubjectCellEditor extends DefaultCellEditor {
        public SubjectCellEditor(String[] options) {
            super(new JComboBox<>(options));
        }
    }
}
