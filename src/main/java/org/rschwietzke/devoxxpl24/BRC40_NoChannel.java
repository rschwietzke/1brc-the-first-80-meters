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
import java.util.Arrays;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Parse the temperature with a trick
 *
 * @author Rene Schwietzke
 */
public class BRC40_NoChannel extends Benchmark
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
        private final byte[] data;
        private final int hashCode;

        public Temperatures(final byte[] city, final int hashCode, final int value)
        {
            this.data = city;
            this.hashCode = hashCode;
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

        public int customEquals(final byte[] other)
        {
            return Arrays.mismatch(data, 0, data.length, other, 0, other.length);
        }

        public String getCity()
        {
            return new String(data, 0, data.length);
        }

        /**
         * Our final printing format
         */
        public String toString()
        {
            return MathUtil.toString(total, count, min, max);
        }
    }

    private static int MIN_BUFFERSIZE = 102400;
    private static int REMAINING_MIN_BUFFERSIZE = 200;

    static class Line
    {
        public boolean EOF = false;
        public boolean hasNewLine = true;

        private final byte[] data = new byte[MIN_BUFFERSIZE];
        private final RandomAccessFile file;

        int pos = 0;
        int end = 0;

        int lineStartPos = 0;
        int semicolonPos = -1;
        int newlinePos = -1;

        int hashCode = -1;
        int temperature;

        public Line(final RandomAccessFile file)
        {
            this.file = file;
        }

        private void moveData()
        {
            System.arraycopy(data, pos, data, 0, data.length - pos);
            end = end - pos;
            lineStartPos = pos = 0;

            // fill the buffer up
            try
            {
                final int readBytes = file.read(data, end, MIN_BUFFERSIZE - end);
                if (readBytes == -1)
                {
                    EOF = true;
                }
                else
                {
                    end += readBytes;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                EOF = true;
                throw new RuntimeException(e);
            }
        }

        /**
         * @param channel the channel to read from
         * @param buffer the buffer to fill
         */
        private void read()
        {
            // do we near the end of the buffer?
            if (end - pos < REMAINING_MIN_BUFFERSIZE)
            {
                moveData();
            }
            else
            {
                lineStartPos = pos;
            }

            // look for semicolon and new line
            // when checking for semicolon, we do the hashcode right away
            int h = 1;
            int i = pos;
            for (; i < end; i++)
            {
                final byte b = data[i];
                if (b == ';')
                {
                    semicolonPos = i++;
                    break;
                }
                h = (h << 5) - h + b;
            }
            this.hashCode = h;

            for (; i < end; i++)
            {
                final byte b = data[i];
                if (b == '\n')
                {
                    newlinePos = i++;
                    pos = i;

                    // resolve temperature
                    temperature = parseDoubleAsInt(data, semicolonPos, newlinePos);

                    return;
                }
            }
            hasNewLine = false;
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

        @Override
        public String toString()
        {
            return new String(data);
        }
    }

    @Override
    public String run(final String fileName) throws IOException
    {
        // our cities with temperatures, assume we get about 400, so we get us decent space
        final FastHashSet cities = new FastHashSet(2000, 0.5f);

        try (var raf = new RandomAccessFile(fileName, "r"))
        {
            final Line line = new Line(raf);

            while (true)
            {
                line.read();

                if (line.hasNewLine)
                {
                    // find and update
                    cities.getPutOrUpdate(line);
                }
                else if (line.EOF)
                {
                    break;
                }
            }
        }

        return cities.toTreeMap().toString();
    }

    private static final int DIGITOFFSET = 48;

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseDoubleAsInt(final byte[] b, final int semicolonPos, final int newlinePos)
    {
        final int end = newlinePos - 1;
        final int length = end - semicolonPos;

        // we know the first three pieces already 9.9
        int p0 = b[end];
        int p1 = b[end - 2] * 10;
        int value = p0 + p1 - (DIGITOFFSET + DIGITOFFSET * 10);

        // we are 9.9
        if (length == 3)
        {
            return value;
        }

        // ok, we are either -9.9 or 99.9 or -99.9
        if (b[semicolonPos + 1] != (byte)'-')
        {
            // we are 99.9
            value += b[end - 3] * 100 - DIGITOFFSET * 100;
            return value;
        }

        // we are either -99.9 or -9.9
        if (length == 4)
        {
            // -9.9
            return -value;
        }

        // -99.9
        value += b[end - 3] * 100 - DIGITOFFSET * 100;
        return -value;
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC40_NoChannel.class, args);
    }

    static class FastHashSet
    {
        // we need only the reference, not the content
        private static final Temperatures FREE_KEY = null;
        /** Fill factor, must be between (0 and 1) */
        private static final float m_fillFactor = 0.5f;

        /** Keys and values */
        private Temperatures[] m_data;

        /** Current map size */
        private int m_size;
        /** Mask to calculate the original position */
        private int m_mask;
        /** We will resize a map once it reaches this size */
        private int m_threshold;

        public FastHashSet( final int size, final float fillFactor )
        {
            final int capacity = arraySize(size, fillFactor);
            m_mask = capacity - 1;

            m_data = new Temperatures[capacity];
            m_threshold = (int) (capacity * fillFactor);
        }

        public void getPutOrUpdate( final Line line)
        {
            final int ptr = line.hashCode & m_mask;
            Temperatures k = m_data[ ptr ];

            if ( k == FREE_KEY )
            {
                final int length = line.semicolonPos - line.lineStartPos;
                final byte[] city = new byte[length];
                System.arraycopy(line.data, line.lineStartPos, city, 0, length);

                m_data[ ptr ] = new Temperatures(city, line.hashCode, line.temperature);
                return;
            }
            else if (Arrays.equals(k.data, 0, k.data.length, line.data, line.lineStartPos, line.semicolonPos))
            {
                k.add(line.temperature);
                return;
            }
            getPutOrUpdateSlow(line, line.temperature, ptr);
        }

        private void getPutOrUpdateSlow( final Line line, int value, int ptr )
        {
            while ( true )
            {
                ptr = (ptr + 1) & m_mask; //that's next index
                Temperatures k = m_data[ ptr ];
                if ( k == FREE_KEY )
                {
                    put(new Temperatures(Arrays.copyOfRange(line.data, line.lineStartPos, line.semicolonPos), line.hashCode, value));
                    return;
                }
                else if (Arrays.mismatch(k.data, 0, k.data.length, line.data, line.lineStartPos, line.semicolonPos) == -1)
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
            else if (k.customEquals( key.data ) == -1)
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
                else if ( k.customEquals( key.data ) == -1)
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
        public TreeMap<String, Temperatures> toTreeMap()
        {
            final var result = new TreeMap<String, Temperatures>();

            final int length = m_data.length;
            for (int i = 0; i < length; i++)
            {
                final Temperatures t = m_data[i];
                if (t != FREE_KEY)
                {
                    result.put(t.getCity(), t);
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
