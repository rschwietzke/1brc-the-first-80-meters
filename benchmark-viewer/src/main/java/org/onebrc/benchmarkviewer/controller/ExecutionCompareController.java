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

import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.onebrc.benchmarkviewer.repository.MeasurementRepository;
import org.onebrc.benchmarkviewer.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controller for the side-by-side execution comparison view.
 */
@Controller
@Transactional(readOnly = true)
public class ExecutionCompareController
{
    private final MeasurementRepository measurementRepository;
    private final SearchService searchService;

    public ExecutionCompareController(final MeasurementRepository measurementRepository, final SearchService searchService)
    {
        this.measurementRepository = measurementRepository;
        this.searchService = searchService;
    }

    @GetMapping("/compare-executions")
    public String compare(
        @RequestParam("m1") final Long m1Id,
        @RequestParam("m2") final Long m2Id,
        final FilterState filterState,
        final Model model)
    {
        final Measurement m1 = this.measurementRepository.findById(m1Id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Measurement 1 not found"));
        final Measurement m2 = this.measurementRepository.findById(m2Id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Measurement 2 not found"));

        // Eagerly initialize the lazy TestRun proxies before the transaction closes
        m1.getTestRun().getHostname();
        m2.getTestRun().getHostname();

        model.addAttribute("m1", m1);
        model.addAttribute("m2", m2);

        final var facets = this.searchService.getFacetCounts(filterState, null, m1.getClassName());
        model.addAttribute("facets", facets);
        model.addAttribute("activeFilters", filterState);
        model.addAttribute("filterActionUrl", "/compare-executions?m1=" + m1Id + "&m2=" + m2Id);

        // Calculate differences
        model.addAttribute("runtimeDiff", m2.getMedianRuntimeMs() - m1.getMedianRuntimeMs());
        model.addAttribute("runtimePct", m1.getMedianRuntimeMs() > 0 ? ((m2.getMedianRuntimeMs() - m1.getMedianRuntimeMs()) / m1.getMedianRuntimeMs()) * 100 : 0);

        model.addAttribute("instrDiff", m2.getInstructions() - m1.getInstructions());
        model.addAttribute("cyclesDiff", m2.getCycles() - m1.getCycles());
        model.addAttribute("branchesDiff", m2.getBranches() - m1.getBranches());
        model.addAttribute("branchMissesDiff", m2.getBranchMisses() - m1.getBranchMisses());
        model.addAttribute("l1MissesDiff", m2.getL1Misses() - m1.getL1Misses());
        model.addAttribute("llcMissesDiff", m2.getLlcMisses() - m1.getLlcMisses());
        model.addAttribute("pageFaultsDiff", m2.getPageFaults() - m1.getPageFaults());
        model.addAttribute("taskClockDiff", m2.getTaskClock() - m1.getTaskClock());
        model.addAttribute("contextSwitchesDiff", m2.getContextSwitches() - m1.getContextSwitches());
        model.addAttribute("cpuMigrationsDiff", m2.getCpuMigrations() - m1.getCpuMigrations());
        model.addAttribute("secElapsedDiff", m2.getSecElapsed() - m1.getSecElapsed());
        model.addAttribute("secUserDiff", m2.getSecUser() - m1.getSecUser());
        model.addAttribute("secSysDiff", m2.getSecSys() - m1.getSecSys());
        model.addAttribute("gcPauseMsDiff", m2.getGcPauseMs() - m1.getGcPauseMs());
        model.addAttribute("allocatedBytesDiff", m2.getAllocatedBytes() - m1.getAllocatedBytes());
        model.addAttribute("jitCompilationMsDiff", m2.getJitCompilationMs() - m1.getJitCompilationMs());

        return "compare-executions";
    }
}
