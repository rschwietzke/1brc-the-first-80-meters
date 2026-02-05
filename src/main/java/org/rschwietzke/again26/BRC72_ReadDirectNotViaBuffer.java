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
 * Our parsing got too big, so it was not inlineable anymore.
 * Copy the code into the readline instead.
 * 
 * @author Rene Schwietzke
 */
public class BRC72_ReadDirectNotViaBuffer extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class City
    {
        public byte[] city;
        public int cityLength;
        public int hashCode;
        private int min;
        private int max;
        private int total;
        private int count;

        public City(final Line line)
        {
            this.city = new byte[line.semicolon];
            for (int i = 0; i < line.semicolon; i++)
            {
                this.city[i] = line.data[i];
            }
            
            this.hashCode = line.hash;
            this.cityLength = line.semicolon;
            this.min = line.temperature;
            this.max = line.temperature;
            this.total = line.temperature;
            this.count = 1;
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
            int len = line.semicolon;
            if (this.cityLength != len)
            {
                return false;
            }

            for (int i = 0; i < len; i++)
            {
                if (this.city[i] != line.data[i])
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
        // our storage, sized to avoid rehashing (413 stations in total)
        final LightSet cities = new LightSet(4096);

        // open the file
        try (var file = new RandomAccessFile(fileName, "r"); 
                var channel = file.getChannel())
        {
            // our transport container for a lot of intel at once
            final Line line = new Line(channel);

            // read all lines until end of file
            while (line.readLine())
            {
                // let the set decide what to do
                cities.update(line);
            }
        }

        // ok, we got everything, now we need to order it
        return cities.toTreeMap().toString();
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
            final int hash = line.hash;
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

        public void addCollision(City city, int index)
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
        public final byte[] data = new byte[250];
        public int semicolon;
        public int temperature;
        public int hash;
        private FileChannel channel;
        private ByteBuffer buffer = ByteBuffer.allocate(8192);
        
        public Line(FileChannel channel)
        {
            this.channel = channel;
            this.buffer.limit(0); // empty
        }

        private boolean fillBuffer() throws IOException
        {
            buffer.compact();
            int read = channel.read(buffer);
            buffer.flip();
            
            if (read == -1 && !buffer.hasRemaining())
            {
                // we reached the end
                return true; // EOF
            }
            return false;
        }
        
        public boolean readLine() throws IOException
        {
            // ok, it is very inefficient to read directly from
            // the input stream or channel, so we have to buffer
            // it first, ensure we have more data than one line
            // is long
            if (buffer.remaining() < 128)
            {
                // this is very unlikely to happen often, so it is no here in the
                // code to make it smaller and hence inlineable
                if (fillBuffer())
                {
                    return false;
                }
            }
                
            // read all data till the \n, calc the hash
            // an parse it
            int length = 0;
            
            // find the semicolon and calculate hash in one go
            int h = 0;
            while (true)
            {
                byte b = this.buffer.get(); 
                this.data[length] = b;

                if (b == ';')
                {
                    this.semicolon = length;
                    break;
                }
                h = 31 * h + b;
                length++;
            }
            this.hash = h;

            // we inlined the parse integer here as well to make it more inlineable
            int value;
            // we can avoid the -48 by just ANDing with 15 (0b00001111)
//            Character  Decimal   Binary
//            0       48 00110000
//            1       49 00110001
//            2       50 00110010
//            3       51 00110011
//            4       52 00110100
//            5       53 00110101
//            6       54 00110110
//            7       55 00110111
//            8       56 00111000
//            9       57 00111001
            
            byte b = buffer.get();
            if (b == '-')
            {
                // ok, -9.9 or -99.9
                // first is always a number
                byte b0 = buffer.get();

                // next is either . or another number
                byte b1 = buffer.get();
                if (b1 != '.')
                {
                    // must be 99.9
                    
                    // skip the .
                    buffer.position(buffer.position() + 1);

                    // the part after the .
                    byte b2 = buffer.get();
                    value = -(100 * (b0 & 15) + 10 * (b1 & 15) + (b2 & 15));
                }
                else
                {
                    // skip .

                    // it is -9.9
                    // the part after the .
                    byte b2 = buffer.get();
                    value = -(10 * (b0 & 15) + (b2 & 15));
                }
            }
            else
            {
                // ok, 9.9 or 99.9
                
                // next is either . or another number
                byte b1 = buffer.get();
                if (b1 != '.')
                {
                    // must be 99.9

                    // skip the .
                    buffer.position(buffer.position() + 1);
                    byte b2 = buffer.get();
                    value = 100 * (b & 15) + 10 * (b1 & 15) + (b2 & 15);
                }
                else
                {
                    // skip .
                    // it is 9.9
                    byte b2 = buffer.get();
                    value = 10 * (b & 15) + (b2 & 15);
                }
            }
            this.temperature = value;            
            
            
            // skip newline
            buffer.position(buffer.position() + 1);

            return true;
        }
        
    }
    
    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC72_ReadDirectNotViaBuffer.class, args);
    }
}
