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
package org.rschwietzke.again26;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Splitting up equalsCity for Inlining
 * 
 * @author Rene Schwietzke
 */
public class BRC113_EqualsCityMismatchSimple extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class City
    {
        public byte[] city;
        public int hashCode;
        // keep the length here to avoid accessing it from the array
        public int length;

        private int min;
        private int max;
        private int total;
        private int count;

        public City(final Line line)
        {
            this.length = line.cityLength;
            this.city = new byte[this.length];

            System.arraycopy(line.backingArray, line.bufferStart, this.city, 0, this.length);
            this.hashCode = line.hash;

            this.min = line.temperature;
            this.max = line.temperature;
            this.total = line.temperature;
            this.count = 1;

            //System.out.format("%s,%d%n", new String(this.city), line.hash);
        }

        /**
         * This is hot code that runs again and again for every line, so we want to keep it as small as possible,
         * @param temperature
         */
        public void merge(final int temperature)
        {
            if (temperature > this.max)
            {
                this.max = temperature;
            }
            else if (temperature < this.min)
            {
                this.min = temperature;
            }
            // this is here to let the expensive branch prediction with a likely
            // miss run first and when we are in the branch, we can fill
            // the rest of the pipeline with this
            // if this is first, we have less instructions but more cycles 
            // cycles are time, not instructions!!!
            this.total += temperature;
            this.count += 1;
        }

        /**
         * But our standard TreeMap will fail otherwise, not safe, but
         * we know what we do. This is not hot, because we only need that for the 
         * TreeMap final calculations.
         * 
         * @param o the other city to compare with
         */
        @Override
        public boolean equals(Object o)
        {
            City other = (City)o;
            return Arrays.compare(this.city, other.city) == 0;
        }

        private boolean arrayEquals(Line line)
        {
            int start = line.bufferStart;
            int sem = line.semicolon;

            return Arrays.equals(this.city, 0, this.city.length, line.backingArray, start, sem);
        }

        /**
         * We need that to ensure we compare against the defining key
         * Because we don't want to trap us against standard equals,
         * we name it differently
         */
        public boolean equalsCity(final Line line)
        {
            final int len = this.length;
            if (len != line.cityLength)
            {
                return false;
            }

            return jdk.internal.util.ArraysSupport.mismatch(this.city, 0, line.backingArray, line.bufferStart, len) == -1;
        }

        public String getCity()
        {
            return new String(this.city);
        }

        /**
         * We need that to ensure we use the city as defining key
         */
        @Override
        public int hashCode()
        {
            return hashCode;
        }

        public String toString()
        {
            // we delegate the formatting to our math util to
            // ensure we do the same everywhere, helps us later to 
            // change the accuracy if needed
            return MathUtil.toStringFromInteger(total, count, min, max);
        }
    }

    @Override
    public String run(final String fileName) throws IOException
    {
        // open the file
        try (var file = new RandomAccessFile(fileName, "r"); 
                var channel = file.getChannel())
        {
            // our transport container for a lot of intel at once
            final Line line = new Line(channel);

            var cities = line.process();

            // ok, we got everything, now we need to order it
            return cities.toTreeMap().toString();
        }
    }

    public static class LightSet
    {
        private City[] data;
        private int size;
        private int mask;  
        private int threshold;

        public LightSet(int initialCapacity)
        {
            if (initialCapacity < 7)
            {
                throw new IllegalArgumentException("Capacity must be >= 7");
            }

            // we need a power of two for the capacity to be able to run modules as bitwise AND
            final int capacity = nextPowerOfTwo(initialCapacity);

            this.mask = capacity - 1;
            this.threshold = capacity >> 1; // load factor 0.5
            this.data = new City[capacity];
        }

        /**
         * Get value for key
         * 
         * Best case: 2 branches, key found
         * Worst case: N branches, key not found after N collisions
         * 
         * @param key   the key to search for
         */
        public void update(final Line line)
        {
            final int index = line.hash & this.mask;

            final City city = this.data[index];
            if (city == null)
            {
                add(line, index);
            }
            // we removed the size check in equalsCity, so we have to have that here
            else if (city.equalsCity(line))
            {
                city.merge(line.temperature); 
            }
            else
            {
                // expensive path
                updateCollision(line, index);
            }
        }

        /**
         * This add is meant to take some of the code out of the update method
         * to make it more inlineable
         */
        private void add(final Line line, int index)
        {
            this.data[index] = new City(line);
            this.size++;

            // check size
            if (this.size > this.threshold)
            {
                resize();
            }
        }

        /**
         * We handle that as separate method to keep the main get() fast and inlineable.
         * 
         * @param key   the key to search for
         * @param index the original index where the collision happened
         * @return     the value or null
         */
        //int c = 0;
        private void updateCollision(final Line line, int index)
        {
            while (true)
            {
                //String c = line.toCity();
                index = (index + 1) & this.mask;

                final City city = this.data[index];
                if (city == null)
                {
                    add(line, index);
                    // System.out.println("Collisions Add: " + ++c);
                    break;
                }
                // we removed the size check in equalsCity, so we have to have that here
                else if (city.equalsCity(line))
                {
                    city.merge(line.temperature); 
                    //System.out.println("Collisions Merge: " + ++c);
                    break;
                }
            }
        }

        public void add(final City city)
        {
            final int hash = city.hashCode();
            final int index = hash & this.mask;

            final City c = this.data[index];
            if (c == null)
            {
                this.data[index] = city;
                this.size++;

                // check size
                if (this.size > this.threshold)
                {
                    resize();
                }
            }
            else if (city.equals(c))
            {
                this.data[index] = city;
            }
            else
            {
                addCollision(city, index);
            }
        }

        private void addCollision(City city, int index)
        {
            while (true)
            {
                index = (index + 1) & this.mask;

                final City c = this.data[index];
                if (c == null)
                {
                    this.data[index] = city;
                    this.size++;

                    // check size
                    if (this.size > this.threshold)
                    {
                        resize();
                    }

                    return;
                }
                else if (city.equals(c))
                {
                    this.data[index] = city;
                    return;
                }
            }
        }

        private void resize()
        {
            final City[] oldData = this.data;
            this.data = new City[oldData.length << 1];

            this.mask = this.data.length - 1;
            this.threshold = this.data.length >> 1;

            // size is new
            this.size = 0;

            // ok, we use put to reinsert everything
            for (int i = 0; i < oldData.length; i++)
            {
                final City c = oldData[i];
                if (c != null)
                {
                    this.add(c);
                }
            }
        }

        public int size()
        {
            return this.size;
        }

        public TreeMap<String, City> toTreeMap()
        {
            final var map = new TreeMap<String, City>();

            // ok, we use put to reinsert everything
            for (int i = 0; i < this.data.length; i++)
            {
                final City c = this.data[i];
                if (c != null)
                {
                    map.put(c.getCity(),  c);
                }
            }

            return map;
        }

        public List<City> values()
        {
            final var keys = new ArrayList<City>(this.size);

            for (int i = 0; i < this.data.length; i++)
            {
                final City c = this.data[i];
                if (c != null)
                {
                    keys.add(c);
                }
            }

            return keys;
        }

        public static int nextPowerOfTwo(final int n)
        {
            if (n <= 0) 
            {
                return 1;
            }

            // Check if n is already a power of two
            if ((n & (n - 1)) == 0) 
            {
                return n;
            } 

            return Integer.highestOneBit(n) << 1;
        }
    }

    public static class Line
    {
        // by agreement, we never have more than 100 bytes of city, so stay
        // un the safe zone
        private int bufferStart = 0;
        private int bufferEnd = 0;
        private int bufferPos = 0;

        public int semicolon;
        public int temperature;
        public int hash;
        public int cityLength;
        private byte[] backingArray = new byte[500_000];

        private FileChannel channel;
        private ByteBuffer buffer = ByteBuffer.wrap(this.backingArray); 

        public Line(FileChannel channel)
        {
            this.channel = channel;
            this.buffer.limit(0); // empty
        }

        public LightSet process() throws IOException
        {
            // our storage, sized to avoid rehashing (about 400 stations in total)
            final LightSet cities = new LightSet(4096);

            // read all lines until end of file
            while (readLine())
            {
                // let the set decide what to do
                cities.update(this);
            }
            return cities;
        }

        private boolean fillBuffer() throws IOException
        {
            // fix the wrapper up first
            buffer.position(this.bufferPos);

            buffer.compact();
            int read = channel.read(buffer);
            buffer.flip();

            if (read == -1 && !buffer.hasRemaining())
            {
                // we reached the end
                this.bufferStart = 0;
                this.bufferEnd = buffer.limit();
                this.bufferPos = 0;
                return true; // EOF
            }
            this.bufferStart = this.buffer.position();
            this.bufferEnd = buffer.limit();
            this.bufferPos = this.bufferStart;

            return false;
        }

        public boolean readLine() throws IOException
        {
            // ok, it is very inefficient to read directly from
            // the input stream or channel, so we have to buffer
            // it first, ensure we have more data than one line
            // is long
            if (this.bufferEnd - this.bufferPos < 128)
            {
                // this is very unlikely to happen often, so it is no here in the
                // code to make it smaller and hence inlineable
                if (fillBuffer())
                {
                    return false;
                }
            }

            // let's operate on the backing array directly to speed things up
            // keep track of the "reads" to be able to calculate the next position

            // read all data till the ; and calc the hash on the go
            int totalRead = this.bufferPos;
            int start = totalRead;

            // find the semicolon and calculate hash in one go
            int h = 0x811c9dc5; // FNV offset basis, but rest is regular factor 31 hashing
            while (true)
            {
                byte b = this.backingArray[totalRead];  
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                totalRead++;

                b = this.backingArray[totalRead];  
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                totalRead++;

                b = this.backingArray[totalRead];  
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                totalRead++;

                b = this.backingArray[totalRead];  
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                totalRead++;
            }
            this.cityLength = totalRead - start;
            this.semicolon = totalRead++;
            this.hash = h;
            this.bufferStart = start;

            // skip newline
            // + 2 because we jump to \n and one more
            this.bufferPos =  parseTemperature(totalRead) + 2;

            //            System.out.format("Read: %s%n" ,
            //                    new String(this.backingArray, 
            //                            this.startPos, totalRead));
            //            System.out.format("Read line: %s;%d - hash: %d%n" ,
            //                    new String(this.backingArray, 
            //                            this.startPos, this.semicolon - this.startPos),
            //                    value, this.hash);

            return true;
        }


        private int parseTemperature(int totalRead)
        {
            // read last if needed
            // 01234 Position, n means newline
            // 9.9n
            // 99.9n
            // -9.9n
            // -99.9n to be covered later

            byte b0 = this.backingArray[totalRead++];

            if (b0 == '-')
            {
                // -9.9 or -99.9
                b0 = this.backingArray[totalRead++];
                byte b2 = this.backingArray[totalRead++];

                if (b2 == '.')
                {
                    // -9.9
                    byte b3 = this.backingArray[totalRead];
                    this.temperature = -(b0 * 10 + b3 - ('0' * 10 + '0'));
                }
                else
                {
                    // -99.9
                    byte b4 = this.backingArray[++totalRead];
                    this.temperature = -(b0 * 100 + b2 * 10 + b4 - ('0' * 100 + '0' * 10 + '0'));
                }
            }
            else
            {
                // 9.9 or 99.9
                byte b1 = this.backingArray[totalRead++];

                if (b1 == '.')
                {
                    // 9.9
                    byte b2 = this.backingArray[totalRead];
                    this.temperature = (b0 * 10 + b2) - ('0' * 10 + '0');
                }
                else
                {
                    // 99.9
                    byte b3 = this.backingArray[++totalRead];
                    this.temperature = b0 * 100 + b1 * 10 + b3 - ('0' * 100 + '0' * 10 + '0');
                }
            }

            return totalRead;
        }

        /**
         * For debugging
         * @return
         */
        private String toCity()
        {
            var ba = new byte[cityLength];
            System.arraycopy(backingArray, bufferStart, ba, 0, cityLength);
            return new String(ba);
        }

    }


    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC113_EqualsCityMismatchSimple.class, args);
    }
}
