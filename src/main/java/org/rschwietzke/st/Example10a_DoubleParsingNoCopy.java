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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.FastHashMap;
import org.rschwietzke.util.ParseDouble;

/**
 * Let's profile for the first time
 * java -agentpath:libasyncProfiler.so=start,event=cpu,file=profile.html -cp target/classes/ org.rschwietzke.st.Example03
 *
 * With alloc instead of cpu, you will see what stuff we turn around.
 *
 * @author Rene Schwietzke
 */
public class Example10a_DoubleParsingNoCopy extends Benchmark
{
	private static class Temperatures
	{
		private double min;
		private double max;
		private double total;
		private long count = 1;

		public Temperatures(final double value)
		{
			this.total = value;
			this.max = value;
			this.min = value;
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
		final FastHashMap<String, Temperatures> cities = new FastHashMap<>();

		// ok, read the data using the NIO file API
		try (final var reader = new BufferedReader(new FileReader(fileName)))
		{
			String line;

			while((line = reader.readLine()) != null)
			{
				// split the line
				int i = line.length() - 1;
				for (; i >= 0; i--)
				{
					if (line.charAt(i) == ';')
					{
						break;
					}
				}

				final String city = line.substring(0, i);
				final int next = i + 1;
				final double temperature = ParseDouble.parseDouble(line , next, line.length() - 1);

				// get us our data store for the city
				Temperatures t = cities.get(city);
				if (t != null)
				{
					t.add(temperature);
				}
				else
				{
					cities.put(city, new Temperatures(temperature));
				}
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<String, Temperatures>();
		cities.keys().forEach(k -> {
			result.put(k, cities.get(k));
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example10a_DoubleParsingNoCopy.class.getDeclaredConstructor(), args);
	}
}
