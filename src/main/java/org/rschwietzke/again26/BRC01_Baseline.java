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
package org.rschwietzke.again26;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.rschwietzke.Benchmark;

/**
 * This is a baseline implementation using only Java standard library to perform the task.
 * This matches the original version by Gunnar but it is the single threaded version only.
 * 
 * https://github.com/gunnarmorling/1brc/blob/main/src/main/java/dev/morling/onebrc/CalculateAverage_baseline.java
 * 
 * It has been slightly modified to use our Benchmark framework and fit my personal formatting style.
 */
public class BRC01_Baseline extends Benchmark 
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
            // This output format is different from the original Gunnar version
            // We also print the count to ensure that we are not missing any data
            // because with a billion rows, a few missing items stay unnoticed.
            return 
                    count + "/"
                    + round(min) + "/" 
                    + round(sum / count) + "/" 
                    + round(max);
        }

        private double round(double value) 
        {
            // that is also different from Gunnar's version, to ensure proper parsing
            // we make things more precise here and output with three decimal digit !!!
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

        Map<String, ResultRow> measurements = new TreeMap<>(Files.lines(Paths.get(fileName))
                .map(l -> new Measurement(l.split(";")))
                .collect(
                        Collectors.groupingBy(m -> m.station(), collector))
                );

        return measurements.toString();

    }

    public static void main(String[] args)
    {
        Benchmark.run(BRC01_Baseline.class, args);
    }
}
