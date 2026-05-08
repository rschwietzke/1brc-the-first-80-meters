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

package org.onebrc.benchmarkviewer.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.ComparisonCandidate;
import org.onebrc.benchmarkviewer.domain.ComparisonRow;
import org.onebrc.benchmarkviewer.domain.EnvironmentKey;
import org.onebrc.benchmarkviewer.service.CompareService;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CompareControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompareService compareService;

    @MockitoBean
    private SearchService searchService;

    @Test
    @DisplayName("7.3: GET /compare returns 200 with layout view")
    void getComparePage_fullPage() throws Exception
    {
        final EnvironmentKey env = new EnvironmentKey("21", "ZGC", "", "", "", "10k");
        final ComparisonCandidate cand = new ComparisonCandidate(1L, "2026-05-05 17:54", env);

        when(this.compareService.getAllComparisonCandidates(any())).thenReturn(Set.of(cand));
        when(this.compareService.formatCandidate(cand)).thenReturn("2026-05-05 17:54 | 21 | ZGC | 10k");
        when(this.searchService.getFacetCounts(any(), any())).thenReturn(Map.of());

        this.mockMvc.perform(get("/compare"))
            .andExpect(status().isOk())
            .andExpect(view().name("compare"))
            .andExpect(model().attributeExists("environments"));
    }

    @Test
    @DisplayName("7.3: GET /compare via HTMX returns fragment")
    void getComparePage_htmxFragment() throws Exception
    {
        when(this.searchService.getFacetCounts(any(), any())).thenReturn(Map.of());

        this.mockMvc.perform(get("/compare")
                .header("HX-Request", "true"))
            .andExpect(status().isOk())
            .andExpect(view().name("fragments/compare :: compare-pane"));
    }

    @Test
    @DisplayName("7.3: GET /compare/results returns results fragment")
    void getCompareResults() throws Exception
    {
        final EnvironmentKey envA = new EnvironmentKey("21", "ZGC", "", "", "", "10k");
        final EnvironmentKey envB = new EnvironmentKey("25", "ZGC", "", "", "", "10k");
        final ComparisonCandidate candA = new ComparisonCandidate(1L, "2026-05-05 17:54", envA);
        final ComparisonCandidate candB = new ComparisonCandidate(2L, "2026-05-06 10:00", envB);

        final ComparisonRow result = ComparisonRow.compute("BRC01", 100.0, 150.0);

        when(this.compareService.getAllComparisonCandidates(any())).thenReturn(Set.of(candA, candB));
        when(this.compareService.parseFormattedCandidate(eq("CandA"), any())).thenReturn(candA);
        when(this.compareService.parseFormattedCandidate(eq("CandB"), any())).thenReturn(candB);
        when(this.compareService.compareCandidates(any(), eq(candA), eq(candB))).thenReturn(List.of(result));

        this.mockMvc.perform(get("/compare/results")
                .param("envA", "CandA")
                .param("envB", "CandB"))
            .andExpect(status().isOk())
            .andExpect(view().name("fragments/compare-results :: results"))
            .andExpect(model().attributeExists("results"));
    }
}
