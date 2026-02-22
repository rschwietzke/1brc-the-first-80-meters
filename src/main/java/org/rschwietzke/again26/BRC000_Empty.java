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
package org.rschwietzke.again26;

import java.io.IOException;

import org.rschwietzke.Benchmark;

/**
 * This is an empty shell to check the framework overhead. We expect almost zero runtime here.
 * The only thing we might see is the Java framework overhead when coming up.
 */
public class BRC00_Empty extends Benchmark
{
    @Override
    public String run(final String fileName) throws IOException
    {
        return "";
    }

    public static void main(String[] args)
    {
		Benchmark.run(BRC00_Empty.class, args);
    }
}
