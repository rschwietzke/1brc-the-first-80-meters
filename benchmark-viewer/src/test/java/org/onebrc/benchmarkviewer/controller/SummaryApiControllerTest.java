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

// AI-generated file: Gemini 3.5 Flash (Antigravity)

package org.onebrc.benchmarkviewer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.service.DataImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SummaryApiControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestRunRepository testRunRepository;

    @MockitoBean
    private MeasurementRepository measurementRepository;

    @MockitoBean
    private DataImportService dataImportService;

    @Test
    @DisplayName("9.4: POST /admin/reimport triggers re-import in service and returns 200")
    void testReimportEndpoint() throws Exception
    {
        doNothing().when(this.dataImportService).reimportAll(any());

        this.mockMvc.perform(post("/admin/reimport"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("success"))
            .andExpect(jsonPath("$.message").value("Re-import complete!"));

        verify(this.dataImportService, times(1)).reimportAll(any());
    }

    @Test
    @DisplayName("9.3: GET /api/runs/{timestamp}/summary returns ECharts categories and series")
    void testGetRunSummary() throws Exception
    {
        final String tsStr = "2026-05-08T21:37:32";
        final LocalDateTime ts = LocalDateTime.of(2026, 5, 8, 21, 37, 32);

        final TestRun run = new TestRun();
        run.setTimestamp(ts);

        final Measurement m = new Measurement();
        m.setClassName("org.onebrc.again26.BRC100");
        m.setMedianRuntimeMs(83.0);
        m.setJdk("JDK_21_OPEN");
        m.setGcOpts("-XX:+UseG1GC");
        m.setVmOpts("-Xms2g -Xmx2g");

        when(this.testRunRepository.findByTimestamp(ts)).thenReturn(Optional.of(run));
        when(this.measurementRepository.findByTestRun(run)).thenReturn(List.of(m));

        this.mockMvc.perform(get("/api/runs/{ts}/summary", tsStr))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categories[0]").value("BRC100"))
            .andExpect(jsonPath("$.series[0].name").value("JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g"))
            .andExpect(jsonPath("$.series[0].data[0]").value(83.0));
    }

    @Test
    @DisplayName("GET /api/runs/{timestamp}/compare/jdk returns grouped bar chart data")
    void testGetJdkCompare() throws Exception
    {
        final String tsStr = "2026-05-08T21:37:32";
        final LocalDateTime ts = LocalDateTime.of(2026, 5, 8, 21, 37, 32);

        final TestRun run = new TestRun();
        run.setTimestamp(ts);

        final Measurement m = new Measurement();
        m.setClassName("org.onebrc.again26.BRC100");
        m.setMedianRuntimeMs(83.0);
        m.setJdk("JDK_21_OPEN");

        when(this.testRunRepository.findByTimestamp(ts)).thenReturn(Optional.of(run));
        when(this.measurementRepository.findByTestRun(run)).thenReturn(List.of(m));

        this.mockMvc.perform(get("/api/runs/{ts}/compare/jdk", tsStr))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categories[0]").value("BRC100"))
            .andExpect(jsonPath("$.series[0].name").value("JDK_21_OPEN"))
            .andExpect(jsonPath("$.series[0].data[0]").value(83.0));
    }

    @Test
    @DisplayName("GET /api/runs/{timestamp}/compare/gc returns grouped bar chart data")
    void testGetGcCompare() throws Exception
    {
        final String tsStr = "2026-05-08T21:37:32";
        final LocalDateTime ts = LocalDateTime.of(2026, 5, 8, 21, 37, 32);

        final TestRun run = new TestRun();
        run.setTimestamp(ts);

        final Measurement m = new Measurement();
        m.setClassName("org.onebrc.again26.BRC100");
        m.setMedianRuntimeMs(83.0);
        m.setGcOpts("-XX:+UseG1GC");

        when(this.testRunRepository.findByTimestamp(ts)).thenReturn(Optional.of(run));
        when(this.measurementRepository.findByTestRun(run)).thenReturn(List.of(m));

        this.mockMvc.perform(get("/api/runs/{ts}/compare/gc", tsStr))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categories[0]").value("BRC100"))
            .andExpect(jsonPath("$.series[0].name").value("-XX:+UseG1GC"))
            .andExpect(jsonPath("$.series[0].data[0]").value(83.0));
    }
}
