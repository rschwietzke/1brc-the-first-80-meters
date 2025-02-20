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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * This is our first Example. It will use a regular Java approach
 * from the books, where we just use everything as we find it in the
 * libs without thinking about the impact.
 *
 * @author Rene Schwietzke
 */
public class BRC03_NoStreamMT extends Benchmark
{
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

		public Temperatures merge(final Temperatures other)
		{
			return new Temperatures(Math.min(min, other.min), Math.max(max, other.max), total + other.total, count + other.count);
		}

		public String toString()
		{
            return MathUtil.toString(total, count, min, max);
        }
	}

	static class Worker extends Thread
	{
		private final Map<String, Temperatures> temperatures = new HashMap<>();
		final ArrayBlockingQueue<List<String>> buffer = new ArrayBlockingQueue<>(1000);

		public Worker()
		{
			this.setDaemon(true);
		}

		@Override
		public void run()
		{
			while (true)
			{
				List<String> lines;
				try
				{
					lines = buffer.take();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
					return;
				}

				if (lines.isEmpty())
				{
					return;
				}

				for (String line : lines)
				{
					final String[] cols = line.split(";");

					final String city = cols[0];
					final double temperature = Double.parseDouble(cols[1]);

					temperatures.merge(city, new Temperatures(temperature), (t1, t2) -> t1.merge(t2));
				}
			}
		}

		public Map<String, Temperatures> temperatures()
		{
			return temperatures;
		}
	}

	@Override
	public String run(final String fileName) throws IOException
	{
		final int cpuCount = Runtime.getRuntime().availableProcessors();

		final List<Worker> threads = new ArrayList<>();
		for (int i = 0; i < cpuCount; i++)
		{
			var t = new Worker();
			threads.add(t);
			t.start();
		}

		try (var reader = Files.newBufferedReader(Paths.get(fileName)))
		{
			String line;
			int round = 0;
			while ((line = reader.readLine()) != null)
			{
				round++;

				var list = new ArrayList<String>(1000);
				list.add(line);

				while (list.size() < 1000 && (line = reader.readLine()) != null)
				{
					list.add(line);
				}

				try
				{
					threads.get(round % cpuCount).buffer.put(list);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		var result = new TreeMap<String, Temperatures>();
		for (Worker thread : threads)
		{
			thread.buffer.add(List.of());
		}
		threads.forEach(t ->
		{
			try
			{
				t.join();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			t.temperatures.forEach((k, v) ->
			{
				result.merge(k, v, (t1, t2) -> t1.merge(t2));
			});
		});

		return result.toString();
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(BRC03_NoStreamMT.class, args);
	}
}
