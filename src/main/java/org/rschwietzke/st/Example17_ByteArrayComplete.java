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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.FastHashMap;
import org.rschwietzke.util.LineInputStream2;
import org.rschwietzke.util.LineInputStream2.LineInfo;
import org.rschwietzke.util.ParseDouble;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example17_ByteArrayComplete extends Benchmark
{
	private static class City implements Comparable<City>
	{
		public String city;
		public int temperature;

		// for comparison, if needed
		private byte[] buffer;
		private int mismatchPos;

		// temporary

		private int hashCode;

		public City()
		{
		}

		public void data(final LineInfo line)
		{
			this.buffer = line.buffer();
			this.hashCode = line.hash();
			this.mismatchPos = line.separator();
			this.temperature = ParseDouble.parseInteger(line.buffer(), line.separator() + 1, line.end());
		}

		public int hashCode()
		{
			return this.hashCode;
		}

		@Override
		public boolean equals(final Object other)
		{
			final City o = (City) other;
			if (o.hashCode != hashCode)
			{
				return false;
			}

			// compare the byte arrays
			final int pos = Arrays.mismatch(buffer, o.buffer);

			// the difference should be the splitpos upwards or no difference
			if (pos == -1)
			{
				return true;
			}
			return pos >= this.mismatchPos;
		}

		/**
		 * Create a copy for hashmap storing, rare event
		 * @return
		 */
		public static City materalize(LineInfo line)
		{
			City c = new City();

			c.city = line.text();
			c.buffer = c.city.getBytes();
			c.hashCode = line.hash();

			return c;
		}

		/**
		 * Only for debugging
		 */
		@Override
		public String toString()
		{
			return city != null ? city : "N/A";
		}

		@Override
		public int compareTo(City o)
		{
			// that is safe, because we use it only after we materalized it
			return CharSequence.compare(city, o.city);
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

		int count = 0;

		try (final var r = new LineInputStream2(new BufferedInputStream(new FileInputStream(fileName))))
		{
			LineInfo l;
			City city = new City();
			while ((l = r.readLine()) != null)
			{
				city.data(l);

				// get us our data store for the city
				final Temperatures t = cities.get(city);
				if (t != null)
				{
					t.add(city.temperature);
				}
				else
				{
					cities.put(City.materalize(l), new Temperatures(city.temperature));
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
		Benchmark.run(Example17_ByteArrayComplete.class.getDeclaredConstructor(), args);
	}
}
