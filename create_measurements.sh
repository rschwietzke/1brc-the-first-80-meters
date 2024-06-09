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
echo "Warmups: $2"
echo "Measurements: $3"

alias time='/usr/bin/time -f "Elapsed: %E, Faults: %F, Minor: %R, Max RSS: %M KB, FS Input: %I, FS Output: %O, System: %S s, User: %U s, Context I/V: %c/%w"'

# Read file several times to warmup cache
echo "File Warmup 1\n"
time wc -l $1
echo "File Warmup 2\n"
time cat $1 > /dev/null

echo "== Measurements"
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC01_BaselineST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC02_NoGroupingST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC03_NoStreamST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC04_CleanupST $1 $2 $3 --batchMode ""
#java -Xms4g -Xmx4g -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC04_CleanupST $1 $2 $3 --batchMode "Memory 4G"
#java -Xms4g -Xmx4g -XX:+AlwaysPreTouch -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC04_CleanupST $1 $2 $3 --batchMode "Memory 4G, touched"
#java -Xmx100m -XX:+AlwaysPreTouch -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC04_CleanupST $1 $2 $3 --batchMode "Memory 100m, touched"
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC05_ReplaceSplitST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC06_NewDoubleParsingST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC07_NoCopyForDoubleST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC08_GoIntST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC09_NoMergeST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC10_MutateST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC11_SizedMapST $1 $2 $3 --batchMode ""
java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC12_NewMapST $1 $2 $3 --batchMode ""


#java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC01_BaselineMT $1 $2 $3 --batchMode ""
#java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC02_NoGroupingMT $1 $2 $3 --batchMode ""
#java -cp $CLASSPATH org.rschwietzke.devoxxpl24.BRC03_NoStreamMT $1 $2 $3 --batchMode ""
