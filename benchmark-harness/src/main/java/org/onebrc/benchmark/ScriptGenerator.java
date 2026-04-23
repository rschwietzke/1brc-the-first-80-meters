package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ScriptGenerator {

    private static List<String> getMatches(String filter, Set<String> available) {
        if (filter.equals("*")) {
            return new ArrayList<>(available);
        }
        return available.contains(filter) ? Collections.singletonList(filter) : Collections.emptyList();
    }

    private static boolean matchesClass(ClassConfig cls, String classFilter) {
        if (classFilter.equals("*")) return true;
        if (classFilter.endsWith(".*")) {
            String pkgPrefix = classFilter.substring(0, classFilter.length() - 2);
            return cls.fqcn.startsWith(pkgPrefix);
        }
        return cls.className.equals(classFilter) || cls.fqcn.equals(classFilter);
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
        public final String tasksetLabel;
        public final String taskset;
        public final String progLabel;
        public final String progOpts;
        public final String dataLabel;
        public final DatasetConfig dataConfig;

        public RunCombination(ClassConfig classConfig, String runName,
                              String jdkLabel, JdkConfig jdkConfig,
                              String gcLabel, String gcOpts,
                              String vmLabel, String vmOpts,
                              String tasksetLabel, String taskset,
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
            this.tasksetLabel = tasksetLabel;
            this.taskset = taskset;
            this.progLabel = progLabel;
            this.progOpts = progOpts;
            this.dataLabel = dataLabel;
            this.dataConfig = dataConfig;
        }
    }

    public static Path generate(List<ClassConfig> classes, BenchmarkConfig config,
                                boolean isJfr, boolean dryRun) throws IOException {

        List<RunCombination> validCombinations = new ArrayList<>();

        for (ClassConfig cls : classes) {
            if (cls.ignore) continue;

            for (RunDefinition runDef : config.runs) {
                if (!matchesClass(cls, runDef.classFilter)) continue;

                boolean excludedRun = cls.exclusions.stream().anyMatch(e -> e.equals("RUN:" + runDef.name));
                if (excludedRun) continue;

                boolean hasRunInclusions = cls.inclusions.stream().anyMatch(e -> e.startsWith("RUN:"));
                if (hasRunInclusions) {
                    boolean includedRun = cls.inclusions.stream().anyMatch(e -> e.equals("RUN:" + runDef.name));
                    if (!includedRun) continue; 
                }

                List<String> matchJdks = getMatches(runDef.jdkFilter, config.jdks.keySet());
                List<String> matchGcs = getMatches(runDef.gcFilter, config.gcOpts.keySet());
                List<String> matchVms = getMatches(runDef.vmFilter, config.vmOpts.keySet());
                List<String> matchTasksets = getMatches(runDef.tasksetFilter, config.tasksets.keySet());
                List<String> matchProgs = getMatches(runDef.progFilter, config.progOpts.keySet());
                List<String> matchDatasets = getMatches(runDef.dataFilter, config.datasets.keySet());

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
                            for (String tsL : matchTasksets) {
                                for (String progL : matchProgs) {
                                    for (String dsL : matchDatasets) {
                                        boolean excludedDs = cls.exclusions.stream().anyMatch(e -> e.equals("DATA:" + dsL));
                                        if (excludedDs) continue;

                                        validCombinations.add(new RunCombination(
                                                cls, runDef.name,
                                                jdkL, jdkConfig,
                                                gcL, config.gcOpts.get(gcL),
                                                vmL, config.vmOpts.get(vmL),
                                                tsL, config.tasksets.get(tsL),
                                                progL, config.progOpts.get(progL),
                                                dsL, config.datasets.get(dsL)
                                        ));
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

        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash\n\n");
        sb.append("echo \"Starting benchmark matrix run\"\n\n");
        
        sb.append("source $HOME/.sdkman/bin/sdkman-init.sh 2>/dev/null || true\n\n");

        sb.append("echo \"JDK,GC_OPTS,VM_OPTS,PROG_OPTS,TASKSET,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,Instructions,Cycles,Branches,BranchMisses,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys\" > data/benchmark-history/").append(timestamp).append(".csv\n\n");

        Map<JdkConfig, List<RunCombination>> groupedByJdk = new LinkedHashMap<>();
        for (RunCombination rc : validCombinations) {
            groupedByJdk.computeIfAbsent(rc.jdkConfig, k -> new ArrayList<>()).add(rc);
        }

        for (Map.Entry<JdkConfig, List<RunCombination>> entry : groupedByJdk.entrySet()) {
            JdkConfig jdk = entry.getKey();
            List<RunCombination> combos = entry.getValue();

            sb.append("echo \"==================================================\"\n");
            sb.append("echo \"JDK Block: ").append(jdk.label).append("\"\n");
            sb.append("echo \"==================================================\"\n");

            if (jdk.isSdkman) {
                String sdkVersion = jdk.pathOrSdkman.substring("sdkman:".length());
                sb.append("sdk install java ").append(sdkVersion).append("\n");
                sb.append("sdk use java ").append(sdkVersion).append("\n");
                sb.append("export JAVA_HOME=\"$SDKMAN_DIR/candidates/java/current\"\n");
            } else {
                sb.append("export JAVA_HOME=\"").append(jdk.pathOrSdkman).append("\"\n");
            }
            sb.append("export PATH=\"$JAVA_HOME/bin:$PATH\"\n\n");

            int releaseVersion = jdk.majorVersion > 0 ? jdk.majorVersion : 25; // fallback
            sb.append("echo \"Compiling for JDK release ").append(releaseVersion).append("\"\n");
            sb.append("mvn clean compile -pl 1brc-implementations -Dmaven.compiler.release=").append(releaseVersion).append("\n\n");

            for (RunCombination combo : combos) {
                String jvmOpts = (combo.gcOpts + " " + combo.vmOpts).trim();
                if (isJfr) {
                    Path jfrDir = Paths.get("data", "benchmark-jfr");
                    Files.createDirectories(jfrDir);
                    String sanitizedEnv = (combo.gcOpts + "_" + combo.vmOpts + "_" + combo.taskset).replaceAll("[^a-zA-Z0-9.-]", "_");
                    String jfrFile = jfrDir.resolve(timestamp + "-" + combo.classConfig.className + "-" + combo.jdkLabel + "-" + sanitizedEnv + "-" + combo.dataLabel + ".jfr").toString();
                    jvmOpts += " -XX:StartFlightRecording=filename=" + jfrFile + ",settings=profile";
                }

                sb.append("export CLASS=\"").append(combo.classConfig.fqcn).append("\"\n");
                sb.append("export DATA=\"").append(combo.dataConfig.path).append("\"\n");
                sb.append("export JVM_OPTS=\"").append(jvmOpts).append("\"\n");
                sb.append("export TASKSET=\"").append(combo.taskset).append("\"\n");
                sb.append("export PROG_OPTS=\"").append(combo.progOpts).append("\"\n");

                sb.append("echo \"Running ").append(combo.classConfig.className).append(" (Run: ").append(combo.runName).append(")\"\n");
                sb.append("echo -n \"").append(combo.jdkLabel).append(",")
                  .append("\\\"").append(combo.gcOpts).append("\\\",")
                  .append("\\\"").append(combo.vmOpts).append("\\\",")
                  .append("\\\"").append(combo.progOpts).append("\\\",")
                  .append("\\\"").append(combo.taskset).append("\\\",")
                  .append(combo.dataLabel).append(",")
                  .append(timestamp).append(",\" >> data/benchmark-history/").append(timestamp).append(".csv\n");
                sb.append("./execute-scenario.sh >> data/benchmark-history/").append(timestamp).append(".csv\n\n");
            }
        }

        // Post-processing
        sb.append("echo \"Analyzing results\"\n");
        sb.append("mvn -q exec:java -pl benchmark-harness -Dexec.mainClass=\"org.onebrc.benchmark.BenchmarkMatrix\" -Dexec.args=\"analyze ").append(timestamp).append("\"\n");

        Files.writeString(outPath, sb.toString());
        
        Set<PosixFilePermission> perms = new HashSet<>(Files.getPosixFilePermissions(outPath));
        perms.add(PosixFilePermission.OWNER_EXECUTE);
        Files.setPosixFilePermissions(outPath, perms);

        return outPath;
    }
}
