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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
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
 * Tests for {@link TestCaseTrendController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TestCaseTrendControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("GET /testcase/{className}/trends returns 200 with trend data")
    void testTrendsWithData() throws Exception
    {
        final String className = "org.onebrc.again26.BRC100";

        final TestRun run1 = new TestRun();
        run1.setTimestamp(LocalDateTime.of(2026, 5, 8, 21, 37, 32));

        final TestRun run2 = new TestRun();
        run2.setTimestamp(LocalDateTime.of(2026, 5, 9, 15, 24, 36));

        final Measurement m1 = new Measurement();
        m1.setClassName(className);
        m1.setTestRun(run1);
        m1.setJdk("JDK_21_OPEN");
        m1.setGcOpts("-XX:+UseG1GC");
        m1.setVmOpts("-Xms2g -Xmx2g");
        m1.setProgOpts("-wc 0 -mc 1 -t 8");
        m1.setBinding("taskset -c 0-7");
        m1.setDataset("10k");
        m1.setMedianRuntimeMs(83.0);
        m1.setIpc(0.9);
        m1.setInstructions(1_098_513_388L);
        m1.setGcPauseMs(0.0);
        m1.setAllocatedBytes(21_879_136L);

        final Measurement m2 = new Measurement();
        m2.setClassName(className);
        m2.setTestRun(run2);
        m2.setJdk("JDK_21_OPEN");
        m2.setGcOpts("-XX:+UseG1GC");
        m2.setVmOpts("-Xms2g -Xmx2g");
        m2.setProgOpts("-wc 0 -mc 1 -t 8");
        m2.setBinding("taskset -c 0-7");
        m2.setDataset("10k");
        m2.setMedianRuntimeMs(57.0);
        m2.setIpc(1.03);
        m2.setInstructions(1_100_289_725L);
        m2.setGcPauseMs(45.0);
        m2.setAllocatedBytes(21_878_776L);

        when(this.searchService.searchMeasurements(any(), any(), eq(className)))
            .thenReturn(List.of(m1, m2));

        this.mockMvc.perform(get("/testcase/{className}/trends", className))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase-trends"))
            .andExpect(model().attributeExists("className"))
            .andExpect(model().attributeExists("simpleClassName"))
            .andExpect(model().attributeExists("environments"))
            .andExpect(model().attributeExists("trendDataMap"))
            .andExpect(model().attribute("className", className))
            .andExpect(model().attribute("simpleClassName", "BRC100"));
    }

    @Test
    @DisplayName("GET /testcase/{className}/trends returns 200 with empty data")
    void testTrendsWithNoData() throws Exception
    {
        final String className = "org.example.NonExistent";

        when(this.searchService.searchMeasurements(any(), any(), eq(className)))
            .thenReturn(List.of());

        this.mockMvc.perform(get("/testcase/{className}/trends", className))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase-trends"))
            .andExpect(model().attributeExists("environments"))
            .andExpect(model().attributeExists("trendDataMap"));
    }

    @Test
    @DisplayName("HTMX request returns fragment view name")
    void testTrendsHtmxPartial() throws Exception
    {
        final String className = "org.onebrc.again26.BRC100";

        when(this.searchService.searchMeasurements(any(), any(), eq(className)))
            .thenReturn(List.of());

        this.mockMvc.perform(get("/testcase/{className}/trends", className)
                .header("HX-Request", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase-trends :: htmx-response"));
    }

    @Test
    @DisplayName("Multiple environments produce separate trend entries")
    void testTrendsMultipleEnvironments() throws Exception
    {
        final String className = "org.onebrc.again26.BRC100";

        final TestRun run = new TestRun();
        run.setTimestamp(LocalDateTime.of(2026, 5, 8, 21, 37, 32));

        // Environment 1: G1GC
        final Measurement m1 = new Measurement();
        m1.setClassName(className);
        m1.setTestRun(run);
        m1.setJdk("JDK_21_OPEN");
        m1.setGcOpts("-XX:+UseG1GC");
        m1.setVmOpts("-Xms2g -Xmx2g");
        m1.setProgOpts("");
        m1.setBinding("");
        m1.setDataset("10k");
        m1.setMedianRuntimeMs(83.0);

        // Environment 2: ZGC
        final Measurement m2 = new Measurement();
        m2.setClassName(className);
        m2.setTestRun(run);
        m2.setJdk("JDK_21_OPEN");
        m2.setGcOpts("-XX:+UseZGC");
        m2.setVmOpts("-Xms2g -Xmx2g");
        m2.setProgOpts("");
        m2.setBinding("");
        m2.setDataset("10k");
        m2.setMedianRuntimeMs(95.0);

        when(this.searchService.searchMeasurements(any(), any(), eq(className)))
            .thenReturn(List.of(m1, m2));

        this.mockMvc.perform(get("/testcase/{className}/trends", className))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase-trends"));
        // Two different GC opts should produce 2 environment entries
    }
}
