package org.onebrc.benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ResultMatrix {

    public static class Key {
        public final String jdk;
        public final String gcOpts;
        public final String vmOpts;
        public final String progOpts;
        public final String binding;
        public final String data;
        public final String className;

        public Key(String jdk, String gcOpts, String vmOpts, String progOpts, String binding, String data, String className) {
            this.jdk = jdk;
            this.gcOpts = gcOpts;
            this.vmOpts = vmOpts;
            this.progOpts = progOpts;
            this.binding = binding;
            this.data = data;
            this.className = className;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return jdk.equals(key.jdk) && gcOpts.equals(key.gcOpts) &&
                   vmOpts.equals(key.vmOpts) && progOpts.equals(key.progOpts) &&
                   binding.equals(key.binding) && data.equals(key.data) && 
                   className.equals(key.className);
        }

        @Override
        public int hashCode() {
            return Objects.hash(jdk, gcOpts, vmOpts, progOpts, binding, data, className);
        }
    }

    public static class RowData {
        public final long medianRuntimeMs;
        public final String checksum;
        public final long perfRuntimeMs;
        public final long jfrRuntimeMs;
        public final long instructions;
        public final long cycles;
        public final long branches;
        public final long branchMisses;
        public final long l1Misses;
        public final long llcMisses;
        public final long pageFaults;
        public final long contextSwitches;
        public final long cpuMigrations;
        public final double ipc;
        public final long gcPauseMs;
        public final long allocatedBytes;
        public final long jitCompilationMs;

        public RowData(long medianRuntimeMs, String checksum, long perfRuntimeMs, long jfrRuntimeMs, long instructions, long cycles, long branches, long branchMisses, long l1Misses, long llcMisses, long pageFaults, long contextSwitches, long cpuMigrations, double ipc, long gcPauseMs, long allocatedBytes, long jitCompilationMs) {
            this.medianRuntimeMs = medianRuntimeMs;
            this.checksum = checksum;
            this.perfRuntimeMs = perfRuntimeMs;
            this.jfrRuntimeMs = jfrRuntimeMs;
            this.instructions = instructions;
            this.cycles = cycles;
            this.branches = branches;
            this.branchMisses = branchMisses;
            this.l1Misses = l1Misses;
            this.llcMisses = llcMisses;
            this.pageFaults = pageFaults;
            this.contextSwitches = contextSwitches;
            this.cpuMigrations = cpuMigrations;
            this.ipc = ipc;
            this.gcPauseMs = gcPauseMs;
            this.allocatedBytes = allocatedBytes;
            this.jitCompilationMs = jitCompilationMs;
        }

        public long getMedianRuntimeMs() { return medianRuntimeMs; }
        public String getChecksum() { return checksum; }
        public long getPerfRuntimeMs() { return perfRuntimeMs; }
        public long getJfrRuntimeMs() { return jfrRuntimeMs; }
        public long getInstructions() { return instructions; }
        public long getCycles() { return cycles; }
        public long getBranches() { return branches; }
        public long getBranchMisses() { return branchMisses; }
        public long getL1Misses() { return l1Misses; }
        public long getLlcMisses() { return llcMisses; }
        public long getPageFaults() { return pageFaults; }
        public long getContextSwitches() { return contextSwitches; }
        public long getCpuMigrations() { return cpuMigrations; }
        public double getIpc() { return ipc; }
        public long getGcPauseMs() { return gcPauseMs; }
        public long getAllocatedBytes() { return allocatedBytes; }
        public long getJitCompilationMs() { return jitCompilationMs; }
    }

    private final Map<Key, RowData> matrix = new HashMap<>();

    public void loadCsv(Path csvFile) throws IOException {
        if (!Files.exists(csvFile)) return;
        List<String> lines = Files.readAllLines(csvFile);
        if (lines.size() <= 1) return;

        String headerLine = lines.get(0);
        String[] headers = headerLine.split(",");
        Map<String, Integer> colMap = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            colMap.put(headers[i].trim(), i);
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            if (parts.length < 8) {
                System.err.println("Warning: skipping fragmented or invalid row " + i + ": " + line);
                continue;
            }
            
            try {
                String jdk = getVal(parts, colMap, "JDK");
                String gcOpts = getVal(parts, colMap, "GC_OPTS");
                String vmOpts = getVal(parts, colMap, "VM_OPTS");
                String progOpts = getVal(parts, colMap, "PROG_OPTS");
                // Fallback to TASKSET if BINDING is not present in old files
                String binding = getVal(parts, colMap, "BINDING");
                if (binding.isEmpty()) binding = getVal(parts, colMap, "TASKSET");
                String data = getVal(parts, colMap, "DATA");
                String cls = getVal(parts, colMap, "Class");

                long median = parseLong(getVal(parts, colMap, "MedianRuntimeMs"));
                String checksum = getVal(parts, colMap, "Checksum");
                long perfRt = parseLong(getVal(parts, colMap, "PerfRuntimeMs"));
                long jfrRt = parseLong(getVal(parts, colMap, "JfrRuntimeMs"));
                long inst = parseLong(getVal(parts, colMap, "Instructions"));
                long cycl = parseLong(getVal(parts, colMap, "Cycles"));
                long br = parseLong(getVal(parts, colMap, "Branches"));
                long brMiss = parseLong(getVal(parts, colMap, "BranchMisses"));
                long l1Miss = parseLong(getVal(parts, colMap, "L1Misses"));
                long llcMiss = parseLong(getVal(parts, colMap, "LLCMisses"));
                long pageFaults = parseLong(getVal(parts, colMap, "PageFaults"));
                long ctxSwitches = parseLong(getVal(parts, colMap, "ContextSwitches"));
                long cpuMigs = parseLong(getVal(parts, colMap, "CpuMigrations"));
                double ipc = parseDouble(getVal(parts, colMap, "IPC"));
                long gc = parseLong(getVal(parts, colMap, "GcPauseMs"));
                long alloc = parseLong(getVal(parts, colMap, "AllocatedBytes"));
                long jit = parseLong(getVal(parts, colMap, "JitCompilationMs"));

                matrix.put(new Key(jdk, gcOpts, vmOpts, progOpts, binding, data, cls), new RowData(median, checksum, perfRt, jfrRt, inst, cycl, br, brMiss, l1Miss, llcMiss, pageFaults, ctxSwitches, cpuMigs, ipc, gc, alloc, jit));
            } catch (Exception e) {
                System.err.println("Warning: skipping invalid row " + i + ": " + e.getMessage());
            }
        }
    }

    private String getVal(String[] parts, Map<String, Integer> colMap, String colName) {
        Integer idx = colMap.get(colName);
        if (idx != null && idx < parts.length) {
            return parts[idx].replace("\"", "");
        }
        return "";
    }

    private long parseLong(String val) {
        if (val == null || val.isEmpty() || val.equals("ERROR")) return 0;
        try {
            return Long.parseLong(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private double parseDouble(String val) {
        if (val == null || val.isEmpty() || val.equals("ERROR")) return 0.0;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public RowData get(Key k) {
        return matrix.get(k);
    }
    
    public Set<String> getDatasets() {
        Set<String> ds = new TreeSet<>();
        for (Key k : matrix.keySet()) ds.add(k.data);
        return ds;
    }

    public Set<String> getClasses() {
        Set<String> cls = new TreeSet<>();
        for (Key k : matrix.keySet()) cls.add(k.className);
        return cls;
    }

    public Set<String> getEnvironments() {
        Set<String> set = new TreeSet<>();
        for (Key k : matrix.keySet()) {
            set.add(k.jdk + " | " + k.gcOpts + " | " + k.vmOpts + " | " + k.progOpts + " | " + k.binding);
        }
        return set;
    }
}
