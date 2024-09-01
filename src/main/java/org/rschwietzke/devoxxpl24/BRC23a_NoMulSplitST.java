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
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.ParseDouble;

/**
 * Parse the temperature with a trick
 *
 * @author Rene Schwietzke
 */
public class BRC23a_NoMulSplitST extends Benchmark
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
		private final City city;
		private final int hashCode;

		public Temperatures(final City city, final int value)
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
            if (value < this.min)
            {
                this.min = value;
            }
            else if (value > this.max)
            {
                this.max = value;
            }
			this.total += value;
			this.count++;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}

		public boolean equals(final Line l)
		{
			return this.city.equals(l);
		}

		public boolean equals(final City c)
		{
			return this.city.equals(c);
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

	private static int MIN_BUFFERSIZE = 102400;
	private static int REMAINING_MIN_BUFFERSIZE = 200;

	static class Line
	{
		public boolean EOF = false;
		public boolean hasNewLine = false;
		private final ByteBuffer buffer = ByteBuffer.allocate(MIN_BUFFERSIZE);
		private final byte[] data = buffer.array();
		private final FileChannel channel;

		int pos = 0;
		int end = 0;

		int lineStartPos = 0;
		int semicolonPos = -1;
		int newlinePos = -1;

		int hashCode = -1;

		public Line(final FileChannel channel)
		{
			this.channel = channel;
		}

		/**
		 * @param channel the channel to read from
		 * @param buffer the buffer to fill
		 */
		private void readFromChannel()
		{
			hashCode = -1;
            hasNewLine = false;

			try
			{
			    // do we near the end of the buffer?
				if (end - pos < REMAINING_MIN_BUFFERSIZE)
				{
				    // we move the buffer indirectly, because the ByteBuffer just
				    // wraps our array, nothing for the tenderhearted
					System.arraycopy(data, pos, data, 0, data.length - pos);
					end = end - pos;
					pos = 0;
					buffer.position(end);

					// fill the buffer up
					final int readBytes = channel.read(buffer);
					if (readBytes == -1)
					{
						EOF = true;
					}

					end = buffer.position();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
				EOF = true;
				throw new RuntimeException(e);
			}

			lineStartPos = pos;

			// look for semicolon and new line
			// when checking for semicolon, we do the hashcode right away
	        int h = 1;
			int i = pos;
			for (; i < end; i++)
			{
				final byte b = data[i];
				if (b == ';')
				{
					semicolonPos = i;
					break;
				}
				int x = -h + b;
				int y = (h << 5);
				h = x + y;
			}
			this.hashCode = h;

			i++;
			for (; i < end; i++)
			{
                final byte b = data[i];
				if (b == '\n')
				{
					newlinePos = i;
					pos = i + 1;
					hasNewLine = true;
					return;
				}
			}
       	}

		@Override
		public int hashCode()
		{
			return hashCode;
		}

		public City getCity()
		{
			var c = new City();
			c.data = Arrays.copyOfRange(data, lineStartPos, semicolonPos);
			c.hashCode = hashCode();

			return c;
		}

		@Override
		public String toString()
		{
			return new String(data);
		}
	}

	static class City
	{
		byte[] data;
		int hashCode = -1;

		public boolean equals(Line line)
		{
			return Arrays.mismatch(data, 0, data.length, line.data, line.lineStartPos, line.semicolonPos) == -1;
		}

		public boolean equals(City city)
		{
			return Arrays.mismatch(data, 0, data.length, city.data, 0, city.data.length) == -1;
		}

		@Override
		public int hashCode()
		{
			return hashCode;
		}

		@Override
		public String toString()
		{
			return new String(data, 0, data.length);
		}
	}

	@Override
	public String run(final String fileName) throws IOException
	{
		// our cities with temperatures, assume we get about 400, so we get us decent space
		final FastHashSet cities = new FastHashSet(2023, 0.5f);

		try (var raf = new RandomAccessFile(fileName, "r");
				var channel = raf.getChannel();)
		{
			final Line line = new Line(channel);

			while (true)
			{
				line.readFromChannel();

				if (line.hasNewLine)
				{
					// parse our temperature inline without an instance of a string for temperature
					final int temperature = ParseDouble.parseIntegerFixed(line.data, line.semicolonPos + 1, line.newlinePos - 1);

					// find and update
					cities.getPutOrUpdate(line, temperature);
				}
				else if (line.EOF)
				{
					break;
				}
			}
		}

		// ok, we got everything, now we need to order it and print it
		final var result = new TreeMap<String, Temperatures>();
		// the simple set is not a standard collection class, so we go manual
		for (Temperatures t : cities.keys())
		{
			result.put(t.city.toString(), t);
		}
		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(BRC23a_NoMulSplitST.class, args);
	}

	static class FastHashSet
	{
		// we need only the reference, not the content
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

		public void getPutOrUpdate( final Line line, int value )
		{
			final int hash = line.hashCode();

			int ptr = hash & m_mask;
			Temperatures k = m_data[ ptr ];

			if ( k == FREE_KEY )
			{
				put(new Temperatures(line.getCity(), value));
				return;
			}

			if ( k.hashCode() == hash && k.equals( line ) )
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
					put(new Temperatures(line.getCity(), value));
					return;
				}
				if (k.hashCode() == hash && k.equals( line ))
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
