package org.onebrc.benchmark;

import java.util.ArrayList;
import java.util.List;

public class ClassConfig {
    public final String className;
    public final String fqcn;
    public boolean ignore = false;
    public String status = "complete"; // baseline, incomplete, complete
    public final List<String> exclusions = new ArrayList<>();
    public final List<String> inclusions = new ArrayList<>();

    public ClassConfig(String className, String fqcn) {
        this.className = className;
        this.fqcn = fqcn;
    }
}
