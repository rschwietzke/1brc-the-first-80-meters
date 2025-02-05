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
import java.util.ArrayList;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * This is an empty shell to check the framework overhead.
 */
public class BRC00_Claude extends Benchmark
{
    @Override
    public String run(final String fileName) throws IOException
    {
        TreeMap<String, ArrayList<Double>> cityData = new TreeMap<>();

        // Read the file
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                String city = parts[0];
                double temp = Double.parseDouble(parts[1]);

                // Add temperature to city's list
                cityData.putIfAbsent(city, new ArrayList<>());
                cityData.get(city).add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TreeMap<String, String> finalResult = new TreeMap<>();

        // Calculate statistics and create output
        StringBuilder result = new StringBuilder();
        for (String city : cityData.keySet()) {
            ArrayList<Double> temperatures = cityData.get(city);
            int count = temperatures.size();
            double min = temperatures.stream().mapToDouble(d -> d).min().getAsDouble();
            double max = temperatures.stream().mapToDouble(d -> d).max().getAsDouble();
            double avg = temperatures.stream().mapToDouble(d -> d).average().getAsDouble();

            finalResult.put(city, MathUtil.toString(count, min, max, avg));
        }

        return finalResult.toString();
    }

    public static void main(String[] args)
    {
        Benchmark.run(BRC00_Claude.class, args);
    }
}
