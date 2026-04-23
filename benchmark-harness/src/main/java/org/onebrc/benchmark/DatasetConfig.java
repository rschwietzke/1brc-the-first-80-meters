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
public class DatasetConfig {
    public final String label;
    public final String path;

    public DatasetConfig(String label, String path) {
        this.label = label;
        this.path = path;
    }

    public static Map<String, DatasetConfig> load(Path configFile) throws IOException {
        Map<String, DatasetConfig> configs = new LinkedHashMap<>();
        if (!Files.exists(configFile)) return configs;

        for (String line : Files.readAllLines(configFile)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                if (key.startsWith("DATASET_")) {
                    key = key.substring("DATASET_".length());
                }
                configs.put(key, new DatasetConfig(key, parts[1].trim()));
            }
        }
        return configs;
    }
}

