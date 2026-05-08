package org.onebrc.benchmarkviewer.util;

import java.nio.file.Path;

/**
 * Utility class for resolving paths to benchmark data files in the viewer application.
 */
public class ViewerDataLocator {

    public static Path getRunDir(Path baseDir, String timestamp) {
        return baseDir.resolve(timestamp);
    }
    
    public static Path getRawDir(Path baseDir, String timestamp) {
        return getRunDir(baseDir, timestamp).resolve("raw");
    }

    public static Path getJfrDir(Path baseDir, String timestamp) {
        return getRunDir(baseDir, timestamp).resolve("jfr");
    }
    
    public static Path getCsvFile(Path baseDir, String timestamp) {
        return getRawDir(baseDir, timestamp).resolve("results.csv");
    }
    
    public static Path getMetaFile(Path baseDir, String timestamp) {
        return getRawDir(baseDir, timestamp).resolve("meta.json");
    }
    
    public static Path getSysInfoFile(Path baseDir, String timestamp) {
        return getRawDir(baseDir, timestamp).resolve("sysinfo.txt");
    }
}
