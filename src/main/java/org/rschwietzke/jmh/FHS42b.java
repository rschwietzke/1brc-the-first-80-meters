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
package org.rschwietzke.jmh;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * Basic setup for FastHashMapBenchmarking
 *
 * @author Rene Schwietzke
 */
public class FHS42b
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    static class Temperatures
    {
        int min;
        int max;
        int total;
        int count;
        final byte[] data;
        final int hashCode;

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

        public double getTotalTemperature()
        {
            return round(this.total);
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

        public String getCity()
        {
            return new String(data, 0, data.length);
        }

        @Override
        public String toString()
        {
            return getCity() + ";" + getTotalTemperature();
        }
    }

    static class Line
    {
        private static int MIN_BUFFERSIZE = 1_000_000;

        final byte[] data = new byte[MIN_BUFFERSIZE];

        int pos = 0;
        int endToReload= 0;
        int newlinePos = -1;
        int end = 0;

        int lineStartPos = 0;
        int semicolonPos = -1;

        int hashCode;
        int temperature;

        boolean EOF = false;

        /**
         *
         * @param s the data to use
         * @param offset into the buffer
         * @param hashCode to overwrite for testing
         */
        public Line(final String s, int offset, int hashCode)
        {
            add(s, offset);
            this.hashCode = hashCode;
        }

        /**
         *
         * @param s the data to use
         * @param offset into the buffer
         */
        public Line(final String s, int offset)
        {
            add(s, offset);
        }

        /**
         */
        public Line()
        {
        }

        /**
         * Add a string, it is up to you to also supply a newline
         * Will return the newline pos
         *
         * @param s the data including a temperature
         * @param offset where to out the data
         * @return the last position used
         */
        public int add(String s, int offset)
        {
            // ok, for testing, we want to get the
            // data fit in
            var b = s.getBytes();

            // find ;
            int i = 0;
            this.hashCode = 0;
            for (i = 0; i < b.length; i++)
            {
                if (b[i] == ';')
                {
                    break;
                }
                hashCode = 31 * hashCode + b[i];
            }
            // set things
            System.arraycopy(b, 0, data, offset,b.length);
            lineStartPos = offset;
            newlinePos = offset + b.length - 1;
            semicolonPos = offset + i;

            // temperature
            i++;
            int multiplier = 1;
            temperature = 0;
            for (; i < b.length - 1; i++)
            {
                if (b[i] == '-')
                {
                    multiplier = -1;
                }
                else if (b[i] == '.')
                {
                }
                else
                {
                    temperature = temperature * 10 + b[i] - 48;
                }
            }
            this.temperature *= multiplier;

            return newlinePos;
        }


        @Override
        public int hashCode()
        {
            return hashCode;
        }

        @Override
        public String toString()
        {
            return new String(data, this.lineStartPos, this.semicolonPos - this.lineStartPos);
        }
    }

    static class FHS42b_FastHashSet
    {
        // we need only the reference, not the content
        private static final Temperatures FREE_KEY = null;
        /** Fill factor, must be between (0 and 1) */
        private static final float m_fillFactor = 0.5f;

        /** Keys and values */
        public Temperatures[] m_data;

        /** Current map size */
        private int m_size;
        /** Mask to calculate the original position */
        private int m_mask;
        /** We will resize a map once it reaches this size */
        private int m_threshold;

        public FHS42b_FastHashSet( final int size, final float fillFactor )
        {
            final int capacity = arraySize(size, fillFactor);
            m_mask = capacity - 1;

            m_data = new Temperatures[capacity];
            m_threshold = (int) (capacity * fillFactor);
        }

        public void putOrUpdate(final Line line)
        {
            final int ptr = line.hashCode & m_mask;
            Temperatures k = m_data[ ptr ];

            if ( k != FREE_KEY )
            {
                int l = line.semicolonPos - line.lineStartPos;
                byte[] data = k.data;

                // was
//                if (Arrays.equals(data, 0, data.length, line.data, line.lineStartPos, line.semicolonPos))
//                {
//                    k.add(line.temperature);
//                }
                // replaced to have less checks in the mix

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
                            putOrUpdateSlow(line, ptr);
                            return;
                        }
                    }
                    k.add(line.temperature);
                }
                else
                {
                    putOrUpdateSlow(line, ptr);
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
     * Ok, we gotta need some test caes
     */

}

