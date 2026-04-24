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

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordingFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.nio.file.Files;

/**
 * @author Antigravity
 */
public class JfrConsumer {

    public static class JfrMetrics {
        public long totalGcPauseMs = 0;
        public long totalAllocatedBytes = 0;
        public long totalJitCompilationMs = 0;
    }

    /**
     * Helper method: consume.
     */
    public static JfrMetrics consume(Path jfrFile) {
        JfrMetrics metrics = new JfrMetrics();
        if (jfrFile == null || !Files.exists(jfrFile)) {
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

