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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Split costs a lot, try the most naive next best things first
 *
 * @author Rene Schwietzke
 */
public class BRC05_ReplaceSplitST extends Benchmark
{
    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    private static class Temperatures
    {
        private final double min;
        private final double max;
        private final double total;
        private final int count;

        public Temperatures(final double value)
        {
            this.min = value;
            this.max = value;
            this.total = value;
            this.count = 1;
        }

        private Temperatures(double min, double max, double total, int count)
        {
            this.min = min;
            this.max = max;
            this.total = total;
            this.count = count;
        }

        /**
         * Combine two temperatures
         *
         * @param other the other city temperature
         * @return a new combined state
         */
        public Temperatures merge(final Temperatures other)
        {
            return new Temperatures(Math.min(min, other.min), Math.max(max, other.max), total + other.total, count + other.count);
        }

        /**
         * Our final printing format
         */
        public String toString()
        {
            return MathUtil.toString(total, count, min, max);
        }
    }

    @Override
    public String run(final String fileName) throws IOException
    {
        // our cities with temperatures
        final Map<String, Temperatures> cities = new HashMap<>();

        try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                // split the line
                final int pos = line.indexOf(';');

                // get us the city
                final String city = line.substring(0, pos);
                final String temperatureAsString = line.substring(pos + 1);

                // parse our temperature
                final double temperature = Double.parseDouble(temperatureAsString);

                // merge the data into the captured data
                cities.merge(city, new Temperatures(temperature), (t1, t2) -> t1.merge(t2));
            }
        }

        // ok, we got everything, now we need to order it and print it
        return new TreeMap<String, Temperatures>(cities).toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
        Benchmark.run(BRC05_ReplaceSplitST.class, args);
    }
}
