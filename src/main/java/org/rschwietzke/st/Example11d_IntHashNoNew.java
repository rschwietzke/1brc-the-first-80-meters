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
public class Example11d_IntHashNoNew extends Benchmark
{
	private static class City implements Comparable<City>
	{
		private String data;
		private int splitPos;
		public int temperature;
		private int hashCode;

		// only for the late print and comparing
		private String city;

		public City()
		{
		}

		public void process(final String data)
		{
			this.data = data;

			// split the line
			final int lastPos = data.length() - 1;

			int h = 0;
			for (int i = 0; i <= lastPos; i++)
			{
				final char c = data.charAt(i);
				if (c == ';')
				{
					splitPos = i;
					break;
				}
				h = (h << 5) - h + c;
			}

			this.hashCode = h;
			this.temperature = ParseDouble.parseInteger(data , splitPos + 1, lastPos);
		}

		public int hashCode()
		{
			return hashCode;
		}

		@Override
		public boolean equals(final Object other)
		{
			final City o = (City) other;
			if (o.hashCode != hashCode)
			{
				return false;
			}
			if (o.splitPos != splitPos)
			{
				return false;
			}

			// dangerous but okish as it seems
			for (int i = 0; i < splitPos; i++)
			{
				if (o.data.charAt(i) != data.charAt(i))
				{
					return false;
				}
			}

			return true;
		}

		public City clone()
		{
			var c = new City();
			c.data = data;
			c.hashCode = hashCode;
			c.splitPos = splitPos;
			c.temperature = temperature;

			return c;
		}

		@Override
		public String toString()
		{
			return city == null ? city = data.substring(0, splitPos) : city;
		}

		@Override
		public int compareTo(City o)
		{
			return CharSequence.compare(toString(), o.toString());
		}
	}


	private static class Temperatures
	{
		private int min;
		private int max;
		private long total;
		private long count = 1;

		public Temperatures(final int value)
		{
			this.total = value;
			this.max = value;
			this.min = value;
		}

		private double round(final double value)
		{
			return Math.round(value / 10.0 * 10.0) / 10.0;
		}

		public Temperatures add(final int value)
		{
			min = Math.min(min, value);
			max = Math.max(max, value);
			total += value;
			count++;

			return this;
		}

		public String toString()
		{
			return round(min) + "," + round((double)total / count) + "," + round(max);
		}
	}

	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashMap<City, Temperatures> cities = new FastHashMap<>();

		// ok, read the data using the NIO file API
		try (final var reader = new BufferedReader(new FileReader(fileName)))
		{
			String line;

			final City city = new City();
			while ((line = reader.readLine()) != null)
			{
				city.process(line);

				// get us our data store for the city
				final Temperatures t = cities.get(city);
				if (t != null)
				{
					t.add(city.temperature);
				}
				else
				{
					cities.put(city.clone(), new Temperatures(city.temperature));
				}
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<City, Temperatures>();
		cities.keys().forEach(k -> {
			result.put(k, cities.get(k));
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example11d_IntHashNoNew.class.getDeclaredConstructor(), args);
	}
}
