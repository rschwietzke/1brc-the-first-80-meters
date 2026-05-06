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

package org.onebrc.benchmarkviewer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.hamcrest.Matchers.containsString;

/**
 * Smoke tests verifying that the Spring Boot application context loads
 * and the landing page is accessible.
 */
@SpringBootTest
@AutoConfigureMockMvc
class BenchmarkViewerApplicationTests
{
    @Autowired
    private MockMvc mockMvc;

    /**
     * Verify that the Spring application context loads successfully.
     */
    @Test
    @DisplayName("Spring context loads")
    void contextLoads()
    {
        // Context loading is verified by Spring Boot test infrastructure.
        // If this test runs without exception, the context started successfully.
    }

    /**
     * Verify that GET / returns HTTP 200 and contains the expected heading.
     */
    @Test
    @DisplayName("GET / returns 200 with Benchmark Explorer heading")
    void landingPageReturns200() throws Exception
    {
        this.mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Benchmark Explorer")));
    }
}
