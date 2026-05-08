package org.onebrc.benchmark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FixData {
    public static void main(String[] args) throws Exception {
        System.out.println("Cleaning up and running CsvMerger for historical data...");
        String[] runs = {"20260508-213732", "20260508-215342", "20260508-220316"};
        
        for (String timestamp : runs) {
            Path csvFile = BenchmarkDataLocator.getCsvFile(timestamp);
            if (!Files.exists(csvFile)) continue;
            
            List<String> lines = Files.readAllLines(csvFile);
            List<String> cleanedLines = new ArrayList<>();
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (parts.length > 26) {
                    // Strip the extra merged columns
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 26; i++) {
                        sb.append(parts[i]);
                        if (i < 25) sb.append(",");
                    }
                    cleanedLines.add(sb.toString());
                } else {
                    cleanedLines.add(line);
                }
            }
            Files.write(csvFile, cleanedLines);
            System.out.println("Cleaned up " + csvFile);
            
            CsvMerger.merge(timestamp);
        }
        System.out.println("Done merging.");
    }
}
