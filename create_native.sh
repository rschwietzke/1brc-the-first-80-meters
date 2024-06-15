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
LOWER=$(echo $1 | tr '[:upper:]' '[:lower:]')
CLASS=org.rschwietzke.devoxxpl24.$1

REFLECTIONCFG="-H:ReflectionConfigurationFiles=reflection.json"
MAXTUNINGCFG="-O3 -H:TuneInlinerExploration=1"
GC="--gc=epsilon -H:-GenLoopSafepoints"
ARCH="-march=native"

# compile a great images, taken from #1br by thomaswuerthinger
native-image $MAXTUNINGCFG $GC $ARCH $REFLECTIONCFG -cp $CLASSPATH -o $LOWER.best $CLASS

# compile a base image with instrumentation
native-image --pgo-instrument $GC $ARCH $REFLECTIONCFG -cp $CLASSPATH -o $LOWER $CLASS

./org.rschwietzke.devoxxpl24.$LOWER $2 $3 $4

# Recompile and take profiler output into account
native-image --pgo $GC $ARCH $REFLECTIONCFG -cp $CLASSPATH -o $LOWER $CLASS
rm default.iprof