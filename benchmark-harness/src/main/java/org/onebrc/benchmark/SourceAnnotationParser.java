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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Antigravity
 */
public class SourceAnnotationParser {
    
    private static final Pattern EXCLUSION_PATTERN = Pattern.compile("^\\s*//\\s*-([A-Z]+):(.+)$");
    private static final Pattern INCLUSION_PATTERN = Pattern.compile("^\\s*//\\s*([A-Z]+):(.+)$");
    private static final Pattern STATUS_PATTERN = Pattern.compile("^\\s*//\\s*status:\\s*(.+)$");
    private static final Pattern IGNORE_PATTERN = Pattern.compile("^\\s*//\\s*ignore\\s*$");
    private static final Pattern PACKAGE_PATTERN = Pattern.compile("^\\s*package\\s+([a-zA-Z0-9_.]+)\\s*;");

    public static List<ClassConfig> parseDirectory(Path srcDir) throws IOException {
        List<ClassConfig> configs = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(srcDir)) {
            paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .forEach(p -> {
                     try {
                         configs.add(parseFile(p));
                     } catch (IOException e) {
                         throw new RuntimeException("Failed to parse " + p, e);
                     }
                 });
        }
        return configs;
    }

    public static ClassConfig parseFile(Path file) throws IOException {
        String className = file.getFileName().toString().replace(".java", "");
        List<String> lines = Files.readAllLines(file);
        
        String pkg = "";
        for (String line : lines) {
            Matcher m = PACKAGE_PATTERN.matcher(line);
            if (m.find()) {
                pkg = m.group(1);
                break;
            }
        }
        String fqcn = pkg.isEmpty() ? className : pkg + "." + className;
        ClassConfig config = new ClassConfig(className, fqcn);

        for (String line : lines) {
            if (IGNORE_PATTERN.matcher(line).find()) {
                config.ignore = true;
            }
            Matcher statusMatcher = STATUS_PATTERN.matcher(line);
            if (statusMatcher.find()) {
                config.status = statusMatcher.group(1).trim();
            }
            Matcher excMatcher = EXCLUSION_PATTERN.matcher(line);
            if (excMatcher.find()) {
                String dim = excMatcher.group(1).trim();
                String val = excMatcher.group(2).trim();
                config.exclusions.add(dim + ":" + val);
            }
            Matcher incMatcher = INCLUSION_PATTERN.matcher(line);
            if (incMatcher.find()) {
                String dim = incMatcher.group(1).trim();
                String val = incMatcher.group(2).trim();
                config.inclusions.add(dim + ":" + val);
            }
        }
        return config;
    }
}

