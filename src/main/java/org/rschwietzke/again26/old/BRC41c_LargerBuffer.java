/*
1 *  Copyright 2023 The original authors
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
public class BRC41c_LargerBuffer extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    static class Temperatures
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

    private static int MIN_BUFFERSIZE = 1_000_000;
    private static int REMAINING_MIN_BUFFERSIZE = 200;

    static class Line
    {
        public boolean EOF = false;

        private final byte[] data = new byte[MIN_BUFFERSIZE];
        private final RandomAccessFile file;

        int pos = 0;
        int end = 0;
        int endToReload = 0;

        int lineStartPos = 0;
        int semicolonPos = -1;
        int newlinePos = -1;

        int hashCode = -1;
        int temperature;

        public Line(final RandomAccessFile file)
        {
            this.file = file;
        }

        private int moveData()
        {
            System.arraycopy(this.data, this.pos, this.data, 0, this.data.length - this.pos);
            this.end = this.end - this.pos;
            this.lineStartPos = this.pos = 0;

            // fill the buffer up
            try
            {
                final int readBytes = file.read(this.data, this.end, MIN_BUFFERSIZE - this.end);
                if (readBytes == -1)
                {
                    this.EOF = true;
                }
                else
                {
                    this.end += readBytes;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
                EOF = true;
                throw new RuntimeException(e);
            }

            return end - REMAINING_MIN_BUFFERSIZE;
        }

        /**
         * @param channel the channel to read from
         * @param buffer the buffer to fill
         */
        private boolean read()
        {
            // do we near the end of the buffer?
            if (pos >= this.endToReload)
            {
                this.endToReload = moveData();
                if (pos >= end)
                {
                    return false;
                }
            }
            else
            {
                lineStartPos = pos;
            }

            // look for semicolon and new line
            // when checking for semicolon, we do the hashcode right away
            int h = 0;
            int i = pos;
            do
            {
                i++;

                final byte b = data[i];
                if (b == ';')
                {
                    break;
                }
                int x = -h + b;
                int y = (h << 5);
                h = x + y;
            }
            while (true);

            this.semicolonPos = i++;
            this.hashCode = h;

            // we know for the numbers that we are very fix in length,
            // so let's read forward
            // we don't check if we have enough data because we have correct
            // data and we read early enough to have always a full line in the buffer
            byte b = data[i++];
            int negative;

            // can be - or 0..9
            if (b == '-')
            {
                negative = -1;
                // read number again
                b = data[i++];
            }
            else
            {
                negative = 1;
            }

            // ok, number for sure, -9 or 9
            int value = b - DIGITOFFSET;
            b = data[i++];

            // now -99 or -9. or 99 or 9.
            if (b == '.')
            {
                // read again for the data after the .
                b = data[i++];
                value *= 10;
                value += b - DIGITOFFSET;
                this.newlinePos = i++;
                this.pos = i;
                this.temperature = negative * value;

                return true;
            }

            // was -99 or 99
            i++;
            byte b2 = data[i++];

            value *= 10;
            value += b - DIGITOFFSET;
            value *= 10;

            // we have seen the end now for certain
            // skip over .
            value += b2 - DIGITOFFSET;

            this.newlinePos = i++;
            this.pos = i;

            this.temperature = negative * value;

            return true;
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
                if (line.read())
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

        public void getPutOrUpdate(final Line line)
        {
            final int ptr = line.hashCode & m_mask;
            Temperatures k = m_data[ ptr ];

            if ( k != FREE_KEY )
            {
                int l = line.semicolonPos - line.lineStartPos;
                byte[] data = k.data;

                // check length first
                if (l == data.length)
                {
                    // iterate old fashioned
                    int start = line.lineStartPos;
                    int i = 0;
                    for (; i < l; i++)
                    {
                        if (data[i] != line.data[start + i])
                        {
                            break;
                        }
                    }
                    if (i == l)
                    {
                        k.add(line.temperature);
                        return;
                    }
                }
            }
            else
            {
                // have to do a proper put to avoid filling up the map
                // without resizing
                put(new Temperatures(
                        Arrays.copyOfRange(
                                line.data,
                                line.lineStartPos, line.semicolonPos),
                            line.hashCode, line.temperature));

                return;
            }

            putOrUpdateSlow(line, ptr);

        }

        private void putOrUpdateSlow( final Line line, int ptr)
        {
            while ( true )
            {
                ptr = (ptr + 1) & m_mask; //that's next index
                Temperatures k = m_data[ ptr ];
                if ( k == FREE_KEY )
                {
                    put(new Temperatures(
                            Arrays.copyOfRange(line.data, line.lineStartPos, line.semicolonPos),
                            line.hashCode, line.temperature));
                    return;
                }
                else if (Arrays.mismatch(k.data, 0, k.data.length, line.data, line.lineStartPos, line.semicolonPos) == -1)
                {
                    k.add(line.temperature);
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

    /**
     * Just to run our benchmark
     */
    public static void main(String[] args)
    {
        Benchmark.run(BRC41c_LargerBuffer.class, args);
    }
}
