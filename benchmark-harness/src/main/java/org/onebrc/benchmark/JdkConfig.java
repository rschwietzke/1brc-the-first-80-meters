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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Represents a logical Java Development Kit configuration.
 * Handles both absolute system paths and 'sdkman:' prefixed dynamic installations,
 * automatically determining the target release version for compilation.
 * 
 * @author Antigravity
 */
public class JdkConfig {
    /** The logical label representing this JDK in the config (e.g., 'JDK 21'). */
    public final String label;
    
    /** The absolute file path to the JDK, or the 'sdkman:' identifier string. */
    public final String pathOrSdkman;
    
    /** The detected major release version of the target JDK (e.g., 17, 21). */
    public final int majorVersion;
    
    /** True if this configuration relies on SDKMAN for dynamic installation and switching. */
    public final boolean isSdkman;

    /**
     * Constructs a new JdkConfig instance.
     */
    public JdkConfig(String label, String pathOrSdkman) {
        this.label = label;
        this.pathOrSdkman = pathOrSdkman;
        this.isSdkman = pathOrSdkman.startsWith("sdkman:");
        this.majorVersion = detectMajorVersion();
    }

    /**
     * Helper method: detectMajorVersion.
     */
    private int detectMajorVersion() {
        // If it's sdkman and not installed yet, we can try to guess from the version string (e.g. 21.0.1-graal -> 21)
        if (isSdkman) {
            String versionStr = pathOrSdkman.substring("sdkman:".length());
            Pattern p = Pattern.compile("^(\\d+)");
            Matcher m = p.matcher(versionStr);
            if (m.find()) {
                return Integer.parseInt(m.group(1));
            }
        } else {
            // Run path/bin/java -version
            try {
                Process p = new ProcessBuilder(pathOrSdkman + "/bin/java", "-version")
                        .redirectErrorStream(true)
                        .start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                Pattern pattern = Pattern.compile("version \"(\\d+)\\.?");
                while ((line = reader.readLine()) != null) {
                    Matcher m = pattern.matcher(line);
                    if (m.find()) {
                        return Integer.parseInt(m.group(1));
                    }
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not detect JDK version for " + label);
            }
        }
        return 0; // Unknown
    }

    /**
     * Helper method: load.
     */
    public static Map<String, JdkConfig> load(Path configFile) throws IOException {
        Map<String, JdkConfig> configs = new LinkedHashMap<>();
        if (!Files.exists(configFile)) return configs;
        
        for (String line : Files.readAllLines(configFile)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] parts = line.split("=", 2);
            if (parts.length == 2) {
                configs.put(parts[0].trim(), new JdkConfig(parts[0].trim(), parts[1].trim()));
            }
        }
        return configs;
    }
}

