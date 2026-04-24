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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.nio.file.Files;
import java.util.List;
import com.google.gson.Gson;

/**
 * Responsible for generating the primary interactive HTML dashboard for a benchmark run.
 * 
 * Uses FreeMarker templates to inject the multi-dimensional {@link ResultMatrix} and the
 * captured system/hardware telemetry into a static HTML report.
 * 
 * @author Antigravity
 */
public class HtmlReportWriter {

    /**
     * Injects the parsed matrix data into the {@code report.html.ftl} template and writes it to disk.
     * Also parses the companion {@code -sysinfo.txt} file to display the execution environment hardware.
     * 
     * @param timestamp The unique identifier string for the execution run.
     * @param matrix The fully parsed {@link ResultMatrix} containing all permutations and telemetry.
     * @throws IOException If the FreeMarker template cannot be loaded or the output file cannot be written.
     */
    public static void write(final String timestamp, final ResultMatrix matrix) throws IOException {
        final Path outPath = Paths.get("data", "benchmark-history", timestamp + ".html");

        final Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(HtmlReportWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        final Map<String, Object> root = new HashMap<>();
        root.put("timestamp", timestamp);

        // Initialize the sysinfo map and attempt to load hardware telemetry
        final Map<String, String> sysInfo = new LinkedHashMap<>();
        final Path sysInfoFile = Paths.get("data", "benchmark-history", timestamp + "-sysinfo.txt");
        if (Files.exists(sysInfoFile)) {
            try {
                final List<String> lines = Files.readAllLines(sysInfoFile);
                // Parse the key-value pairs (e.g. 'CPU Cores: 16') from the sysinfo file
                for (final String line : lines) {
                    final int idx = line.indexOf(":");
                    if (idx > 0) {
                        sysInfo.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
                    }
                }
            } catch (final Exception e) {
                System.err.println("Failed to parse sysinfo.txt: " + sysInfoFile);
            }
        }
        root.put("sysInfo", sysInfo);

        final Set<String> datasets = matrix.getDatasets();
        final Set<String> classes = matrix.getClasses();
        final Set<String> environments = matrix.getEnvironments();

        root.put("datasets", datasets);
        root.put("classes", classes);
        root.put("environments", environments);

        // Flatten the multidimensional ResultMatrix into a flat String-keyed map.
        // This makes it significantly easier to query from within the FreeMarker template engine.
        final Map<String, ResultMatrix.RowData> flatMatrix = new HashMap<>();
        for (final String ds : datasets) {
            for (final String cls : classes) {
                for (final String env : environments) {
                    // Extract the individual permutation dimensions (JDK, GC, VM, ProgOpts, Binding)
                    final String[] parts = env.split(" \\| ", -1);
                    if (parts.length < 5) continue; // Safety check
                    
                    final ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, cls);
                    final ResultMatrix.RowData rd = matrix.get(k);
                    if (rd != null) {
                        // Store the data using a composite key: "Environment | Dataset | Class"
                        flatMatrix.put(env + " | " + ds + " | " + cls, rd);
                    }
                }
            }
        }
        root.put("matrix", flatMatrix);

        // Reparse the source directory to extract the current status (baseline/incomplete/complete)
        // for each class so we can visually distinct them in the HTML report.
        final Path srcDir = Paths.get("1brc-implementations", "src", "main", "java");
        final List<ClassConfig> classConfigs = SourceAnnotationParser.parseDirectory(srcDir);
        final Map<String, String> classStatuses = new HashMap<>();
        for (final ClassConfig cc : classConfigs) {
            classStatuses.put(cc.className, cc.status);
        }
        root.put("classStatuses", classStatuses);

        // Extract the baseline checksums for each Dataset/Environment combination.
        // This is used by the frontend to highlight executions that produce incorrect output hashes.
        final Map<String, String> baselineChecksums = new HashMap<>();
        for (final String ds : datasets) {
            for (final String env : environments) {
                // First, identify which class is designated as the 'baseline'
                String baselineClass = null;
                for (final String cls : classes) {
                    if ("baseline".equals(classStatuses.get(cls))) {
                        baselineClass = cls;
                        break;
                    }
                }
                // If a baseline class is found, grab its checksum for this specific environment+dataset
                if (baselineClass != null) {
                    final String[] parts = env.split(" \\| ", -1);
                    if (parts.length >= 5) {
                        final ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, baselineClass);
                        final ResultMatrix.RowData rd = matrix.get(k);
                        // Ignore errored out runs when determining the correct baseline checksum
                        if (rd != null && rd.checksum != null && !rd.checksum.equals("ERROR")) {
                            baselineChecksums.put(env + " | " + ds, rd.checksum);
                        }
                    }
                }
            }
        }

        final Gson gson = new Gson();
        final String jsonPayload = gson.toJson(flatMatrix);
        root.put("jsonData", jsonPayload);
        root.put("classStatusesJson", gson.toJson(classStatuses));
        root.put("baselineChecksumsJson", gson.toJson(baselineChecksums));

        final Template template = cfg.getTemplate("report.html.ftl");

        try (final FileWriter out = new FileWriter(outPath.toFile())) {
            template.process(root, out);
            System.out.println("Generated HTML report: " + outPath);
        } catch (final Exception e) {
            throw new IOException("Failed to process Freemarker template", e);
        }
    }
}

