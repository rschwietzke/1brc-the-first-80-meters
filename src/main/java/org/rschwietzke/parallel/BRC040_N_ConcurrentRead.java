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
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;
import org.rschwietzke.util.PositionableReader;

/**
 * N threads concurrently read distinct byte-range chunks of the file using RandomAccessFile
 * and a custom PositionableReader. This is a read-throughput probe only: lines are counted
 * but not parsed, to measure whether concurrent reads can saturate available I/O bandwidth.
 *
 * @author Rene Schwietzke
 */
public class BRC040_N_ConcurrentRead extends Benchmark
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
    public String run(final String filePath) throws IOException
    {
        // first, we must know the file size
        long size = -1;
        try(var r = new RandomAccessFile(filePath, "r"))
        {
            size = r.length();
        }

        // define chunks
        final long chunkSize = size / this.getThreadCount();

        final List<Future<Long>> results = new ArrayList<>();
        
        try (var executor = Executors.newFixedThreadPool(this.getThreadCount()))
        {
            for (int i = 1; i <= this.getThreadCount(); i++)
            {
                long from = i == 1 ? 0 : (i - 1 ) * chunkSize - 1;
                long to = i == this.getThreadCount() ? size : i * chunkSize;
                
                results.add(executor.submit(new Reader(filePath, from, to)));
                // System.out.println("from: " + from + " to: " + to);
            }
        }

        long total = 0;
        for (Future<Long> r : results)
        {
            try 
            {
                total += r.get();
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
        System.out.format("Total lines %d%n", total);
        
        return new TreeMap<String, Temperatures>().toString();
    }

    class Reader implements Callable<Long>
    {
        private long from;
        private long to;
        private String filePath;

        public Reader(String filePath, long from, long to)
        {
            this.from = from;
            this.to = to;
            this.filePath = filePath;
        }

        @Override
        public Long call() throws Exception 
        {
            long lineCount = 0;

            try(var r = new PositionableReader(filePath, from, to))
            {
                @SuppressWarnings("unused")
                String line;
                
                while ((line = r.readln()) != null)
                {
                    lineCount++;
                }
            }

            return lineCount;
        }

    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC040_N_ConcurrentRead.class, args);
    }
}
