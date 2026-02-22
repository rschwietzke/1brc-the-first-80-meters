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
 * Immutable classes or records are cool but overhead when it comes to speed, so let's go 
 * classic POJO and mutate the data instead.
 *
 * @author Rene Schwietzke
 */
public class BRC25_MutateData extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
	private static class Temperatures
	{
		private int min;
		private int max;
		private long total;
		private long count;

		public Temperatures(final int temperature)
		{
			this.min = temperature;
			this.max = temperature;
			this.total = temperature;
			this.count = 1;
		}


		public Temperatures merge(final int temperature)
        {
            this.min = Math.min(this.min, temperature);
            this.max = Math.max(this.max, temperature);
            this.total += temperature;
            this.count += 1;
            return this;
        }

		public String toString()
		{
		    // we delegate the formatting to our math util to
		    // ensure we do the same everywhere, helps us later to 
		    // change the accuracy if needed
            return MathUtil.toStringFromInteger(total, count, min, max);
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

    			// ok, parse to an int and forget about the . for a moment
    			final int temperature = parseInteger(line, semicolon + 1, line.length());

    			// create new when needed, mutate when merging
				cities.compute(city, 
				        (k, v) -> v == null ? new Temperatures(temperature) : v.merge(temperature));
    		}
    	}

    	// ok, we got everything, now we need to order it
        return new TreeMap<String, Temperatures>(cities).toString();
    }
    
    private static final int DIGITOFFSET = 48;

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     * 
     * Best case, two branches
     * Worst case, two branches
     */
    public static int parseInteger(final String s, int offset, final int end)
    {
        int l = end - offset;
        var p0 = s.charAt(end - 1);
        var p2 = s.charAt(end - 3) * 10;
        var value = p2 + p0 - (DIGITOFFSET * 10 + DIGITOFFSET);
        
        final char firstChar = s.charAt(offset);
        if (firstChar == '-')
        {
            if (l == 5)
            {
                // -99.9
                var p3 = s.charAt(end - 4) * 100;
                value = p3 + value - (DIGITOFFSET * 100);
            }
            else
            {
                // -9.9
            }
            return -value;
        }
        else
        {
            if (l == 4)
            {
                // 99.9
                // we can use firstChar directly as we know its not '-'
                var p3 = firstChar * 100;
                value = p3 + value - (DIGITOFFSET * 100);
            }
            else
            {
                // 9.9
            }
            return value;
        }
    }
    
    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC25_MutateData.class, args);
    }
}
