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

/**
 * @author Antigravity
 */
public class RunDefinition {
    public final String name;
    public final String jdkFilter;
    public final String gcFilter;
    public final String vmFilter;
    public final String bindingFilter;
    public final String progFilter;
    public final String dataFilter;
    public final String classFilter;

    public RunDefinition(String name, java.util.Map<String, String> properties) {
        this.name = name;
        this.jdkFilter = properties.getOrDefault("JDK_FILTER", "*");
        this.gcFilter = properties.getOrDefault("GC_FILTER", "*");
        this.vmFilter = properties.getOrDefault("VM_FILTER", "*");
        this.bindingFilter = properties.getOrDefault("BINDING_FILTER", properties.getOrDefault("TASKSET_FILTER", "*"));
        this.progFilter = properties.getOrDefault("PROG_FILTER", "*");
        this.dataFilter = properties.getOrDefault("DATA_FILTER", "*");
        this.classFilter = properties.getOrDefault("CLASS_FILTER", "*");
    }
}

