package org.onebrc.benchmark;

public class RunDefinition {
    public final String name;
    public final String jdkFilter;
    public final String gcFilter;
    public final String vmFilter;
    public final String tasksetFilter;
    public final String progFilter;
    public final String dataFilter;
    public final String classFilter;

    public RunDefinition(String name, java.util.Map<String, String> properties) {
        this.name = name;
        this.jdkFilter = properties.getOrDefault("JDK_FILTER", "*");
        this.gcFilter = properties.getOrDefault("GC_FILTER", "*");
        this.vmFilter = properties.getOrDefault("VM_FILTER", "*");
        this.tasksetFilter = properties.getOrDefault("TASKSET_FILTER", "*");
        this.progFilter = properties.getOrDefault("PROG_FILTER", "*");
        this.dataFilter = properties.getOrDefault("DATA_FILTER", "*");
        this.classFilter = properties.getOrDefault("CLASS_FILTER", "*");
    }
}
