package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BenchmarkConfig {
    
    public final Map<String, JdkConfig> jdks = new LinkedHashMap<>();
    public final Map<String, String> gcOpts = new LinkedHashMap<>();
    public final Map<String, String> vmOpts = new LinkedHashMap<>();
    public final Map<String, String> progOpts = new LinkedHashMap<>();
    public final Map<String, String> tasksets = new LinkedHashMap<>();
    public final Map<String, DatasetConfig> datasets = new LinkedHashMap<>();
    public final List<RunDefinition> runs = new ArrayList<>();

    public static BenchmarkConfig load(Path configFile) throws IOException {
        BenchmarkConfig config = new BenchmarkConfig();
        if (!Files.exists(configFile)) return config;

        String currentSection = "";
        for (String line : Files.readAllLines(configFile)) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            if (line.startsWith("[") && line.endsWith("]")) {
                currentSection = line.substring(1, line.length() - 1).toUpperCase();
                continue;
            }

            String[] parts = line.split("=", 2);
            if (parts.length != 2) continue;

            String key = parts[0].trim();
            String val = parts[1].trim();

            switch (currentSection) {
                case "JDKS":
                    config.jdks.put(key, new JdkConfig(key, val));
                    break;
                case "GC_OPTS":
                    config.gcOpts.put(key, val);
                    break;
                case "VM_OPTS":
                    config.vmOpts.put(key, val);
                    break;
                case "PROG_OPTS":
                    config.progOpts.put(key, val);
                    break;
                case "TASKSETS":
                    config.tasksets.put(key, val);
                    break;
                case "DATASETS":
                    if (key.startsWith("DATASET_")) key = key.substring("DATASET_".length());
                    config.datasets.put(key, new DatasetConfig(key, val));
                    break;
                case "RUNS":
                    config.runs.add(new RunDefinition(key, val));
                    break;
            }
        }
        return config;
    }
}
