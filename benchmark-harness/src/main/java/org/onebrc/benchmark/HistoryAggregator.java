package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HistoryAggregator {

    public static class RunSummary {
        public final String timestamp;
        public final String comment;
        public final int totalCombinations;
        public final String fastestClass;
        public final long fastestMedianMs;
        public final double fastestIpc;

        public RunSummary(String timestamp, String comment, int totalCombinations, String fastestClass, long fastestMedianMs, double fastestIpc) {
            this.timestamp = timestamp;
            this.comment = comment;
            this.totalCombinations = totalCombinations;
            this.fastestClass = fastestClass;
            this.fastestMedianMs = fastestMedianMs;
            this.fastestIpc = fastestIpc;
        }
        
        public String getTimestamp() { return timestamp; }
        public String getComment() { return comment; }
        public int getTotalCombinations() { return totalCombinations; }
        public String getFastestClass() { return fastestClass; }
        public long getFastestMedianMs() { return fastestMedianMs; }
        public double getFastestIpc() { return fastestIpc; }
    }

    public static List<RunSummary> aggregate() {
        List<RunSummary> summaries = new ArrayList<>();
        Path historyDir = Paths.get("data", "benchmark-history");
        if (!Files.exists(historyDir)) return summaries;

        Gson gson = new Gson();

        try (Stream<Path> paths = Files.list(historyDir)) {
            List<Path> csvFiles = paths
                .filter(p -> p.toString().endsWith(".csv") && !p.getFileName().toString().contains("-meta"))
                .collect(Collectors.toList());

            for (Path csvFile : csvFiles) {
                try {
                    String timestamp = csvFile.getFileName().toString().replace(".csv", "");
                    Path metaFile = historyDir.resolve(timestamp + "-meta.json");
                    
                    String comment = "";
                    int totalRuns = 0;
                    
                    if (Files.exists(metaFile)) {
                        try {
                            String jsonStr = Files.readString(metaFile);
                            JsonObject meta = gson.fromJson(jsonStr, JsonObject.class);
                            if (meta.has("comment")) comment = meta.get("comment").getAsString();
                            if (meta.has("totalRuns")) totalRuns = meta.get("totalRuns").getAsInt();
                        } catch (Exception e) {
                            System.err.println("Failed to parse meta.json: " + metaFile);
                        }
                    }

                    String fastestClass = "-";
                    long fastestMs = Long.MAX_VALUE;
                    double fastestIpc = 0.0;

                    if (Files.exists(csvFile)) {
                        ResultMatrix matrix = new ResultMatrix();
                        matrix.loadCsv(csvFile);
                        for (String cls : matrix.getClasses()) {
                            for (String env : matrix.getEnvironments()) {
                                for (String ds : matrix.getDatasets()) {
                                    String[] parts = env.split(" \\| ", -1);
                                    if (parts.length < 5) continue;
                                    ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, cls);
                                    ResultMatrix.RowData rd = matrix.get(k);
                                    if (rd != null && rd.medianRuntimeMs > 0 && rd.medianRuntimeMs < fastestMs) {
                                        fastestMs = rd.medianRuntimeMs;
                                        fastestClass = cls;
                                        fastestIpc = rd.ipc;
                                    }
                                }
                            }
                        }
                    }

                    if (fastestMs == Long.MAX_VALUE) fastestMs = 0;

                    summaries.add(new RunSummary(timestamp, comment, totalRuns, fastestClass, fastestMs, fastestIpc));

                } catch (Exception e) {
                    System.err.println("Error processing run summary for " + csvFile + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read history directory: " + e.getMessage());
        }

        // Sort descending by timestamp (newest first)
        summaries.sort(Comparator.comparing(RunSummary::getTimestamp).reversed());
        return summaries;
    }
}
