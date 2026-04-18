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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * 1-N pattern using standard Executor thread pools.
 *
 * Difference to BRC034_1_N_Virtual: Switched to `Executors.newFixedThreadPool(...)`.
 *
 * @author René Schwietzke
 */
public class BRC036_1_N_ExecutorPool extends Benchmark
{
    /**
     * Holds our temperature data
     */
    private static class Temperatures
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
    public String run(final String fileName) throws IOException
    {
        final int maxThreadCount = this.getThreadCount();
        // the main thread produces
        final var pool = Executors.newFixedThreadPool(Math.max(1, maxThreadCount));

        final var futures = new ArrayList<Future<Map<String, Temperatures>>>();
        final var cities = new HashMap<String, Temperatures>();

        try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
            String line;
            
            List<String> batch = new ArrayList<>(100000);

            // read all lines until end of file
            while ((line = reader.readLine()) != null)
            {
                batch.add(line);
                if (batch.size() == 100000)
                {
                    futures.add(pool.submit(new Measurer(batch)));
                    batch = new ArrayList<>(100000);
                }
                else
                {
                    continue;
                }

                // need enough data
                if (futures.size() > 100)
                {
                    // let's drain it well enough
                    while (futures.size() > 50)
                    {
                        try
                        {
                            var f = futures.removeFirst();
                            var c = f.get();
                            
                            c.forEach((k, v) -> 
                                cities.merge(k, v, (t1, t2) -> t1.merge(t2)));
                        }
                        catch(Exception e)
                        {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            // submit last
            futures.add(pool.submit(new Measurer(batch)));
            pool.shutdown();
            try 
            {
                pool.awaitTermination(1, TimeUnit.MINUTES);
            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
            
            // drain
            for (var f : futures)
            {
                try
                {
                    var c = f.get();
                    
                    c.forEach((k, v) -> 
                        cities.merge(k, v, (t1, t2) -> t1.merge(t2)));
                }
                catch(Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
            
            // ok, we got everything, now we need to order it
            return new TreeMap<String, Temperatures>(cities).toString();
        }
    }

    /**
     * I do all the parsing and transforming
     */
    class Measurer implements Callable<Map<String, Temperatures>>
    {
        private final List<String> src;

        public Measurer(List<String> src)
        {
            this.src = src;
        }

        @Override
        public Map<String, Temperatures> call() throws Exception 
        {
            final Map<String, Temperatures> cities = new HashMap<>();

            for (var line : src)
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
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC036_1_N_ExecutorPool.class, args);
    }
}
