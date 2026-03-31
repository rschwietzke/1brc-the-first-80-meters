// JVM_OPTS: $HIGH_MEM
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
package org.rschwietzke.parallel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.TreeMap;

import org.rschwietzke.Benchmark;
import org.rschwietzke.util.MathUtil;

/**
 * Just see how fast we can read the data without processing.
 *
 * @author Rene Schwietzke
 */
public class BRC021_NoStreamFileOnly extends Benchmark
{
    @Override
    public String run(final String fileName) throws IOException
    {
    	// open the file
    	try (var reader = Files.newBufferedReader(Paths.get(fileName)))
        {
    		String line;
    		
    		// read all lines until end of file
    		while ((line = reader.readLine()) != null)
    		{
    		}
    	}

    	// ok, we got everything, now we need to order it
        return new TreeMap<String, String>().toString();
    }

    public static void main(String[] args) throws NoSuchMethodException, SecurityException
    {
		Benchmark.run(BRC021_NoStreamFileOnly.class, args);
    }
}
