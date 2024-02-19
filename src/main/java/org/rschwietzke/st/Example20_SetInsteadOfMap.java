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
import org.rschwietzke.util.City20;
import org.rschwietzke.util.City20.Temperatures20;
import org.rschwietzke.util.FastHashSet20;
import org.rschwietzke.util.LineInputStream2;
import org.rschwietzke.util.LineInputStream2.LineInfo;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example20_SetInsteadOfMap extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashSet20 cities = new FastHashSet20();

		try (final var r = new LineInputStream2(new BufferedInputStream(new FileInputStream(fileName))))
		{
			LineInfo l;
			City20 city = new City20();
			while ((l = r.readLine()) != null)
			{
				// put all data together
				city.data(l);

				// get us our data store for the city, which is another
				// version of a city
				City20 totalsCity = cities.get(city);
				if (totalsCity == null)
				{
					// get us a storeable copy
					totalsCity = City20.materalize(l);
					cities.put(totalsCity);
				}
				// store aka add the temperature to the totals
				totalsCity.temperatures.add(city.temperature);
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<City20, Temperatures20>();
		cities.keys().forEach(k -> {
			result.put(k, cities.get(k).temperatures);
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example20_SetInsteadOfMap.class.getDeclaredConstructor(), args);
	}
}
