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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Antigravity
 */
public class OverviewWriter {

    public static void write() throws IOException {
        Path outPath = Paths.get("data", "benchmark-history", "index.html");

        HistoryAggregator.AggregateResult result = HistoryAggregator.aggregate();

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(OverviewWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Map<String, Object> root = new HashMap<>();
        root.put("summaries", result.summaries);
        root.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        // To feed ECharts, we want chronological order (oldest to newest)
        List<HistoryAggregator.RunSummary> chronological = new java.util.ArrayList<>(result.summaries);
        java.util.Collections.reverse(chronological);
        root.put("chronological", chronological);

        Template template = cfg.getTemplate("index.html.ftl");

        try (FileWriter out = new FileWriter(outPath.toFile())) {
            template.process(root, out);
            System.out.println("Generated Overview Dashboard: " + outPath);
        } catch (Exception e) {
            throw new IOException("Failed to process Freemarker template", e);
        }

        // Generate the static site for permutations
        PermutationWriter.write(result);
    }
}

