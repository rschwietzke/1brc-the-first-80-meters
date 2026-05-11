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

/**
 * Data transfer object that holds time-series trend data for a single
 * environment configuration of a test case. Each list is parallel — the
 * same index across all lists corresponds to the same run timestamp.
 *
 * @param timestamps       run timestamps (formatted for display, ascending order)
 * @param runtime          median runtime in milliseconds
 * @param ipc              instructions per cycle
 * @param instructions     total instructions executed
 * @param cycles           total CPU cycles
 * @param branches         total branches evaluated
 * @param branchMisses     total branch mispredictions
 * @param l1Misses         L1 data cache load misses
 * @param llcMisses        last-level cache misses
 * @param pageFaults       page faults
 * @param contextSwitches  context switches
 * @param gcPauseMs        total GC pause time in milliseconds
 * @param allocatedBytes   total allocated bytes
 * @param jitCompilationMs JIT compilation time in milliseconds
 * @param errors           whether each run was an error
 */
public record TrendData(
    List<String> timestamps,
    List<Double> runtime,
    List<Double> ipc,
    List<Long> instructions,
    List<Long> cycles,
    List<Long> branches,
    List<Long> branchMisses,
    List<Long> l1Misses,
    List<Long> llcMisses,
    List<Long> pageFaults,
    List<Long> contextSwitches,
    List<Double> gcPauseMs,
    List<Long> allocatedBytes,
    List<Double> jitCompilationMs,
    List<Boolean> errors
)
{
}
