/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// AI-generated file: Gemini 3.5 Flash (Antigravity)

package org.onebrc.benchmarkviewer.controller;

import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.service.DataImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * REST API Controller for serving benchmark summary and comparison data in ECharts format,
 * and handling administrative commands such as data re-importing.
 */
@RestController
public class SummaryApiController
{
    private final TestRunRepository testRunRepository;
    private final MeasurementRepository measurementRepository;
    private final DataImportService dataImportService;
    private final String dataDir;

    public SummaryApiController(final TestRunRepository testRunRepository,
                                final MeasurementRepository measurementRepository,
                                final DataImportService dataImportService,
                                @Value("${benchmark.data.directory:data/benchmark-history}") final String dataDir)
    {
        this.testRunRepository = testRunRepository;
        this.measurementRepository = measurementRepository;
        this.dataImportService = dataImportService;
        this.dataDir = dataDir;
    }

    @PostMapping("/admin/reimport")
    public final ResponseEntity<Map<String, String>> reimport()
    {
        this.dataImportService.reimportAll(Path.of(this.dataDir));
        final Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Re-import complete!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/runs/{timestamp}/summary")
    public final ResponseEntity<Map<String, Object>> getRunSummary(@PathVariable("timestamp") final String timestamp)
    {
        final List<Measurement> measurements = this.getMeasurementsByTimestamp(timestamp);

        final TreeSet<String> classes = measurements.stream()
            .map(Measurement::getClassName)
            .collect(Collectors.toCollection(TreeSet::new));

        // Group measurements by environment configuration key
        final Map<String, List<Measurement>> envGroups = measurements.stream()
            .collect(Collectors.groupingBy(m -> 
                m.getJdk() + " | " + m.getGcOpts() + " | " + m.getVmOpts()
            ));

        final List<Map<String, Object>> series = new ArrayList<>();
        for (final Map.Entry<String, List<Measurement>> entry : envGroups.entrySet())
        {
            final String envName = entry.getKey();
            final Map<String, Double> classToRuntime = entry.getValue().stream()
                .collect(Collectors.toMap(Measurement::getClassName, Measurement::getMedianRuntimeMs, (r1, r2) -> r1));

            final List<Double> data = new ArrayList<>();
            for (final String cls : classes)
            {
                data.add(classToRuntime.getOrDefault(cls, 0.0));
            }

            final Map<String, Object> s = new HashMap<>();
            s.put("name", envName);
            s.put("type", "bar");
            s.put("data", data);
            series.add(s);
        }

        final Map<String, Object> result = new HashMap<>();
        result.put("categories", classes.stream()
            .map(cls -> cls.substring(cls.lastIndexOf('.') + 1))
            .collect(Collectors.toList()));
        result.put("series", series);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/runs/{timestamp}/compare/jdk")
    public final ResponseEntity<Map<String, Object>> getJdkCompare(@PathVariable("timestamp") final String timestamp)
    {
        final List<Measurement> measurements = this.getMeasurementsByTimestamp(timestamp);

        final TreeSet<String> classes = measurements.stream()
            .map(Measurement::getClassName)
            .collect(Collectors.toCollection(TreeSet::new));

        // Group by JDK
        final Map<String, List<Measurement>> jdkGroups = measurements.stream()
            .collect(Collectors.groupingBy(Measurement::getJdk));

        final List<Map<String, Object>> series = new ArrayList<>();
        for (final Map.Entry<String, List<Measurement>> entry : jdkGroups.entrySet())
        {
            final String jdkName = entry.getKey();
            
            // Average runtime per class for this JDK
            final Map<String, Double> classToAvgRuntime = entry.getValue().stream()
                .collect(Collectors.groupingBy(
                    Measurement::getClassName,
                    Collectors.averagingDouble(Measurement::getMedianRuntimeMs)
                ));

            final List<Double> data = new ArrayList<>();
            for (final String cls : classes)
            {
                data.add(classToAvgRuntime.getOrDefault(cls, 0.0));
            }

            final Map<String, Object> s = new HashMap<>();
            s.put("name", jdkName);
            s.put("type", "bar");
            s.put("data", data);
            series.add(s);
        }

        final Map<String, Object> result = new HashMap<>();
        result.put("categories", classes.stream()
            .map(cls -> cls.substring(cls.lastIndexOf('.') + 1))
            .collect(Collectors.toList()));
        result.put("series", series);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/runs/{timestamp}/compare/gc")
    public final ResponseEntity<Map<String, Object>> getGcCompare(@PathVariable("timestamp") final String timestamp)
    {
        final List<Measurement> measurements = this.getMeasurementsByTimestamp(timestamp);

        final TreeSet<String> classes = measurements.stream()
            .map(Measurement::getClassName)
            .collect(Collectors.toCollection(TreeSet::new));

        // Group by GC
        final Map<String, List<Measurement>> gcGroups = measurements.stream()
            .collect(Collectors.groupingBy(Measurement::getGcOpts));

        final List<Map<String, Object>> series = new ArrayList<>();
        for (final Map.Entry<String, List<Measurement>> entry : gcGroups.entrySet())
        {
            final String gcName = entry.getKey().isBlank() ? "Default GC" : entry.getKey();
            
            // Average runtime per class for this GC
            final Map<String, Double> classToAvgRuntime = entry.getValue().stream()
                .collect(Collectors.groupingBy(
                    Measurement::getClassName,
                    Collectors.averagingDouble(Measurement::getMedianRuntimeMs)
                ));

            final List<Double> data = new ArrayList<>();
            for (final String cls : classes)
            {
                data.add(classToAvgRuntime.getOrDefault(cls, 0.0));
            }

            final Map<String, Object> s = new HashMap<>();
            s.put("name", gcName);
            s.put("type", "bar");
            s.put("data", data);
            series.add(s);
        }

        final Map<String, Object> result = new HashMap<>();
        result.put("categories", classes.stream()
            .map(cls -> cls.substring(cls.lastIndexOf('.') + 1))
            .collect(Collectors.toList()));
        result.put("series", series);

        return ResponseEntity.ok(result);
    }

    private List<Measurement> getMeasurementsByTimestamp(final String timestamp)
    {
        final LocalDateTime dateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        final TestRun run = this.testRunRepository.findByTimestamp(dateTime)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Run not found for timestamp: " + timestamp));

        return this.measurementRepository.findByTestRun(run);
    }
}
