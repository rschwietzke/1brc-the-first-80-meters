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
 * Different take on parsing to reduce calculation even more.
 *
 * @author Rene Schwietzke
 */
public class BRC22_LooplessParsing2 extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
	private static class Temperatures
	{
		private final int min;
		private final int max;
		private final long total;
		private final long count;

		public Temperatures(final int value)
		{
			this.min = value;
			this.max = value;
			this.total = value;
			this.count = 1;
		}

		private Temperatures(int min, int max, long total, long count)
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
		Benchmark.run(BRC22_LooplessParsing2.class, args);
    }
}
