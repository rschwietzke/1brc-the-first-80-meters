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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiFunction;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Check the casting and clean it up. This avoid extra cycles for
 * type checks everyone already knows. 
 *
 * @author Rene Schwietzke
 */
public class BRC31_OpenMapLessCasting extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class Temperatures
    {
        private int min;
        private int max;
        private long total;
        private long count;

        public Temperatures(final int temperature)
        {
            this.min = temperature;
            this.max = temperature;
            this.total = temperature;
            this.count = 1;
        }


        public Temperatures merge(final int temperature)
        {
            this.min = Math.min(this.min, temperature);
            this.max = Math.max(this.max, temperature);
            this.total += temperature;
            this.count += 1;
            return this;
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
        final LightMap<String, Temperatures> cities = new LightMap<>(413 * 3);

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

                // create new when needed, mutate when merging
                cities.compute(city, 
                        (k, v) -> v == null ? new Temperatures(temperature) : v.merge(temperature));
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

    public static class LightMap<K, V>
    {
        private Object[] data;
        private int size;
        private int mask1;  
        private int mask2;  
        private int threshold;

        public LightMap(int initialCapacity)
        {
            if (initialCapacity < 7)
            {
                throw new IllegalArgumentException("Capacity must be >= 7");
            }

            // we need a power of two for the capacity to be able to run modules as bitwise AND
            final int capacity = nextPowerOfTwo(initialCapacity);

            this.mask1 = capacity - 1;
            this.mask2 = (capacity << 1) - 1;
            this.threshold = (capacity >> 1); // load factor 0.5

            // we are twice the size
            this.data = new Object[capacity << 1];
        }

        /**
         * Get value for key
         * 
         * Best case: 2 branches, key found
         * Worst case: N branches, key not found after N collisions
         * 
         * @param key   the key to search for
         * @return      the value or null
         */
        @SuppressWarnings("unchecked")
        public V get(K key)
        {
            final int hash = key.hashCode();
            final int index = (hash & this.mask1) << 1;

            Object k = this.data[index];
            if (k == null)
            {
                return null;
            }
            else if (k.equals(key))
            {
                return (V)this.data[index + 1];
            }
            else
            {
                return getCollision(key, index);
            }
        }

        /**
         * We handle that as separate method to keep the main get() fast and inlineable.
         * 
         * @param key   the key to search for
         * @param index the original index where the collision happened
         * @return     the value or null
         */
        @SuppressWarnings("unchecked")
        private V getCollision(K key, int index)
        {
            // collision
            int nextIndex = (index + 2) & this.mask2;
            while (true)
            {
                K k = (K) data[nextIndex];
                if (k == null)
                {
                    return null;
                }
                else if (k.equals(key))
                {
                    return (V)data[nextIndex + 1];
                }
                nextIndex = (nextIndex + 2) & this.mask2;
            }
        }

        /**
         * Computes a value for the given key and stores it in the map.
         * 
         * @param key
         * @param remappingFunction
         * @return the computed value
         */
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
        {
            final int hash = key.hashCode();
            final int index = (hash & this.mask1) << 1;

            final Object k = this.data[index];
            if (k == null)
            {
                this.data[index] = key;
                final V v = remappingFunction.apply(key, null);
                this.data[index + 1] = v;
                this.size++;

                // check size
                if (this.size > (this.threshold))
                {
                    resize();
                }

                return v;
            }
            else if (k.equals(key))
            {
                V v = (V) this.data[index + 1];
                v = remappingFunction.apply(key, v);
                this.data[index + 1] = v;
                return v;
            }
            else
            {
                return computeCollision(key, index, remappingFunction);
            }
        }

        public V computeCollision(K key, int index, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
        {
            // collision
            int nextIndex = (index + 2) & this.mask2;
            while (true)
            {
                final Object k = this.data[nextIndex];
                if (k == null)
                {
                    final V v = remappingFunction.apply(key, null);
                    this.data[nextIndex] = key;
                    this.data[nextIndex + 1] = v;
                    this.size++;

                    // check size
                    if (this.size > this.threshold)
                    {
                        resize();
                    }

                    return v;
                }
                else if (k.equals(key))
                {
                    V v = (V) this.data[nextIndex + 1];
                    v = remappingFunction.apply(key, v);
                    this.data[nextIndex + 1] = v;
                    return v;
                }
                nextIndex = (nextIndex + 2) & this.mask2;
            }
        }
        
        public V put(K key, V value)
        {
            final int hash = key.hashCode();
            final int index = (hash & this.mask1) << 1;

            Object k = this.data[index];
            if (k == null)
            {
                this.data[index] = key;
                this.data[index + 1] = value;
                this.size++;

                // check size
                if (this.size > this.threshold)
                {
                    resize();
                }
                
                return null;
            }
            else if (k.equals(key))
            {
                @SuppressWarnings("unchecked")
                final V old = (V) this.data[index + 1];
                this.data[index + 1] = value;
                return old;
            }
            else
            {
                return putCollision(key, value, index);
            }
        }

        
        @SuppressWarnings("unchecked")
        public V putCollision(K key, V value, int index)
        {
            // collision
            int nextIndex = (index + 2) & this.mask2;
            while (true)
            {
                final Object k = this.data[nextIndex];
                if (k == null)
                {
                    this.data[nextIndex] = key;
                    this.data[nextIndex + 1] = value;
                    this.size++;

                    // check size
                    if (this.size > this.threshold)
                    {
                        resize();
                    }
                    
                    return null;
                }
                else if (k.equals(key))
                {
                    final V old = (V) this.data[nextIndex + 1];
                    this.data[nextIndex + 1] = value;
                    return old;
                }
                nextIndex = (nextIndex + 2) & this.mask2;
            }
        }
        private void resize()
        {
            final Object[] oldData = this.data;
            this.data = new Object[this.data.length << 1];
            this.mask1 = (this.data.length >> 1) - 1;
            this.mask2 = this.data.length - 1;
            this.threshold = this.data.length >> 2;
            
            // size is new
            this.size = 0;

            // ok, we use put to reinsert everything
            for (int i = 0; i < oldData.length - 1; i += 2)
            {
                final Object k = oldData[i];
                if (k != null)
                {
                    @SuppressWarnings("unchecked")
                    K key = (K) k;
                    @SuppressWarnings("unchecked")
                    V value = (V) oldData[i + 1];
                    this.put(key,  value);
                }
            }
        }

        public int size()
        {
            return this.size;
        }

        public TreeMap<K, V> toTreeMap()
        {
            final var map = new TreeMap<K, V>();
            
            // ok, we use put to reinsert everything
            for (int i = 0; i < this.data.length - 1; i += 2)
            {
                final Object k = this.data[i];
                if (k != null)
                {
                    @SuppressWarnings("unchecked")
                    K key = (K) k;
                    @SuppressWarnings("unchecked")
                    V value = (V) this.data[i + 1];
                    map.put(key,  value);
                }
            }
            
            return map;
        }

        public List<K> keys()
        {
            final var keys = new ArrayList<K>(this.size);
            
            for (int i = 0; i < this.data.length - 1; i += 2)
            {
                final Object k = this.data[i];
                if (k != null)
                {
                    @SuppressWarnings("unchecked")
                    K key = (K) k;
                    keys.add(key);
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
        Benchmark.run(BRC31_OpenMapLessCasting.class, args);
    }
}
