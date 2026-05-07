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

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.TestRun;
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
 * Tests for {@link SidebarController}.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SidebarControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SearchService searchService;

    @MockitoBean
    private TestRunRepository testRunRepository;

    @Test
    @DisplayName("5.4: GET /sidebar returns fragment with facet data")
    void testGetSidebar() throws Exception
    {
        final TestRun run = new TestRun();
        run.setId(1L);
        when(this.testRunRepository.findById(1L)).thenReturn(Optional.of(run));
        when(this.searchService.getFacetCounts(any(), any()))
            .thenReturn(Map.of("jdk", Map.of("21", 10L, "25", 5L)));

        this.mockMvc.perform(get("/sidebar?testRunId=1"))
            .andExpect(status().isOk())
            .andExpect(view().name("fragments/sidebar :: content"))
            .andExpect(model().attributeExists("facets"))
            .andExpect(model().attributeExists("activeFilters"));
    }

    @Test
    @DisplayName("5.5: GET /sidebar?jdk=21 returns updated counts")
    void testGetSidebarFiltered() throws Exception
    {
        final TestRun run = new TestRun();
        run.setId(1L);
        when(this.testRunRepository.findById(1L)).thenReturn(Optional.of(run));
        when(this.searchService.getFacetCounts(any(), any()))
            .thenReturn(Map.of("jdk", Map.of("21", 10L, "25", 5L)));

        this.mockMvc.perform(get("/sidebar?testRunId=1&jdk=21"))
            .andExpect(status().isOk())
            .andExpect(view().name("fragments/sidebar :: content"))
            .andExpect(model().attributeExists("facets"))
            .andExpect(model().attributeExists("activeFilters"));
    }
}
