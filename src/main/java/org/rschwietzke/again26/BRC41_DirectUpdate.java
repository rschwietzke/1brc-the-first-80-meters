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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * We check for null twice, in get and in the main loop, so
 * enable the Set to run updates.
 * 
 * @author Rene Schwietzke
 */
public class BRC41_DirectUpdate extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class City
    {
        private String city;
        private int min;
        private int max;
        private long total;
        private long count;

        public City(final String city, final int temperature)
        {
            this.city = city;
            this.min = temperature;
            this.max = temperature;
            this.total = temperature;
            this.count = 1;
        }

        public void merge(final int temperature)
        {
            this.min = Math.min(this.min, temperature);
            this.max = Math.max(this.max, temperature);
            this.total += temperature;
            this.count += 1;
        }

        /**
         * But our standard TreeMap will fail
         */
        @Override
        public boolean equals(Object o)
        {
            return equalsKey(o != null ? ((City)o).city : null);
        }

        /**
         * We need that to ensure we compare against the defining key
         * Because we don't want to trap us against standard equals,
         * we name it differently
         */
        public boolean equalsKey(String key)
        {
            return this.city.equals(key);
        }

        /**
         * We need that to ensure we use the city as defining key
         */
        @Override
        public int hashCode()
        {
            return this.city.hashCode();
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
        final LightSet cities = new LightSet(413 * 3);

        // open the file
        try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
            String line;

            // read all lines until end of file
            while ((line = reader.readLine()) != null)
            {
                // split the line using indexOf
                final int semicolon = line.indexOf(';');
                final String city = line.substring(0, semicolon);

                // ok, parse to an int and forget about the . for a moment
                final int temperature = parseInteger(line, semicolon + 1, line.length());

                // let the set decide what to do
                cities.update(city, temperature);
            }
        }

        // ok, we got everything, now we need to order it
        return cities.toTreeMap().toString();
    }

    private static final int DIGITOFFSET = 48;

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     * 
     * Best case, two branches
     * Worst case, two branches
     */
    public static int parseInteger(final String s, int offset, final int end)
    {
        int l = end - offset;
        var p0 = s.charAt(end - 1);
        var p2 = s.charAt(end - 3) * 10;
        var value = p2 + p0 - (DIGITOFFSET * 10 + DIGITOFFSET);

        final char firstChar = s.charAt(offset);
        if (firstChar == '-')
        {
            if (l == 5)
            {
                // -99.9
                var p3 = s.charAt(end - 4) * 100;
                value = p3 + value - (DIGITOFFSET * 100);
            }
            else
            {
                // -9.9
            }
            return -value;
        }
        else
        {
            if (l == 4)
            {
                // 99.9
                // we can use firstChar directly as we know its not '-'
                var p3 = firstChar * 100;
                value = p3 + value - (DIGITOFFSET * 100);
            }
            else
            {
                // 9.9
            }
            return value;
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
        public void update(final String key, int temperature)
        {
            final int hash = key.hashCode();
            final int index = hash & this.mask;

            final City city = this.data[index];
            if (city == null)
            {
                add(key, index, temperature);
            }
            else if (city.equalsKey(key))
            {
                city.merge(temperature); 
            }
            else
            {
                // expensive path
                updateCollision(key, index, temperature);
            }
        }

        /**
         * This add is meant to take some of the code out of the update method
         * to make it more inlineable
         */
        private void add(final String key, int index, int temperature)
        {
            this.data[index] = new City(key, temperature);
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
        private void updateCollision(final String key, int index, int temperature)
        {
            while (true)
            {
                index = (index + 1) & this.mask;

                final City city = this.data[index];
                if (city == null)
                {
                    add(key, index, temperature);
                    
                    return;
                }
                else if (city.equalsKey(key))
                {
                    city.merge(temperature); 
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
            else if (city.equalsKey(c.city))
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
                else if (city.equalsKey(c.city))
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
                    map.put(c.city,  c);
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

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC41_DirectUpdate.class, args);
    }
}
