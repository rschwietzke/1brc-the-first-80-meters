package org.onebrc.benchmark;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MachineFingerprint {
    public final String hostname;
    public final String kernel;
    public final String os;
    public final String cpuModel;
    public final int cpuCores;
    public final String cpuMaxMhz;
    public final String totalMemory;

    private MachineFingerprint(String hostname, String kernel, String os, String cpuModel, int cpuCores, String cpuMaxMhz, String totalMemory) {
        this.hostname = hostname;
        this.kernel = kernel;
        this.os = os;
        this.cpuModel = cpuModel;
        this.cpuCores = cpuCores;
        this.cpuMaxMhz = cpuMaxMhz;
        this.totalMemory = totalMemory;
    }

    public static MachineFingerprint collect() {
        String hn = runCmd("hostname", "unknown_host");
        String kern = runCmd("uname -r", "unknown_kernel");
        String o = readOsRelease();
        
        String cModel = "unknown_cpu";
        int cCores = 0;
        String cMaxMhz = "unknown_mhz";
        
        try {
            Path cpuinfo = Paths.get("/proc/cpuinfo");
            if (Files.exists(cpuinfo)) {
                for (String line : Files.readAllLines(cpuinfo)) {
                    if (line.startsWith("model name")) {
                        cModel = line.split(":", 2)[1].trim();
                    }
                    if (line.startsWith("processor")) {
                        cCores++;
                    }
                    if (line.startsWith("cpu MHz")) {
                        // just keep the last or parse max elsewhere
                        cMaxMhz = line.split(":", 2)[1].trim();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Note: could not read /proc/cpuinfo");
        }
        
        String mem = "unknown_mem";
        try {
            Path meminfo = Paths.get("/proc/meminfo");
            if (Files.exists(meminfo)) {
                for (String line : Files.readAllLines(meminfo)) {
                    if (line.startsWith("MemTotal:")) {
                        mem = line.split(":", 2)[1].trim();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Note: could not read /proc/meminfo");
        }

        return new MachineFingerprint(hn, kern, o, cModel, cCores, cMaxMhz, mem);
    }

    private static String runCmd(String cmd, String fallback) {
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = r.readLine();
            if (line != null) return line.trim();
        } catch (Exception e) {
            System.err.println("Note: command failed: " + cmd);
        }
        return fallback;
    }

    private static String readOsRelease() {
        try {
            Path osRelease = Paths.get("/etc/os-release");
            if (Files.exists(osRelease)) {
                for (String line : Files.readAllLines(osRelease)) {
                    if (line.startsWith("PRETTY_NAME=")) {
                        return line.split("=", 2)[1].replace("\"", "");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Note: could not read /etc/os-release");
        }
        return "unknown_os";
    }

    public MachineMatch compare(MachineFingerprint other) {
        if (other == null) return MachineMatch.DIFFERENT;
        if (this.hostname.equals(other.hostname) &&
            this.kernel.equals(other.kernel) &&
            this.cpuModel.equals(other.cpuModel)) {
            return MachineMatch.SAME;
        }
        return MachineMatch.DIFFERENT;
    }
    
    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s | %d cores | %s MHz | %s",
                hostname, os, kernel, cpuModel, cpuCores, cpuMaxMhz, totalMemory);
    }
}
