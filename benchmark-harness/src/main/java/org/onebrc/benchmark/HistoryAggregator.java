/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Antigravity
 */
public class HistoryAggregator {

    public static class RunSummary {
        public final String timestamp;
        public final String comment;
        public final int totalCombinations;
        public final String fastestClass;
        public final long fastestMedianMs;
        public final double fastestIpc;
        public final Map<String, String> sysInfo;

        /**
         * Constructs a new RunSummary instance.
         */
        public RunSummary(String timestamp, String comment, int totalCombinations, String fastestClass, long fastestMedianMs, double fastestIpc, Map<String, String> sysInfo) {
            this.timestamp = timestamp;
            this.comment = comment;
            this.totalCombinations = totalCombinations;
            this.fastestClass = fastestClass;
            this.fastestMedianMs = fastestMedianMs;
            this.fastestIpc = fastestIpc;
            this.sysInfo = sysInfo;
        }
        
        public String getTimestamp() { return timestamp; }
        public String getComment() { return comment; }
        public int getTotalCombinations() { return totalCombinations; }
        public String getFastestClass() { return fastestClass; }
        public long getFastestMedianMs() { return fastestMedianMs; }
        public double getFastestIpc() { return fastestIpc; }
        public Map<String, String> getSysInfo() { return sysInfo; }
    }

    public static class HistoricalDataPoint {
        public final String timestamp;
        public final ResultMatrix.RowData data;

        /**
         * Constructs a new HistoricalDataPoint instance.
         */
        public HistoricalDataPoint(String timestamp, ResultMatrix.RowData data) {
            this.timestamp = timestamp;
            this.data = data;
        }

        public String getTimestamp() { return timestamp; }
        public ResultMatrix.RowData getData() { return data; }
    }

    public static class AggregateResult {
        public final List<RunSummary> summaries;
        public final Map<String, List<HistoricalDataPoint>> permutations;

        /**
         * Constructs a new AggregateResult instance.
         */
        public AggregateResult(List<RunSummary> summaries, Map<String, List<HistoricalDataPoint>> permutations) {
            this.summaries = summaries;
            this.permutations = permutations;
        }
    }

    /**
     * Helper method: aggregate.
     */
    public static AggregateResult aggregate() {
        List<RunSummary> summaries = new ArrayList<>();
        Map<String, List<HistoricalDataPoint>> permutations = new java.util.HashMap<>();
        
        Path historyDir = Paths.get("data", "benchmark-history");
        if (!Files.exists(historyDir)) return new AggregateResult(summaries, permutations);

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

                    Map<String, String> sysInfo = new LinkedHashMap<>();
                    Path sysInfoFile = historyDir.resolve(timestamp + "-sysinfo.txt");
                    if (Files.exists(sysInfoFile)) {
                        try {
                            List<String> lines = Files.readAllLines(sysInfoFile);
                            for (String line : lines) {
                                int idx = line.indexOf(":");
                                if (idx > 0) {
                                    sysInfo.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to parse sysinfo.txt: " + sysInfoFile);
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
                                    if (rd != null) {
                                        String permKey = env + " | " + ds + " | " + cls;
                                        permutations.computeIfAbsent(permKey, x -> new ArrayList<>())
                                                    .add(new HistoricalDataPoint(timestamp, rd));
                                        
                                        if (rd.medianRuntimeMs > 0 && rd.medianRuntimeMs < fastestMs) {
                                            fastestMs = rd.medianRuntimeMs;
                                            fastestClass = cls;
                                            fastestIpc = rd.ipc;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (fastestMs == Long.MAX_VALUE) fastestMs = 0;

                    summaries.add(new RunSummary(timestamp, comment, totalRuns, fastestClass, fastestMs, fastestIpc, sysInfo));

                } catch (Exception e) {
                    System.err.println("Error processing run summary for " + csvFile + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read history directory: " + e.getMessage());
        }

        summaries.sort(Comparator.comparing(RunSummary::getTimestamp).reversed());
        
        for (List<HistoricalDataPoint> list : permutations.values()) {
            list.sort(Comparator.comparing(p -> p.timestamp)); // Oldest first for charts
        }
        
        return new AggregateResult(summaries, permutations);
    }
}

