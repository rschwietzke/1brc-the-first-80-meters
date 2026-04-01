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
package org.rschwietzke.parallel;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.PositionableByteReader;
import org.rschwietzke.util.PositionableByteReader.Line;

/**
 * Same byte-level processing as BRC075 but replaces binary recursive splitting with a flat
 * pre-computed chunk split: the root Mapper task divides the file into N equal chunks upfront
 * and submits all N leaf tasks at once via invokeAll(), avoiding deep recursion and redundant
 * splitting overhead. Includes per-thread memory tracking via ThreadMXBean.
 *
 * @author Rene Schwietzke
 */
public class BRC077_Bytes_ForkJoin_1Level extends Benchmark
{
    private static class City
    {
        private int min;
        private int max;
        private long total;
        private long count;
        private final byte[] name;
        private final int hashCode;
        
        public City(byte[] bytes, int semicolon, int temperature, int hashCode)
        {
            this.name = Arrays.copyOfRange(bytes, 0, semicolon);
            this.max = temperature;
            this.min = temperature;
            this.total = temperature;
            this.count = 1;
            this.hashCode = hashCode;
        }

        public City update(int temperature)
        {
            this.min = Math.min(this.min, temperature);
            this.max = Math.max(this.max, temperature);
            count++;
            this.total += temperature;
            
            return this;
        }
        
        public City merge(final City city)
        {
            this.min = Math.min(this.min, city.min);
            this.max = Math.max(this.max, city.max);
            this.total += city.total;
            this.count += city.count;
            
            return this;
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }
        
        public String getCity()
        {
            return new String(name, StandardCharsets.UTF_8);
        }
        
        @Override
        public boolean equals(Object o)
        {
            if (o instanceof City c)
            {
                if (this.name.length == c.name.length)
                {
                    for (int i = 0; i < this.name.length; i++)
                    {
                        if (this.name[i] != c.name[i])
                        {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
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

        // Ok, let's recursively map and reduce
        try (var executor = new ForkJoinPool(this.getThreadCount()))
        {
            var result = executor.submit(new Mapper(filePath, 0, size, this.getThreadCount()));
            
            var cities = new TreeMap<String, City>();
            result.get().keySet().forEach(c -> cities.put(c.getCity(), c));
            
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
    class Mapper extends RecursiveTask<Map<City, City>>
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
        protected Map<City, City> compute() 
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
                final Map<City, City> cities = new HashMap<>();

                for (var t : tasks) 
                {
                    try 
                    {
                        final var result = t.get();
                        
                        result.forEach((k, v) -> 
                            cities.compute(k, (_, v2) -> v2 == null ? v : v2.merge(v)));
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

        private Map<City, City> map()
        {
            try (var r = new PositionableByteReader(filePath, from > 0 ? from - 1 : 0, to))
            {
                Line line;
                final Map<City, City> cities = new HashMap<>();

                while ((line = r.readln()) != null)
                {
                    // second our double temperature
                    final int temperature = parseInteger(line.bytes, line.semicolon + 1, line.length);

                    // temp solution because of Set
                    final City city = new City(line.bytes, line.semicolon, temperature, line.cityHash);
                    
                    // create new when needed, mutate when merging
                    cities.compute(city, 
                            (_, v) -> v == null ? city : v.update(temperature));
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
        Benchmark.run(BRC077_Bytes_ForkJoin_1Level.class, args);
    }
}
