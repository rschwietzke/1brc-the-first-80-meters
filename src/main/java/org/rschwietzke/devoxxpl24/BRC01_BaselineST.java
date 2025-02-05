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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.rschwietzke.Benchmark;

/**
 * This is a rewritten version to expand every step a little to make it more clear
 * and have a better handle on tuning.
 */
public class BRC01_BaselineST extends Benchmark {

    private static record Measurement(String station, double value)
    {
        private Measurement(String[] parts)
        {
            this(parts[0], Double.parseDouble(parts[1]));
        }
    }

    private static record ResultRow(int count, double min, double mean, double max)
    {
        public String toString()
        {
            return count + "/" + round(min) + "/" + round(mean) + "/" + round(max);
        }

        private double round(double value)
        {
            return Math.round(value * 10.0) / 10.0;
        }
    };

    private static class MeasurementAggregator
    {
        private double min = Double.POSITIVE_INFINITY;
        private double max = Double.NEGATIVE_INFINITY;
        private double total;
        private int count;
    }

    @Override
    public String run(final String fileName) throws IOException
    {
        final Collector<Measurement, MeasurementAggregator, ResultRow> collector =
        		Collector.of(MeasurementAggregator::new,
                (agg, m) ->
        		{
                    agg.min = Math.min(agg.min, m.value);
                    agg.max = Math.max(agg.max, m.value);
                    agg.total += m.value;
                    agg.count++;
                },
        		(agg1, agg2) ->
                {
                    var res = new MeasurementAggregator();
                    res.min = Math.min(agg1.min, agg2.min);
                    res.max = Math.max(agg1.max, agg2.max);
                    res.total = agg1.total + agg2.total;
                    res.count = agg1.count + agg2.count;

                    return res;
                },
                agg ->
                {
                    return new ResultRow(agg.count, agg.min, agg.total / (double)agg.count, agg.max);
                });

        var result = Files.lines(Paths.get(fileName))
        		.map(l -> l.split(";"))
                .map(l -> new Measurement(l))
                .collect(Collectors.groupingByConcurrent(m -> m.station(), collector));

        return new TreeMap<>(result).toString();
    }

    public static void main(String[] args)
    {
		Benchmark.run(BRC01_BaselineST.class, args);
    }
}
