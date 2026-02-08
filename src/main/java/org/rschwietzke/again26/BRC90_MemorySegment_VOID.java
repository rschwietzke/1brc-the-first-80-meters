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
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;



/**
 * Use some JDK array utils to speed up the city name comparison
 * 
 * @author Rene Schwietzke
 */
public class BRC90_MemorySegment_VOID extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class City
    {
        public byte[] city;
        public int hashCode;
        
        private int min;
        private int max;
        private int total;
        private int count;

        public City(final Line line)
        {
            int len = line.cityLength;
            this.city = new byte[len];
            
            MemorySegment.copy(line.memorySegment, ValueLayout.JAVA_BYTE, 
                    line.bufferStart, this.city, 0, len);
            
            this.hashCode = line.hash;
            
            this.min = line.temperature;
            this.max = line.temperature;
            this.total = line.temperature;
            this.count = 1;
            
//            System.out.format("Created city: %s, temp: %d%n", new String(this.city), line.temperature);
        }

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
            this.total += temperature;
            this.count += 1;
        }

        /**
         * But our standard TreeMap will fail otherwise, not safe, but
         * we know what we do       
         */
        @Override
        public boolean equals(Object o)
        {
            City other = (City)o;
            return Arrays.compare(this.city, other.city) == 0;
        }

        /**
         * We need that to ensure we compare against the defining key
         * Because we don't want to trap us against standard equals,
         * we name it differently
         */
        public boolean equalsCity(Line line)
        {
            int len = this.city.length;
            if (len != line.cityLength)
            {
                return false;
            }

            long start = line.bufferStart;
            long sem = line.semicolon;
            int pos = 0;
            for (long i = start; i < sem; i++)
            {
                if (this.city[pos++] != line.memorySegment.get(ValueLayout.JAVA_BYTE, i))
                {
                    return false;
                }
            }
            return true;
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
        try (Arena arena = Arena.ofConfined();
                FileChannel channel = FileChannel.open(Path.of(fileName), StandardOpenOption.READ))
        {
            // 1. Map the file into memory
            // Note: 'size' can be larger than Integer.MAX_VALUE
            long fileSize = channel.size();
            MemorySegment segment = channel.map(
                FileChannel.MapMode.READ_ONLY, 
                0, 
                fileSize, 
                arena
            );
            
            // our transport container for a lot of intel at once
            final Line line = new Line(segment);

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
            final int hash = line.hash ; // ensure non-negative
            final int index = hash & this.mask;

            final City city = this.data[index];
            if (city == null)
            {
                add(line, index);
            }
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
        private void updateCollision(final Line line, int index)
        {
            while (true)
            {
                index = (index + 1) & this.mask;

                final City city = this.data[index];
                if (city == null)
                {
                    add(line, index);
                    return;
                }
                else if (city.equalsCity(line))
                {
                    city.merge(line.temperature); 
                    return;
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
        private long bufferPos = 0;
        private long bufferStart = 0;
        
        public long semicolon;
        public int temperature;
        public int hash;
        public int cityLength;

        public final MemorySegment memorySegment;
        
        public Line(final MemorySegment memorySegment)
        {
            this.memorySegment = memorySegment;
        }
        
        public LightSet process() throws IOException
        {
            // our storage, sized to avoid rehashing (about 400 stations in total)
            final LightSet cities = new LightSet(4096);

            // read all lines until end of
            long size = memorySegment.byteSize();
            while (this.bufferPos < size)
            {
                readLine();
            
                // let the set decide what to do
                cities.update(this);
            }
            return cities;
        }
        
        public void readLine() throws IOException
        {
            // find the semicolon and calculate hash in one go
            int h = 0;
            this.bufferStart = this.bufferPos;
            long pos = this.bufferPos;
            
            while (true)
            {
                byte b = memorySegment.get(ValueLayout.JAVA_BYTE, pos);
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                pos++;

                b = memorySegment.get(ValueLayout.JAVA_BYTE, pos);  
                if (b == ';')
                {
                    break;
                }
                h = (h << 5) - h + b;
                pos++;
            }
            this.cityLength = (int) (pos - this.bufferStart);
            this.semicolon = pos++;
            this.hash = h;
            
            // skip newline
            // + 2 because we jump to \n and one more
            this.bufferPos =  parseTemperature(pos) + 2;

//            System.out.format("Read from %d to %d (%d bytes), temp: %d%n", 
//                    this.bufferStart, t, t - this.bufferStart, this.temperature);
//            this.bufferPos = t;
            
//            System.out.format("Read line: %s;%d - hash: %d%n" ,
//                    new String(this.backingArray, 
//                            this.startPos, this.semicolon - this.startPos),
//                    value, this.hash);
        }
        
    
        private long parseTemperature(long pos)
        {
            int value;
            
            byte b = memorySegment.get(ValueLayout.JAVA_BYTE, pos++);
            if (b == '-')
            {
                // ok, -9.9 or -99.9
                // first is always a number
                byte b0 = memorySegment.get(ValueLayout.JAVA_BYTE, pos++);
                b0 &= 15;

                // next is either . or another number
                byte b1 = memorySegment.get(ValueLayout.JAVA_BYTE, pos++);
                if (b1 != '.')
                {
                    b1 &= 15;

                    // must be 99.9
                    
                    // skip the ., we just read a number
                    pos++;

                    // the part after the .
                    byte b2 = memorySegment.get(ValueLayout.JAVA_BYTE, pos);
                    value = -(100 * b0 + 10 * b1 + (b2 & 15));
                }
                else
                {
                    // skip .

                    // it is -9.9
                    // the part after the .
                    byte b2 = memorySegment.get(ValueLayout.JAVA_BYTE, pos);
                    value = -(10 * b0 + (b2 & 15));
                }
            }
            else
            {
                // ok, 9.9 or 99.9
                b &= 15;

                // next is either . or another number
                byte b1 = memorySegment.get(ValueLayout.JAVA_BYTE, pos++);
                if (b1 != '.')
                {
                    // must be 99.9
                    b1 &= 15;

                    // skip the .
                    pos++;
                    
                    byte b2 = memorySegment.get(ValueLayout.JAVA_BYTE, pos);
                    value = 100 * b + 10 * b1 + (b2 & 15);
                }
                else
                {
                    // skip .
                    // it is 9.9
                    byte b2 = memorySegment.get(ValueLayout.JAVA_BYTE, pos);
                    value = 10 * b + (b2 & 15);
                }
            }
            this.temperature = value;       
            
            return pos;
        }
    }
    

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC90_MemorySegment_VOID.class, args);
    }
}
