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
 * Holds side-by-side comparison data for a single class between two environments.
 */
public record ComparisonRow(
    String className,
    Double runtimeA,
    Double runtimeB,
    Double deltaMs,
    Double deltaPct,
    boolean hiddenSignal
)
{
    /**
     * Checks if this is a newly introduced regression (baseline runtime was 0, new runtime > 0).
     */
    public boolean isNewRegression()
    {
        return this.deltaPct == Double.POSITIVE_INFINITY;
    }

    /**
     * Factory method to compute a ComparisonRow from two raw runtime values.
     *
     * @param className the class name
     * @param runtimeA  median runtime from environment A
     * @param runtimeB  median runtime from environment B
     * @return a computed ComparisonRow
     */
    public static ComparisonRow compute(final String className, final Double runtimeA, final Double runtimeB)
    {
        return compute(className, runtimeA, runtimeB, false);
    }

    /**
     * Factory method to compute a ComparisonRow with explicit hiddenSignal status.
     */
    public static ComparisonRow compute(final String className, final Double runtimeA, final Double runtimeB, final boolean hiddenSignal)
    {
        final Double safeA = runtimeA != null ? runtimeA : 0.0;
        final Double safeB = runtimeB != null ? runtimeB : 0.0;

        final double deltaMs = safeB - safeA;
        final double deltaPct;

        if (safeA == 0.0)
        {
            if (safeB == 0.0)
            {
                deltaPct = 0.0;
            }
            else
            {
                deltaPct = Double.POSITIVE_INFINITY; // new regression
            }
        }
        else
        {
            deltaPct = (deltaMs / safeA) * 100.0;
        }

        return new ComparisonRow(className, safeA, safeB, deltaMs, deltaPct, hiddenSignal);
    }

    /**
     * Checks for hidden signal anomalies between two measurements.
     */
    public static boolean checkHiddenSignal(final Measurement a, final Measurement b, final double deltaPct)
    {
        if (a == null || b == null)
        {
            return false;
        }

        // Stable runtime: absolute percentage change < 5%
        if (Math.abs(deltaPct) >= 5.0)
        {
            return false;
        }

        // Check counter shifts > 20%
        return isShifted(a.getInstructions(), b.getInstructions())
            || isShifted(a.getCycles(), b.getCycles())
            || isShifted(a.getBranches(), b.getBranches())
            || isShifted(a.getBranchMisses(), b.getBranchMisses())
            || isShifted(a.getL1Misses(), b.getL1Misses())
            || isShifted(a.getLlcMisses(), b.getLlcMisses())
            || isShifted(a.getPageFaults(), b.getPageFaults())
            || isShifted(a.getContextSwitches(), b.getContextSwitches())
            || isShifted(a.getGcPauseMs(), b.getGcPauseMs())
            || isShifted(a.getAllocatedBytes(), b.getAllocatedBytes())
            || isShifted(a.getJitCompilationMs(), b.getJitCompilationMs());
    }

    private static boolean isShifted(final double a, final double b)
    {
        if (a == 0.0)
        {
            return false;
        }
        final double pct = ((b - a) / a) * 100.0;
        return Math.abs(pct) > 20.0;
    }

    private static boolean isShifted(final long a, final long b)
    {
        if (a == 0L)
        {
            return false;
        }
        final double pct = ((double)(b - a) / a) * 100.0;
        return Math.abs(pct) > 20.0;
    }
}
