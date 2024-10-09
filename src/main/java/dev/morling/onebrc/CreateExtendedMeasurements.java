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
package dev.morling.onebrc;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;
import java.util.stream.Collectors;

public class CreateExtendedMeasurements
{
    private static class WeatherStation
    {
        final RandomGenerator r;
        final String id;
        final double meanTemperature;

        public WeatherStation(final String id)
        {
        	this.id = id;
            this.r = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create(id.hashCode());
        	this.meanTemperature = r.nextGaussian(r.nextInt(-30, 40), 10);
        }

        double measurement()
        {
            var result = 10000d;
            while (result >= 100 || result <= -100)
            {
                double m = r.nextGaussian(meanTemperature, 10);
                result = Math.round(m * 10.0) / 10.0;
            }
            return result;
        }
    }

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        if (args.length != 3) {
            System.out.println("Usage: create_extended_measurements.sh <city count> <number of records to create> <filename>");
            System.exit(1);
        }

        int cityCount = 0;
        int size = 0;
        try
        {
            cityCount = Integer.parseInt(args[0]);
            size = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            System.err.println("Usage: create_extended_measurements.sh <city count> <number of records to create> <filename>");
            System.exit(1);
        }

        // make things repeatable
        var r = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create(424242L);

        // read all stations
        final List<String> cities = Files.lines(
                Path.of(CreateExtendedMeasurements.class.getResource("/cities.txt").toURI()))
                .filter(c -> !c.trim().startsWith("#"))
                .sorted()
                .collect(Collectors.toList());

        // shuffle them
        Collections.shuffle(cities, r);

        // take the count needed turn it into a wheather station
        var stations = cities.stream()
                .limit(cityCount)
                .map(c -> new WeatherStation(c))
                .toList();

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(args[2]))) {
            for (int i = 0; i < size; i++) {
                if (i > 0 && i % 50_000_000 == 0) {
                    System.out.printf("Wrote %,d measurements in %s ms%n", i, System.currentTimeMillis() - start);
                }
                WeatherStation station = stations.get(r.nextInt(stations.size()));
                bw.write(station.id);
                bw.write(";" + station.measurement());

                bw.write('\n');
            }
        }
        System.out.printf("Created file with %,d measurements in %s ms%n", size, System.currentTimeMillis() - start);
    }
}
