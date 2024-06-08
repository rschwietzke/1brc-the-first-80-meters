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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;

/**
 * This is our first Example. It will use a regular Java approach
 * from the books, where we just use everything as we find it in the
 * libs without thinking about the impact.
 *
 * @author Rene Schwietzke
 */
public class BRC03_NoStreamST extends Benchmark
{
	private static class Temperatures
	{
		private final double min;
		private final double max;
		private final double total;
		private final long count;

		public Temperatures(final double value)
		{
			this.min = value;
			this.max = value;
			this.total = value;
			this.count = 1;
		}

		private Temperatures(double min, double max, double total, long count)
		{
			this.min = min;
			this.max = max;
			this.total = total;
			this.count = count;
		}

		private double round(double value)
		{
			return Math.round(value * 10.0) / 10.0;
		}

		public Temperatures merge(final Temperatures other)
		{
			return new Temperatures(Math.min(min, other.min), Math.max(max, other.max), total + other.total, count + other.count);
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
    	final Map<String, Temperatures> cities = new HashMap<>();

    	try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
    		String line;
    		while ((line = reader.readLine()) != null)
    		{
    			// split the line
    			final String[] cols = line.split(";");

    			final String city = cols[0];
    			final double temperature = Double.parseDouble(cols[1]);

    			// get us our data store for the city
				cities.merge(city, new Temperatures(temperature), (t1, t2) -> t1.merge(t2));
    		}
    	}

    	// ok, we got everything, now we need to order it
        return new TreeMap<String, Temperatures>(cities).toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC03_NoStreamST.class, args);
    }
}
