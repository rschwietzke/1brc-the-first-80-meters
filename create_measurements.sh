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
echo "File: $1"

# Read file several times to warmup cache
echo "File Warmup\n"
wc -l $1
cat $1 > /dev/null

echo "== Measurements"
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC01_BaselineMT $1 1 1 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC01_BaselineST $1 1 1 --batchMode ""

java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC02_NoGroupingMT $1 1 1 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC02_NoGroupingST $1 1 1 --batchMode ""

java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC03_NoStreamMT $1 1 1 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC03_NoStreamST $1 1 1 --batchMode ""
