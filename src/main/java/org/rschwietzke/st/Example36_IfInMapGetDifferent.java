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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.ParseDouble;

/**
 *
 * Fixed an equals defects, hence this is slower.
 *
 * @author Rene Schwietzke
 */
public class Example36_IfInMapGetDifferent extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashSet cities = new FastHashSet(1000, 0.5f);

		try (final var r = new LineInputStream(new RandomAccessFile(fileName, "r")))
		{
			LineInfo l = new LineInfo(); // data transfer object
			while ((l = r.readLine(l)) != null)
			{
				// get us our data store for the city, which is another
				// version of a city
				final City totalsCity = cities.getPutOnEmpty(l);

				// put all data together
				final int temperature = ParseDouble.parseIntegerFixed(l.buffer, l.separator + 1, l.end);

				// store aka add the temperature to the totals
				totalsCity.temperatures.add(temperature);
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
		Benchmark.run(Example36_IfInMapGetDifferent.class.getDeclaredConstructor(), args);
	}

	public static class City implements Comparable<City>
	{
		public String city;
		private byte[] buffer;
		private int hashCode;

		public Temperatures temperatures;

		public int hashCode()
		{
			return this.hashCode;
		}

		public boolean equals(final City o)
		{
			// here we need the comparison that is matching a temp and a fixed city
			return Arrays.mismatch(this.buffer, o.buffer) == -1;
		}

		public boolean equals(final LineInfo lineInfo)
		{
			final int l = this.buffer.length;
			if (l != lineInfo.separator - lineInfo.from)
			{
				return false;
			}

			for (int i = 0; i < l; i++)
			{
				byte a = this.buffer[i];
				byte b = lineInfo.buffer[lineInfo.from + i];
				if (a != b)
				{
					return false;
				}
			}
			return true;

//			// too expensive
//			return Arrays.mismatch(
//					this.buffer, 0, this.buffer.length - 1,
//					lineInfo.buffer, lineInfo.from, lineInfo.separator - 1) == -1;
		}

		public City()
		{
		}

		public City(final LineInfo line)
		{
			this.city = line.text();
			this.buffer = this.city.getBytes(); // needed for equals
			this.hashCode = line.hash; // to avoid recalculation
			this.temperatures = new Temperatures();
		}

		public static City materalize(final LineInfo line)
		{
			final City c = new City();

			c.city = line.text();
			c.buffer = c.city.getBytes(); // needed for equals
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
			return city;
		}

		@Override
		public int compareTo(City o)
		{
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
		private final RandomAccessFile file;
		private byte[] buffer;
		private int startPos = 0;
		private int length = 0;
		private int separatorPos = -1;
		private int hash = 0;

		public String toString()
		{
			return Arrays.toString(new String(buffer).toCharArray());
		}

		public LineInputStream(final RandomAccessFile file) throws IOException
		{
			this.file = file;
			this.buffer = new byte[4000];

			// read something already to avoid ifs later
			this.length = file.read(this.buffer, 0, this.buffer.length);
		}

		public LineInfo readLine(final LineInfo data) throws IOException
		{
			// this.hash = 0; // not needed, we do that later anyway
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
				data.set(this.buffer, this.startPos, this.separatorPos, currentPos - 1, this.hash);
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
			int localLength = this.length;

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

				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(localBuffer, startPos, buffer, 0, localBuffer.length - moveBy);

				// we can fill the rest now
				final int read = file.read(localBuffer, localBuffer.length - moveBy, moveBy);
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

				// ok, we are not sitting at the front, so we can move
				int moveBy = startPos;
				System.arraycopy(localBuffer, startPos, localBuffer, 0, localBuffer.length - moveBy);

				// we can fill the rest now
				final int read = file.read(localBuffer, localBuffer.length - moveBy, moveBy);
				if (read == -1)
				{
					// ok, nothing more
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

		@Override
		public void close() throws IOException
		{
			file.close();
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

	    /**
	     * Computes key.hashCode() and spreads (XORs) higher bits of hash
	     * to lower.  Because the table uses power-of-two masking, sets of
	     * hashes that vary only in bits above the current mask will
	     * always collide. (Among known examples are sets of Float keys
	     * holding consecutive whole numbers in small tables.)  So we
	     * apply a transform that spreads the impact of higher bits
	     * downward. There is a tradeoff between speed, utility, and
	     * quality of bit-spreading. Because many common sets of hashes
	     * are already reasonably distributed (so don't benefit from
	     * spreading), and because we use trees to handle large sets of
	     * collisions in bins, we just XOR some shifted bits in the
	     * cheapest possible way to reduce systematic lossage, as well as
	     * to incorporate impact of the highest bits that would otherwise
	     * never be used in index calculations because of table bounds.
	     *
	     * From java.util.HashMap
	     */
	    static final int hash(final int hashCode) {
	        return hashCode ^ (hashCode >>> 16);
	    }

	    public City getPutOnEmpty(final LineInfo line)
	    {
	        int ptr = hash(line.hashCode()) & m_mask;
	        City k = this.m_data[ ptr ];

	        if ( k != FREE_KEY )
	        {
		        if (k.hashCode() == line.hashCode() && k.equals(line))
		        {
		            return k;
		        }
	        }
	        else
	        {
	        	k = City.materalize(line);
	        	put(k);
	            return k;
	        }


//	        System.out.println(line.text() + " " + line.hashCode() + " "+ hash(line.hashCode()));

	        return searchSlot(line, ptr);
	    }

	    private City searchSlot(final LineInfo line, int ptr)
	    {
	        while ( true )
	        {
	            ptr = (ptr + 1) & m_mask; //that's next index
	            City k = m_data[ ptr ];
	            if ( k == FREE_KEY )
	            {
	            	k = new City(line);
	            	put(k);
	                return k;  //end of chain already
	            }
	            if (k.hashCode() == line.hashCode() && k.equals(line))
	            {
	                return k;
	            }
	        }
	    }

	    public City put(final City key)
	    {
	        int ptr = hash(key.hashCode()) & m_mask;
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