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

JVMARGS_DEFAULT="-cp $CLASSPATH -XX:+AlwaysPreTouch --add-exports java.base/jdk.internal.util=ALL-UNNAMED"

JVMARGS_LOWMEM="-Xmx10m -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC"
JVMARGS_HIGHMEM="-Xms2g -Xmx2g "

JVMARGS_LOW="$JVMARGS_DEFAULT $JVMARGS_LOWMEM"
JVMARGS_HIGH="$JVMARGS_DEFAULT $JVMARGS_HIGHMEM"

CLASSES_HIGHMEM="
BRC000_Empty
BRC001_Baseline
BRC010_NoStream
BRC012_SplitRemoved
BRC014_NewParseDouble
BRC015_ParseDoubleSimpler1
BRC016_NoExtraString
BRC017_ParseDoubleSimpler2
BRC020_IntegerValue
BRC021_LooplessParsing
BRC022_LooplessParsing2
BRC025_MutateData
BRC027_SizedMap
BRC030_OpenMap
BRC031_OpenMapLessCasting
BRC033_SimplerLambda
BRC035_NoLambda
BRC036_TurnedIfs
BRC037_LessNullChecks
BRC040_ASetIsGoodEnough
BRC041_DirectUpdate
BRC042_NoSplittedString
BRC045_KeepChars
BRC047_IntOnly
BRC048_ImprovedStringHandling
BRC049_ManualMinMax
BRC050_LargerSet"

CLASSES_LOWMEM="
BRC060_ReadingBytes
BRC061_RandomAccessFile
BRC063_OneAddLess
BRC065_OneLoopLess
BRC067_ParseDifferently
BRC068_InlineParsing
BRC069_InlineParsing_65
BRC070_EqualsCity
BRC072_ReadDirectNotViaBuffer
BRC075_LargeByteBuffer
BRC077_LessAdditions
BRC078_CalculateEarlier
BRC079_RunWithoutByteBuffer
BRC080_JDKArrayUtils
BRC081_LoopUnroll
BRC082_VectorSearchOnlyForLargeArray
BRC083_MainLoop
BRC090_MemorySegment_VOID
BRC091_Reviewed83
BRC092_ParseIntegerLessBranches_VOID
BRC093_ParseIntegerLessCode_VOID
BRC094_Reviewed91
BRC095_Hash
BRC096_ReadInt_VOID
BRC097_EqualsCity
BRC098_ParseTemperature_VOID
BRC099_ArrayAccess
BRC100_DirectTempWrite
BRC101_ParseFrom65_VOID
BRC105_ParseTemp_95
BRC106_ParseTemp_105
BRC107_ParseTemp
BRC110_EqualsCitySplit
BRC111_EqualsCitySplit_Reverse
BRC112_EqualsCityMismatch
BRC113_EqualsCityMismatchSimple
BRC120_OnlyHashing_From95
BRC121_UpdatedLoops
"

alias time='/usr/bin/time -f "Elapsed: %E, Faults: %F, Minor: %R, Max RSS: %M KB, FS Input: %I, FS Output: %O, System: %S s, User: %U s, Context I/V: %c/%w"'

# Read file several times to warmup cache
echo "=== Cache Warming"
echo "File Warmup 1: "
time wc -l $1

echo "File Warmup 2: "
time cat $1 > /dev/null

echo "=== Measurements"
for c in $CLASSES_HIGHMEM
do
    perf stat -o $c.perf.txt java $JVMARGS_HIGH org.rschwietzke.again26.$c -f $1 -wc $2 -mc $3 --batchmode ""
done

for c in $CLASSES_LOWMEM
do
    perf stat -o $c.perf.txt java $JVMARGS_LOW org.rschwietzke.again26.$c -f $1 -wc $2 -mc $3 --batchmode ""
done
