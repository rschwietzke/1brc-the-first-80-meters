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

import org.onebrc.benchmarkviewer.domain.ComparisonCandidate;
import org.onebrc.benchmarkviewer.domain.ComparisonRow;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.service.CompareService;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * Controller for the global environment comparison view.
 */
@Controller
public class CompareController
{
    private final CompareService compareService;
    private final SearchService searchService;

    public CompareController(
        final CompareService compareService,
        final SearchService searchService)
    {
        this.compareService = compareService;
        this.searchService = searchService;
    }

    /**
     * Display the initial comparison selector page.
     */
    @GetMapping("/compare")
    public String getComparePage(
        @RequestHeader(value = "HX-Request", required = false) final String hxRequest,
        final FilterState filterState,
        final Model model)
    {
        final Set<ComparisonCandidate> uniqueCandidates = this.compareService.getAllComparisonCandidates(filterState);
        final List<String> formattedCandidates = uniqueCandidates.stream().map(this.compareService::formatCandidate).toList();

        model.addAttribute("environments", formattedCandidates);

        // Also add facets for OOB sidebar update if requested via HTMX
        final var facets = this.searchService.getFacetCounts(filterState, null, null);
        model.addAttribute("facets", facets);
        model.addAttribute("activeFilters", filterState);
        model.addAttribute("filterActionUrl", "/compare");

        if ("true".equals(hxRequest))
        {
            return "fragments/compare :: htmx-response";
        }

        return "compare";
    }

    /**
     * Compute and return the results of a comparison via HTMX.
     */
    @GetMapping("/compare/results")
    public String getCompareResults(
        @RequestParam(value = "envA", required = false) final String envAStr,
        @RequestParam(value = "envB", required = false) final String envBStr,
        final FilterState filterState,
        final Model model)
    {
        final Set<ComparisonCandidate> uniqueCandidates = this.compareService.getAllComparisonCandidates(filterState);
        final ComparisonCandidate candA = this.compareService.parseFormattedCandidate(envAStr, uniqueCandidates);
        final ComparisonCandidate candB = this.compareService.parseFormattedCandidate(envBStr, uniqueCandidates);

        if (candA == null || candB == null)
        {
            // Debug info to help fix the issue
            StringBuilder debug = new StringBuilder();
            debug.append("Debug Info:\n");
            debug.append("envAStr received: '").append(envAStr).append("'\n");
            debug.append("envBStr received: '").append(envBStr).append("'\n");
            debug.append("uniqueCandidates size: ").append(uniqueCandidates.size()).append("\n");
            if (!uniqueCandidates.isEmpty()) {
                debug.append("First candidate formatted: '").append(this.compareService.formatCandidate(uniqueCandidates.iterator().next())).append("'\n");
            }
            model.addAttribute("debugInfo", debug.toString());
            
            // Invalid selection
            model.addAttribute("results", List.of());
            return "fragments/compare-results :: results";
        }

        final List<ComparisonRow> results = this.compareService.compareCandidates(filterState, candA, candB);
        
        model.addAttribute("results", results);

        return "fragments/compare-results :: results";
    }
}
