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
 * The central CLI entry point for the 1BRC Benchmark Harness.
 * 
 * This class handles routing for all major harness operations including:
 * <ul>
 *   <li>{@code generate}: Parses configurations and builds the execution shell scripts.</li>
 *   <li>{@code analyze}: Parses execution results and generates HTML/Markdown reports.</li>
 *   <li>{@code list-runs}: Validates and prints the combinations matrix without writing scripts.</li>
 *   <li>{@code compare-run}: Compares two historical execution runs.</li>
 * </ul>
 * 
 * @author Antigravity
 */
public class BenchmarkMatrix {

    /**
     * Main execution router. Validates arguments and dispatches to the appropriate operation handler.
     * 
     * @param args The command line arguments passed from the Maven invocation.
     * @throws IOException If any file-system operations fail during generation or analysis.
     */
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

    /**
     * Handles the 'generate' command.
     * 
     * Reads the configuration from `benchmark.conf`, scans the source directory for annotated classes,
     * and evaluates the run permutations. Generates the final execution shell script.
     * 
     * @param args Command-line arguments. Supports `--dry-run`, `--info`, `--jfr`, and `--comment <msg>`.
     * @throws IOException If parsing configurations or writing the script to disk fails.
     */
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

    /**
     * Handles the 'analyze' command.
     * 
     * Takes the results from a generated CSV file and merges them with historical metrics. It then uses
     * the FreeMarker and Markdown writers to generate the static HTML dashboard and tracking files.
     * 
     * @param args Expected to contain the timestamp of the run. If empty, scans history and processes all.
     * @throws IOException If file operations during analysis fail.
     */
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

    /**
     * Helper method to process a single benchmark run by its timestamp identifier.
     * Merges JFR data into the CSV, loads it into memory, and delegates to the report writers.
     * 
     * @param timestamp The unique execution run identifier.
     * @throws IOException If reading the CSV or writing the reports fails.
     */
    private static void processSingleRun(String timestamp) throws IOException {
        CsvMerger.merge(timestamp);
        
        ResultMatrix matrix = new ResultMatrix();
        matrix.loadCsv(Paths.get("data", "benchmark-history", timestamp + ".csv"));
        
        HtmlReportWriter.write(timestamp, matrix);
        MarkdownReportWriter.write(timestamp, matrix);
    }

    /**
     * Handles the 'list-runs' command.
     * 
     * Scans the `data/benchmark-history/` directory to identify all previous benchmark executions.
     * Prints a formatted table indicating the presence of scripts, CSVs, HTML, and MD reports.
     * 
     * @param args Unused command-line arguments.
     */
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

    /**
     * Compares two distinct benchmark runs to calculate performance regressions or improvements.
     * 
     * @param args The two timestamps to compare.
     */
    private static void compareRun(List<String> args) {
        // TODO: Implement compare-run
        System.out.println("Compare run not implemented yet.");
    }
}

