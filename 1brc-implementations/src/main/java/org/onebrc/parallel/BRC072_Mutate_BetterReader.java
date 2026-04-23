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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.onebrc.Benchmark;
import org.onebrc.util.MathUtil;
import org.onebrc.util.PositionableReader2;

/**
 * Combines mutation optimizations with a more efficient file reader.
 *
 * Difference to BRC070_Mutate: Integrated a memory-mapped file `MappedByteBuffer` approach.
 *
 * @author René Schwietzke
 */
public class BRC072_Mutate_BetterReader extends Benchmark
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
        
        public Temperatures merge(final Temperatures temperature)
        {
            this.min = Math.min(this.min, temperature.min);
            this.max = Math.max(this.max, temperature.max);
            this.total += temperature.total;
            this.count += temperature.count;
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
                                (k1, v1) -> cities.compute(k1, 
                                        (_, v2) -> v2 == null ? v1 : v2.merge(v1))));

                return cities;
            }
            else
            {
                return map();
            }

        }

        private Map<String, Temperatures> map()
        {
            try (var r = new PositionableReader2(filePath, from > 0 ? from - 1 : 0, to))
            {
                String line;
                final Map<String, Temperatures> cities = new HashMap<>();

                while ((line = r.readln()) != null)
                {
                    // split the line using indexOf
                    final int semicolon = line.indexOf(';');
                    final String city = line.substring(0, semicolon);

                    // second our double temperature
                    final int temperature = parseInteger(line, semicolon + 1, line.length());

                    // create new when needed, mutate when merging
                    cities.compute(city, 
                            (_, v) -> v == null ? new Temperatures(temperature) : v.merge(temperature));
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
    
    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC072_Mutate_BetterReader.class, args);
    }
}
