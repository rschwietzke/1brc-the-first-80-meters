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
 *
 *
 * @author Rene Schwietzke
 */
public class BRC57_SimplerHashing_VOID extends Benchmark
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

        /**
         * Merge two temperatures
         *
         * @param value the temperature to add
         */
        public void merge(final Temperatures t)
        {
            if (t.min < this.min)
            {
                this.min = t.min;
            }
            else if (t.max > this.max)
            {
                this.max = t.max;
            }
            this.total += t.total;
            this.count += t.count;
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

    static class Line
    {
        private static int MIN_BUFFERSIZE = 1_000_000;
        private static int REMAINING_MIN_BUFFERSIZE = 200;

        private final byte[] data = new byte[MIN_BUFFERSIZE];
        private final RandomAccessFile file;

        int pos = 0;
        int endToReload= 0;
        int newlinePos = -1;
        int end = 0;

        int lineStartPos = 0;
        int semicolonPos = -1;

        int hashCode;
        int temperature;

        boolean EOF = false;

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
        private void read()
        {
            // do we near the end of the buffer?
            if (pos >= this.endToReload)
            {
                this.endToReload = moveData();
            }
            else
            {
                this.lineStartPos = this.pos;
            }

            // look for semicolon and new line
            // when checking for semicolon, we do the hashcode right away
            int h = 0;
            int i = this.pos;
            for (;;)
            {
                i++;
                final byte b = data[i];
                if (b == ';')
                {
                    break;
                }
                h ^= b + (h << 5);
            }

            this.semicolonPos = i++;
            this.hashCode = h;

            // we know for the numbers that we are very fix in length,
            // so let's read forward
            // we don't check if we have enough data because we have correct
            // data and we read early enough to have always a full line in the buffer

            // could be 9 or -
            int value = data[i++];

            // can be - or 0..9
            if (value == '-')
            {
                // got a - so it is -[9]9.9 or -[9].9
                // next is a number, overwrite value
                value = data[i++] ^ DIGITOFFSET;

                // next is -9[9].9 or -9[.]9
                var dot = data[i++];
                if (dot >= '0')
                {
                    // got no . so 9[9].9
                    value = value * 10 + (dot ^ DIGITOFFSET);

                    // skip .
                    i++;
                }
                else
                {
                    // drop . read
                }

                // next is -99[.]9 or -9.[9]
                value = value * 10 + (data[i++] ^ DIGITOFFSET);

                this.temperature = -value;
                this.pos = i + 1;
                this.newlinePos = i;            }
            else
            {
                // [9]9.9 or [9].9
                // already read one number
                // just make this a number
                value = value ^ DIGITOFFSET;

                // next is 9[9].9 or 9[.]9
                var dot = data[i++];
                if (dot >= '0')
                {
                    // got no . so read 9[9].9
                    value = value * 10 + (dot ^ DIGITOFFSET);

                    // skip .
                    i++;
                }
                else
                {
                    // drop . read
                }

                // next is 99[.]9 or 9.[9]
                value = value * 10 + (data[i++] ^ DIGITOFFSET);

                this.temperature = value;
                this.pos = i + 1;
                this.newlinePos = i;            }
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
        final FastHashSet cities = new FastHashSet(4096 << 1);

        try (var raf = new RandomAccessFile(fileName, "r"))
        {
            final Line line = new Line(raf);

            while (!line.EOF)
            {
                line.read();
                cities.putOrUpdate(line);
            }

            // crawl to the end
            for (; line.pos < line.end; )
            {
                line.read();
                cities.putOrUpdate(line);
            }
        }

        return cities.toTreeMap().toString();
    }

    private static final int DIGITOFFSET = 48;

    static class FastHashSet
    {
        // we need only the reference, not the content
        private static final Temperatures FREE_KEY = null;

        /** Mask to calculate the original position */
        private int m_mask;

        /** Keys and values */
        private Temperatures[] m_data;

        /** Current map size */
        private int m_size;
        /** We will resize a map once it reaches this size */
        private int m_threshold;

        public FastHashSet(final int size)
        {
            m_mask = size - 1;
            m_data = new Temperatures[size];
            m_threshold = (int) (size * 0.5f);
        }

        public void putOrUpdate(final Line line)
        {
            final int ptr = line.hashCode & m_mask;
            final Temperatures k = m_data[ptr];

            if ( k != FREE_KEY )
            {
                int l = line.semicolonPos - line.lineStartPos;

                // check length first
                if (l == k.data.length)
                {
                    // iterate old fashioned
                    int start = line.lineStartPos;
                    for (int i = 0; i < l; i++)
                    {
                        if (k.data[i] != line.data[start + i])
                        {
                            putOrUpdateSlow(line, ptr);
                            return;
                        }
                    }

                    k.add(line.temperature);
                }
                else
                {
                    // we got a collision
                    putOrUpdateSlow(line, ptr);
                }
            }
            else
            {
                // have to do a proper put to avoid filling up the map
                // without resizing
                put(line);
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

        private void putOrUpdateSlow(final Line line, int ptr)
        {
            outer:
            while (true)
            {
                ptr = (ptr + 1) & m_mask; //that's next index
                final Temperatures k = m_data[ptr];

                if (k == FREE_KEY)
                {
                    // proper put
                    put(line);
                    return;
                }
                // we do the slower mismatch here, rare, don't care at the moment
                else
                {
                    final int l = line.semicolonPos - line.lineStartPos;

                    // check length first
                    if (l == k.data.length)
                    {
                        // iterate old fashioned
                        int start = line.lineStartPos;
                        int i = 0;
                        for (; i < l; i++)
                        {
                            if (k.data[i] != line.data[start + i])
                            {
                                // no match, look again
                                continue outer;
                            }
                        }

                        // matched
                        k.add(line.temperature);
                        return;
                    }
                }
            }
        }

        private Temperatures put(final Temperatures key)
        {
            int ptr = key.hashCode();

            while ( true )
            {
                ptr = ptr & m_mask; //that's next index calculation
                final Temperatures k = m_data[ptr];

                if ( k == FREE_KEY )
                {
                    m_data[ptr] = key;
                    if ( m_size >= m_threshold )
                    {
                        rehash( m_data.length * 2 ); //size is set inside
                    }
                    else
                    {
                        ++m_size;
                    }
                    return null;
                }
                else if (k.customEquals( key.data ) == -1)
                {
                    final Temperatures ret = m_data[ptr];
                    m_data[ptr] = key;
                    return ret;
                }
                ptr++;
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
    }

    /**
     * Just to run our benchmark
     */
    public static void main(String[] args)
    {
        Benchmark.run(BRC57_SimplerHashing_VOID.class, args);
    }
}
