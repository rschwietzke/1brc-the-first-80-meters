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

public class JdkConfig {
    public final String label;
    public final String pathOrSdkman;
    public final int majorVersion;
    public final boolean isSdkman;

    public JdkConfig(String label, String pathOrSdkman) {
        this.label = label;
        this.pathOrSdkman = pathOrSdkman;
        this.isSdkman = pathOrSdkman.startsWith("sdkman:");
        this.majorVersion = detectMajorVersion();
    }

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
