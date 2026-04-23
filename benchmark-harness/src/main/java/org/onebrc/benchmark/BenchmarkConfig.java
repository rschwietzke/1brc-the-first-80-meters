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
import java.util.*;

/**
 * @author Antigravity
 */
public class BenchmarkConfig {
    
    public final Map<String, JdkConfig> jdks = new LinkedHashMap<>();
    public final Map<String, String> gcOpts = new LinkedHashMap<>();
    public final Map<String, String> vmOpts = new LinkedHashMap<>();
    public final Map<String, String> progOpts = new LinkedHashMap<>();
    public final Map<String, String> bindings = new LinkedHashMap<>();
    public final Map<String, DatasetConfig> datasets = new LinkedHashMap<>();
    public final Map<String, String> variables = new LinkedHashMap<>();
    public final List<RunDefinition> runs = new ArrayList<>();

    public static BenchmarkConfig load(Path configFile) throws IOException {
        BenchmarkConfig config = new BenchmarkConfig();
        if (!Files.exists(configFile)) return config;

        String currentSection = "";
        String currentRunName = null;
        Map<String, String> currentRunProps = null;

        List<String> lines = Files.readAllLines(configFile);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            boolean isLastLine = (i == lines.size() - 1);

            if (line.startsWith("[") && line.endsWith("]")) {
                if (currentRunName != null) {
                    config.runs.add(new RunDefinition(currentRunName, currentRunProps));
                    currentRunName = null;
                    currentRunProps = null;
                }

                currentSection = line.substring(1, line.length() - 1).toUpperCase();
                if (currentSection.startsWith("RUN:")) {
                    currentRunName = currentSection.substring(4).trim();
                    currentRunProps = new LinkedHashMap<>();
                }
                continue;
            }

            if (!line.isEmpty() && !line.startsWith("#")) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0].trim().toUpperCase();
                    String rawVal = parts[1].trim();
                    
                    // Variable substitution
                    String val = rawVal;
                    for (Map.Entry<String, String> entry : config.variables.entrySet()) {
                        val = val.replace("${" + entry.getKey() + "}", entry.getValue())
                                 .replace("$" + entry.getKey(), entry.getValue());
                    }

                    if (currentRunProps != null) {
                        currentRunProps.put(key, val);
                    } else {
                        switch (currentSection) {
                            case "":
                                config.variables.put(key, val); // Store global variables
                                break;
                            case "JDKS":
                                config.jdks.put(parts[0].trim(), new JdkConfig(parts[0].trim(), val));
                                break;
                            case "GC_OPTS":
                                config.gcOpts.put(parts[0].trim(), val);
                                break;
                            case "VM_OPTS":
                                config.vmOpts.put(parts[0].trim(), val);
                                break;
                            case "PROG_OPTS":
                                config.progOpts.put(parts[0].trim(), val);
                                break;
                            case "TASKSETS":
                            case "BINDINGS":
                                config.bindings.put(parts[0].trim(), val);
                                break;
                            case "DATASETS":
                                String originalKey = parts[0].trim();
                                if (originalKey.startsWith("DATASET_")) originalKey = originalKey.substring("DATASET_".length());
                                config.datasets.put(originalKey, new DatasetConfig(originalKey, val));
                                break;
                            case "VARIABLES":
                                config.variables.put(key, val);
                                break;
                        }
                    }
                }
            }

            if (isLastLine && currentRunName != null) {
                config.runs.add(new RunDefinition(currentRunName, currentRunProps));
            }
        }
        return config;
    }
}

