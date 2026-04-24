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
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Generates the execution scripts based on the active permutation matrix.
 * It uses FreeMarker to dynamically build the bash scripts.
 * @author Antigravity
 */
public class ScriptGenerator {

    private static List<String> getMatches(String filter, Set<String> available) {
        if (filter.equals("*")) {
            return new ArrayList<>(available);
        }
        List<String> matched = new ArrayList<>();
        Pattern p = null;
        try {
            p = Pattern.compile(filter);
        } catch (PatternSyntaxException e) {
            // Not a valid regex, fallback to exact match only
        }
        for (String item : available) {
            if (item.equals(filter) || (p != null && p.matcher(item).matches())) {
                matched.add(item);
            }
        }
        return matched;
    }

    private static boolean matchesClass(ClassConfig cls, String classFilter) {
        if (classFilter.equals("*")) return true;
        if (classFilter.endsWith(".*")) {
            String pkgPrefix = classFilter.substring(0, classFilter.length() - 2);
            if (cls.fqcn.startsWith(pkgPrefix)) return true;
        }
        if (cls.className.equals(classFilter) || cls.fqcn.equals(classFilter)) {
            return true;
        }
        try {
            Pattern p = Pattern.compile(classFilter);
            if (p.matcher(cls.className).matches() || p.matcher(cls.fqcn).matches()) {
                return true;
            }
        } catch (PatternSyntaxException e) {
            // Ignore
        }
        return false;
    }

    public static class RunCombination {
        public final ClassConfig classConfig;
        public final String runName;
        public final String jdkLabel;
        public final JdkConfig jdkConfig;
        public final String gcLabel;
        public final String gcOpts;
        public final String vmLabel;
        public final String vmOpts;
        public final String bindingLabel;
        public final String binding;
        public final String progLabel;
        public final String progOpts;
        public final String dataLabel;
        public final DatasetConfig dataConfig;

        public RunCombination(ClassConfig classConfig, String runName,
                              String jdkLabel, JdkConfig jdkConfig,
                              String gcLabel, String gcOpts,
                              String vmLabel, String vmOpts,
                              String bindingLabel, String binding,
                              String progLabel, String progOpts,
                              String dataLabel, DatasetConfig dataConfig) {
            this.classConfig = classConfig;
            this.runName = runName;
            this.jdkLabel = jdkLabel;
            this.jdkConfig = jdkConfig;
            this.gcLabel = gcLabel;
            this.gcOpts = gcOpts;
            this.vmLabel = vmLabel;
            this.vmOpts = vmOpts;
            this.bindingLabel = bindingLabel;
            this.binding = binding;
            this.progLabel = progLabel;
            this.progOpts = progOpts;
            this.dataLabel = dataLabel;
            this.dataConfig = dataConfig;
        }
    }

    /**
     * Represents a grouped block of executions for a specific JDK version.
     * This is used by the FreeMarker template to group compilations and avoid redundant JDK switching.
     */
    public static class JdkBlock {
        public final JdkConfig jdk;
        public final String sdkVersion;
        public final int releaseVersion;
        public final String mavenCmd;
        public final List<ComboView> combos;

        public JdkBlock(JdkConfig jdk, String sdkVersion, int releaseVersion, String mavenCmd, List<ComboView> combos) {
            this.jdk = jdk;
            this.sdkVersion = sdkVersion;
            this.releaseVersion = releaseVersion;
            this.mavenCmd = mavenCmd;
            this.combos = combos;
        }

        public JdkConfig getJdk() { return jdk; }
        public String getSdkVersion() { return sdkVersion; }
        public int getReleaseVersion() { return releaseVersion; }
        public String getMavenCmd() { return mavenCmd; }
        public List<ComboView> getCombos() { return combos; }
    }

    /**
     * Represents a fully resolved execution permutation (a single "run" of the benchmark).
     * Exposes flattened properties for easy consumption by the FreeMarker shell template.
     */
    public static class ComboView {
        public final String runName;
        public final String jdkLabel;
        public final String gcOpts;
        public final String vmOpts;
        public final String binding;
        public final String progOpts;
        public final String dataLabel;
        public final String className;
        public final String simpleClassName;
        public final String data;
        public final String jvmOpts;
        public final String jfrFile;

        public ComboView(RunCombination rc, String jfrFile) {
            this.runName = rc.runName;
            this.jdkLabel = rc.jdkLabel;
            this.gcOpts = rc.gcOpts;
            this.vmOpts = rc.vmOpts;
            this.binding = rc.binding;
            this.progOpts = rc.progOpts;
            this.dataLabel = rc.dataLabel;
            this.className = rc.classConfig.fqcn;
            this.simpleClassName = rc.classConfig.className;
            this.data = rc.dataConfig.path;
            this.jvmOpts = (rc.gcOpts + " " + rc.vmOpts).trim();
            this.jfrFile = jfrFile;
        }

        public String getRunName() { return runName; }
        public String getJdkLabel() { return jdkLabel; }
        public String getGcOpts() { return gcOpts; }
        public String getVmOpts() { return vmOpts; }
        public String getBinding() { return binding; }
        public String getProgOpts() { return progOpts; }
        public String getDataLabel() { return dataLabel; }
        public String getClassName() { return className; }
        public String getSimpleClassName() { return simpleClassName; }
        public String getData() { return data; }
        public String getJvmOpts() { return jvmOpts; }
        public String getJfrFile() { return jfrFile; }
    }

    /**
     * Evaluates all permutations and generates the execution bash script.
     */
    public static Path generate(List<ClassConfig> classes, BenchmarkConfig config,
                                boolean isJfr, boolean dryRun, boolean isInfo, String comment) throws IOException {

        System.out.println("Starting Benchmark Matrix Generation...");
        System.out.println("Loaded Configurations:");
        System.out.printf("  JDKs:       %d%n", config.jdks.size());
        System.out.printf("  GC Configs: %d%n", config.gcOpts.size());
        System.out.printf("  VM Configs: %d%n", config.vmOpts.size());
        System.out.printf("  Prog Opts:  %d%n", config.progOpts.size());
        System.out.printf("  Bindings:   %d%n", config.bindings.size());
        System.out.printf("  Datasets:   %d%n", config.datasets.size());
        System.out.printf("  Run Matrix: %d explicit runs defined%n", config.runs.size());
        System.out.printf("  Classes:    %d active (%d disabled)%n", 
            classes.stream().filter(c -> !c.ignore).count(),
            classes.stream().filter(c -> c.ignore).count());

        System.out.println("\nEvaluating Run Matrix combinations...");

        List<RunCombination> validCombinations = new ArrayList<>();

        for (RunDefinition runDef : config.runs) {
            System.out.printf("  Run: %s%n", runDef.name);

            List<String> matchJdks = getMatches(runDef.jdkFilter, config.jdks.keySet());
            List<String> matchGcs = getMatches(runDef.gcFilter, config.gcOpts.keySet());
            List<String> matchVms = getMatches(runDef.vmFilter, config.vmOpts.keySet());
            List<String> matchBindings = getMatches(runDef.bindingFilter, config.bindings.keySet());
            List<String> matchProgs = getMatches(runDef.progFilter, config.progOpts.keySet());
            List<String> matchDatasets = getMatches(runDef.dataFilter, config.datasets.keySet());

            long matchingClasses = classes.stream()
                .filter(c -> !c.ignore)
                .filter(c -> matchesClass(c, runDef.classFilter))
                .count();

            System.out.printf("    Matches -> JDKs: %d, GCs: %d, VMs: %d, Bindings: %d, Progs: %d, Datasets: %d, Classes: %d%n",
                matchJdks.size(), matchGcs.size(), matchVms.size(),
                matchBindings.size(), matchProgs.size(), matchDatasets.size(), matchingClasses);

            if (matchJdks.isEmpty()) System.out.printf("    ! Warning: JDK filter '%s' matched nothing.%n", runDef.jdkFilter);
            if (matchGcs.isEmpty()) System.out.printf("    ! Warning: GC filter '%s' matched nothing.%n", runDef.gcFilter);
            if (matchVms.isEmpty()) System.out.printf("    ! Warning: VM filter '%s' matched nothing.%n", runDef.vmFilter);
            if (matchBindings.isEmpty()) System.out.printf("    ! Warning: Binding filter '%s' matched nothing.%n", runDef.bindingFilter);
            if (matchProgs.isEmpty()) System.out.printf("    ! Warning: Prog filter '%s' matched nothing.%n", runDef.progFilter);
            if (matchDatasets.isEmpty()) System.out.printf("    ! Warning: Dataset filter '%s' matched nothing.%n", runDef.dataFilter);
            if (matchingClasses == 0) System.out.printf("    ! Warning: Class filter '%s' matched nothing.%n", runDef.classFilter);

            for (ClassConfig cls : classes) {
                if (cls.ignore) continue;
                if (!matchesClass(cls, runDef.classFilter)) continue;

                boolean excludedRun = cls.exclusions.stream().anyMatch(e -> e.equals("RUN:" + runDef.name));
                if (excludedRun) continue;

                boolean hasRunInclusions = cls.inclusions.stream().anyMatch(e -> e.startsWith("RUN:"));
                if (hasRunInclusions) {
                    boolean includedRun = cls.inclusions.stream().anyMatch(e -> e.equals("RUN:" + runDef.name));
                    if (!includedRun) continue;
                }

                for (String jdkL : matchJdks) {
                    JdkConfig jdkConfig = config.jdks.get(jdkL);
                    boolean excludedJdk = cls.exclusions.stream().anyMatch(e -> {
                        if (e.startsWith("JDK:")) {
                            String excludedVal = e.substring("JDK:".length());
                            return excludedVal.equals(jdkL) || excludedVal.equals(String.valueOf(jdkConfig.majorVersion));
                        }
                        return false;
                    });
                    if (excludedJdk) continue;

                    for (String gcL : matchGcs) {
                        for (String vmL : matchVms) {
                            for (String bdL : matchBindings) {
                                for (String progL : matchProgs) {
                                    for (String dsL : matchDatasets) {
                                        boolean excludedDs = cls.exclusions.stream().anyMatch(e -> e.equals("DATA:" + dsL));
                                        if (excludedDs) continue;

                                        RunCombination rc = new RunCombination(
                                                cls, runDef.name,
                                                jdkL, jdkConfig,
                                                gcL, config.gcOpts.get(gcL),
                                                vmL, config.vmOpts.get(vmL),
                                                bdL, config.bindings.get(bdL),
                                                progL, config.progOpts.get(progL),
                                                dsL, config.datasets.get(dsL)
                                        );
                                        validCombinations.add(rc);
                                        
                                        if (dryRun || isInfo) {
                                            String bindingPrefix = rc.binding.isEmpty() ? "" : (rc.binding + " ");
                                            String jvmArgs = (rc.gcOpts + " " + rc.vmOpts).trim();
                                            System.out.printf("      -> CMD: %sjava %s -cp target/classes %s %s %s%n",
                                                    bindingPrefix, jvmArgs, rc.classConfig.fqcn, rc.dataConfig.path, rc.progOpts);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        System.out.printf("Total Valid Run Combinations Scheduled: %d%n", validCombinations.size());

        if (dryRun) {
            return Paths.get("dry-run.sh");
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path outPath = Paths.get("data", "benchmark-history", timestamp + "-run.sh");
        Files.createDirectories(outPath.getParent());
        
        if (!dryRun) {
            String safeComment = comment == null ? "" : comment.replace("\"", "\\\"");
            String metaJson = String.format("{\n  \"timestamp\": \"%s\",\n  \"totalRuns\": %d,\n  \"comment\": \"%s\"\n}", 
                                           timestamp, validCombinations.size(), safeComment);
            Files.writeString(Paths.get("data", "benchmark-history", timestamp + "-meta.json"), metaJson);
        }

        Map<JdkConfig, List<RunCombination>> groupedByJdk = new LinkedHashMap<>();
        for (RunCombination rc : validCombinations) {
            groupedByJdk.computeIfAbsent(rc.jdkConfig, k -> new ArrayList<>()).add(rc);
        }

        List<JdkBlock> jdkBlocks = new ArrayList<>();
        for (Map.Entry<JdkConfig, List<RunCombination>> entry : groupedByJdk.entrySet()) {
            JdkConfig jdk = entry.getKey();
            List<RunCombination> combos = entry.getValue();

            String sdkVersion = "";
            if (jdk.isSdkman) {
                sdkVersion = jdk.pathOrSdkman.substring("sdkman:".length());
            }

            int releaseVersion = jdk.majorVersion > 0 ? jdk.majorVersion : 25;
            String mavenCmd = "mvn clean compile -pl 1brc-implementations -Dmaven.compiler.source=" + releaseVersion + " -Dmaven.compiler.target=" + releaseVersion;
            if (releaseVersion == 21) {
                mavenCmd += " -Pjdk21-preview";
            }

            List<ComboView> comboViews = new ArrayList<>();
            for (RunCombination combo : combos) {
                Path jfrDir = Paths.get("data", "benchmark-jfr");
                Files.createDirectories(jfrDir);
                String sanitizedEnv = (combo.gcOpts + "_" + combo.vmOpts + "_" + combo.binding).replaceAll("[^a-zA-Z0-9.-]", "_");
                String jfrFile = jfrDir.resolve(timestamp + "-" + combo.classConfig.className + "-" + combo.jdkLabel + "-" + sanitizedEnv + "-" + combo.dataLabel + ".jfr").toString();
                
                comboViews.add(new ComboView(combo, jfrFile));
            }

            jdkBlocks.add(new JdkBlock(jdk, sdkVersion, releaseVersion, mavenCmd, comboViews));
        }

        Map<String, Object> model = new HashMap<>();
        model.put("timestamp", timestamp);
        model.put("totalRuns", validCombinations.size());
        model.put("iterations", config.variables.getOrDefault("ITERATIONS", "3"));
        model.put("jdkBlocks", jdkBlocks);

        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
            cfg.setClassForTemplateLoading(ScriptGenerator.class, "/templates");
            cfg.setDefaultEncoding("UTF-8");
            
            freemarker.template.Template template = cfg.getTemplate("run-script.sh.ftl");
            
            try (java.io.FileWriter writer = new java.io.FileWriter(outPath.toFile())) {
                template.process(model, writer);
            }
        } catch (freemarker.template.TemplateException e) {
            throw new IOException("Failed to generate shell script from FreeMarker template", e);
        }

        Set<PosixFilePermission> perms = new HashSet<>(Files.getPosixFilePermissions(outPath));
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        Files.setPosixFilePermissions(outPath, perms);

        return outPath;
    }
}

