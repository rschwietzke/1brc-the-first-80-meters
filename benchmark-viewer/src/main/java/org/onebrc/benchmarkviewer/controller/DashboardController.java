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

import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Dashboard controller serving the main landing page.
 */
@Controller
public class DashboardController
{
    private final TestRunRepository testRunRepository;

    public DashboardController(final TestRunRepository testRunRepository)
    {
        this.testRunRepository = testRunRepository;
    }

    /**
     * Render the landing page (test run list or placeholder).
     *
     * @return the Thymeleaf view name for the index page
     */
    @GetMapping("/")
    public String index(final Model model,
                        @RequestHeader(value = "HX-Request", required = false) final String hxRequest)
    {
        model.addAttribute("testRuns", this.testRunRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp")));
        
        if ("true".equals(hxRequest))
        {
            return "fragments/run-list :: content";
        }
        
        return "index";
    }
}
