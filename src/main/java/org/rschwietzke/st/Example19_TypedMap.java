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
import org.rschwietzke.util.City19;
import org.rschwietzke.util.FastHashMap19;
import org.rschwietzke.util.LineInputStream2;
import org.rschwietzke.util.LineInputStream2.LineInfo;
import org.rschwietzke.util.Temperatures19;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example19_TypedMap extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		// our storage
		final FastHashMap19 cities = new FastHashMap19();

		try (final var r = new LineInputStream2(new BufferedInputStream(new FileInputStream(fileName))))
		{
			LineInfo l;
			City19 city = new City19();
			while ((l = r.readLine()) != null)
			{
				city.data(l);

				// get us our data store for the city
				final Temperatures19 t = cities.get(city);
				if (t != null)
				{
					t.add(city.temperature);
				}
				else
				{
					cities.put(City19.materalize(l), new Temperatures19(city.temperature));
				}
			}
		}

		// ok, we got everything, now we need to order it
		var result = new TreeMap<City19, Temperatures19>();
		cities.keys().forEach(k -> {
			result.put(k, cities.get(k));
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example19_TypedMap.class.getDeclaredConstructor(), args);
	}
}
