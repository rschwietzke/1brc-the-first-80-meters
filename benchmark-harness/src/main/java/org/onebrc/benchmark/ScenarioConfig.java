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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Antigravity
 */
public class ScenarioConfig {
    public final String label;
    public final String jvmOpts;
    public final String taskset;
    public final String args;

    public ScenarioConfig(String label, String valueString) {
        this.label = label;
        // Parse SCENARIO_<LABEL>="JVM_OPTS=...; TASKSET=...; ARGS=..."
        String parsedJvm = "";
        String parsedTaskset = "";
        String parsedArgs = "";
        
        // Remove quotes if present
        if (valueString.startsWith("\"") && valueString.endsWith("\"")) {
            valueString = valueString.substring(1, valueString.length() - 1);
        }
        
        String[] parts = valueString.split(";");
        for (String part : parts) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                String k = kv[0].trim();
                String v = kv[1].trim();
                if (k.equals("JVM_OPTS")) parsedJvm = v;
                if (k.equals("TASKSET")) parsedTaskset = v;
                if (k.equals("ARGS")) parsedArgs = v;
            }
        }
        this.jvmOpts = parsedJvm;
        this.taskset = parsedTaskset;
        this.args = parsedArgs;
    }

    public static Map<String, ScenarioConfig> load(Path configFile) throws IOException {
        Map<String, ScenarioConfig> configs = new LinkedHashMap<>();
        if (!Files.exists(configFile)) return configs;

        for (String line : Files.readAllLines(configFile)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                if (key.startsWith("SCENARIO_")) {
                    key = key.substring("SCENARIO_".length());
                }
                configs.put(key, new ScenarioConfig(key, parts[1].trim()));
            }
        }
        return configs;
    }
}

