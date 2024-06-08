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
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.rschwietzke.Benchmark;

/**
 * Control the grouping yourselves
 */
public class BRC02_NoGroupingST extends Benchmark {

	private static record Measurement(String station, double value)
	{
		private Measurement(String[] parts)
		{
			this(parts[0], Double.parseDouble(parts[1]));
		}
	}

	private static class MeasurementAggregator
	{
		private double min = Double.POSITIVE_INFINITY;
		private double max = Double.NEGATIVE_INFINITY;
		private double sum;
		private long count;

		public MeasurementAggregator(final Measurement m)
		{
			this.min = m.value;
			this.max = m.value;
			this.sum = m.value;
			this.count = 1;
		}

		public String toString()
		{
			var mean = Math.round(this.sum * 10.0) / 10.0 / this.count;
			return round(min) + "," + round(mean) + "," + round(max);
		}

		private double round(double value)
		{
			return Math.round(value * 10.0) / 10.0;
		}
	}

	@Override
	public String run(final String fileName) throws IOException {
		final var map = new ConcurrentHashMap<String, MeasurementAggregator>();

		final BiFunction<MeasurementAggregator, MeasurementAggregator, MeasurementAggregator> collector = (agg1, agg2) -> {
			agg1.min = Math.min(agg1.min, agg2.min);
			agg1.max = Math.max(agg1.max, agg2.max);
			agg1.sum = agg1.sum + agg2.sum;
			agg1.count = agg1.count + agg2.count;

			return agg1;
		};

		Files.lines(Paths.get(fileName))
			.map(l -> l.split(";"))
			.map(l -> new Measurement(l))
			.forEach(m -> map.merge(m.station, new MeasurementAggregator(m), collector));

		return new TreeMap<>(map).toString();
	}

	public static void main(String[] args)
	{
		Benchmark.run(BRC02_NoGroupingST.class, args);
	}
}
