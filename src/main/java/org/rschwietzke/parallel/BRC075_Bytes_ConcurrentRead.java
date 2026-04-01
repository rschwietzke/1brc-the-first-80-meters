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
 * Eliminates all String and char processing from the hot path. PositionableByteReader reads
 * raw bytes. We are just checking our speed for the moment.
 *
 * @author Rene Schwietzke
 */
public class BRC075_Bytes_ConcurrentRead extends Benchmark
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
            var result = executor.submit(new Mapper(filePath, 0, size));
            
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
        private long from;
        private long to;
        private String filePath;

        public Mapper(String filePath, long from, long to)
        {
            this.from = from;
            this.to = to;
            this.filePath = filePath;
        }

        @Override
        protected Map<City, City> compute() 
        {
            // split work if not yet small enough
            if (to - from >= 20_000_000L)
            {
                final List<Mapper> tasks = new ArrayList<>();
                
                long size = to - from;
                
                long from1 = from == 0 ? 0 : from;
                long to1 = from + size / 2;
                long from2 = to1;
                long to2 = to;
                
                tasks.add(
                        new Mapper(filePath, from1, to1));
                tasks.add(
                        new Mapper(filePath, from2, to2));

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
                }

                return cities;
            } 
            catch (IOException e) 
            {
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC075_Bytes_ConcurrentRead.class, args);
    }
}
