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
import java.util.Map;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;

/**
 * This is an empty shell to check the framework overhead.
 */
public class BRC00_Gemini extends Benchmark
{
    @Override
    public String run(final String fileName) throws IOException
    {
        Map<String, TemperatureData> cityTemperatures = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String city = parts[0].trim();
                double temperature = Double.parseDouble(parts[1].replace(",", "."));
                TemperatureData data = cityTemperatures.computeIfAbsent(city, k -> new TemperatureData());
                data.addTemperature(temperature);
            }
        }

        TreeMap<String, TemperatureData> results = new TreeMap<>();
        results.putAll(cityTemperatures);

        return results.toString();
    }

    private static class TemperatureData {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        int count = 0;

        void addTemperature(double temperature) {
            min = Math.min(min, temperature);
            max = Math.max(max, temperature);
            sum += temperature;
            count++;
        }

        public String toString()
        {
            final double mean = this.sum / (double)this.count;
            return round(min) + "/" + round(mean) + "/" + round(max);
        }

        private double round(double value)
        {
            return Math.round(value * 10.0) / 10.0;
        }
    }



    public static void main(String[] args)
    {
        Benchmark.run(BRC00_Gemini.class, args);
    }
}
