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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the parsed benchmark configuration defining the execution matrix (JDKs, GCs, Bindings, etc.).
 * @author Antigravity
 */
public class BenchmarkConfig {
    
    /** Map of configured JDK installations, parsed from the [JDKS] block. */
    public final Map<String, JdkConfig> jdks = new LinkedHashMap<>();
    
    /** Map of Garbage Collector JVM flags, parsed from the [GC_OPTS] block. */
    public final Map<String, String> gcOpts = new LinkedHashMap<>();
    
    /** Map of general JVM performance flags, parsed from the [VM_OPTS] block. */
    public final Map<String, String> vmOpts = new LinkedHashMap<>();
    
    /** Map of Java application arguments, parsed from the [PROG_OPTS] block. */
    public final Map<String, String> progOpts = new LinkedHashMap<>();
    
    /** Map of OS-level execution prefixes (e.g., numactl, taskset), parsed from the [BINDINGS] block. */
    public final Map<String, String> bindings = new LinkedHashMap<>();
    
    /** Map of data file configurations (path + dimension definitions), parsed from the [DATASETS] block. */
    public final Map<String, DatasetConfig> datasets = new LinkedHashMap<>();
    
    /** Key-value store of globally defined interpolation variables (e.g., ITERATIONS=3). */
    public final Map<String, String> variables = new LinkedHashMap<>();
    
    /** Ordered list of explicitly defined execution scenarios to evaluate against the class annotations. */
    public final List<RunDefinition> runs = new ArrayList<>();

    /**
     * Parses the `benchmark.conf` file into a structured, in-memory configuration matrix.
     * Evaluates global variables and performs token substitution for dynamic pathing.
     * 
     * @param configFile The path to the configuration file.
     * @return A populated BenchmarkConfig object ready for permutation generation.
     * @throws IOException If the file is inaccessible or unreadable.
     */
    public static BenchmarkConfig load(Path configFile) throws IOException {
        BenchmarkConfig config = new BenchmarkConfig();
        if (!Files.exists(configFile)) return config;

        String currentSection = "";
        String currentRunName = null;
        Map<String, String> currentRunProps = null;

        List<String> lines = Files.readAllLines(configFile);
        // Parse the configuration file line-by-line
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            boolean isLastLine = (i == lines.size() - 1);

            // Handle section header transitions (e.g., [JDKS], [RUN:fast])
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

