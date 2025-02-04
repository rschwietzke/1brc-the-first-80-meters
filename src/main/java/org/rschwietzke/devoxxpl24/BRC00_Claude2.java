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
package org.rschwietzke.devoxxpl24;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;

/**
 * This is an empty shell to check the framework overhead.
 */
public class BRC00_Claude2 extends Benchmark
{
    @Override
    public String run(final String fileName) throws IOException
    {
        // Use HashMap for faster initial aggregation
        HashMap<String, Stats> cityStats = new HashMap<>(1000);

        try (BufferedReader br = new BufferedReader(
                new FileReader(fileName), 1024 * 1024)) { // 1MB buffer

            String line;
            StringBuilder cityBuilder = new StringBuilder(50);

            while ((line = br.readLine()) != null) {
                // Manual parsing instead of split() for better performance
                int separatorIndex = line.indexOf(';');
                if (separatorIndex == -1) continue;

                // Reuse StringBuilder for city name
                cityBuilder.setLength(0);
                cityBuilder.append(line, 0, separatorIndex);
                String city = cityBuilder.toString();

                // Parse temperature directly from the substring
                double temp = parseTemperature(
                        line.substring(separatorIndex + 1));

                // Update statistics
                cityStats.computeIfAbsent(city, k -> new Stats())
                .update(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Convert to TreeMap for sorted output
        TreeMap<String, Stats> sortedStats = new TreeMap<>(cityStats);
        return sortedStats.toString();
    }

    private static class Stats {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        long count = 0;

        void update(double value) {
            min = Math.min(min, value);
            max = Math.max(max, value);
            sum += value;
            count++;
        }

        private double round(double value)
        {
            return Math.round(value * 10.0) / 10.0;
        }

        public String toString()
        {
            final double mean = this.sum / (double)this.count;
            return round(min) + "/" + round(mean) + "/" + round(max);
        }
    }

    // Custom temperature parser that's faster than Double.parseDouble
    private static double parseTemperature(String s) {
        // Assuming format XX.X or XXX.X
        boolean negative = s.charAt(0) == '-';
        int startIndex = negative ? 1 : 0;
        int dotIndex = s.indexOf('.');

        int wholeNumber = 0;
        for (int i = startIndex; i < dotIndex; i++) {
            wholeNumber = wholeNumber * 10 + (s.charAt(i) - '0');
        }

        int decimal = s.charAt(dotIndex + 1) - '0';
        double result = wholeNumber + (decimal / 10.0);
        return negative ? -result : result;
    }


    public static void main(String[] args)
    {
        Benchmark.run(BRC00_Claude2.class, args);
    }
}
