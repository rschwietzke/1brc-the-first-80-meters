/*
 *  Copyright 2023 The original authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.rschwietzke.parallel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.rschwietzke.Benchmark;

/**
 * Improved multi-threaded implementation focusing only on execution strategy.
 * 
 * Optimizations:
 * - Runs the parallel stream in a dedicated custom ForkJoinPool to ensure maximum
 *   resource allocation, avoiding the JVM's common pool.
 * - Uses standard groupingBy instead of groupingByConcurrent. For small key spaces 
 *   (~400 stations), avoiding lock contention on a shared concurrent map by merging 
 *   thread-local maps is much faster.
 */
public class BRC015_ParallelGemini extends Benchmark 
{
    private static record Measurement(String station, double value) 
    {
        private Measurement(String[] parts) 
        {
            this(parts[0], Double.parseDouble(parts[1]));
        }
    }

    private static record ResultRow(long count, double sum, double min, double max) 
    {
        public String toString() 
        {
            return 
                    count + "/"
                    + round(min) + "/" 
                    + round(sum / count) + "/" 
                    + round(max);
        }

        private double round(double value) 
        {
            return Math.round(value * 1000.0d) / 1000.0d;
        }
    };

    private static class MeasurementAggregator 
    {
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        private double sum;
        private long count;
    }

    @Override
    public String run(final String fileName) throws IOException
    {
        Collector<Measurement, MeasurementAggregator, ResultRow> collector = Collector.of(
                MeasurementAggregator::new,
                (a, m) -> 
                {
                    a.min = Math.min(a.min, m.value);
                    a.max = Math.max(a.max, m.value);
                    a.sum += m.value;
                    a.count++;
                },
                (agg1, agg2) -> 
                {
                    var res = new MeasurementAggregator();
                    res.min = Math.min(agg1.min, agg2.min);
                    res.max = Math.max(agg1.max, agg2.max);
                    res.sum = agg1.sum + agg2.sum;
                    res.count = agg1.count + agg2.count;

                    return res;
                },
                agg -> 
                {
                    return new ResultRow(agg.count, agg.sum, agg.min, agg.max);
                });

        // Dedicated thread pool for this processing task
        try (ForkJoinPool customThreadPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors())) 
        {
            Map<String, ResultRow> measurements = customThreadPool.submit(() ->
            {
                try
                {
                    return new TreeMap<String, ResultRow>(Files.lines(Paths.get(fileName))
                            .parallel()
                            .map(l -> new Measurement(l.split(";")))
                            .collect(
                                    // Non-concurrent groupingBy creates thread-local maps and merges them.
                                    // This is faster than groupingByConcurrent for small numbers of unique keys.
                                    Collectors.groupingBy(m -> m.station, collector))
                            );
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }).get();

            return measurements.toString();
        }
        catch (InterruptedException | ExecutionException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args)
    {
        Benchmark.run(BRC015_ParallelGemini.class, args);
    }
}
