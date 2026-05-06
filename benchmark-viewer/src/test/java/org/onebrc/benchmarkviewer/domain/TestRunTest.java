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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the TestRun entity mapping and constraints.
 */
class TestRunTest
{
    @Test
    @DisplayName("Should successfully construct and populate TestRun entity")
    void testConstructAndPopulate()
    {
        final LocalDateTime timestamp = LocalDateTime.now();
        final TestRun run = new TestRun();
        run.setTimestamp(timestamp);
        run.setTotalRuns(100);
        run.setComment("Nightly run");
        run.setHostname("localhost");
        run.setKernelVersion("6.5.0");
        run.setOs("Linux");
        run.setCpu("AMD Ryzen 9");
        run.setCpuCores(16);
        run.setMemory("32GB");

        assertThat(run.getTimestamp()).isEqualTo(timestamp);
        assertThat(run.getTotalRuns()).isEqualTo(100);
        assertThat(run.getComment()).isEqualTo("Nightly run");
        assertThat(run.getHostname()).isEqualTo("localhost");
        assertThat(run.getKernelVersion()).isEqualTo("6.5.0");
        assertThat(run.getOs()).isEqualTo("Linux");
        assertThat(run.getCpu()).isEqualTo("AMD Ryzen 9");
        assertThat(run.getCpuCores()).isEqualTo(16);
        assertThat(run.getMemory()).isEqualTo("32GB");
    }
}
