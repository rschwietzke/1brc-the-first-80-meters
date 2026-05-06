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
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TestRunRepository testRunRepository;

    @Test
    @DisplayName("GET / returns 200 and renders full page without HX-Request")
    void testGetDashboardFullPage() throws Exception
    {
        final TestRun run = new TestRun();
        run.setHostname("test-host");
        when(this.testRunRepository.findAll()).thenReturn(List.of(run));

        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("testRuns"))
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("GET / returns fragment with HX-Request")
    void testGetDashboardFragment() throws Exception
    {
        final TestRun run = new TestRun();
        run.setHostname("test-host");
        when(this.testRunRepository.findAll()).thenReturn(List.of(run));

        this.mockMvc.perform(get("/")
                        .header("HX-Request", "true"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("testRuns"))
                .andExpect(view().name("fragments/run-list :: content"));
    }
}
