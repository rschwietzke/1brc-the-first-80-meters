package org.onebrc.benchmark;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BenchmarkDataLocator {
    
    public static final Path ROOT = Paths.get("data");

    public static Path getRunDir(String timestamp) {
        return ROOT.resolve(timestamp);
    }
    
    public static Path getRawDir(String timestamp) {
        return getRunDir(timestamp).resolve("raw");
    }

    public static Path getReportsDir(String timestamp) {
        return getRunDir(timestamp).resolve("reports");
    }

    public static Path getScriptsDir(String timestamp) {
        return getRunDir(timestamp).resolve("scripts");
    }

    public static Path getJfrDir(String timestamp) {
        return getRunDir(timestamp).resolve("jfr");
    }
    
    public static Path getCsvFile(String timestamp) {
        return getRawDir(timestamp).resolve("results.csv");
    }
    
    public static Path getMetaFile(String timestamp) {
        return getRawDir(timestamp).resolve("meta.json");
    }
    
    public static Path getSysInfoFile(String timestamp) {
        return getRawDir(timestamp).resolve("sysinfo.txt");
    }
    
    public static Path getRunScriptFile(String timestamp) {
        return getScriptsDir(timestamp).resolve("run.sh");
    }
    
    public static Path getHtmlReportFile(String timestamp) {
        return getReportsDir(timestamp).resolve("results.html");
    }
    
    public static Path getMdReportFile(String timestamp) {
        return getReportsDir(timestamp).resolve("results.md");
    }
    
    public static Path getPermutationsDir() {
        return ROOT.resolve("permutations");
    }
    
    public static Path getGlobalOverviewFile() {
        return ROOT.resolve("index.html");
    }
}
