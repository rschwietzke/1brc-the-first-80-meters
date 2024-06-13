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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.ParseDouble;

/**
 * We strip things even more and use a set without generics.
 * We are also use fixed markers in the set.
 *
 * @author Rene Schwietzke
 */
public class BRC13_HardcodedSetST extends Benchmark
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
		private final String city;
		private final int hashCode;

		public Temperatures(final String city, final int value)
		{
			this.city = city;
			this.hashCode = city.hashCode();
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

		@Override
		public int hashCode()
		{
			return hashCode;
		}

		public boolean equals(final String s)
		{
			return this.city.equals(s);
		}

		/**
		 * 1BRC wants to have one decimal digits
		 * @param value the value to transform
		 * @return the rounded value
		 */
		private double round(double value)
		{
			return Math.round(value) / 10.0;
		}

		/**
		 * Our final printing format
		 */
		public String toString()
		{
			return round(min) + "," + round(((double)total / (double)count)) + "," + round(max);
		}
	}

    @Override
    public String run(final String fileName) throws IOException
    {
    	// our cities with temperatures, assume we get about 400, so we get us decent space
    	final FastHashSet cities = new FastHashSet(2023, 0.5f);

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

    			// find and update
    			cities.getPutOrUpdate(city, temperature);
    		}
    	}

    	// ok, we got everything, now we need to order it and print it
    	final var result = new TreeMap<String, Temperatures>();
    	// the simple set is not a standard collection class, so we go manual
    	for (Temperatures t : cities.keys())
    	{
    		result.put(t.city, t);
    	}
        return result.toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC13_HardcodedSetST.class, args);
    }

    static class FastHashSet
    {
        private static final Temperatures FREE_KEY = null;

        /** Keys and values */
        private Temperatures[] m_data;

        /** Fill factor, must be between (0 and 1) */
        private final float m_fillFactor;
        /** We will resize a map once it reaches this size */
        private int m_threshold;
        /** Current map size */
        private int m_size;
        /** Mask to calculate the original position */
        private int m_mask;

        public FastHashSet()
        {
            this(13, 0.5f);
        }

        public FastHashSet( final int size, final float fillFactor )
        {
            final int capacity = arraySize(size, fillFactor);
            m_mask = capacity - 1;
            m_fillFactor = fillFactor;

            m_data = new Temperatures[capacity];
            m_threshold = (int) (capacity * fillFactor);
        }

        public void getPutOrUpdate( final String city, int value )
        {
        	final int hash = city.hashCode();
            int ptr = hash & m_mask;
            Temperatures k = m_data[ ptr ];

            if ( k == FREE_KEY )
            {
            	put(new Temperatures(city, value));
            	return;
            }

            if ( k.equals( city ) )
            {
            	k.add(value);
                return;
            }

            while ( true )
            {
                ptr = (ptr + 1) & m_mask; //that's next index
                k = m_data[ ptr ];
                if ( k == FREE_KEY )
                {
                	put(new Temperatures(city, value));
                    return;
                }
                if ( k.equals( city ) )
                {
                	k.add(value);
                    return;
                }
            }
        }

        private Temperatures put(final Temperatures key)
        {
        	final int hash = key.hashCode();
            int ptr = hash & m_mask;
            Temperatures k = m_data[ptr];

            if ( k == FREE_KEY ) //end of chain already
            {
                m_data[ ptr ] = key;
                if ( m_size >= m_threshold )
                    rehash( m_data.length * 2 ); //size is set inside
                else
                    ++m_size;
                return null;
            }
            else if (k.hashCode() == hash && k.equals( key.city ))
            {
                final Temperatures ret = m_data[ptr];
                m_data[ptr] = key;
                return ret;
            }

            while ( true )
            {
                ptr = (ptr + 1) & m_mask; //that's next index calculation
                k = m_data[ ptr ];
                if ( k == FREE_KEY )
                {
                    m_data[ ptr ] = key;
                    if ( m_size >= m_threshold )
                        rehash( m_data.length * 2 ); //size is set inside
                    else
                        ++m_size;
                    return null;
                }
                else if ( k.hashCode() == hash && k.equals( key.city ) )
                {
                    final Temperatures ret = m_data[ptr];
                    m_data[ptr] = key;
                    return ret;
                }
            }
        }

        public int size()
        {
            return m_size;
        }

        private void rehash( final int newcapacity )
        {
            m_threshold = (int) (newcapacity * m_fillFactor);
            m_mask = newcapacity - 1;

            final int oldcapacity = m_data.length;
            final Temperatures[] oldData = m_data;

            m_data = new Temperatures[newcapacity];

            m_size = 0;

            for ( int i = 0; i < oldcapacity; i++ )
            {
                final Temperatures oldKey = oldData[ i ];
                if( oldKey != FREE_KEY)
                {
                    put(oldKey);
                }
            }
        }

        /**
         * Returns a list of all values
         *
         * @return
         */
        public List<Temperatures> keys()
        {
            final List<Temperatures> result = new ArrayList<>(this.m_size);

            final int length = m_data.length;
            for (int i = 0; i < length; i++)
            {
                final Temperatures o = m_data[i];
                if (o != FREE_KEY)
                {
                    result.add(o);
                }
            }

            return result;
        }

        /**
         * Clears the map, reuses the data structure by clearing it out.
         * It won't shrink the underlying array!
         */
        public void clear()
        {
            this.m_size = 0;
            Arrays.fill(m_data, FREE_KEY);
        }

        /** Return the least power of two greater than or equal to the specified value.
        *
        * <p>Note that this function will return 1 when the argument is 0.
        *
        * @param x a long integer smaller than or equal to 2<sup>62</sup>.
        * @return the least power of two greater than or equal to the specified value.
        */
       public static long nextPowerOfTwo( long x ) {
           if ( x == 0 ) return 1;
           x--;
           x |= x >> 1;
           x |= x >> 2;
           x |= x >> 4;
           x |= x >> 8;
           x |= x >> 16;
           return ( x | x >> 32 ) + 1;
       }

       /** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to <code>Math.ceil( expected / f )</code>.
        *
        * @param expected the expected number of elements in a hash table.
        * @param f the load factor.
        * @return the minimum possible size for a backing array.
        * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
        */
       public static int arraySize( final int expected, final float f ) {
           final long s = Math.max( 2, nextPowerOfTwo( (long)Math.ceil( expected / f ) ) );
           if ( s > (1 << 30) ) throw new IllegalArgumentException( "Too large (" + expected + " expected elements with load factor " + f + ")" );
           return (int)s;
       }
    }

}
