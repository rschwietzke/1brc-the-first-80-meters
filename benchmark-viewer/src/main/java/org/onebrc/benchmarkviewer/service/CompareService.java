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

package org.onebrc.benchmarkviewer.service;

import org.onebrc.benchmarkviewer.domain.ComparisonCandidate;
import org.onebrc.benchmarkviewer.domain.ComparisonRow;
import org.onebrc.benchmarkviewer.domain.EnvironmentKey;
import org.onebrc.benchmarkviewer.domain.FilterState;
import org.onebrc.benchmarkviewer.domain.Measurement;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Service for computing delta comparisons between any two candidates globally.
 */
@Service
public class CompareService
{
    private final SearchService searchService;

    public CompareService(final SearchService searchService)
    {
        this.searchService = searchService;
    }

    /**
     * Converts a ComparisonCandidate to a human-readable string format suitable for dropdowns.
     * Format: Timestamp | JDK | GC | VM | Prog | Binding | Dataset
     */
    public String formatCandidate(final ComparisonCandidate candidate)
    {
        final List<String> parts = new ArrayList<>();
        
        // Add timestamp nicely formatted
        if (candidate.runTimestamp() != null)
        {
            parts.add(candidate.runTimestamp().replace('T', ' '));
        }

        final EnvironmentKey key = candidate.environmentKey();
        if (!key.jdk().isBlank())
        {
            parts.add(key.jdk());
        }
        if (!key.gcOpts().isBlank())
        {
            parts.add(key.gcOpts());
        }
        if (!key.vmOpts().isBlank())
        {
            parts.add(key.vmOpts());
        }
        if (!key.progOpts().isBlank())
        {
            parts.add(key.progOpts());
        }
        if (!key.binding().isBlank())
        {
            parts.add(key.binding());
        }
        if (!key.dataset().isBlank())
        {
            parts.add(key.dataset());
        }
        return String.join(" | ", parts);
    }

    /**
     * Reconstructs a ComparisonCandidate from a formatted string by matching.
     */
    public ComparisonCandidate parseFormattedCandidate(final String formatted, final Set<ComparisonCandidate> availableCandidates)
    {
        if (formatted == null || availableCandidates == null)
        {
            return null;
        }

        for (final ComparisonCandidate candidate : availableCandidates)
        {
            if (this.formatCandidate(candidate).equals(formatted))
            {
                return candidate;
            }
        }
        return null;
    }

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
     * Get all unique ComparisonCandidates globally (across all runs), respecting filters.
     */
    public Set<ComparisonCandidate> getAllComparisonCandidates(final FilterState filterState)
    {
        final List<Measurement> allFiltered = this.searchService.searchMeasurements(filterState, null, null);
        final Set<ComparisonCandidate> candidates = new TreeSet<>();
        
        for (final Measurement m : allFiltered)
        {
            final EnvironmentKey key = this.extractKey(m);
            
            // Format timestamp nicely for the label. If it's full ISO, just take up to minutes
            final String ts = m.getRunTimestamp();
            final String shortTs = ts != null && ts.length() >= 16 ? ts.substring(0, 16) : ts;
            
            candidates.add(new ComparisonCandidate(m.getTestRunId(), shortTs, key));
        }
        
        return candidates;
    }

    /**
     * Compares two candidates and returns a list of delta rows sorted by largest regression first.
     */
    public List<ComparisonRow> compareCandidates(
        final FilterState filterState,
        final ComparisonCandidate candA,
        final ComparisonCandidate candB)
    {
        // Fetch all measurements matching the filter state, bypassing potential testRunId index matching issues
        final List<Measurement> allMeasurements = this.searchService.searchMeasurements(filterState, null, null);

        final Map<String, Measurement> measurementsA = new TreeMap<>();
        final Map<String, Measurement> measurementsB = new TreeMap<>();

        for (final Measurement m : allMeasurements)
        {
            if (m.isError())
            {
                continue;
            }

            final EnvironmentKey key = this.extractKey(m);
            final Long runId = m.getTestRunId();

            if (runId != null && runId.equals(candA.testRunId()) && key.equals(candA.environmentKey()))
            {
                measurementsA.put(m.getClassName(), m);
            }
            
            if (runId != null && runId.equals(candB.testRunId()) && key.equals(candB.environmentKey()))
            {
                measurementsB.put(m.getClassName(), m);
            }
        }

        final Set<String> allClasses = new TreeSet<>(measurementsA.keySet());
        allClasses.addAll(measurementsB.keySet());

        final List<ComparisonRow> results = new ArrayList<>();
        for (final String className : allClasses)
        {
            final Measurement a = measurementsA.get(className);
            final Measurement b = measurementsB.get(className);
            final Double runtimeA = a != null ? a.getMedianRuntimeMs() : 0.0;
            final Double runtimeB = b != null ? b.getMedianRuntimeMs() : 0.0;

            final double deltaMs = runtimeB - runtimeA;
            final double deltaPct;

            if (runtimeA == 0.0)
            {
                if (runtimeB == 0.0)
                {
                    deltaPct = 0.0;
                }
                else
                {
                    deltaPct = Double.POSITIVE_INFINITY;
                }
            }
            else
            {
                deltaPct = (deltaMs / runtimeA) * 100.0;
            }

            final boolean hiddenSignal = ComparisonRow.checkHiddenSignal(a, b, deltaPct);
            results.add(ComparisonRow.compute(className, runtimeA, runtimeB, hiddenSignal));
        }

        results.sort(Comparator.comparing(ComparisonRow::deltaPct).reversed());

        return results;
    }
}
