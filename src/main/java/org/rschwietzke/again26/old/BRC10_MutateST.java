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
package org.rschwietzke.again26.old;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.ParseDouble;

/**
 * Avoid the creation of intermediate Temperature instances
 *
 * @author Rene Schwietzke
 */
public class BRC10_MutateST extends Benchmark
{
	/**
	 * Holds our temperature data without the station, because the
	 * map already knows that
	 */
	private static class Temperatures
	{
		private int min;
		private int max;
		private int total;
		private int count;

		public Temperatures(final int value)
		{
			this.min = value;
			this.max = value;
			this.total = value;
			this.count = 1;
		}

		/**
		 * Combine two temperatures
		 *
		 * @param value the temperature to add
		 */
		public void add(final int value)
		{
			this.min = Math.min(this.min, value);
			this.max = Math.max(this.max, value);
			this.total += value;
			this.count++;
		}

		/**
		 * Our final printing format
		 */
		public String toString()
        {
            return MathUtil.toString(total, count, min, max);
        }
	}

    @Override
    public String run(final String fileName) throws IOException
    {
    	// our cities with temperatures
    	final Map<String, Temperatures> cities = new HashMap<>();

    	try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
    		String line;
    		while ((line = reader.readLine()) != null)
    		{
    			// split the line
    			final int pos = line.indexOf(';');

    			// get us the city
    			final String city = line.substring(0, pos);

    			// parse our temperature inline without an instance of a string for temperature
    			final int temperature = ParseDouble.parseInteger(line, pos + 1, line.length() - 1);

    			// get city
    			final Temperatures v = cities.get(city);
    			if (v != null)
    			{
    				// know it, put both together
    				v.add(temperature);
    			}
    			else
    			{
    				// we have not seen that city yet, create a container and store it
    				cities.put(city, new Temperatures(temperature));
    			}

    		}
    	}

    	// ok, we got everything, now we need to order it and print it
    	final var result = new TreeMap<String, Temperatures>(cities);
        return result.toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC10_MutateST.class, args);
    }
}
