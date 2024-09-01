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
package org.rschwietzke.devoxxpl24;

import static java.util.stream.Collectors.groupingBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import org.rschwietzke.Benchmark;

/**
 * This is a rewritten version to expand every step a little to make it more clear
 * and have a better handle on tuning.
 */
public class BRC01_BaselineMT extends Benchmark
{
    private static record Measurement(String station, double value)
    {
        private Measurement(String[] parts)
        {
            this(parts[0], Double.parseDouble(parts[1]));
        }
    }

    private static record ResultRow(double min, double mean, double max)
    {
        public String toString()
        {
            return round(min) + "," + round(mean) + "," + round(max);
        }

        private double round(double value)
        {
            return Math.round(value * 10.0) / 10.0;
        }
    }

    private static class MeasurementAggregator
    {
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        private double sum;
        private long count;
    }

    @Override
    public String run(final String fileName) throws IOException {
        Collector<Measurement, MeasurementAggregator, ResultRow> collector = Collector.of(MeasurementAggregator::new,
                (agg, m) -> {
                    agg.min = Math.min(agg.min, m.value);
                    agg.max = Math.max(agg.max, m.value);
                    agg.sum += m.value;
                    agg.count++;
                }, (agg1, agg2) -> {
                    var res = new MeasurementAggregator();
                    res.min = Math.min(agg1.min, agg2.min);
                    res.max = Math.max(agg1.max, agg2.max);
                    res.sum = agg1.sum + agg2.sum;
                    res.count = agg1.count + agg2.count;

                    return res;
                }, agg -> {
                    return new ResultRow(agg.min, agg.sum / agg.count, agg.max);
                },
                Characteristics.CONCURRENT);

        var result = Files.lines(Paths.get(fileName))
        		.parallel()
        		.map(l -> l.split(";"))
                .map(l -> new Measurement(l))
                .collect(groupingBy(m -> m.station(), collector));

        return new TreeMap<>(result).toString();
    }

    public static void main(String[] args)
    {
		Benchmark.run(BRC01_BaselineMT.class, args);
    }
}
