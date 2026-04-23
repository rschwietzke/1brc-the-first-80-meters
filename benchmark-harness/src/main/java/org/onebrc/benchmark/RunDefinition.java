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

    public RunDefinition(String name, String valueString) {
        this.name = name;
        String[] parts = valueString.split(";");
        
        this.jdkFilter = getPart(parts, 0);
        this.gcFilter = getPart(parts, 1);
        this.vmFilter = getPart(parts, 2);
        this.tasksetFilter = getPart(parts, 3);
        this.progFilter = getPart(parts, 4);
        this.dataFilter = getPart(parts, 5);
        this.classFilter = getPart(parts, 6);
    }

    private String getPart(String[] parts, int index) {
        if (index < parts.length) {
            return parts[index].trim();
        }
        return "*";
    }
}
