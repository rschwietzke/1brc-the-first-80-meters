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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HeatColorUtilTest
{
    @Test
    @DisplayName("4.2: Normalizes correctly mapping min to green and max to red")
    void testCalculateColorMinMaxMid()
    {
        final double min = 100.0;
        final double max = 200.0;
        
        // Min value -> Green (Hue 120)
        assertThat(HeatColorUtil.calculateColor(min, min, max)).isEqualTo("hsl(120, 70%, 80%)");
        
        // Max value -> Red (Hue 0)
        assertThat(HeatColorUtil.calculateColor(max, min, max)).isEqualTo("hsl(0, 70%, 80%)");
        
        // Mid value -> Yellow (Hue 60)
        assertThat(HeatColorUtil.calculateColor(150.0, min, max)).isEqualTo("hsl(60, 70%, 80%)");
    }

    @Test
    @DisplayName("4.3: Handles uniform min/max edge cases safely")
    void testCalculateColorEdgeCases()
    {
        // When all values are the same, should return the base green color safely
        assertThat(HeatColorUtil.calculateColor(100.0, 100.0, 100.0)).isEqualTo("hsl(120, 70%, 80%)");
        
        // Out of bounds values should clamp
        assertThat(HeatColorUtil.calculateColor(50.0, 100.0, 200.0)).isEqualTo("hsl(120, 70%, 80%)");
        assertThat(HeatColorUtil.calculateColor(250.0, 100.0, 200.0)).isEqualTo("hsl(0, 70%, 80%)");
    }

    @Test
    @DisplayName("4.4: ERROR values map to distinct grey color")
    void testCalculateColorError()
    {
        // NaN should map to distinct grey
        assertThat(HeatColorUtil.calculateColor(Double.NaN, 100.0, 200.0)).isEqualTo("hsl(0, 0%, 90%)");
    }
}
