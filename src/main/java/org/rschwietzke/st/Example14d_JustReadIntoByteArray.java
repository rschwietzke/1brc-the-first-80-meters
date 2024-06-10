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

import org.rschwietzke.Benchmark;

/**
 *
 *
 * @author Rene Schwietzke
 */
public class Example14d_JustReadIntoByteArray extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		int count = 0;

		try (final var reader = new BufferedInputStream(new FileInputStream(fileName)))
		{
			byte[] b = new byte[1024];
			int total = 0;
			while ((total = reader.read(b)) != -1)
			{
				for (int i = 0; i < total; i++)
				{
					if (b[i] == 10)
					{
						count++;
					}
				}
			}
		}

		System.out.println("Lines: " + count);
		return String.valueOf(count);
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example14d_JustReadIntoByteArray.class.getDeclaredConstructor(), args);
	}
}
