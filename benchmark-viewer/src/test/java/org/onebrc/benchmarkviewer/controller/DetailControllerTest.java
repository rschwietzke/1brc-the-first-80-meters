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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Tests for {@link DetailController}.
 *
 * <p>Verifies the measurement detail endpoint returns the correct view name,
 * populates the model with the expected attributes, and rejects invalid input
 * with appropriate HTTP status codes.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class DetailControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestRunRepository testRunRepository;

    @MockitoBean
    private MeasurementRepository measurementRepository;

    @MockitoBean
    private SearchService searchService;

    /**
     * Helper to create a test run with sensible defaults.
     */
    private TestRun createTestRun(final Long id, final LocalDateTime timestamp)
    {
        final TestRun run = new TestRun();
        run.setId(id);
        run.setTimestamp(timestamp);
        run.setHostname("test-host");
        return run;
    }

    /**
     * Helper to create a measurement with sensible defaults.
     */
    private Measurement createMeasurement(final Long id, final TestRun testRun)
    {
        final Measurement m = new Measurement();
        m.setId(id);
        m.setTestRun(testRun);
        m.setClassName("com.example.BRC01");
        m.setJdk("21");
        m.setGcOpts("");
        m.setVmOpts("");
        m.setProgOpts("");
        m.setBinding("");
        m.setDataset("10k");
        m.setMedianRuntimeMs(100.0);
        m.setError(false);
        return m;
    }

    @Test
    @DisplayName("HTMX detail request returns 200 with fragment view and model attributes")
    void getDetail_htmxRequest_shouldReturnFragmentView() throws Exception
    {
        final LocalDateTime timestamp = LocalDateTime.of(2026, 5, 5, 17, 54, 2);
        final TestRun run = this.createTestRun(1L, timestamp);
        final Measurement measurement = this.createMeasurement(42L, run);

        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.of(run));
        when(this.measurementRepository.findById(eq(42L))).thenReturn(Optional.of(measurement));

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02/detail/42")
                .header("HX-Request", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("fragments/detail :: htmx-response"))
            .andExpect(model().attributeExists("testRun"))
            .andExpect(model().attributeExists("measurement"))
            .andExpect(model().attributeExists("hasJfrFile"))
            .andExpect(model().attributeExists("jfrFileName"));
    }

    @Test
    @DisplayName("Non-HTMX detail request returns the layout-decorated view")
    void getDetail_fullPageRequest_shouldReturnLayoutView() throws Exception
    {
        final LocalDateTime timestamp = LocalDateTime.of(2026, 5, 5, 17, 54, 2);
        final TestRun run = this.createTestRun(1L, timestamp);
        final Measurement measurement = this.createMeasurement(42L, run);

        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.of(run));
        when(this.measurementRepository.findById(eq(42L))).thenReturn(Optional.of(measurement));

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02/detail/42"))
            .andExpect(status().isOk())
            .andExpect(view().name("detail"));
    }

    @Test
    @DisplayName("Invalid timestamp format returns 400 BAD_REQUEST")
    void getDetail_invalidTimestamp_shouldReturn400() throws Exception
    {
        this.mockMvc.perform(get("/runs/not-a-timestamp/detail/42")
                .header("HX-Request", "true"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Unknown test run returns 404 NOT_FOUND")
    void getDetail_unknownRun_shouldReturn404() throws Exception
    {
        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02/detail/42")
                .header("HX-Request", "true"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Unknown measurement returns 404 NOT_FOUND")
    void getDetail_unknownMeasurement_shouldReturn404() throws Exception
    {
        final LocalDateTime timestamp = LocalDateTime.of(2026, 5, 5, 17, 54, 2);
        final TestRun run = this.createTestRun(1L, timestamp);

        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.of(run));
        when(this.measurementRepository.findById(eq(99L))).thenReturn(Optional.empty());

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02/detail/99")
                .header("HX-Request", "true"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Measurement belonging to a different run returns 400 BAD_REQUEST")
    void getDetail_measurementWrongRun_shouldReturn400() throws Exception
    {
        final LocalDateTime timestamp = LocalDateTime.of(2026, 5, 5, 17, 54, 2);
        final TestRun run = this.createTestRun(1L, timestamp);

        // Create a measurement that belongs to a different run (ID = 999)
        final TestRun otherRun = this.createTestRun(999L, timestamp.plusDays(1));
        final Measurement measurement = this.createMeasurement(42L, otherRun);

        when(this.testRunRepository.findByTimestamp(any())).thenReturn(Optional.of(run));
        when(this.measurementRepository.findById(eq(42L))).thenReturn(Optional.of(measurement));

        this.mockMvc.perform(get("/runs/2026-05-05T17:54:02/detail/42")
                .header("HX-Request", "true"))
            .andExpect(status().isBadRequest());
    }
}
