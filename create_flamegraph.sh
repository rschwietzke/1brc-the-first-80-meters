#!/bin/sh
#
#  Copyright 2023 The original authors
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
CLASSPATH=target/classes/

java -agentpath:/home/rschwietzke/bin/async-profiler/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so=start,event=cache-misses,flamegraph,file=$1-cache-misses.html  -cp target/classes/ org.rschwietzke.devoxxpl24.$1 $2 $3 $4
java -agentpath:/home/rschwietzke/bin/async-profiler/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so=start,event=cpu,flamegraph,file=$1-cpu.html  -cp target/classes/ org.rschwietzke.devoxxpl24.$1 $2 $3 $4
java -agentpath:/home/rschwietzke/bin/async-profiler/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so=start,event=alloc,flamegraph,file=$1-alloc.html  -cp target/classes/ org.rschwietzke.devoxxpl24.$1 $2 $3 $4
java -agentpath:/home/rschwietzke/bin/async-profiler/async-profiler-3.0-linux-x64/lib/libasyncProfiler.so=start,event=branch-misses,flamegraph,file=$1-branch-misses.html  -cp target/classes/ org.rschwietzke.devoxxpl24.$1 $2 $3 $4
