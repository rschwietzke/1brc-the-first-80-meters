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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.PositionableReader;

/**
 * Single Thread Reader, Multi-Thread Tranforming, Single-Thread 
 *
 * @author Rene Schwietzke
 */
public class BRC048_VT_ChunkSize_10MB extends Benchmark
{
    /**
     * Holds our temperature data
     */
    static class Temperatures
    {
        private final double min;
        private final double max;
        private final double total;
        private final long count;
        private final String city;

        public Temperatures(final String city, final double value)
        {
            this.city = city;
            this.min = value;
            this.max = value;
            this.total = value;
            this.count = 1;
        }

        private Temperatures(final String city, double min, double max, double total, long count)
        {
            this.city = city;
            this.min = min;
            this.max = max;
            this.total = total;
            this.count = count;
        }

        public Temperatures merge(final Temperatures other)
        {
            return new Temperatures(this.city, Math.min(min, other.min), Math.max(max, other.max), total + other.total, count + other.count);
        }

        public String toString()
        {
            // we delegate the formatting to our math util to
            // ensure we do the same everywhere, helps us later to 
            // change the accuracy if needed
            return MathUtil.toString(total, count, min, max);
        }
    }

    @Override
    public String run(final String filePath) throws IOException
    {
        var result = new FileChunker(filePath, this.getThreadCount()).run();
        return new TreeMap<String, Temperatures>(result).toString();
    }

    /**
     * Split up the file into a number of chunks and let subtasks run on each
     */
    @SuppressWarnings("serial")
    class FileChunker
    {
        private final String filePath;
        private final int chunkCount;
        
        public FileChunker(String filePath, int chunkCount)
        {
            this.filePath = filePath;
            this.chunkCount = chunkCount;
        }
        
        public Map<String, Temperatures> run()
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

            // define chunks
            final long chunkSize = 10_000_000; 

            final List<Mapper> tasks = new ArrayList<>();
            
            long from = 0;  
            long to = chunkSize;
            
            while (to < size)
            {
                tasks.add(
                        new Mapper(filePath, 
                        from == 0 ? 0 : from - 1,
                        (size - to) < chunkSize ? size : to ));
                
                from = from + chunkSize;
                to = to + chunkSize;
            }
            
            List<Future<Map<String, Temperatures>>> results = new ArrayList<>();
            
            try (var executor = Executors.newVirtualThreadPerTaskExecutor())
            {
                results = tasks.stream().map(t -> executor.submit(t)).toList();
            }
            
            // reduce result
            final Map<String, Temperatures> cities = new HashMap<>();
            
            results.stream().map(mapper -> 
            {
                try 
                {
                    return mapper.get();
                } 
                catch (InterruptedException e) 
                {
                    throw new RuntimeException(e);
                } 
                catch (ExecutionException e) 
                {
                    throw new RuntimeException(e);
                }
            }).forEach(
                    map -> map.forEach(
                            (k, v) -> cities.merge(k, v, (t1, t2) -> t1.merge(t2))));
            
            return cities;
        }
        
    }
    
    class Mapper implements Callable<Map<String, Temperatures>>
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
        public Map<String, Temperatures> call() throws Exception
        {
            try (var r = new PositionableReader(filePath, from, to))
            {
                String line;
                final Map<String, Temperatures> cities = new HashMap<>();
                
                while ((line = r.readln()) != null)
                {
                    var splitData = line.split(";");

                    // first is the city
                    var city = splitData[0];

                    // second our double temperature
                    var temperature = Double.parseDouble(splitData[1]);

                    // get us our measurement record
                    var measurement = new Temperatures(city, temperature);                

                    cities.merge(city, measurement, (t1, t2) -> t1.merge(t2));
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
        Benchmark.run(BRC048_VT_ChunkSize_10MB.class, args);
    }
}
