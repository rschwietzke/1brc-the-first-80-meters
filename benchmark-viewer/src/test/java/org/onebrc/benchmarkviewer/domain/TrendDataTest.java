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

package org.onebrc.benchmarkviewer.domain;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TrendData} record.
 */
class TrendDataTest
{
    @Test
    @DisplayName("TrendData record stores all parallel lists correctly")
    void testTrendDataConstruction()
    {
        final TrendData data = new TrendData(
            List.of("2026-05-08 21:37", "2026-05-09 15:24"),
            List.of(83.0, 57.0),
            List.of(0.9, 1.03),
            List.of(1_098_513_388L, 1_100_289_725L),
            List.of(1_215_571_539L, 1_063_794_718L),
            List.of(211_957_222L, 210_917_169L),
            List.of(10_543_043L, 10_201_085L),
            List.of(500L, 600L),
            List.of(100L, 200L),
            List.of(700L, 800L),
            List.of(3L, 4L),
            List.of(0.0, 45.0),
            List.of(21_879_136L, 21_878_776L),
            List.of(0.0, 123.4),
            List.of(false, false)
        );

        assertThat(data.timestamps()).hasSize(2);
        assertThat(data.runtime()).containsExactly(83.0, 57.0);
        assertThat(data.ipc()).containsExactly(0.9, 1.03);
        assertThat(data.instructions()).containsExactly(1_098_513_388L, 1_100_289_725L);
        assertThat(data.gcPauseMs()).containsExactly(0.0, 45.0);
        assertThat(data.allocatedBytes()).containsExactly(21_879_136L, 21_878_776L);
        assertThat(data.jitCompilationMs()).containsExactly(0.0, 123.4);
        assertThat(data.errors()).containsExactly(false, false);
    }

    @Test
    @DisplayName("TrendData with empty lists is valid")
    void testTrendDataEmpty()
    {
        final TrendData data = new TrendData(
            List.of(), List.of(), List.of(), List.of(), List.of(),
            List.of(), List.of(), List.of(), List.of(), List.of(),
            List.of(), List.of(), List.of(), List.of(), List.of()
        );

        assertThat(data.timestamps()).isEmpty();
        assertThat(data.runtime()).isEmpty();
    }
}
