/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// AI-generated file: Gemini 3.1 Pro (High)

package org.onebrc.benchmarkviewer.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class DataImportServiceTest
{
    @Test
    @DisplayName("Should parse sysinfo.txt correctly")
    void testParseSysinfo()
    {
        final DataImportService service = new DataImportService(null, null);
        final String sysinfoContent = """
            Hostname: some-host
            Kernel: 6.8.0-31-generic
            OS: Ubuntu 24.04 LTS
            CPU: AMD Ryzen 9 7950X 16-Core Processor
            Cores: 32
            Memory: 62.6Gi
            """;

        final TestRun run = new TestRun();
        service.parseSysinfo(run, sysinfoContent);

        assertThat(run.getHostname()).isEqualTo("some-host");
        assertThat(run.getKernelVersion()).isEqualTo("6.8.0-31-generic");
        assertThat(run.getOs()).isEqualTo("Ubuntu 24.04 LTS");
        assertThat(run.getCpu()).isEqualTo("AMD Ryzen 9 7950X 16-Core Processor");
        assertThat(run.getCpuCores()).isEqualTo(32);
        assertThat(run.getMemory()).isEqualTo("62.6Gi");
    }

    @Test
    @DisplayName("Should parse meta.json correctly")
    void testParseMetaJson()
    {
        final DataImportService service = new DataImportService(null, null);
        final String json = """
            {
              "timestamp": "20260505-175402",
              "totalRuns": 1224,
              "comment": "Test run"
            }
            """;

        final TestRun run = new TestRun();
        service.parseMeta(run, json);

        assertThat(run.getTimestamp()).isEqualTo(LocalDateTime.of(2026, 5, 5, 17, 54, 2));
        assertThat(run.getComment()).isEqualTo("Test run");
    }

    @Test
    @DisplayName("Should parse CSV correctly")
    void testParseCsv()
    {
        final DataImportService service = new DataImportService(null, null);
        final String csvContent = """
            JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys,GcPauseMs,AllocatedBytes,JitCompilationMs
            JDK_21_OPEN,"-XX:+UseZGC","-Xms1g -Xmx1g","-wc 0 -mc 1 -t 8","taskset -c 0-7",10k,20260505-175402,org.onebrc.again26.BRC100,10.5,OK,20.0,30.0,100,200,300,400,500,600,700,800,900,1000,1.5,1.1,1.2,1.3,45.2,21879136,123.4
            JDK_21_OPEN,"-XX:+UseZGC","-Xms2g -Xmx2g","-wc 0 -mc 1 -t 8","taskset -c 0-7",10k,20260505-175402,org.onebrc.again26.BRC101,0,ERROR,0,0,10,20,30,40,50,60,70,80,90,100,0.5,0.1,0.2,0.3,0,0,0
            """;

        final TestRun run = new TestRun();
        final List<Measurement> measurements = service.parseCsv(run, csvContent);

        assertThat(measurements).hasSize(2);

        final Measurement m1 = measurements.get(0);
        assertThat(m1.getTestRun()).isEqualTo(run);
        assertThat(m1.getJdk()).isEqualTo("JDK_21_OPEN");
        assertThat(m1.getGcOpts()).isEqualTo("-XX:+UseZGC");
        assertThat(m1.getVmOpts()).isEqualTo("-Xms1g -Xmx1g");
        assertThat(m1.getProgOpts()).isEqualTo("-wc 0 -mc 1 -t 8");
        assertThat(m1.getBinding()).isEqualTo("taskset -c 0-7");
        assertThat(m1.getDataset()).isEqualTo("10k");
        assertThat(m1.getClassName()).isEqualTo("org.onebrc.again26.BRC100");
        assertThat(m1.isError()).isFalse();
        assertThat(m1.getMedianRuntimeMs()).isEqualTo(10.5);
        assertThat(m1.getPerfRuntimeMs()).isEqualTo(20.0);
        assertThat(m1.getInstructions()).isEqualTo(100L);
        assertThat(m1.getSecSys()).isEqualTo(1.3);
        assertThat(m1.getGcPauseMs()).isEqualTo(45.2);
        assertThat(m1.getAllocatedBytes()).isEqualTo(21879136L);
        assertThat(m1.getJitCompilationMs()).isEqualTo(123.4);

        final Measurement m2 = measurements.get(1);
        assertThat(m2.getClassName()).isEqualTo("org.onebrc.again26.BRC101");
        assertThat(m2.isError()).isTrue();
        assertThat(m2.getMedianRuntimeMs()).isEqualTo(0.0);
        assertThat(m2.getInstructions()).isEqualTo(10L);
        assertThat(m2.getGcPauseMs()).isEqualTo(0.0);
        assertThat(m2.getAllocatedBytes()).isEqualTo(0L);
        assertThat(m2.getJitCompilationMs()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should parse CSV without JFR columns gracefully")
    void testParseCsvWithoutJfrColumns()
    {
        final DataImportService service = new DataImportService(null, null);
        final String csvContent = """
            JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys
            JDK_21_OPEN,"-XX:+UseZGC","-Xms1g -Xmx1g","-wc 0 -mc 1 -t 8","taskset -c 0-7",10k,20260505-175402,org.onebrc.again26.BRC100,10.5,OK,20.0,30.0,100,200,300,400,500,600,700,800,900,1000,1.5,1.1,1.2,1.3
            """;

        final TestRun run = new TestRun();
        final List<Measurement> measurements = service.parseCsv(run, csvContent);

        assertThat(measurements).hasSize(1);

        final Measurement m = measurements.get(0);
        assertThat(m.getMedianRuntimeMs()).isEqualTo(10.5);
        assertThat(m.getGcPauseMs()).isEqualTo(0.0);
        assertThat(m.getAllocatedBytes()).isEqualTo(0L);
        assertThat(m.getJitCompilationMs()).isEqualTo(0.0);
    }
}
