package org.onebrc.benchmark;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;

public class JfrConsumer {

    public static class JfrMetrics {
        public long totalGcPauseMs = 0;
        public long totalAllocatedBytes = 0;
        public long totalJitCompilationMs = 0;
    }

    public static JfrMetrics consume(Path jfrFile) {
        JfrMetrics metrics = new JfrMetrics();
        if (jfrFile == null || !java.nio.file.Files.exists(jfrFile)) {
            return metrics;
        }

        try (RecordingFile recordingFile = new RecordingFile(jfrFile)) {
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent event = recordingFile.readEvent();
                String eventName = event.getEventType().getName();

                if ("jdk.GarbageCollection".equals(eventName)) {
                    Duration duration = event.getDuration();
                    if (duration != null) {
                        metrics.totalGcPauseMs += duration.toMillis();
                    }
                } else if ("jdk.ObjectAllocationInNewTLAB".equals(eventName) ||
                           "jdk.ObjectAllocationOutsideTLAB".equals(eventName)) {
                    if (event.hasField("allocationSize")) {
                        metrics.totalAllocatedBytes += event.getLong("allocationSize");
                    }
                } else if ("jdk.Compilation".equals(eventName)) {
                    Duration duration = event.getDuration();
                    if (duration != null) {
                        metrics.totalJitCompilationMs += duration.toMillis();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: Failed to parse JFR file " + jfrFile + " - " + e.getMessage());
        }

        return metrics;
    }
}
