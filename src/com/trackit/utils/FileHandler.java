package com.trackit.utils;

import com.trackit.exceptions.FileIOException;
import java.io.*;
import java.util.*;

public class FileHandler {
    
    public static List<String> readFile(String filename) throws FileIOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        
        if (!file.exists()) {
            return lines;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.trim().startsWith("#")) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            throw new FileIOException("Error reading file: " + filename, e);
        }
        return lines;
    }
    
    public static void writeFile(String filename, List<String> lines) throws FileIOException {
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    bw.write(line);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            throw new FileIOException("Error writing file: " + filename, e);
        }
    }
    
    public static void appendFile(String filename, String line) throws FileIOException {
        try {
            File file = new File(filename);
            file.getParentFile().mkdirs();
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new FileIOException("Error appending to file: " + filename, e);
        }
    }
    
    public static void ensureFileExists(String filename) throws FileIOException {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new FileIOException("Error creating file: " + filename, e);
        }
    }
}
