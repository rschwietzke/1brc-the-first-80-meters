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

// AI-generated file: Antigravity

package org.onebrc.benchmarkviewer.controller;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests for {@link ExecutionCompareController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExecutionCompareControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MeasurementRepository measurementRepository;

    @Test
    @DisplayName("GET /compare-executions returns 200 with compare view")
    void testCompareExecutions() throws Exception
    {
        final TestRun run1 = new TestRun();
        run1.setTimestamp(LocalDateTime.now());
        final Measurement m1 = new Measurement();
        m1.setId(1L);
        m1.setTestRun(run1);
        m1.setMedianRuntimeMs(100.0);

        final TestRun run2 = new TestRun();
        run2.setTimestamp(LocalDateTime.now());
        final Measurement m2 = new Measurement();
        m2.setId(2L);
        m2.setTestRun(run2);
        m2.setMedianRuntimeMs(120.0);

        when(this.measurementRepository.findById(1L)).thenReturn(Optional.of(m1));
        when(this.measurementRepository.findById(2L)).thenReturn(Optional.of(m2));

        this.mockMvc.perform(get("/compare-executions?m1=1&m2=2"))
            .andExpect(status().isOk())
            .andExpect(view().name("compare-executions"))
            .andExpect(model().attributeExists("m1"))
            .andExpect(model().attributeExists("m2"))
            .andExpect(model().attributeExists("runtimeDiff"))
            .andExpect(model().attributeExists("runtimePct"))
            .andExpect(model().attribute("runtimeDiff", 20.0));
    }
}
