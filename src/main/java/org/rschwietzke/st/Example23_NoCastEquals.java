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
package org.rschwietzke.st;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.City23;
import org.rschwietzke.util.City23.Temperatures20;
import org.rschwietzke.util.FastHashSet23;
import org.rschwietzke.util.LineInputStream2;
import org.rschwietzke.util.LineInputStream2.LineInfo;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example23_NoCastEquals extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashSet23 cities = new FastHashSet23(500, 0.5f);

		try (final var r = new LineInputStream2(new BufferedInputStream(new FileInputStream(fileName))))
		{
			LineInfo l;
			City23 city = new City23();
			while ((l = r.readLine()) != null)
			{
				// put all data together
				city.data(l);

				// get us our data store for the city, which is another
				// version of a city
				City23 totalsCity = cities.getPutOnEmpty(city, l);
				// store aka add the temperature to the totals
				totalsCity.temperatures.add(city.temperature);
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<City23, Temperatures20>();
		cities.keys().forEach(k -> {
			result.put(k, k.temperatures);
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example23_NoCastEquals.class.getDeclaredConstructor(), args);
	}
}
