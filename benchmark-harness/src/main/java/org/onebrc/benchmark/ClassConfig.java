/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.onebrc.benchmark;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the configuration and execution metadata parsed directly from source code annotations.
 * Extracts constraints like \@BenchmarkIgnore, \@BenchmarkExclude, and \@BenchmarkStatus.
 * 
 * @author Antigravity
 */
public class ClassConfig {
    /** The simple name of the implementation class (e.g., 'SerialReader'). */
    public final String className;
    
    /** The Fully Qualified Class Name (e.g., 'org.onebrc.parallel.SerialReader'). */
    public final String fqcn;
    
    /** If true, the class is marked with \@BenchmarkIgnore and should not be scheduled. */
    public boolean ignore = false;
    
    /** Indicates the completion status: 'baseline', 'incomplete', or 'complete'. */
    public String status = "complete";
    
    /** A list of explicit filter tokens (e.g., 'JDK:17', 'RUN:fast') that this class forbids. */
    public final List<String> exclusions = new ArrayList<>();
    
    /** A list of explicit filter tokens that this class requires (whitelist execution). */
    public final List<String> inclusions = new ArrayList<>();

    /**
     * Constructs a new ClassConfig instance.
     */
    public ClassConfig(String className, String fqcn) {
        this.className = className;
        this.fqcn = fqcn;
    }
}

