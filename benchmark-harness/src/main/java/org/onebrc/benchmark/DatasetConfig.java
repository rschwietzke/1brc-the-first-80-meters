package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
