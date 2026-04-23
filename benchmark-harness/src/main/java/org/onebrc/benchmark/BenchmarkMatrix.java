package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
            System.err.println("Error: analyze requires a timestamp argument.");
            System.exit(1);
        }
        String timestamp = args.get(0);
        CsvMerger.merge(timestamp);
        
        ResultMatrix matrix = new ResultMatrix();
        matrix.loadCsv(Paths.get("data", "benchmark-history", timestamp + ".csv"));
        
        HtmlReportWriter.write(timestamp, matrix);
        MarkdownReportWriter.write(timestamp, matrix);
        OverviewWriter.write();
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
