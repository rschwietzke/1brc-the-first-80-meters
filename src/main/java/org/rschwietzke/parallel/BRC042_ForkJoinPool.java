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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.PositionableReader;

/**
 * Implements work-stealing using ForkJoinPool.
 *
 * Difference to BRC040_N_ConcurrentRead: Replaced `newFixedThreadPool` 
 * with `ForkJoinPool` and introduced a `FileChunker` task.
 *
 * @author René Schwietzke
 */
public class BRC042_ForkJoinPool extends Benchmark
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
        Future<Map<String, Temperatures>> result = null;
        
        // Ok, call our initial file splitter to start more tasks later
        try (var executor = new ForkJoinPool(this.getThreadCount()))
        {
            result = executor.submit(new FileChunker(filePath, this.getThreadCount()));

            return new TreeMap<String, Temperatures>(result.get()).toString();
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

    /**
     * Split up the file into a number of chunks and let subtasks run on each
     */
    @SuppressWarnings("serial")
    class FileChunker extends RecursiveTask<Map<String, Temperatures>>
    {
        private final String filePath;
        private final int chunkCount;
        
        public FileChunker(String filePath, int chunkCount)
        {
            this.filePath = filePath;
            this.chunkCount = chunkCount;
        }
        
        @Override
        protected Map<String, Temperatures> compute()
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
            final long chunkSize = size / chunkCount;

            final List<Mapper> tasks = new ArrayList<>();
            
            for (int i = 1; i <= chunkCount; i++)
            {
                long from = i == 1 ? 0 : (i - 1 ) * chunkSize - 1;
                long to = i == chunkCount ? size : i * chunkSize;
                tasks.add(new Mapper(filePath, from, to));
            }
            ForkJoinTask.invokeAll(tasks);
            
            var results = tasks.stream().map(t -> {
                try 
                {
                    return t.get();
                } 
                catch (InterruptedException e) 
                {
                    throw new RuntimeException(e);
                } 
                catch (ExecutionException e) 
                {
                    throw new RuntimeException(e);
                }
            }).toList();
            
            // ok, finalize things and map all our results into one result
            var reducer = new Reducer(results);
            ForkJoinTask.invokeAll(reducer);
            
            try 
            {
                return reducer.get();
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
        
    }
    
    @SuppressWarnings("serial")
    class Mapper extends RecursiveTask<Map<String, Temperatures>>
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
        protected Map<String, Temperatures> compute() 
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
    
    @SuppressWarnings("serial")
    class Reducer extends RecursiveTask<Map<String, Temperatures>>
    {
        private Collection<Map<String, Temperatures>> data;

        public Reducer(Collection<Map<String, Temperatures>> data)
        {
            this.data = data;
        }

        @Override
        public Map<String, Temperatures> compute()
        {
            final Map<String, Temperatures> cities = new HashMap<>();
            
            data.forEach(map ->
                map.forEach((k, v) ->  cities.merge(k, v, (t1, t2) -> t1.merge(t2))));
                
            return cities;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC042_ForkJoinPool.class, args);
    }
}
