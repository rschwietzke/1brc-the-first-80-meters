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
package org.rschwietzke.st;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.rschwietzke.Benchmark;

/**
 * This is our first Example. It will use a regular Java approach
 * from the books, where we just use everything as we find it in the
 * libs without thinking about the impact.
 *
 * @author Rene Schwietzke
 */
public class Example01c_BasicStreamParallel extends Benchmark
{

	private static class Temperatures
	{
		private double min = Double.MAX_VALUE;
		private double max = Double.MIN_VALUE;
		private double total;
		private long count;

		public Temperatures(final double value)
		{
			this.add(value);
		}

		private double round(double value)
		{
			return Math.round(value * 10.0) / 10.0;
		}

		public Temperatures add(final double value)
		{
			min = Math.min(min, value);
			max = Math.max(max, value);
			total += value;
			count++;

			return this;
		}

		public String toString()
		{
			return round(min) + "," + round(total / count) + "," + round(max);
		}
	}

	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final Map<String, Temperatures> cities = new ConcurrentHashMap<>();

		// ok, read the data using the NIO file API
		Files.lines(Paths.get(fileName)).parallel().forEach(s ->
		{
			// split the line
			final String[] cols = s.split(";");

			final String city = cols[0];
			final double temperature = Double.parseDouble(cols[1]);

			// get us our data store for the city
			cities.compute(city, (k, v) -> v == null ? new Temperatures(temperature) : v.add(temperature));
		});

		// ok, we got everything, now we need to order it
		return new TreeMap<String, Temperatures>(cities).toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example01c_BasicStreamParallel.class.getDeclaredConstructor(), args);
	}
}
