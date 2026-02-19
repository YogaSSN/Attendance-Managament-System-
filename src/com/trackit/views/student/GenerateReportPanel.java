package com.trackit.views.student;

import com.trackit.models.Student;
import com.trackit.services.ReportService;
import com.trackit.utils.Constants;
import com.trackit.utils.ErrorHandler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateReportPanel extends JPanel {
    private final Student student;
    private final ReportService reportService;
    private final JTextArea output = new JTextArea();

    public GenerateReportPanel(Student student, ReportService reportService) {
        this.student = student;
        this.reportService = reportService;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        buildUI();
    }

    private void buildUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(Color.WHITE);
        JComboBox<String> options = new JComboBox<>(new String[]{"My Attendance Report", "Timetable Export"});
        JButton gen = new JButton("Generate");
        gen.addActionListener(e -> generate((String) options.getSelectedItem()));
        JButton save = new JButton("Save to File");
        save.addActionListener(e -> saveToFile());
        JButton print = new JButton("Print");
        print.addActionListener(e -> { try { output.print(); } catch (Exception ex) { ErrorHandler.showErrorMessage(this, ex.getMessage()); }});
        top.add(new JLabel("Report:"));
        top.add(options);
        top.add(gen);
        top.add(save);
        top.add(print);

        output.setEditable(false);
        output.setFont(new Font("Consolas", Font.PLAIN, 13));
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(output), BorderLayout.CENTER);
    }

    private void generate(String type) {
        if ("My Attendance Report".equals(type)) {
            String report = reportService.generateStudentReport(student.getRollNo());
            String date = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(new Date());
            output.setText(report + "\nGenerated on: " + date + "\n");
        } else {
            output.setText(buildTimetableExport());
        }
    }

    private String buildTimetableExport() {
        StringBuilder sb = new StringBuilder();
        sb.append("================= TIMETABLE EXPORT =================\n");
        sb.append(String.format("%s | Roll: %s | %s %s\n\n", student.getName(), student.getRollNo(), student.getDepartment(), student.getSection()));
        String[] days = {"MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY"};
        for (String d : days) {
            sb.append(d).append("\n");
            for (int p = 1; p <= 8; p++) {
                final int period = p;
                String code = student.getTimetable().getDaySchedule(d).stream().filter(s -> s.getPeriod() == period).map(s -> s.getSubjectCode()).findFirst().orElse("-");
                sb.append(String.format("  P%-2d : %s\n", p, code));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private void saveToFile() {
        try {
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            String fileName = String.format("StudentReport_%s_%s.txt", student.getRollNo(), date);
            File dir = new File("TrackIT/data/reports/student_reports");
            if (!dir.exists()) dir.mkdirs();
            File out = new File(dir, fileName);
            try (FileWriter fw = new FileWriter(out)) { fw.write(output.getText()); }
            JOptionPane.showMessageDialog(this, "Saved to: " + out.getAbsolutePath());
        } catch (Exception e) {
            ErrorHandler.showErrorMessage(this, "Failed to save: " + e.getMessage());
        }
    }
}


