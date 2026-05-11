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

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.onebrc.benchmarkviewer.domain.EnvironmentKey;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.domain.TrendData;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Controller for the test case trend analysis page.
 *
 * <p>Renders interactive ECharts time-series charts for all metrics of a
 * given test case (identified by fully-qualified class name), grouped by
 * environment configuration. The user selects an environment via a dropdown
 * to view its charts.</p>
 */
@Controller
@Transactional(readOnly = true)
public class TestCaseTrendController
{
    /** Display format for timestamps on chart x-axes. */
    private static final DateTimeFormatter CHART_TS_FORMATTER =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final SearchService searchService;

    /**
     * Construct the trend controller.
     *
     * @param searchService search service for querying measurements
     */
    public TestCaseTrendController(final SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Render the trend analysis page for a given test case.
     *
     * <p>Fetches all measurements matching the FQCN, groups them by
     * {@link EnvironmentKey}, sorts each group chronologically, and
     * builds {@link TrendData} DTOs for chart rendering.</p>
     *
     * @param className the fully-qualified class name (FQCN) of the test case
     * @param hxRequest HTMX request header, present when the call is an HTMX partial
     * @param model     the Spring MVC model
     * @return the Thymeleaf view name
     */
    @GetMapping("/testcase/{className}/trends")
    public String getTrends(
        @PathVariable("className") final String className,
        @RequestHeader(value = "HX-Request", required = false) final String hxRequest,
        final Model model)
    {
        // Fetch all measurements for this class name across all runs
        final List<Measurement> allMeasurements =
            this.searchService.searchMeasurements(null, null, className);

        // Group by EnvironmentKey
        final Map<EnvironmentKey, List<Measurement>> grouped = new TreeMap<>();
        for (final Measurement m : allMeasurements)
        {
            final EnvironmentKey key = this.extractKey(m);
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(m);
        }

        // Sort each group by timestamp ascending and build TrendData
        final Map<String, EnvironmentKey> environmentLabels = new LinkedHashMap<>();
        final Map<String, TrendData> trendDataMap = new LinkedHashMap<>();

        int envIndex = 0;
        for (final Map.Entry<EnvironmentKey, List<Measurement>> entry : grouped.entrySet())
        {
            final EnvironmentKey envKey = entry.getKey();
            final List<Measurement> measurements = entry.getValue();

            // Sort by test run timestamp ascending
            measurements.sort((a, b) ->
                a.getTestRun().getTimestamp().compareTo(b.getTestRun().getTimestamp()));

            final TrendData trend = this.buildTrendData(measurements);

            // Create a stable key for the environment (for use in template/JS)
            final String envId = "env-" + envIndex;
            environmentLabels.put(envId, envKey);
            trendDataMap.put(envId, trend);

            envIndex++;
        }

        model.addAttribute("className", className);
        model.addAttribute("simpleClassName",
            className.substring(className.lastIndexOf('.') + 1));
        model.addAttribute("environments", environmentLabels);
        model.addAttribute("trendDataMap", trendDataMap);

        if ("true".equals(hxRequest))
        {
            return "testcase-trends :: htmx-response";
        }

        return "testcase-trends";
    }

    /**
     * Extract an {@link EnvironmentKey} from a measurement, substituting
     * empty strings for null values.
     *
     * @param m the measurement
     * @return a non-null environment key
     */
    private EnvironmentKey extractKey(final Measurement m)
    {
        return new EnvironmentKey(
            m.getJdk() != null ? m.getJdk() : "",
            m.getGcOpts() != null ? m.getGcOpts() : "",
            m.getVmOpts() != null ? m.getVmOpts() : "",
            m.getProgOpts() != null ? m.getProgOpts() : "",
            m.getBinding() != null ? m.getBinding() : "",
            m.getDataset() != null ? m.getDataset() : ""
        );
    }

    /**
     * Build a {@link TrendData} DTO from a list of measurements already
     * sorted chronologically.
     *
     * @param measurements sorted measurements for a single environment
     * @return the trend data DTO with parallel lists
     */
    private TrendData buildTrendData(final List<Measurement> measurements)
    {
        final List<String> timestamps = new ArrayList<>();
        final List<Double> runtime = new ArrayList<>();
        final List<Double> ipc = new ArrayList<>();
        final List<Long> instructions = new ArrayList<>();
        final List<Long> cycles = new ArrayList<>();
        final List<Long> branches = new ArrayList<>();
        final List<Long> branchMisses = new ArrayList<>();
        final List<Long> l1Misses = new ArrayList<>();
        final List<Long> llcMisses = new ArrayList<>();
        final List<Long> pageFaults = new ArrayList<>();
        final List<Long> contextSwitches = new ArrayList<>();
        final List<Double> gcPauseMs = new ArrayList<>();
        final List<Long> allocatedBytes = new ArrayList<>();
        final List<Double> jitCompilationMs = new ArrayList<>();
        final List<Boolean> errors = new ArrayList<>();

        for (final Measurement m : measurements)
        {
            timestamps.add(m.getTestRun().getTimestamp().format(CHART_TS_FORMATTER));
            runtime.add(m.getMedianRuntimeMs());
            ipc.add(m.getIpc());
            instructions.add(m.getInstructions());
            cycles.add(m.getCycles());
            branches.add(m.getBranches());
            branchMisses.add(m.getBranchMisses());
            l1Misses.add(m.getL1Misses());
            llcMisses.add(m.getLlcMisses());
            pageFaults.add(m.getPageFaults());
            contextSwitches.add(m.getContextSwitches());
            gcPauseMs.add(m.getGcPauseMs());
            allocatedBytes.add(m.getAllocatedBytes());
            jitCompilationMs.add(m.getJitCompilationMs());
            errors.add(m.isError());
        }

        return new TrendData(
            timestamps, runtime, ipc, instructions, cycles,
            branches, branchMisses, l1Misses, llcMisses,
            pageFaults, contextSwitches, gcPauseMs, allocatedBytes,
            jitCompilationMs, errors
        );
    }
}
