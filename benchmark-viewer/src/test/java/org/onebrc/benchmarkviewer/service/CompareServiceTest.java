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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.onebrc.benchmarkviewer.domain.ComparisonRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompareServiceTest
{
    @Test
    @DisplayName("7.1: Compute correct delta Ms and percentage")
    void computeDeltas()
    {
        final ComparisonRow row = ComparisonRow.compute("BRC01", 100.0, 150.0);
        assertThat(row.deltaMs()).isEqualTo(50.0);
        assertThat(row.deltaPct()).isEqualTo(50.0); // 50% regression
    }

    @Test
    @DisplayName("7.1: Compute improvement (negative delta)")
    void computeImprovement()
    {
        final ComparisonRow row = ComparisonRow.compute("BRC01", 200.0, 100.0);
        assertThat(row.deltaMs()).isEqualTo(-100.0);
        assertThat(row.deltaPct()).isEqualTo(-50.0); // 50% improvement
    }

    @Test
    @DisplayName("7.1: Handle zero runtime properly")
    void computeZeroHandling()
    {
        final ComparisonRow rowNew = ComparisonRow.compute("BRC01", 0.0, 150.0);
        assertThat(rowNew.deltaPct()).isEqualTo(Double.POSITIVE_INFINITY);

        final ComparisonRow rowMissing = ComparisonRow.compute("BRC01", 100.0, 0.0);
        assertThat(rowMissing.deltaPct()).isEqualTo(-100.0);

        final ComparisonRow rowZeroBoth = ComparisonRow.compute("BRC01", 0.0, 0.0);
        assertThat(rowZeroBoth.deltaPct()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("7.2: Delta sorting - biggest regressions first")
    void testDeltaSorting()
    {
        final List<ComparisonRow> results = new ArrayList<>();
        results.add(ComparisonRow.compute("Imp10", 100.0, 90.0)); // -10%
        results.add(ComparisonRow.compute("Reg50", 100.0, 150.0)); // +50%
        results.add(ComparisonRow.compute("Imp50", 100.0, 50.0)); // -50%
        results.add(ComparisonRow.compute("Reg20", 100.0, 120.0)); // +20%

        results.sort(Comparator.comparing(ComparisonRow::deltaPct).reversed());

        assertThat(results.get(0).className()).isEqualTo("Reg50");
        assertThat(results.get(1).className()).isEqualTo("Reg20");
        assertThat(results.get(2).className()).isEqualTo("Imp10");
        assertThat(results.get(3).className()).isEqualTo("Imp50");
    }
}
