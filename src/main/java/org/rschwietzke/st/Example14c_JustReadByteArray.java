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
public class Example14c_JustReadByteArray extends Benchmark
{
	@Override
	public String run(final String fileName) throws IOException
	{
		int count = 0;

		try (final var reader = new BufferedInputStream(new FileInputStream(fileName)))
		{
			while(true)
			{
				final var b = reader.readNBytes(256);
				if (b.length == 0)
				{
					break;
				}
				count += b.length;
			}
		}

		return String.valueOf(count);
	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException
	{
		Benchmark.run(Example14c_JustReadByteArray.class.getDeclaredConstructor(), args);
	}
}
