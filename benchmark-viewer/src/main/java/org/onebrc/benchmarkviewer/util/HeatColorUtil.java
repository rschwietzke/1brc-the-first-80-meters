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

package org.onebrc.benchmarkviewer.util;

/**
 * Utility for calculating heat map colors based on benchmark runtimes.
 * Generates an HSL color string where faster (lower) times are cooler/greener
 * and slower (higher) times are warmer/redder.
 */
public final class HeatColorUtil
{
    private HeatColorUtil() {}

    /**
     * Calculate an HSL color representation for a given value between min and max.
     * 120 (Green) for min value, 0 (Red) for max value.
     *
     * @param value The median runtime value
     * @param min   The minimum runtime in the dataset
     * @param max   The maximum runtime in the dataset
     * @return CSS HSL color string
     */
    public static String calculateColor(final double value, final double min, final double max)
    {
        if (Double.isNaN(value) || Double.isInfinite(value))
        {
            return "hsl(0, 0%, 90%)"; // Error or invalid
        }
        if (max == min)
        {
            return "hsl(120, 70%, 80%)"; // Uniform baseline
        }

        // Normalize value between 0.0 (min) and 1.0 (max)
        final double normalized = Math.max(0.0, Math.min(1.0, (value - min) / (max - min)));
        
        // Map 0.0 -> 120 (Green)
        // Map 1.0 -> 0 (Red)
        final int hue = (int) (120 * (1.0 - normalized));
        
        return String.format("hsl(%d, 70%%, 80%%)", hue);
    }
}
