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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

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
        // Parse CLI flags defining execution mode
        boolean dryRun = args.contains("--dry-run");
        boolean isJfr = args.contains("--jfr");
        boolean isInfo = args.contains("--info");
        
        // Extract optional run comment
        String comment = "";
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals("--comment") && i + 1 < args.size()) {
                comment = args.get(i + 1);
                break;
            }
        }

        // Step 1: Discover and parse all benchmarking annotations from source code
        Path srcDir = Paths.get("1brc-implementations", "src", "main", "java");
        List<ClassConfig> classes = SourceAnnotationParser.parseDirectory(srcDir);

        // Step 2: Load the base execution configurations (JDKs, GCs, Data files)
        BenchmarkConfig config = BenchmarkConfig.load(Paths.get("benchmark.conf"));

        // Validate that we have actionable configurations
        if (config.jdks.isEmpty() || config.runs.isEmpty()) {
            System.err.println("Error: Missing configuration or runs in benchmark.conf.");
            System.err.println("See benchmark.conf.example and create the config first.");
            System.exit(1);
        }

        // Step 3: Compute the Cartesian product and build the FreeMarker script
        Path scriptPath = ScriptGenerator.generate(classes, config, isJfr, dryRun, isInfo, comment);
        
        // Output the script path on the final line for benchmark-matrix.sh to pick up
        System.out.println(scriptPath.toAbsolutePath().toString());
    }

    /**
     * Handles the 'analyze' command.
     * 
     * Takes the results from a generated CSV file and merges them with historical metrics. It then uses
     * the FreeMarker and Markdown writers to generate the static HTML dashboard and tracking files.
     * Supports --output-report to write an additional Markdown copy to a specified path.
     * 
     * @param args Expected to contain the timestamp of the run and optional --output-report flag.
     * @throws IOException If file operations during analysis fail.
     */
    private static void analyze(List<String> args) throws IOException {
        // Extract optional --output-report path
        Path outputReportPath = extractArgValue(args, "--output-report");

        // Filter out flag arguments to find the timestamp
        String timestamp = null;
        for (String arg : args) {
            if (!arg.startsWith("--") && timestamp == null) {
                timestamp = arg;
            }
        }

        if (timestamp == null) {
            // Mode A: No specific timestamp provided, trigger a full history rebuild
            System.out.println("No timestamp provided. Scanning history and regenerating all reports...");
            Path historyDir = Paths.get("data", "benchmark-history");
            if (Files.exists(historyDir)) {
                // Find all raw CSV result files, excluding the metadata tracking files
                try (Stream<Path> paths = Files.list(historyDir)) {
                    paths.filter(p -> p.toString().endsWith(".csv") && !p.getFileName().toString().contains("-meta"))
                         .forEach(p -> {
                             // Extract the raw timestamp from the filename and process it
                             String ts = p.getFileName().toString().replace(".csv", "");
                             try {
                                 processSingleRun(ts, null);
                             } catch (IOException e) {
                                 System.err.println("Failed to process run " + ts + ": " + e.getMessage());
                             }
                         });
                }
            }
        } else {
            // Mode B: Specifically process the requested timestamp run
            processSingleRun(timestamp, outputReportPath);
        }
        
        // Finally, trigger the generation of the global overview and aggregate dashboards
        System.out.println("Generating Global Overview Dashboards...");
        OverviewWriter.write();
    }

    /**
     * Helper method to process a single benchmark run by its timestamp identifier.
     * Merges JFR data into the CSV, loads it into memory, and delegates to the report writers.
     * 
     * @param timestamp The unique execution run identifier.
     * @param outputReportPath Optional additional output path for the Markdown report. May be null.
     * @throws IOException If reading the CSV or writing the reports fails.
     */
    private static void processSingleRun(String timestamp, Path outputReportPath) throws IOException {
        CsvMerger.merge(timestamp);
        
        ResultMatrix matrix = new ResultMatrix();
        matrix.loadCsv(Paths.get("data", "benchmark-history", timestamp + ".csv"));
        
        HtmlReportWriter.write(timestamp, matrix);
        MarkdownReportWriter.write(timestamp, matrix, outputReportPath);
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
     * Handles the 'compare-run' command.
     *
     * Loads two historical benchmark runs, compares their machine fingerprints to determine
     * whether deltas represent genuine regressions or cross-machine trends, and prints a
     * detailed comparison table with per-permutation delta percentages.
     *
     * Usage: compare-run <current-timestamp> [<baseline-timestamp>]
     * If baseline-timestamp is omitted, the most recent prior run is auto-selected.
     *
     * @param args The timestamps to compare.
     */
    private static void compareRun(List<String> args) {
        if (args.isEmpty()) {
            System.err.println("Usage: compare-run <current-timestamp> [<baseline-timestamp>]");
            System.exit(1);
        }

        final String currentTs = args.get(0);
        final Path historyDir = Paths.get("data", "benchmark-history");

        // Filter out flag arguments to find the baseline timestamp (if provided)
        String foundBaselineTs = null;
        for (int i = 1; i < args.size(); i++) {
            if (!args.get(i).startsWith("--") && foundBaselineTs == null && !args.get(i - 1).equals("--output-report")) {
                foundBaselineTs = args.get(i);
            }
        }

        final String baselineTs;
        if (foundBaselineTs != null) {
            baselineTs = foundBaselineTs;
        } else {
            String auto = ArchiveManager.getMostRecentPriorTo(currentTs);
            if (auto == null) {
                System.out.println("No prior benchmark run found for comparison. Skipping cross-run analysis.");
                return;
            }
            baselineTs = auto;
            System.out.println("Auto-selected baseline run: " + baselineTs);
        }

        Path outputReportPath = extractArgValue(args, "--output-report");

        // Validate that both CSVs exist
        final Path currentCsv = historyDir.resolve(currentTs + ".csv");
        final Path baselineCsv = historyDir.resolve(baselineTs + ".csv");
        if (!Files.exists(currentCsv)) {
            System.err.println("Error: Current run CSV not found: " + currentCsv);
            return;
        }
        if (!Files.exists(baselineCsv)) {
            System.err.println("Error: Baseline run CSV not found: " + baselineCsv);
            return;
        }

        try {
            // Load both result matrices
            final ResultMatrix currentMatrix = new ResultMatrix();
            currentMatrix.loadCsv(currentCsv);

            final ResultMatrix baselineMatrix = new ResultMatrix();
            baselineMatrix.loadCsv(baselineCsv);

            // Load and compare machine fingerprints
            final Path currentSysInfo = historyDir.resolve(currentTs + "-sysinfo.txt");
            final Path baselineSysInfo = historyDir.resolve(baselineTs + "-sysinfo.txt");

            final MachineFingerprint currentFp = MachineFingerprint.fromSysInfo(
                    MachineFingerprint.parseSysInfoFile(currentSysInfo));
            final MachineFingerprint baselineFp = MachineFingerprint.fromSysInfo(
                    MachineFingerprint.parseSysInfoFile(baselineSysInfo));

            final MachineMatch match = currentFp.compare(baselineFp);

            // Print header
            System.out.println();
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("  Cross-Run Comparison: " + currentTs + " vs " + baselineTs);
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println();

            if (match == MachineMatch.SAME) {
                System.out.println("  Machine: SAME (hostname, kernel, CPU all match)");
                System.out.println("  Deltas are labeled as regressions ⚠️  or improvements ✅");
            } else {
                System.out.println("  ⚠️  Machine: DIFFERENT");
                System.out.println("  Current:  " + currentFp);
                System.out.println("  Baseline: " + baselineFp);
                System.out.println("  Deltas are labeled as trends only (📈/📉) — interpret with caution.");
            }
            System.out.println();

            // Build the union of all keys across both matrices
            final Set<ResultMatrix.Key> allKeys = new TreeSet<>((a, b) -> {
                int c = a.className.compareTo(b.className);
                if (c != 0) return c;
                c = a.jdk.compareTo(b.jdk);
                if (c != 0) return c;
                c = a.data.compareTo(b.data);
                if (c != 0) return c;
                c = a.gcOpts.compareTo(b.gcOpts);
                if (c != 0) return c;
                c = a.vmOpts.compareTo(b.vmOpts);
                if (c != 0) return c;
                c = a.progOpts.compareTo(b.progOpts);
                if (c != 0) return c;
                return a.binding.compareTo(b.binding);
            });
            allKeys.addAll(currentMatrix.getKeys());
            allKeys.addAll(baselineMatrix.getKeys());

            // Print comparison table
            System.out.printf("  %-50s | %-12s | %-12s | %-8s | %-12s | %s%n",
                    "Class", "JDK", "Data", "Current", "Baseline", "Delta");
            System.out.println("  " + "-".repeat(110));

            int regressions = 0;
            int improvements = 0;
            int newCombos = 0;
            int retiredCombos = 0;

            for (final ResultMatrix.Key key : allKeys) {
                final ResultMatrix.RowData current = currentMatrix.get(key);
                final ResultMatrix.RowData baseline = baselineMatrix.get(key);

                final String shortClass = key.className.contains(".")
                        ? key.className.substring(key.className.lastIndexOf('.') + 1)
                        : key.className;

                if (current != null && baseline != null) {
                    // Both runs have this combination — compute delta
                    final long curMs = current.medianRuntimeMs;
                    final long baseMs = baseline.medianRuntimeMs;

                    String deltaStr;
                    if (baseMs == 0) {
                        deltaStr = "N/A (baseline=0)";
                    } else {
                        final double deltaPct = ((double)(curMs - baseMs) / baseMs) * 100.0;
                        if (match == MachineMatch.SAME) {
                            if (deltaPct > 5.0) {
                                deltaStr = String.format("+%.1f%% ⚠️", deltaPct);
                                regressions++;
                            } else if (deltaPct < -5.0) {
                                deltaStr = String.format("%.1f%% ✅", deltaPct);
                                improvements++;
                            } else {
                                deltaStr = String.format("%.1f%%", deltaPct);
                            }
                        } else {
                            if (deltaPct > 5.0) {
                                deltaStr = String.format("+%.1f%% 📈", deltaPct);
                                regressions++;
                            } else if (deltaPct < -5.0) {
                                deltaStr = String.format("%.1f%% 📉", deltaPct);
                                improvements++;
                            } else {
                                deltaStr = String.format("%.1f%%", deltaPct);
                            }
                        }
                    }

                    System.out.printf("  %-50s | %-12s | %-12s | %6d ms | %6d ms | %s%n",
                            shortClass, key.jdk, key.data, curMs, baseMs, deltaStr);

                } else if (current != null) {
                    // New combination not in baseline
                    newCombos++;
                    System.out.printf("  %-50s | %-12s | %-12s | %6d ms | %10s | %s%n",
                            shortClass, key.jdk, key.data, current.medianRuntimeMs, "—", "— (new)");

                } else {
                    // Retired combination not in current run
                    retiredCombos++;
                    System.out.printf("  %-50s | %-12s | %-12s | %10s | %6d ms | %s%n",
                            shortClass, key.jdk, key.data, "—", baseline.medianRuntimeMs, "(retired)");
                }
            }

            // Print summary
            System.out.println();
            System.out.println("  Summary: " + allKeys.size() + " total combinations, "
                    + regressions + " regression(s), " + improvements + " improvement(s), "
                    + newCombos + " new, " + retiredCombos + " retired");
            System.out.println();

            // Generate reports with cross-run data
            HtmlReportWriter.write(currentTs, currentMatrix, baselineMatrix, match);
            MarkdownReportWriter.write(currentTs, currentMatrix, outputReportPath);
            System.out.println("  Generated HTML/MD reports incorporating baseline comparison.");
            System.out.println();

        } catch (final IOException e) {
            System.err.println("Error during comparison: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Extracts the value following a named flag argument (e.g., --output-report path).
     * Returns null if the flag is not present.
     */
    private static Path extractArgValue(List<String> args, String flagName) {
        for (int i = 0; i < args.size(); i++) {
            if (args.get(i).equals(flagName) && i + 1 < args.size()) {
                return Paths.get(args.get(i + 1));
            }
        }
        return null;
    }
}

