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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import org.onebrc.benchmarkviewer.domain.EnvironmentKey;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TestRun;
import org.onebrc.benchmarkviewer.repository.TestRunRepository;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for the run-detail view (variation matrix).
 *
 * <p>Renders a grouped matrix of measurements for a single test run,
 * keyed by {@link EnvironmentKey} (JDK, GC, VM options, etc.) on the rows
 * and class names on the columns. Supports HTMX partial responses and
 * faceted sidebar filtering via {@link SearchService}.</p>
 */
@Controller
public class RunDetailController
{
    private final TestRunRepository testRunRepository;
    private final SearchService searchService;

    /**
     * Construct the run-detail controller.
     *
     * @param testRunRepository repository for looking up test runs
     * @param searchService     search service for filtered queries and facets
     */
    public RunDetailController(final TestRunRepository testRunRepository,
                               final SearchService searchService)
    {
        this.testRunRepository = testRunRepository;
        this.searchService = searchService;
    }

    /**
     * Render the variation matrix for a given test run.
     *
     * <p>Groups all measurements by their environment configuration and
     * computes per-column and global min/max values for heatmap coloring.
     * When the request originates from HTMX, returns only the matrix and
     * sidebar fragments; otherwise returns the full layout-decorated page.</p>
     *
     * @param timestampStr ISO-8601 formatted timestamp identifying the test run
     * @param hxRequest    HTMX request header, present when the call is an HTMX partial
     * @param filterState  current sidebar filter selections bound from query parameters
     * @param model        the Spring MVC model
     * @return the Thymeleaf view name
     */
    @GetMapping("/runs/{timestampStr}")
    public String getRunDetail(
        @PathVariable("timestampStr") final String timestampStr,
        @RequestHeader(value = "HX-Request", required = false) final String hxRequest,
        final FilterState filterState,
        final Model model)
    {
        final LocalDateTime timestamp;
        try
        {
            timestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
        catch (final DateTimeParseException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid timestamp format");
        }

        final TestRun testRun = this.testRunRepository.findByTimestamp(timestamp)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Test run not found"));

        // Group measurements into a matrix: EnvironmentKey -> (ClassName -> Measurement)
        final Map<EnvironmentKey, Map<String, Measurement>> matrix = new TreeMap<>();
        final TreeSet<String> classes = new TreeSet<>();

        // Track per-column and global min/max for heatmap normalization
        final Map<String, Double> columnMin = new TreeMap<>();
        final Map<String, Double> columnMax = new TreeMap<>();
        double globalMin = Double.MAX_VALUE;
        double globalMax = Double.MIN_VALUE;

        final List<Measurement> measurements =
            this.searchService.searchMeasurements(filterState, testRun.getId(), null);

        for (final Measurement m : measurements)
        {
            final String cls = m.getClassName();
            classes.add(cls);

            if (!m.isError())
            {
                final double val = m.getMedianRuntimeMs();
                columnMin.put(cls, Math.min(columnMin.getOrDefault(cls, Double.MAX_VALUE), val));
                columnMax.put(cls, Math.max(columnMax.getOrDefault(cls, Double.MIN_VALUE), val));
                globalMin = Math.min(globalMin, val);
                globalMax = Math.max(globalMax, val);
            }

            final EnvironmentKey env = new EnvironmentKey(
                m.getJdk() != null ? m.getJdk() : "",
                m.getGcOpts() != null ? m.getGcOpts() : "",
                m.getVmOpts() != null ? m.getVmOpts() : "",
                m.getProgOpts() != null ? m.getProgOpts() : "",
                m.getBinding() != null ? m.getBinding() : "",
                m.getDataset() != null ? m.getDataset() : ""
            );

            matrix.computeIfAbsent(env, k -> new TreeMap<>()).put(cls, m);
        }

        model.addAttribute("testRun", testRun);
        model.addAttribute("classes", classes);
        model.addAttribute("matrix", matrix);
        model.addAttribute("columnMin", columnMin);
        model.addAttribute("columnMax", columnMax);
        model.addAttribute("globalMin", globalMin == Double.MAX_VALUE ? 0.0 : globalMin);
        model.addAttribute("globalMax", globalMax == Double.MIN_VALUE ? 0.0 : globalMax);

        // Also add facets for OOB sidebar update
        final var facets = this.searchService.getFacetCounts(filterState, testRun.getId(), null);
        model.addAttribute("facets", facets);
        model.addAttribute("activeFilters", filterState);
        model.addAttribute("filterActionUrl", "/runs/" + timestampStr);

        if ("true".equals(hxRequest))
        {
            return "run-detail :: htmx-response";
        }

        return "run-detail";
    }
}
