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
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * We don't want to have an extra new string for parsing the double,
 * that does not make sense here.
 *
 * @author Rene Schwietzke
 */
public class BRC16_NoExtraString extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
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

		public Temperatures merge(final Temperatures other)
		{
			return new Temperatures(Math.min(min, other.min), Math.max(max, other.max), total + other.total, count + other.count);
		}

		public String toString()
		{
		    // we delegate the formatting to our math util to
		    // ensure we do the same everywhere, helps us later to 
		    // change the accuracy if needed
            return MathUtil.toString(total, count, min, max);
        }
	}

    @Override
    public String run(final String fileName) throws IOException
    {
    	// our storage
    	final Map<String, Temperatures> cities = new HashMap<>();

    	// open the file
    	try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
    		String line;
    		
    		// read all lines until end of file
    		while ((line = reader.readLine()) != null)
    		{
    			// split the line using indexOf
    		    final int semicolon = line.indexOf(';');
    		    final String city = line.substring(0, semicolon);

    			// don't create a new string for the double parsing, we are save
    		    // due to our line format
    			final double temperature = parseDouble(line, semicolon + 1, line.length() - 1);

    			// get us our measurement record
    			var measurement = new Temperatures(temperature);
    			
    			// store it, when it exists, merge both measurements
				cities.merge(city, measurement, (t1, t2) -> t1.merge(t2));
    		}
    	}

    	// ok, we got everything, now we need to order it
        return new TreeMap<String, Temperatures>(cities).toString();
    }
    
    private static final int DIGITOFFSET = 48;

    private static final double[] multipliers = {
        1, 1, 0.1, 0.01, 0.001, 0.000_1, 0.000_01, 0.000_001, 0.000_000_1, 0.000_000_01,
        0.000_000_001, 0.000_000_000_1, 0.000_000_000_01, 0.000_000_000_001, 0.000_000_000_000_1,
        0.000_000_000_000_01, 0.000_000_000_000_001, 0.000_000_000_000_000_1, 0.000_000_000_000_000_01};

    /**
     * Parses the chars and returns the result as double.
     * char set. Due to conversion limitations, the result might be different from Double.parseDouble aka precision.
     *
     * @param s
     *            the characters to parse
     * @return the converted string as double
     */
    public static double parseDouble(final String s, final int offset, final int end)
    {
        final int negative = s.charAt(offset) == '-' ? offset + 1 : offset;

        long value = 0;
        int decimalPos = end;

        for (int i = negative; i <= end; i++)
        {
            final int d = s.charAt(i);
            if (d == '.')
            {
                decimalPos = i;
                continue;
            }
            final int v = d - DIGITOFFSET;
            value = value * 10 + v;
        }

        // adjust the decimal places
        value = negative != offset ? -value : value;
        return value * multipliers[end - decimalPos + 1];
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC16_NoExtraString.class, args);
    }
}
