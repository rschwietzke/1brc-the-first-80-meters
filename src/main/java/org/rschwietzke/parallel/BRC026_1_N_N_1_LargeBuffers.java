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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Single Thread Reader, Multi-Thread Tranforming, Single-Thread 
 *
 * @author Rene Schwietzke
 */
public class BRC026_1_N_N_1_LargeBuffers extends Benchmark
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
        BlockingQueue<String> splittingQueueInput = new LinkedBlockingDeque<>(100000);
        BlockingQueue<String[]> measureQueueInput = new LinkedBlockingDeque<>(100000);
        BlockingQueue<Temperatures> mappingQueueInput = new LinkedBlockingDeque<>(100000);

        var maxThreadCount = this.getThreadCount();

        int consumerCount = Math.max(1, (maxThreadCount - 2) / 2);

        var readerThread = new ReaderThread(Paths.get(fileName), splittingQueueInput, consumerCount);
        readerThread.start();

        for (int i = 0; i < consumerCount; i++)
        {
            var sThread = new SplittingThread(splittingQueueInput, measureQueueInput);
            sThread.start();
            
            var mThread = new MeasurementThread(measureQueueInput, mappingQueueInput);
            mThread.start();
        }

        var mapperThread = new MapperThread(mappingQueueInput, consumerCount);
        mapperThread.start();

        // wait till done
        try 
        {
            mapperThread.join();
        } 
        catch (InterruptedException e) 
        {
            throw new RuntimeException(e);
        }

        // get our result
        var cities = mapperThread.getCities();

        // ok, we got everything, now we need to order it
        return new TreeMap<String, Temperatures>(cities).toString();
    }

    /**
     * I read the disk and fill a queue
     */
    class ReaderThread extends Thread 
    {
        private final Path src;
        private final BlockingQueue<String> target;
        private final int consumerCount;
        public final static String ENDMARKER = new String("ENDMARKER");

        public ReaderThread(Path src, BlockingQueue<String> target, int consumerCount)
        {
            this.target = target;
            this.src = src;
            this.consumerCount = consumerCount;

            this.setName("ReaderThread");
        }

        public void run() 
        {
            try (var reader = Files.newBufferedReader(src))
            {
                String line;

                // read all lines until end of file
                while ((line = reader.readLine()) != null)
                {
                    target.put(line);
                }

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
     * I split the strings into pieces
     */
    class SplittingThread extends Thread 
    {
        private final BlockingQueue<String> src;
        private final BlockingQueue<String[]> target;
        public final static String[] ENDMARKER = new String[] {"", ""};

        public SplittingThread(BlockingQueue<String> src, BlockingQueue<String[]> target)
        {
            this.target = target;
            this.src = src;
            this.setName("SplittingThread");
        }

        public void run() 
        {
            String line;
            try 
            {
                while ((line = src.take()) != ReaderThread.ENDMARKER)
                {
                    target.put(line.split(";")); 
                }
                target.put(ENDMARKER);
            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * I read the split data and create measurements
     */
    class MeasurementThread extends Thread 
    {
        private final BlockingQueue<String[]> src;
        private final BlockingQueue<Temperatures> target;
        public final static Temperatures ENDMARKER = new Temperatures("", 0.0);

        public MeasurementThread(BlockingQueue<String[]> src, BlockingQueue<Temperatures> target)
        {
            this.target = target;
            this.src = src;
            this.setName("MeasurementThread");
        }

        public void run() 
        {
            // read all lines until end of file
            String [] data;
            try 
            {
                int x = 0;
                while ((data = src.take()) != SplittingThread.ENDMARKER)
                {
                    // first is the city
                    final String city = data[0];

                    // second our double temperature
                    final double temperature = Double.parseDouble(data[1]);

                    // get us our measurement record
                    target.put(new Temperatures(city, temperature));                
                }

                // tell the next threads that we are done
                // we have to do that for as many consumers as we have
                target.put(ENDMARKER);
            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * I manage the data storage
     */
    class MapperThread extends Thread 
    {
        private final BlockingQueue<Temperatures> src;
        private final Map<String, Temperatures> cities = new HashMap<>();
        private final int measurerCount;

        public MapperThread(BlockingQueue<Temperatures> src, int measurerCount)
        {
            this.src = src;
            this.setName("MapperThread");
            this.measurerCount = measurerCount;
        }

        public void run() 
        {
            try 
            {
                int endMarkerCount = 0;

                while (true)
                {
                    Temperatures temp = src.take();
                    if (temp == MeasurementThread.ENDMARKER)
                    {
                        endMarkerCount++;
                        if (endMarkerCount == this.measurerCount)
                        {
                            break;
                        }
                        else
                        {
                            continue;
                        }
                    }

                    // store it, when it exists, merge both measurements
                    cities.merge(temp.city, temp, (t1, t2) -> t1.merge(t2));
                }

            } 
            catch (InterruptedException e) 
            {
                throw new RuntimeException(e);
            }
        }

        public synchronized Map<String, Temperatures> getCities()
        {
            return cities;
        }
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC026_1_N_N_1_LargeBuffers.class, args);
    }
}
