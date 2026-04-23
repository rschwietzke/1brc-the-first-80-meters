package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArchiveManager {

    public static class RunArchive implements Comparable<RunArchive> {
        public final String timestamp;
        public final boolean hasScript;
        public final boolean hasCsv;
        public final boolean hasHtml;
        public final boolean hasMd;

        public RunArchive(String timestamp, boolean hasScript, boolean hasCsv, boolean hasHtml, boolean hasMd) {
            this.timestamp = timestamp;
            this.hasScript = hasScript;
            this.hasCsv = hasCsv;
            this.hasHtml = hasHtml;
            this.hasMd = hasMd;
        }

        @Override
        public int compareTo(RunArchive o) {
            return o.timestamp.compareTo(this.timestamp); // Newest first
        }
    }

    public static List<RunArchive> listRuns() {
        Path historyDir = Paths.get("data", "benchmark-history");
        if (!Files.exists(historyDir)) return Collections.emptyList();

        try (Stream<Path> paths = Files.walk(historyDir, 1)) {
            List<String> timestamps = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(n -> n.endsWith("-run.sh"))
                    .map(n -> n.replace("-run.sh", ""))
                    .distinct()
                    .collect(Collectors.toList());

            List<RunArchive> archives = new ArrayList<>();
            for (String ts : timestamps) {
                boolean script = Files.exists(historyDir.resolve(ts + "-run.sh"));
                boolean csv = Files.exists(historyDir.resolve(ts + ".csv"));
                boolean html = Files.exists(historyDir.resolve(ts + ".html"));
                boolean md = Files.exists(historyDir.resolve(ts + ".md"));
                archives.add(new RunArchive(ts, script, csv, html, md));
            }
            Collections.sort(archives);
            return archives;
        } catch (IOException e) {
            System.err.println("Error reading history dir: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static String getMostRecentPriorTo(String currentTimestamp) {
        List<RunArchive> runs = listRuns();
        for (RunArchive run : runs) {
            if (run.timestamp.compareTo(currentTimestamp) < 0 && run.hasCsv) {
                return run.timestamp;
            }
        }
        return null; // No prior run found
    }
}
