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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.ParseDouble;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example26_SetWithNull extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashSet cities = new FastHashSet(500, 0.5f);

		try (final var r = new LineInputStream(new BufferedInputStream(new FileInputStream(fileName))))
		{
			LineInfo l = new LineInfo(); // data tranfer object
			City city = new City();
			while ((l = r.readLine(l)) != null)
			{
				// put all data together
				city.data(l);

				// get us our data store for the city, which is another
				// version of a city
				City totalsCity = cities.getPutOnEmpty(city, l);
				// store aka add the temperature to the totals
				totalsCity.temperatures.add(city.temperature);
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<City, Temperatures>();
		cities.keys().forEach(k -> {
			result.put(k, k.temperatures);
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example26_SetWithNull.class.getDeclaredConstructor(), args);
	}

	public static class City implements Comparable<City>
	{
		public String city;
		public int temperature;

		// for comparison, if needed
		private byte[] buffer;
		private int mismatchPos;

		// temporary

		private int hashCode;

		public Temperatures temperatures;

		public City()
		{
		}

		public void data(final LineInfo line)
		{
			this.buffer = line.buffer;
			this.hashCode = line.hash;
			this.mismatchPos = line.separator;
			this.temperature = ParseDouble.parseInteger(line.buffer, line.separator + 1, line.end);
		}

		public int hashCode()
		{
			return this.hashCode;
		}

		public boolean equals(final City o)
		{
			// compare the byte arrays
			final int pos = Arrays.mismatch(buffer, o.buffer);

			// the difference should be the splitpos upwards or no difference
			return pos >= this.mismatchPos || pos == -1;
		}

		/**
		 * Create a copy for hashmap storing, rare event
		 * @return
		 */
		public static City materalize(final LineInfo line)
		{
			final City c = new City();

			c.city = line.text();
			c.buffer = c.city.getBytes(); // needed for hashing
			c.hashCode = line.hash; // to avoid recalculation
			c.temperatures = new Temperatures();

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

	public static class Temperatures
	{
		private int min = Integer.MAX_VALUE;
		private int max = Integer.MIN_VALUE;
		private long total = 0L;
		private int count = 0;

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

	public class LineInputStream implements AutoCloseable
	{
		private final InputStream src;
		private byte[] buffer;
		private int startPos = 0;
		private int length = 0;
		private int separatorPos = -1;
		private int hash = 0;

		public String toString()
		{
			return Arrays.toString(new String(buffer).toCharArray());
		}

		public LineInputStream(final InputStream src) throws IOException
		{
			this.src = src;
			this.buffer = new byte[1000];
		}

		public LineInputStream(final InputStream src, int size) throws IOException
		{
			this.src = src;
			this.buffer = new byte[size];
		}

		public LineInfo readLine(final LineInfo data) throws IOException
		{
			this.hash = 0;
			this.separatorPos = -1;
			int currentPos = this.startPos;

			// read text and hash it
			currentPos = readText(currentPos);

			// read number
			currentPos = readNumber(currentPos);

			// if we got here, we might still have data

			// done
			if (this.separatorPos >= 0)
			{
				data.set(buffer, startPos, separatorPos, currentPos - 1, hash);
				this.startPos = currentPos + 1;
				return data;
			}
			else
			{
				// maybe we ended exactly at the end with the last line, so we
				// had a round with nothing
				return null;
			}
		}

		private int readText(int currentPos) throws IOException
		{
			int localHash = 0;
			byte[] localBuffer = buffer;
			int localLength = length;

			while (true)
			{
				// read the text first and calculate a hash
				for (; currentPos < localLength; currentPos++)
				{
					final byte c = localBuffer[currentPos];

					if (c == (byte)';')
					{
						// number continues later
						this.separatorPos = currentPos;
						this.hash = localHash;
						currentPos++;

						return currentPos;
					}
					else
					{
						// calculate hash
						localHash = (localHash << 5) - (localHash + c);
					}
				}

				// if we got here, we have not seen a ;, so we have to reload the buffer
				// if we are not into the buffer at all, we cannot move anything forward
				if (this.startPos == 0)
				{
					// we have not moved a bit, so increase buffer size
					localBuffer = buffer = Arrays.copyOf(buffer, (localBuffer.length << 1));

					// load from current pos up
					final int read = src.read(localBuffer, currentPos, localBuffer.length - currentPos);
					if (read == -1)
					{
						// all read, we end here
						this.hash = localHash;
						return currentPos;
					}

					this.length = localLength = currentPos + read;
				}
				else
				{
					// ok, we are not sitting at the front, so we can move
					int moveBy = startPos;
					System.arraycopy(localBuffer, startPos, buffer, 0, localBuffer.length - moveBy);

					// we can fill the rest now
					final int read = src.read(localBuffer, localBuffer.length - moveBy, moveBy);
					if (read == -1)
					{
						// ok, nothing more
						this.hash = localHash;
						return currentPos;
					}

					// adjust start pos and pos, we are into our current data, so don't start from 0
					// just continue reading, but we increase currentPos over length, so take it back
					// one
					currentPos = currentPos - moveBy;
					// current data record start is now here
					this.startPos = 0;
					// how much can we read
					this.length = localLength = currentPos + read;
					// seperator moved too
					this.separatorPos = this.separatorPos - moveBy;
				}
			}
		}

		private int readNumber(int currentPos) throws IOException
		{
			byte[] localBuffer = this.buffer;
			int localLength = this.length;

			while (true)
			{
				// read the text first and calculate a hash
				for (; currentPos < localLength; currentPos++)
				{
					final byte c = localBuffer[currentPos];

					if (c == (byte)'\n')
					{
						// end of line
						return currentPos;
					}
				}

				// if we got here, we have not seen a ;, so we have to reload the buffer
				// if we are not into the buffer at all, we cannot move anything forward
				if (this.startPos == 0)
				{
					// we have not moved a bit, so increase buffer size
					this.buffer = localBuffer = Arrays.copyOf(localBuffer, (localBuffer.length << 1));

					// load from current pos up
					final int read = src.read(localBuffer, currentPos, localBuffer.length - currentPos);
					if (read == -1)
					{
						// all read, we end here
						return currentPos;
					}

					this.length = localLength = currentPos + read;
				}
				else
				{
					// ok, we are not sitting at the front, so we can move
					int moveBy = startPos;
					System.arraycopy(localBuffer, startPos, localBuffer, 0, localBuffer.length - moveBy);

					// we can fill the rest now
					final int read = src.read(localBuffer, localBuffer.length - moveBy, moveBy);
					if (read == -1)
					{
						// ok, nothing more
						return currentPos;
					}

					// adjust start pos and pos, we are into our current data, so don't start from 0
					// just continue reading, but we increase currentPos over length, so take it back
					// one
					currentPos = currentPos - moveBy - 1;
					// current data record start is now here
					this.startPos = 0;
					// how much can we read
					this.length = localLength = currentPos + read + 1;
					// seperator moved too
					this.separatorPos = this.separatorPos - moveBy;
				}
			}
		}

		@Override
		public void close() throws IOException
		{
			src.close();
		}
	}

	public static class LineInfo
	{
		byte[] buffer;
		int from;
		int separator;
		int end;
		int hash;

		public void set(byte[] buffer, int from, int separator, int end, int hash)
		{
			this.buffer = buffer;
			this.from = from;
			this.end = end;
			this.separator = separator;
			this.hash = hash;
		}

		@Override
		public String toString()
		{
			return "LineInfo [text=" + text() + ", number=" + number() + ", hash=" + hash + "]";
		}

		public String text()
		{
			return new String(buffer, from, separator - from);
		}

		public String number()
		{
			return new String(buffer, separator + 1, end - separator);
		}

		@Override
		public int hashCode()
		{
			return this.hash;
		}
	}

	public static class FastHashSet
	{
		// we need only the reference, not the content
	    private static final City FREE_KEY = null;

	    /** Keys and values */
	    private City[] m_data;

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
	        if ( fillFactor <= 0 || fillFactor >= 1 )
	            throw new IllegalArgumentException( "FillFactor must be in (0, 1)" );
	        if ( size <= 0 )
	            throw new IllegalArgumentException( "Size must be positive!" );
	        final int capacity = arraySize(size, fillFactor);
	        m_mask = capacity - 1;
	        m_fillFactor = fillFactor;

	        m_data = new City[capacity];
	        Arrays.fill( m_data, FREE_KEY );

	        m_threshold = (int) (capacity * fillFactor);
	    }

	    public City getPutOnEmpty( final City key, LineInfo line )
	    {
	        int ptr = key.hashCode() & m_mask;
	        City k = m_data[ ptr ];

	        if ( k == FREE_KEY )
	        {
	        	k = City.materalize(line);
	        	put(k);
	            return k;  //end of chain already
	        }

	        if ( k.hashCode() == key.hashCode() && k.equals( key ) )
	        {
	            return k;
	        }

	        while ( true )
	        {
	            ptr = (ptr + 1) & m_mask; //that's next index
	            k = m_data[ ptr ];
	            if ( k == FREE_KEY )
	            {
	            	k = City.materalize(line);
	            	put(k);
	                return k;  //end of chain already
	            }
	            if (k.hashCode() == key.hashCode() && k.equals( key ))
	            {
	                return k;
	            }
	        }
	    }

	    public City put(final City key)
	    {
	        int ptr = key.hashCode() & m_mask;
	        City k = m_data[ptr];

	        if ( k == FREE_KEY ) //end of chain already
	        {
	            m_data[ ptr ] = key;
	            if ( m_size >= m_threshold )
	                rehash( m_data.length * 2 ); //size is set inside
	            else
	                ++m_size;
	            return null;
	        }
	        else if (k.hashCode() == key.hashCode() && k.equals( key ))
	        {
	            final City ret = m_data[ptr];
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
	            else if ( k.hashCode() == key.hashCode() && k.equals( key ) )
	            {
	                final City ret = m_data[ptr];
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
	        final City[] oldData = m_data;

	        m_data = new City[newcapacity];
	        Arrays.fill( m_data, FREE_KEY );

	        m_size = 0;

	        for ( int i = 0; i < oldcapacity; i++ )
	        {
	            final City oldKey = oldData[ i ];
	            if( oldKey != FREE_KEY )
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
	    public List<City> keys()
	    {
	        final List<City> result = new ArrayList<>(this.m_size);

	        final int length = m_data.length;
	        for (int i = 0; i < length; i++)
	        {
	            final City o = m_data[i];
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