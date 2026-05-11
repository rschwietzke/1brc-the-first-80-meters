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

import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for the sidebar filter panel.
 *
 * <p>Renders the sidebar fragment containing faceted filter checkboxes.
 * Each facet group shows term counts computed by {@link SearchService},
 * with cross-dimension drill-down semantics.</p>
 */
@Controller
public class SidebarController
{
    private final SearchService searchService;
    private final TestRunRepository testRunRepository;

    /**
     * Construct the sidebar controller.
     *
     * @param searchService     search service for facet-count computation
     * @param testRunRepository repository for looking up test runs
     */
    public SidebarController(final SearchService searchService,
                             final TestRunRepository testRunRepository)
    {
        this.searchService = searchService;
        this.testRunRepository = testRunRepository;
    }

    /**
     * Render the sidebar filter fragment for a given test run.
     *
     * @param testRunId   the test run ID to scope the facets to
     * @param filterState current filter selections bound from query parameters
     * @param model       the Spring MVC model
     * @return the Thymeleaf fragment name for the sidebar
     */
    @GetMapping("/sidebar")
    public String getSidebar(
        @RequestParam("testRunId") final Long testRunId,
        final FilterState filterState,
        final Model model)
    {
        final TestRun testRun = this.testRunRepository.findById(testRunId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found"));

        final var facets = this.searchService.getFacetCounts(filterState, testRunId, null);

        model.addAttribute("testRun", testRun);
        model.addAttribute("facets", facets);
        model.addAttribute("activeFilters", filterState);
        // By default, just use the current test run for the action URL if sidebar is loaded standalone
        model.addAttribute("filterActionUrl", "/runs/" + testRun.getTimestamp().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return "fragments/sidebar :: content";
    }
}
