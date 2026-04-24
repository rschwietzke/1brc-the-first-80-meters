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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Generates static HTML files for historical performance permutations.
 * @author Antigravity
 */
public class PermutationWriter {

    /**
     * Generates a short, filesystem-safe deterministic hash for a permutation key.
     * This ensures the generated HTML report filenames do not exceed OS path limits or contain illegal characters.
     * 
     * @param input The raw permutation key (e.g., 'JDK 21 | G1GC | SerialReader').
     * @return A hexadecimal string representing the hash.
     */
    private static String hash(String input) {
        return Integer.toHexString(input.hashCode());
    }

    /**
     * Writes static HTML dashboard files summarizing the historical performance of specific execution permutations.
     * Generates both individual historical charts per permutation and a master index directory file.
     * 
     * @param result The aggregated historical dataset spanning multiple run configurations.
     * @throws IOException If FreeMarker templates cannot be loaded or files cannot be written to disk.
     */
    public static void write(HistoryAggregator.AggregateResult result) throws IOException {
        Path permutationsDir = Paths.get("data", "benchmark-history", "permutations");
        if (!Files.exists(permutationsDir)) {
            Files.createDirectories(permutationsDir);
        }

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(PermutationWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Template permTemplate = cfg.getTemplate("permutation.html.ftl");
        Template indexTemplate = cfg.getTemplate("index-permutations.html.ftl");

        String generatedAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        List<Map<String, String>> indexEntries = new ArrayList<>();

        for (Map.Entry<String, List<HistoryAggregator.HistoricalDataPoint>> entry : result.permutations.entrySet()) {
            String permutationKey = entry.getKey();
            List<HistoryAggregator.HistoricalDataPoint> history = entry.getValue();
            
            // Extract class name (the last segment of the key)
            String[] parts = permutationKey.split(" \\| ");
            String className = parts[parts.length - 1];
            
            String fileId = "history-" + hash(permutationKey);
            String filename = fileId + ".html";
            Path outPath = permutationsDir.resolve(filename);

            Map<String, Object> root = new HashMap<>();
            root.put("permutationKey", permutationKey);
            root.put("className", className);
            root.put("history", history);
            root.put("generatedAt", generatedAt);

            try (FileWriter out = new FileWriter(outPath.toFile())) {
                permTemplate.process(root, out);
            } catch (Exception e) {
                System.err.println("Failed to generate permutation HTML for " + permutationKey + ": " + e.getMessage());
            }

            Map<String, String> indexEntry = new HashMap<>();
            indexEntry.put("key", permutationKey);
            indexEntry.put("className", className);
            indexEntry.put("filename", filename);
            indexEntries.add(indexEntry);
        }

        // Generate the permutations index
        // Group by class name for easier browsing
        indexEntries.sort(Comparator.comparing(e -> e.get("className")));
        Map<String, List<Map<String, String>>> groupedEntries = new LinkedHashMap<>();
        for (Map<String, String> e : indexEntries) {
            groupedEntries.computeIfAbsent(e.get("className"), k -> new ArrayList<>()).add(e);
        }

        Template classIndexTemplate = cfg.getTemplate("index-class.html.ftl");
        for (Map.Entry<String, List<Map<String, String>>> entry : groupedEntries.entrySet()) {
            String className = entry.getKey();
            Path classIndexOutPath = permutationsDir.resolve("index-" + className + ".html");
            Map<String, Object> classIndexRoot = new HashMap<>();
            classIndexRoot.put("className", className);
            classIndexRoot.put("permutations", entry.getValue());
            classIndexRoot.put("generatedAt", generatedAt);
            try (FileWriter out = new FileWriter(classIndexOutPath.toFile())) {
                classIndexTemplate.process(classIndexRoot, out);
            } catch (Exception e) {
                System.err.println("Failed to generate class permutations index HTML for " + className + ": " + e.getMessage());
            }
        }

        Path indexOutPath = permutationsDir.resolve("index.html");
        Map<String, Object> indexRoot = new HashMap<>();
        indexRoot.put("classes", new ArrayList<>(groupedEntries.keySet()));
        indexRoot.put("generatedAt", generatedAt);
        indexRoot.put("groupedPermutations", groupedEntries); // Keep for backwards compatibility if needed in template

        try (FileWriter out = new FileWriter(indexOutPath.toFile())) {
            indexTemplate.process(indexRoot, out);
        } catch (Exception e) {
            System.err.println("Failed to generate permutations index HTML: " + e.getMessage());
        }

        System.out.println("Generated " + result.permutations.size() + " dedicated permutation pages, and " + groupedEntries.size() + " class index pages.");
    }
}

