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

// AI-generated file: Gemini 3.1 Pro (High)

package org.onebrc.benchmarkviewer.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.transaction.annotation.Transactional;

@Service
public class DataImportService
{
    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    private final TestRunRepository testRunRepository;
    private final MeasurementRepository measurementRepository;
    public DataImportService(final TestRunRepository testRunRepository, final MeasurementRepository measurementRepository)
    {
        this.testRunRepository = testRunRepository;
        this.measurementRepository = measurementRepository;
    }

    @Transactional
    public void importDirectory(final Path directory)
    {
        if (!Files.exists(directory) || !Files.isDirectory(directory))
        {
            log.warn("Directory {} does not exist", directory);
            return;
        }

        try (final Stream<Path> paths = Files.walk(directory, 1))
        {
            paths.filter(p -> p.toString().endsWith("-meta.json"))
                 .forEach(this::importRun);
        }
        catch (final IOException e)
        {
            log.error("Failed to scan directory", e);
        }
    }

    private void importRun(final Path metaJsonPath)
    {
        final String baseName = metaJsonPath.getFileName().toString().replace("-meta.json", "");
        final Path dir = metaJsonPath.getParent();
        
        final Path sysinfoPath = dir.resolve(baseName + "-sysinfo.txt");
        final Path csvPath = dir.resolve(baseName + ".csv");

        if (!Files.exists(sysinfoPath) || !Files.exists(csvPath))
        {
            log.warn("Missing files for run {}", baseName);
            return;
        }

        try
        {
            final String metaJson = Files.readString(metaJsonPath);
            final TestRun tempRun = new TestRun();
            parseMeta(tempRun, metaJson);
            
            if (tempRun.getTimestamp() == null)
            {
                log.warn("No timestamp found in {}", metaJsonPath);
                return;
            }

            // Skip if already exists
            if (this.testRunRepository.findByTimestamp(tempRun.getTimestamp()).isPresent())
            {
                log.info("Run {} already imported, skipping.", tempRun.getTimestamp());
                return;
            }

            final TestRun run = new TestRun();
            run.setTimestamp(tempRun.getTimestamp());
            run.setTotalRuns(tempRun.getTotalRuns());
            run.setComment(tempRun.getComment());

            parseSysinfo(run, Files.readString(sysinfoPath));
            
            // Save run first to get ID
            this.testRunRepository.save(run);

            final List<Measurement> measurements = parseCsv(run, Files.readString(csvPath));
            this.measurementRepository.saveAll(measurements);

            log.info("Imported {} measurements for run {}", measurements.size(), run.getTimestamp());
        }
        catch (final IOException e)
        {
            log.error("Failed to import run {}", baseName, e);
        }
    }

    void parseSysinfo(final TestRun run, final String sysinfoContent)
    {
        if (sysinfoContent == null)
        {
            return;
        }
        
        final String[] lines = sysinfoContent.split("\\r?\\n");
        for (final String line : lines)
        {
            final int colonIdx = line.indexOf(':');
            if (colonIdx > 0)
            {
                final String key = line.substring(0, colonIdx).trim();
                final String value = line.substring(colonIdx + 1).trim();

                switch (key)
                {
                    case "Hostname" -> run.setHostname(value);
                    case "Kernel" -> run.setKernelVersion(value);
                    case "OS" -> run.setOs(value);
                    case "CPU" -> run.setCpu(value);
                    case "Cores" -> {
                        try
                        {
                            run.setCpuCores(Integer.parseInt(value));
                        }
                        catch (final NumberFormatException e)
                        {
                            log.warn("Failed to parse Cores value: {}", value);
                        }
                    }
                    case "Memory" -> run.setMemory(value);
                }
            }
        }
    }

    void parseMeta(final TestRun run, final String jsonContent)
    {
        if (jsonContent == null || jsonContent.isBlank())
        {
            return;
        }

        try
        {
            // Simple parsing for flat JSON
            final String ts = extractJsonStringValue(jsonContent, "timestamp");
            if (ts != null)
            {
                run.setTimestamp(LocalDateTime.parse(ts, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            final String runs = extractJsonNumberValue(jsonContent, "totalRuns");
            if (runs != null)
            {
                run.setTotalRuns(Integer.parseInt(runs));
            }
            final String comment = extractJsonStringValue(jsonContent, "comment");
            if (comment != null)
            {
                run.setComment(comment);
            }
        }
        catch (final Exception e)
        {
            log.error("Failed to parse meta json", e);
        }
    }

    private String extractJsonStringValue(final String json, final String key)
    {
        final String search = "\"" + key + "\":";
        int idx = json.indexOf(search);
        if (idx < 0) return null;
        idx = json.indexOf("\"", idx + search.length());
        if (idx < 0) return null;
        final int endIdx = json.indexOf("\"", idx + 1);
        if (endIdx < 0) return null;
        return json.substring(idx + 1, endIdx);
    }

    private String extractJsonNumberValue(final String json, final String key)
    {
        final String search = "\"" + key + "\":";
        final int idx = json.indexOf(search);
        if (idx < 0) return null;
        int startIdx = idx + search.length();
        while (startIdx < json.length() && Character.isWhitespace(json.charAt(startIdx))) startIdx++;
        int endIdx = startIdx;
        while (endIdx < json.length() && (Character.isDigit(json.charAt(endIdx)) || json.charAt(endIdx) == '.' || json.charAt(endIdx) == '-')) endIdx++;
        if (startIdx == endIdx) return null;
        return json.substring(startIdx, endIdx);
    }

    List<Measurement> parseCsv(final TestRun run, final String csvContent)
    {
        final List<Measurement> measurements = new ArrayList<>();
        if (csvContent == null || csvContent.isBlank())
        {
            return measurements;
        }

        try (final CSVParser parser = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .build()
                .parse(new StringReader(csvContent)))
        {
            for (final CSVRecord record : parser)
            {
                final Measurement m = new Measurement();
                m.setTestRun(run);

                // Dimensions
                m.setJdk(record.get("JDK"));
                m.setGcOpts(record.get("GC_OPTS"));
                m.setVmOpts(record.get("VM_OPTS"));
                m.setProgOpts(record.get("PROG_OPTS"));
                m.setBinding(record.isMapped("BINDING") ? record.get("BINDING") : record.get("TASKSET")); // Fallback
                m.setDataset(record.get("DATA"));
                m.setRunTimestamp(record.get("RunTimestamp"));
                m.setClassName(record.get("Class"));

                // Error
                final String checksum = record.get("Checksum");
                m.setError("ERROR".equals(checksum));

                // Metrics
                m.setMedianRuntimeMs(parseDouble(record.get("MedianRuntimeMs")));
                m.setPerfRuntimeMs(parseDouble(record.get("PerfRuntimeMs")));
                m.setJfrRuntimeMs(parseDouble(record.get("JfrRuntimeMs")));
                m.setInstructions(parseLong(record.get("Instructions")));
                m.setCycles(parseLong(record.get("Cycles")));
                m.setBranches(parseLong(record.get("Branches")));
                m.setBranchMisses(parseLong(record.get("BranchMisses")));
                m.setL1Misses(parseLong(record.get("L1Misses")));
                m.setLlcMisses(parseLong(record.get("LLCMisses")));
                m.setPageFaults(parseLong(record.get("PageFaults")));
                m.setTaskClock(parseLong(record.get("TaskClock")));
                m.setContextSwitches(parseLong(record.get("ContextSwitches")));
                m.setCpuMigrations(parseLong(record.get("CpuMigrations")));
                m.setIpc(parseDouble(record.get("IPC")));
                m.setSecElapsed(parseDouble(record.get("SecElapsed")));
                m.setSecUser(parseDouble(record.get("SecUser")));
                m.setSecSys(parseDouble(record.get("SecSys")));

                measurements.add(m);
            }
        }
        catch (final Exception e)
        {
            log.error("Failed to parse CSV", e);
        }

        return measurements;
    }

    private double parseDouble(final String value)
    {
        if (value == null || value.isBlank())
        {
            return 0.0;
        }
        try
        {
            return Double.parseDouble(value);
        }
        catch (final NumberFormatException e)
        {
            return 0.0;
        }
    }

    private long parseLong(final String value)
    {
        if (value == null || value.isBlank())
        {
            return 0L;
        }
        try
        {
            return Long.parseLong(value);
        }
        catch (final NumberFormatException e)
        {
            return 0L;
        }
    }
}
