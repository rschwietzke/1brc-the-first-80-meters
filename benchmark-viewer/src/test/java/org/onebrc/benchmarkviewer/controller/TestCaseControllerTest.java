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
import java.util.List;
import java.util.Map;

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
 * Tests for {@link TestCaseController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TestCaseControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("GET /testcase/{className} returns 200 with testcase view")
    void getTestCase() throws Exception
    {
        final TestRun run = new TestRun();
        run.setId(1L);
        run.setTimestamp(LocalDateTime.now());

        final Measurement m = new Measurement();
        m.setId(1L);
        m.setTestRun(run);
        m.setClassName("BRC01");

        when(this.searchService.searchMeasurements(any(), eq(null), eq("BRC01")))
            .thenReturn(new java.util.ArrayList<>(List.of(m)));
        when(this.searchService.getFacetCounts(any(), eq(null), eq("BRC01")))
            .thenReturn(Map.of());

        this.mockMvc.perform(get("/testcase/BRC01"))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase"))
            .andExpect(model().attributeExists("measurements"))
            .andExpect(model().attributeExists("className"))
            .andExpect(model().attributeExists("facets"));
    }

    @Test
    @DisplayName("GET /testcase/{className} via HTMX returns fragment")
    void getTestCaseHtmx() throws Exception
    {
        final TestRun run = new TestRun();
        run.setId(1L);
        run.setTimestamp(LocalDateTime.now());

        final Measurement m = new Measurement();
        m.setId(1L);
        m.setTestRun(run);
        m.setClassName("BRC01");

        when(this.searchService.searchMeasurements(any(), eq(null), eq("BRC01")))
            .thenReturn(new java.util.ArrayList<>(List.of(m)));
        when(this.searchService.getFacetCounts(any(), eq(null), eq("BRC01")))
            .thenReturn(Map.of());

        this.mockMvc.perform(get("/testcase/BRC01")
                .header("HX-Request", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("testcase :: htmx-response"));
    }
}
