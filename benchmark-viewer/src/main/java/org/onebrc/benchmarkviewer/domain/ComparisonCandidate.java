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

package org.onebrc.benchmarkviewer.domain;

/**
 * Represents a unique combination of a test run and an environment,
 * used as a candidate for side-by-side comparison.
 */
public record ComparisonCandidate(
    Long testRunId,
    String runTimestamp,
    EnvironmentKey environmentKey
) implements Comparable<ComparisonCandidate>
{
    @Override
    public int compareTo(ComparisonCandidate o)
    {
        // Sort descending by timestamp first
        int cmp = o.runTimestamp().compareTo(this.runTimestamp());
        if (cmp != 0)
        {
            return cmp;
        }
        // Then by environment key
        return this.environmentKey().compareTo(o.environmentKey());
    }
}
