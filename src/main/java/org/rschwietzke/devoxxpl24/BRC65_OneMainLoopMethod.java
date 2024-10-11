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
 *
 *
 * @author Rene Schwietzke
 */
public class BRC65_OneMainLoopMethod extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    static class Temperatures
    {
        private int min;
        private int max;
        private long total;
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

        /**
         * -1 if the same, position of different otherwise
         */
        public int customEquals(final byte[] other)
        {
            return Arrays.mismatch(data, 0, data.length, other, 0, other.length);
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

        public void run(final FastHashSet cities)
        {
            while (!EOF)
            {
                read();
                cities.putOrUpdate(this);
            }

            // crawl to the end
            for (; pos < end; )
            {
                read();
                cities.putOrUpdate(this);
            }
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
                final byte b = data[i];
                if (b == ';')
                {
                    break;
                }
                var x = h << 5;
                var y = b - h;
                h = x + y;

                i++;

                // we can safely do that because we know there will be more afterwards aka
                // numbers
                final byte b2 = data[i];
                if (b2 == ';')
                {
                    break;
                }
                var x2 = h << 5;
                var y2 = b2 - h;
                h = x2 + y2;

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
                this.newlinePos = i;
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
                this.newlinePos = i;
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
            line.run(cities);
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
            this.m_mask = capacity - 1;

            this.m_data = new Temperatures[capacity];
            this.m_threshold = capacity >> 2;
        }

        public void putOrUpdate(final Line line)
        {
            // first index
            int ptr = line.hashCode & m_mask;

            for (;;)
            {
                final Temperatures k = m_data[ptr];

                // having something here is typical, because we
                // mostly update and don't insert
                if (k != FREE_KEY)
                {
                    final int newLength = line.semicolonPos - line.lineStartPos;
                    var isEquals = newLength == k.data.length;

                    // quick check by length as first change to bail out
                    // likely the size is not the same, so this is saving
                    // cycles
                    if (isEquals)
                    {
                        // iterate old fashioned
                        int start = line.lineStartPos;
                        for (int i = 0; i < newLength; i++)
                        {
                            // unrolling does not help here, tried that before
                            isEquals &= k.data[i] == line.data[start + i];
                        }

                        if (isEquals)
                        {
                            // same, update and leave
                            k.add(line.temperature);
                            return;
                        }
                    }

                    // we got a collision
                    ptr = (ptr + 1) & m_mask; //that's next index
                    continue;
                }
                else
                {
                    // new to this party? add it properly and grow the
                    // set if needed
                    put(line);

                    // stop and we got that entry
                    return;
                }
            }
        }

        private void put(final Line line)
        {
            // create us a new storage object
            put(new Temperatures(
                    Arrays.copyOfRange(
                            line.data,
                            line.lineStartPos, line.semicolonPos),
                    line.hashCode, line.temperature));
        }

        private void put(final Temperatures key)
        {
            // initial position
            int ptr = key.hashCode & m_mask;

            while (true)
            {
                // we do that again here, code is just nicer
                var currentKey = this.m_data[ptr];

                if (currentKey == FREE_KEY)
                {
                    // store it
                    this.m_data[ptr] = key;
                    this.m_size++;

                    // are we too full? Especially important for
                    // more cities
                    rehash();

                    // set and done
                    return;
                }
                else if (currentKey.customEquals(key.data) == -1)
                {
                    throw new RuntimeException(
                            String.format("Key '%s;%s' is already in the set as '%s;%s'",
                                    currentKey.getCity(), currentKey.toString(),
                                    key.getCity(), key.toString()));
                }

                // that's the next index
                ptr = (ptr + 1) & m_mask;
            }
        }

        public int size()
        {
            return this.m_size;
        }

        private void rehash()
        {
            // anything to do?
            if (this.m_size < this.m_threshold)
            {
                // nothing to do
                return;
            }

            final var newCapacity = m_data.length << 2;
            this.m_threshold = newCapacity >> 2;
            this.m_mask = newCapacity - 1;

            final var oldCapacity = this.m_data.length;
            final var oldData = this.m_data;

            this.m_data = new Temperatures[newCapacity];
            this.m_size = 0;

            // ok, copy from old to new. Only care about
            // used positions obviously
            for (int i = 0; i < oldCapacity; i++)
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
        Benchmark.run(BRC65_OneMainLoopMethod.class, args);
    }
}
