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

native-image -O3 -H:TuneInlinerExploration=1 --gc=epsilon -H:-GenLoopSafepoints -march=native -H:ReflectionConfigurationFiles=reflection.json -cp $CLASSPATH -o org.rschwietzke.devoxxpl24.$LOWER.best org.rschwietzke.devoxxpl24.$1
native-image --pgo-instrument --gc=epsilon -H:-GenLoopSafepoints -march=native -H:ReflectionConfigurationFiles=reflection.json  -cp $CLASSPATH org.rschwietzke.devoxxpl24.$1

./org.rschwietzke.devoxxpl24.$LOWER $2 $3 $4

native-image --pgo --gc=epsilon -H:-GenLoopSafepoints -march=native -H:ReflectionConfigurationFiles=reflection.json -cp $CLASSPATH org.rschwietzke.devoxxpl24.$1
rm default.iprof