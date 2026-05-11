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

import java.util.List;

import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Controller for the test case view.
 * Shows all historical executions of a specific test case (className) with filtering.
 */
@Controller
@Transactional(readOnly = true)
public class TestCaseController
{
    private final SearchService searchService;

    public TestCaseController(final SearchService searchService)
    {
        this.searchService = searchService;
    }

    @GetMapping("/testcase/{className}")
    public String getTestCase(
        @PathVariable("className") final String className,
        @RequestHeader(value = "HX-Request", required = false) final String hxRequest,
        final FilterState filterState,
        final Model model)
    {
        final List<Measurement> rawMeasurements = this.searchService.searchMeasurements(filterState, null, className);
        final List<Measurement> measurements = new java.util.ArrayList<>(rawMeasurements);
        
        // Sort by timestamp descending
        measurements.sort((m1, m2) -> m2.getTestRun().getTimestamp().compareTo(m1.getTestRun().getTimestamp()));

        model.addAttribute("className", className);
        model.addAttribute("simpleClassName", className.substring(className.lastIndexOf('.') + 1));
        model.addAttribute("measurements", measurements);

        final var facets = this.searchService.getFacetCounts(filterState, null, className);
        model.addAttribute("facets", facets);
        model.addAttribute("activeFilters", filterState);
        model.addAttribute("filterActionUrl", "/testcase/" + className);

        if ("true".equals(hxRequest))
        {
            return "testcase :: htmx-response";
        }

        return "testcase";
    }
}
