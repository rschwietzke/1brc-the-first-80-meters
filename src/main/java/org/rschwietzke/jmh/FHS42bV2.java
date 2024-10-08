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

import org.rschwietzke.jmh.FHS42b.Line;
import org.rschwietzke.jmh.FHS42b.Temperatures;

/**
 * Basic setup for FastHashMapBenchmarking
 *
 * @author Rene Schwietzke
 */
public class FHS42bV2
{
    static class FHS42bV2_FastHashSet
    {
        // we need only the reference, not the content
        private static final Temperatures FREE_KEY = null;

        /** Keys and values */
        public Temperatures[] m_data;

        /** Mask to calculate the original position */
        private int m_mask;
        /** Current map size */
        private int m_size;
        /** We will resize a map once it reaches this size */
        private int m_threshold;

        public FHS42bV2_FastHashSet(final int size)
        {
            final int capacity = arraySize(size, 0.5f);
            m_mask = capacity - 1;

            m_data = new Temperatures[capacity];
            m_threshold = (int) (capacity * 0.5f);
        }

        public void putOrUpdate(final Line line)
        {
            int ptr = line.hashCode & m_mask;
            Temperatures k = m_data[ptr];

            outer:
            for (;;)
            {
                if (k != FREE_KEY)
                {
                    int start = line.lineStartPos;
                    final int l = line.semicolonPos - start;
                    final byte[] data = k.data;

                    // we could compare the hashcode and fail early
                    // but we have to do length before a compare
                    // anyway
                    if (l == data.length)
                    {
                        // safe to compare, same length

                        // iterate old fashioned
                        int i = 0;
                        for (; i < l; i++)
                        {
                            if (data[i] != line.data[start])
                            {
                                // get us the next position, because
                                // this one is full already
                                ptr = (ptr + 1) & m_mask;
                                k = m_data[ptr];
                                continue outer;
                            }
                            start++;
                        }

                        // safe, we have i == l
                        // end the loop
                        k.add(line.temperature);
                        return;
                    }
                    else
                    {
                        // pos full and length match failed
                        ptr = (ptr + 1) & m_mask;
                        k = m_data[ptr];
                    }
                }
                else
                {
                    // position was empty, that is rare!
                    // have to do a proper put to avoid filling up the map
                    // without resizing
                    put(line);
                    return;
                }
            }
        }

        private void put(final Line line)
        {
            put(new Temperatures(
                    Arrays.copyOfRange(
                            line.data,
                            line.lineStartPos, line.semicolonPos),
                    line.hashCode, line.temperature));
        }

        private void put(final Temperatures key)
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
                return;
            }
            else if (k.customEquals( key.data ) == -1)
            {
                m_data[ptr] = key;
                return;
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
                    return;
                }
                else if ( k.customEquals( key.data ) == -1)
                {
                    m_data[ptr] = key;
                    return;
                }
            }
        }

        public int size()
        {
            return m_size;
        }

        private void rehash( final int newcapacity )
        {
            m_threshold = (int) (newcapacity * 0.5f);
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

