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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.PositionableReader;

/**
 * Fine-tunes the chunk splitting logic of BRC044.
 *
 * Difference to BRC048_VT_ChunkSize_10MB: Implemented manual byte-scanning `seekToNextNewline` behavior instead of `BufferedReader`.
 *
 * @author René Schwietzke
 */
public class BRC060_44_SplitTune extends Benchmark
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
            // split work if not yet small enough
            if (to - from >= 20_000_000L)
            {
//                System.out.printf("Split[%d]: %,d/%,d -> %,d%n", count, from, to, to - from);

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
                final Map<String, Temperatures> cities = new HashMap<>();

                tasks.stream().map(mapper -> 
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
            else
            {
//                System.out.printf("Map[%d]: %,d/%,d -> %,d%n", count, from, to, to - from);
                return map();
            }

        }

        private Map<String, Temperatures> map()
        {
            try (var r = new PositionableReader(filePath, from > 0 ? from - 1 : 0, to))
            {
                String line;
                final Map<String, Temperatures> cities = new HashMap<>();

                while ((line = r.readln()) != null)
                {
                    // split the line using indexOf
                    final int semicolon = line.indexOf(';');
                    final String city = line.substring(0, semicolon);
                    final String temperatureString = line.substring(semicolon + 1);

                    // second our double temperature
                    var temperature = Double.parseDouble(temperatureString);

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
        Benchmark.run(BRC060_44_SplitTune.class, args);
    }
}
