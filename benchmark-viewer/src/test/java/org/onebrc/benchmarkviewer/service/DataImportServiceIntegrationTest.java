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
import org.junit.jupiter.api.io.TempDir;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DataImportServiceIntegrationTest
{
    @Autowired
    private DataImportService dataImportService;

    @Autowired
    private TestRunRepository testRunRepository;

    @Autowired
    private MeasurementRepository measurementRepository;

    @Test
    @DisplayName("Should import directory and avoid duplicate imports")
    void testImportDirectoryAndAvoidDuplicates(@TempDir Path tempDir) throws Exception
    {
        final String baseName = "20260505-175402";

        // Create meta.json
        Files.writeString(tempDir.resolve(baseName + "-meta.json"), """
            {
              "timestamp": "2026-05-05T17:54:02",
              "totalRuns": 2,
              "comment": "Test run"
            }
            """);

        // Create sysinfo.txt
        Files.writeString(tempDir.resolve(baseName + "-sysinfo.txt"), """
            Hostname: host1
            Kernel: Linux 6.8
            OS: Ubuntu
            CPU: AMD
            Cores: 8
            Memory: 16G
            """);

        // Create csv
        Files.writeString(tempDir.resolve(baseName + ".csv"), """
            JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys
            JDK_21_OPEN,"-XX:+UseZGC","-Xms1g -Xmx1g","-wc 0 -mc 1 -t 8","taskset -c 0-7",10k,20260505-175402,org.onebrc.again26.BRC100,10.5,OK,20.0,30.0,100,200,300,400,500,600,700,800,900,1000,1.5,1.1,1.2,1.3
            JDK_21_OPEN,"-XX:+UseZGC","-Xms2g -Xmx2g","-wc 0 -mc 1 -t 8","taskset -c 0-7",10k,20260505-175402,org.onebrc.again26.BRC101,0,ERROR,0,0,10,20,30,40,50,60,70,80,90,100,0.5,0.1,0.2,0.3
            """);

        // First import
        this.dataImportService.importDirectory(tempDir);

        assertThat(this.testRunRepository.count()).isEqualTo(1);
        assertThat(this.measurementRepository.count()).isEqualTo(2);

        // Second import should skip
        this.dataImportService.importDirectory(tempDir);

        assertThat(this.testRunRepository.count()).isEqualTo(1);
        assertThat(this.measurementRepository.count()).isEqualTo(2);
    }
}
