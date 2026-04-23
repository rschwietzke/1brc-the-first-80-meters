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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Antigravity
 */
public class BenchmarkMatrix {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: BenchmarkMatrix <command> [args]");
            System.err.println("Commands: generate, analyze, list-runs, compare-run");
            System.exit(1);
        }

        String command = args[0];
        List<String> commandArgs = Arrays.asList(args).subList(1, args.length);

        switch (command) {
            case "generate":
                generate(commandArgs);
                break;
            case "analyze":
                analyze(commandArgs);
                break;
            case "list-runs":
                listRuns(commandArgs);
                break;
            case "compare-run":
                compareRun(commandArgs);
                break;
            default:
                System.err.println("Unknown command: " + command);
                System.exit(1);
        }
    }

    private static void generate(List<String> args) throws IOException {
        boolean dryRun = args.contains("--dry-run");
        boolean isJfr = args.contains("--jfr");
        boolean isInfo = args.contains("--info");
        
        String comment = "";
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals("--comment") && i + 1 < args.size()) {
                comment = args.get(i + 1);
                break;
            }
        }

        Path srcDir = Paths.get("1brc-implementations", "src", "main", "java");
        List<ClassConfig> classes = SourceAnnotationParser.parseDirectory(srcDir);

        BenchmarkConfig config = BenchmarkConfig.load(Paths.get("benchmark.conf"));

        if (config.jdks.isEmpty() || config.runs.isEmpty()) {
            System.err.println("Error: Missing configuration or runs in benchmark.conf.");
            System.err.println("See benchmark.conf.example and create the config first.");
            System.exit(1);
        }

        Path scriptPath = ScriptGenerator.generate(classes, config, isJfr, dryRun, isInfo, comment);
        
        // Output the script path on the final line for benchmark-matrix.sh to pick up
        System.out.println(scriptPath.toAbsolutePath().toString());
    }

    private static void analyze(List<String> args) throws IOException {
        if (args.isEmpty()) {
            System.out.println("No timestamp provided. Scanning history and regenerating all reports...");
            Path historyDir = Paths.get("data", "benchmark-history");
            if (java.nio.file.Files.exists(historyDir)) {
                try (java.util.stream.Stream<Path> paths = java.nio.file.Files.list(historyDir)) {
                    paths.filter(p -> p.toString().endsWith(".csv") && !p.getFileName().toString().contains("-meta"))
                         .forEach(p -> {
                             String ts = p.getFileName().toString().replace(".csv", "");
                             try {
                                 processSingleRun(ts);
                             } catch (IOException e) {
                                 System.err.println("Failed to process run " + ts + ": " + e.getMessage());
                             }
                         });
                }
            }
        } else {
            String timestamp = args.get(0);
            processSingleRun(timestamp);
        }
        
        System.out.println("Generating Global Overview Dashboards...");
        OverviewWriter.write();
    }

    private static void processSingleRun(String timestamp) throws IOException {
        CsvMerger.merge(timestamp);
        
        ResultMatrix matrix = new ResultMatrix();
        matrix.loadCsv(Paths.get("data", "benchmark-history", timestamp + ".csv"));
        
        HtmlReportWriter.write(timestamp, matrix);
        MarkdownReportWriter.write(timestamp, matrix);
    }

    private static void listRuns(List<String> args) {
        List<ArchiveManager.RunArchive> runs = ArchiveManager.listRuns();
        if (runs.isEmpty()) {
            System.out.println("No benchmark runs found in data/benchmark-history/");
            return;
        }
        System.out.println("Timestamp       | Script | CSV | HTML | MD ");
        System.out.println("----------------+--------+-----+------+----");
        for (ArchiveManager.RunArchive r : runs) {
            System.out.printf("%-15s | %-6s | %-3s | %-4s | %-2s%n",
                    r.timestamp,
                    r.hasScript ? "Yes" : "No",
                    r.hasCsv ? "Yes" : "No",
                    r.hasHtml ? "Yes" : "No",
                    r.hasMd ? "Yes" : "No"
            );
        }
    }

    private static void compareRun(List<String> args) {
        // TODO: Implement compare-run
        System.out.println("Compare run not implemented yet.");
    }
}

