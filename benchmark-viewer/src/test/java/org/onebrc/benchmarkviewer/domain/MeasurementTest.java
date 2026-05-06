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

package org.onebrc.benchmarkviewer.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MeasurementTest
{
    @Test
    @DisplayName("Should successfully construct and populate Measurement entity")
    void testConstructAndPopulate()
    {
        final TestRun run = new TestRun();
        run.setId(1L);

        final Measurement m = new Measurement();
        m.setTestRun(run);
        
        // Dimensions
        m.setJdk("JDK_21_OPEN");
        m.setGcOpts("-XX:+UseZGC");
        m.setVmOpts("-Xms1g -Xmx1g");
        m.setProgOpts("-wc 0 -mc 1 -t 8");
        m.setBinding("taskset -c 0-7");
        m.setDataset("10k");
        m.setClassName("org.onebrc.again26.BRC100_DirectTempWrite");
        m.setRunTimestamp("20260505-175402");
        
        // Error
        m.setError(true);

        // Metrics
        m.setMedianRuntimeMs(1.5);
        m.setPerfRuntimeMs(2.0);
        m.setJfrRuntimeMs(2.1);
        m.setInstructions(1000L);
        m.setCycles(2000L);
        m.setBranches(300L);
        m.setBranchMisses(10L);
        m.setL1Misses(50L);
        m.setLlcMisses(5L);
        m.setPageFaults(2L);
        m.setTaskClock(5000L);
        m.setContextSwitches(100L);
        m.setCpuMigrations(10L);
        m.setIpc(0.5);
        m.setSecElapsed(1.1);
        m.setSecUser(0.8);
        m.setSecSys(0.3);

        assertThat(m.getTestRun()).isEqualTo(run);
        assertThat(m.getJdk()).isEqualTo("JDK_21_OPEN");
        assertThat(m.getGcOpts()).isEqualTo("-XX:+UseZGC");
        assertThat(m.getVmOpts()).isEqualTo("-Xms1g -Xmx1g");
        assertThat(m.getProgOpts()).isEqualTo("-wc 0 -mc 1 -t 8");
        assertThat(m.getBinding()).isEqualTo("taskset -c 0-7");
        assertThat(m.getDataset()).isEqualTo("10k");
        assertThat(m.getClassName()).isEqualTo("org.onebrc.again26.BRC100_DirectTempWrite");
        assertThat(m.getRunTimestamp()).isEqualTo("20260505-175402");
        assertThat(m.isError()).isTrue();
        
        assertThat(m.getMedianRuntimeMs()).isEqualTo(1.5);
        assertThat(m.getInstructions()).isEqualTo(1000L);
        assertThat(m.getIpc()).isEqualTo(0.5);
    }
}
