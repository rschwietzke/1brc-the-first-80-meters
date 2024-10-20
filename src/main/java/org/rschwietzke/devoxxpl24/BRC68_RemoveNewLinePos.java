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

/**
 * Remove some data not needed
 *
 * Result: Saved 2 billion instructions but
 * gained 115k branched, missed from 2.8 to 3.0%
 * Bummer! That cannot be right... where do the branches
 * come from?
 *
 * @author Rene Schwietzke
 */
public class BRC68_RemoveNewLinePos extends Benchmark
{
    /**
     * Hold the city and temp data, everything as int
     */
    static class Temperatures
    {
        private int min;
        private int max;
        private long total;
        private int count;
        private final byte[] city;
        private final int length;
        private final int hashCode;

        public Temperatures(final byte[] city, final int hashCode, final int value)
        {
            this.city = city;
            this.length = city.length;
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

        /**
         * We only use that path for our grow and put of the set
         * @param other
         * @return
         */
        public int customEquals(final byte[] other)
        {
            return Arrays.mismatch(city, 0, city.length, other, 0, other.length);
        }

        /**
         * We have a custom equals WHERE we need it, this here
         * is not used and we prevent us from making mistakes.
         */
        @Override
        public boolean equals(final Object o)
        {
            // just to filter long nights out
            throw new RuntimeException("Equals is not supported");
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
            return new String(city, 0, city.length);
        }

        /**
         * Our final printing format
         */
        public String toString()
        {
            final double mean = (double)this.total / (double)this.count;
            return round(min) + "/" + round(mean) + "/" + round(max);
        }
    }

    static class Line
    {
        private static int MIN_BUFFERSIZE = 1_000_000;
        private static int REMAINING_MIN_BUFFERSIZE = 200;

        private final byte[] data = new byte[MIN_BUFFERSIZE];
        private final RandomAccessFile file;

        // we are not using a ByteBuffer, too expensive too
        // call it for each byte
        int pos = 0;
        int endToReload= 0;
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
            // ok, move the remaining data first
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
            // this branch is likely one of the expensive ones
            // but a loop on the array would not be any better
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
                byte b = data[i];
                // do the calc before the jump so the CPU
                // can safely run them with suffering from
                // mispredictions, does not help by the way
                var x = h << 5;
                var y = b - h;
                if (b == ';')
                {
                    break;
                }
                // only know we can use it
                h = x + y;
                i++;

                // we can safely do that because we know there will be more afterwards aka
                // numbers
                b = data[i];
                x = h << 5;
                y = b - h;
                if (b == ';')
                {
                    break;
                }
                h = x + y;
                i++;
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
            }
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
            }
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
        final FastHashSet cities = new FastHashSet(4096);

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
            // no check for power of 2, so be careful
            final int capacity = size;
            m_mask = capacity - 1;

            m_data = new Temperatures[capacity];
            m_threshold = (int) (capacity * 0.5f);
        }

        public void putOrUpdate(final Line line)
        {
            // having the while loop here is slower due to
            // more branch misses, don't know yet exactly
            // why it turns out that way

            final int ptr = line.hashCode & m_mask;
            final Temperatures k = m_data[ptr];

            if ( k != FREE_KEY )
            {
                final int newLength = line.semicolonPos - line.lineStartPos;
                // we keep the array length in our own variable to avoid
                // a null check against k.city
                var isEquals = newLength == k.length;

                if (isEquals)
                {
                    // iterate old fashioned
                    final int start = line.lineStartPos;
                    for (int i = 0; i < newLength; i++)
                    {
                        // unrolling does not help here, tried that
                        isEquals &= k.city[i] == line.data[start + i];
                    }

                    if (isEquals)
                    {
                        k.add(line.temperature);
                        return;
                    }
                }

                // we got a collision
                putOrUpdateSlow(line, ptr);
                return;
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

        /**
         * The rare path when we have a collision
         *
         * @param line
         * @param ptr the last position
         */
        private void putOrUpdateSlow(final Line line, int ptr)
        {
            outer:
                while (true)
                {
                    ptr = (ptr + 1) & m_mask; //that's next index
                    final Temperatures k = m_data[ptr];

                    if (k == FREE_KEY)
                    {
                        // proper put, with a chance to grow
                        put(line);
                        return; // all done
                    }
                    // we do the slower mismatch here, rare, don't care at the moment
                    else
                    {
                        final int l = line.semicolonPos - line.lineStartPos;

                        // check length first
                        if (l == k.length)
                        {
                            // iterate old fashioned
                            int start = line.lineStartPos;
                            int i = 0;
                            for (; i < l; i++)
                            {
                                if (k.city[i] != line.data[start + i])
                                {
                                    // no match, look again, next pos
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
                        rehash( m_data.length << 2 ); //size is set inside
                    }
                    else
                    {
                        ++m_size;
                    }
                    return null;
                }
                else if (k.customEquals(key.city) == -1)
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

        private void rehash(final int newcapacity)
        {
            this.m_threshold = (int) (newcapacity * 0.5f);
            this.m_mask = newcapacity - 1;

            final int oldcapacity = this.m_data.length;
            final Temperatures[] oldData = this.m_data;

            this.m_data = new Temperatures[newcapacity];
            this.m_size = 0;

            for (int i = 0; i < oldcapacity; i++)
            {
                final Temperatures oldKey = oldData[i];
                if (oldKey != FREE_KEY)
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
        Benchmark.run(BRC68_RemoveNewLinePos.class, args);
    }
}
