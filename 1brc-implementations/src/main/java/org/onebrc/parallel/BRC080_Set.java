// JVM_OPTS: $HIGH_MEM
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
package org.onebrc.parallel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.onebrc.Benchmark;
import org.onebrc.util.MathUtil;
import org.onebrc.util.PositionableByteReader;
import org.onebrc.util.PositionableByteReader.Line;

/**
 * Improve the Map handling by using a Set instead
 *
 * @author René Schwietzke
 */
public class BRC080_Set extends Benchmark
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
            this.length = line.semicolon;
            this.city = new byte[this.length];

            System.arraycopy(line.bytes, 0, this.city, 0, this.length);
            this.hashCode = line.cityHash;

            this.min = line.temperature;
            this.max = line.temperature;
            this.total = line.temperature;
            this.count = 1;
            
            // System.out.format("%s,%d%n", new String(this.city), line.cityHash);
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
         * This is hot code that runs again and again for every line, so we want to keep it as small as possible,
         * @param temperature
         */
        public void merge(final City city)
        {
            if (city.max > this.max)
            {
                this.max = city.max;
            }
            if (city.min < this.min)
            {
                this.min = city.min;
            }
            // this is here to let the expensive branch prediction with a likely
            // miss run first and when we are in the branch, we can fill
            // the rest of the pipeline with this
            // if this is first, we have less instructions but more cycles 
            // cycles are time, not instructions!!!
            this.total += city.total;
            this.count += city.count;
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

        public boolean equalsLine(final Line line)
        {
            if (this.length == line.semicolon)
            {
                for (int i = 0; i < this.length; i++)
                {
                    if (this.city[i] != line.bytes[i])
                    {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        
        public String getCity()
        {
            return new String(this.city);
        }

        /**
         * We need that to ensure we use the city as defining key.
         */
        @Override
        public int hashCode()
        {
            // for our treemap
            return this.hashCode;
        }
        
        public String toString()
        {
            // we delegate the formatting to our math util to
            // ensure we do the same everywhere, helps us later to 
            // change the accuracy if needed
            return MathUtil.toStringFromInteger(total, count, min, max);
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
            final int index = line.cityHash & this.mask;

            final City city = this.data[index];
            if (city == null)
            {
                add(line, index);
            }
            else if (city.equalsLine(line))
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
                //String c = line.toCity();
                index = (index + 1) & this.mask;

                final City city = this.data[index];
                if (city == null)
                {
                    add(line, index);
                    // System.out.println("Collisions Add: " + ++c);
                    break;
                }
                else if (city.equalsLine(line))
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
                this.data[index].merge(city);
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
                    this.data[index].merge(city);
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

    @Override
    public String run(final String filePath) throws IOException
    {
        // first, we must know the file size
        long size = -1;
        try (var r = new RandomAccessFile(filePath, "r"))
        {
            size = r.length();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        // just let the pool run, divide, and reduce until we just have one result
        try (var executor = new ForkJoinPool(this.getThreadCount()))
        {
            var result = executor.submit(
                    new Mapper(filePath, 0, size, this.getThreadCount()));
            
            var cities = new TreeMap<String, City>();
            result.get().values().forEach(c -> cities.put(c.getCity(), c));
            
            return cities.toString();
        }
        catch (InterruptedException e)
        {
            throw new RuntimeException(e);
        } 
        catch (ExecutionException e) 
        {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("serial")
    class Mapper extends RecursiveTask<LightSet>
    {
        private final long from;
        private final long to;
        private final String filePath;
        private final int taskCount;

        public Mapper(String filePath, long from, long to, int taskCount)
        {
            this.from = from;
            this.to = to;
            this.filePath = filePath;
            this.taskCount = taskCount;
        }

        @Override
        protected LightSet compute() 
        {
            // split only when we should
            if (taskCount > 1)
            {
                final List<Mapper> tasks = new ArrayList<>(taskCount);
                
                final long size = this.to - this.from;
                final long chunkSize = size / taskCount;

                long from = -chunkSize;
                long to = 0;
                while (to < size)
                {
                    from += chunkSize;
                    to = from + chunkSize;
                    
                    // when close to the end, make one chunk larger than really small... otherwise 
                    // we lose data
                    to = (size - to) < chunkSize ? size : to;

//                    System.out.format("from= %,d, to=%,d, size=%,d%n", from, to, size);
                    tasks.add(new Mapper(filePath, from, to, 1));
                }
                
                ForkJoinTask.invokeAll(tasks);

                // reduce result
                final LightSet cities = new LightSet(4096);

                for (var t : tasks) 
                {
                    try 
                    {
                        final LightSet result = t.get();
                        
                        final List<City> resultCities = result.values();
                        for (var c : resultCities)
                        {
                            // we can just add, the LightSet deals with 
                            // existing entries by merging them
                            cities.add(c);
                        }
                    } 
                    catch (InterruptedException e) 
                    {
                        throw new RuntimeException(e);
                    } 
                    catch (ExecutionException e) 
                    {
                        throw new RuntimeException(e);
                    }
                }

                return cities;
            }
            else
            {
                return map();
            }

        }

        private LightSet map()
        {
            try (var r = new PositionableByteReader(filePath, from > 0 ? from - 1 : 0, to))
            {
                Line line;
                final LightSet cities = new LightSet(4096);

                while ((line = r.readln()) != null)
                {
                    // second our double temperature
                    line.temperature = parseInteger(line.bytes, line.semicolon + 1, line.length);

                    // create new when needed, mutate when merging
                    cities.update(line);
                }

                return cities;
            } 
            catch (IOException e) 
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    private static final int DIGITOFFSET = 48;

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     * 
     * Best case, two branches
     * Worst case, two branches
     */
    public static int parseInteger(final byte[] s, int offset, final int end)
    {
        int l = end - offset;
        var p0 = s[end - 1];
        var p2 = s[end - 3] * 10;
        var value = p2 + p0 - (DIGITOFFSET * 10 + DIGITOFFSET);
        
        final byte firstChar = s[offset];
        if (firstChar == '-')
        {
            if (l == 5)
            {
                // -99.9
                var p3 = s[end - 4] * 100;
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
    
    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC080_Set.class, args);
    }
}
