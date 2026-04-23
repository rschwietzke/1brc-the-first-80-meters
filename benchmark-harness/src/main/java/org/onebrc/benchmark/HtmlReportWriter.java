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

/**
 * @author Antigravity
 */
public class HtmlReportWriter {

    public static void write(String timestamp, ResultMatrix matrix) throws IOException {
        Path outPath = Paths.get("data", "benchmark-history", timestamp + ".html");

        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);
        cfg.setClassForTemplateLoading(HtmlReportWriter.class, "/templates");
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        Map<String, Object> root = new HashMap<>();
        root.put("timestamp", timestamp);

        java.util.Map<String, String> sysInfo = new java.util.LinkedHashMap<>();
        Path sysInfoFile = Paths.get("data", "benchmark-history", timestamp + "-sysinfo.txt");
        if (java.nio.file.Files.exists(sysInfoFile)) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(sysInfoFile);
                for (String line : lines) {
                    int idx = line.indexOf(":");
                    if (idx > 0) {
                        sysInfo.put(line.substring(0, idx).trim(), line.substring(idx + 1).trim());
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to parse sysinfo.txt: " + sysInfoFile);
            }
        }
        root.put("sysInfo", sysInfo);

        Set<String> datasets = matrix.getDatasets();
        Set<String> classes = matrix.getClasses();
        Set<String> environments = matrix.getEnvironments();

        root.put("datasets", datasets);
        root.put("classes", classes);
        root.put("environments", environments);

        Map<String, ResultMatrix.RowData> flatMatrix = new HashMap<>();
        for (String ds : datasets) {
            for (String cls : classes) {
                for (String env : environments) {
                    String[] parts = env.split(" \\| ", -1);
                    if (parts.length < 5) continue; // Safety check
                    ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, cls);
                    ResultMatrix.RowData rd = matrix.get(k);
                    if (rd != null) {
                        flatMatrix.put(env + " | " + ds + " | " + cls, rd);
                    }
                }
            }
        }
        root.put("matrix", flatMatrix);

        Path srcDir = Paths.get("1brc-implementations", "src", "main", "java");
        java.util.List<ClassConfig> classConfigs = SourceAnnotationParser.parseDirectory(srcDir);
        Map<String, String> classStatuses = new HashMap<>();
        for (ClassConfig cc : classConfigs) {
            classStatuses.put(cc.className, cc.status);
        }
        root.put("classStatuses", classStatuses);

        Map<String, String> baselineChecksums = new HashMap<>();
        for (String ds : datasets) {
            for (String env : environments) {
                String baselineClass = null;
                for (String cls : classes) {
                    if ("baseline".equals(classStatuses.get(cls))) {
                        baselineClass = cls;
                        break;
                    }
                }
                if (baselineClass != null) {
                    String[] parts = env.split(" \\| ", -1);
                    if (parts.length >= 5) {
                        ResultMatrix.Key k = new ResultMatrix.Key(parts[0], parts[1], parts[2], parts[3], parts[4], ds, baselineClass);
                        ResultMatrix.RowData rd = matrix.get(k);
                        if (rd != null && rd.checksum != null && !rd.checksum.equals("ERROR")) {
                            baselineChecksums.put(env + " | " + ds, rd.checksum);
                        }
                    }
                }
            }
        }

        com.google.gson.Gson gson = new com.google.gson.Gson();
        String jsonPayload = gson.toJson(flatMatrix);
        root.put("jsonData", jsonPayload);
        root.put("classStatusesJson", gson.toJson(classStatuses));
        root.put("baselineChecksumsJson", gson.toJson(baselineChecksums));

        Template template = cfg.getTemplate("report.html.ftl");

        try (FileWriter out = new FileWriter(outPath.toFile())) {
            template.process(root, out);
            System.out.println("Generated HTML report: " + outPath);
        } catch (Exception e) {
            throw new IOException("Failed to process Freemarker template", e);
        }
    }
}

