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

// AI-generated file: Claude Opus 4.6 (Thinking)

package org.onebrc.benchmarkviewer.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests for {@link RunDetailController}.
 *
 * <p>Verifies the variation matrix endpoint returns the correct view name,
 * populates the model with grouped matrix data, and responds correctly
 * to HTMX partial requests.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class RunDetailControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestRunRepository testRunRepository;

    @MockitoBean
    private MeasurementRepository measurementRepository;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("4.1: GET /runs/{timestamp} returns 200, model contains grouped matrix data")
    void getRunDetail_ShouldReturnMatrixView() throws Exception
    {
        final LocalDateTime timestamp = LocalDateTime.of(2026, 5, 5, 17, 54, 2);
        final TestRun run = new TestRun();
        run.setId(1L);
        run.setTimestamp(timestamp);

        final Measurement m1 = new Measurement();
        m1.setClassName("BRC01");
        m1.setJdk("21");
        m1.setMedianRuntimeMs(100.0);
        m1.setError(false);
        // Default empty string for other fields to prevent NPE in EnvironmentKey
        m1.setGcOpts("");
        m1.setVmOpts("");
        m1.setProgOpts("");
        m1.setBinding("");
        m1.setDataset("");

        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.of(run));
        when(this.searchService.searchMeasurements(any(), any())).thenReturn(List.of(m1));
        when(this.searchService.getFacetCounts(any(), any())).thenReturn(Map.of());

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02")
                .header("HX-Request", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("run-detail :: htmx-response"))
            .andExpect(model().attributeExists("testRun"))
            .andExpect(model().attributeExists("classes"))
            .andExpect(model().attributeExists("matrix"));
    }
}
