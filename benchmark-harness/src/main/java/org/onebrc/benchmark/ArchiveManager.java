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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Antigravity
 */
public class ArchiveManager {

    public static class RunArchive implements Comparable<RunArchive> {
        public final String timestamp;
        public final boolean hasScript;
        public final boolean hasCsv;
        public final boolean hasHtml;
        public final boolean hasMd;

        /**
         * Constructs a new RunArchive instance.
         */
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

    /**
     * Helper method: listRuns.
     */
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

    /**
     * Helper method: getMostRecentPriorTo.
     */
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

