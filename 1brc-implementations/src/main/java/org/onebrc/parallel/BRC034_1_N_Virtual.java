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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.onebrc.Benchmark;
import org.onebrc.util.MathUtil;

/**
 * 1-N pattern utilizing Java Virtual Threads.
 *
 * Difference to BRC032_1_N_Batched: Switched to `Thread.ofVirtual().start(...)` 
 * for managing concurrency.
 *
 * @author René Schwietzke
 */
public class BRC034_1_N_Virtual extends Benchmark
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
    public String run(final String fileName)
    {
        // let't do a raw and basic old-school implementation without
        // most modern Java help
        final BlockingQueue<List<String>> measureQueueInput = new LinkedBlockingDeque<>(1000000);

        final int maxThreadCount = this.getThreadCount();

        // 1 thread for reader, rest for us
        final int measurerCount = Math.max(1, (maxThreadCount - 1));

        var reader = new Reader(Paths.get(fileName), measureQueueInput, measurerCount);
        Thread.ofVirtual().name("ReaderThread").start(reader);

        final List<Thread> measurementThreads = new ArrayList<>();
        final List<Measurer> measurers = new ArrayList<>();
        for (int i = 0; i < measurerCount; i++)
        {
            var measurer = new Measurer(measureQueueInput);
            measurers.add(measurer);
            
            var thread = Thread.ofVirtual().name("MeasurementThread-" + i).start(measurer);
            measurementThreads.add(thread);
        }

        // wait till done
        try 
        {
            for (Thread t : measurementThreads)
            {
                t.join();
            }
        } 
        catch (InterruptedException e) 
        {
            throw new RuntimeException(e);
        }

        final Map<String, Temperatures> cities = new HashMap<>();
        for (Measurer m : measurers)
        {
            var c = m.getCities();

            c.forEach((k, v) -> 
            cities.merge(k, v, (t1, t2) -> t1.merge(t2)));
        }
        
        // ok, we got everything, now we need to order it
        return new TreeMap<String, Temperatures>(cities).toString();
    }

    /**
     * I read the disk and fill a queue
     */
    class Reader implements Runnable 
    {
        private final Path src;
        private final BlockingQueue<List<String>> target;
        private final int consumerCount;
        public final static List<String> ENDMARKER = new ArrayList<>();

        public Reader(Path src, BlockingQueue<List<String>> target, int consumerCount)
        {
            this.target = target;
            this.src = src;
            this.consumerCount = consumerCount;
        }

        public void run() 
        {
            try (var reader = Files.newBufferedReader(src))
            {
                String line;
                List<String> batch = new ArrayList<>(1000);
                int size = 0;

                // read all lines until end of file
                while ((line = reader.readLine()) != null)
                {
                    batch.add(line);
                    if (batch.size() == 1000)
                    {
                        target.put(batch);
                        batch = new ArrayList<>(1000);
                    }
                }
                // in case something is remaining
                target.put(batch);

                // tell the next thread that we are done
                // we have to do that for as many consumers as we have
                for (int i = 0; i < consumerCount; i++)
                {
                    target.put(ENDMARKER);
                }
            } 
            catch (IOException e) 
            {
                throw new RuntimeException(e);
            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * I do all the parsing and transforming
     */
    class Measurer implements Runnable 
    {
        private final BlockingQueue<List<String>> src;
        private final Map<String, Temperatures> cities = new HashMap<>();

        public Measurer(BlockingQueue<List<String>> src)
        {
            this.src = src;
        }

        public void run() 
        {
            // read all lines until end of file
            List<String> data;
            try 
            {
                while ((data = src.take()) != Reader.ENDMARKER)
                {
                    for (String line : data)
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
                }

            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
        }

        public Map<String, Temperatures> getCities()
        {
            return cities;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC034_1_N_Virtual.class, args);
    }
}
