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

JVMARGS_DEFAULT="-cp $CLASSPATH -XX:+AlwaysPreTouch"

JVMARGS_LOWMEM="-Xmx10m -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC"
JVMARGS_HIGHMEM="-Xms2g -Xmx2g "

JVMARGS_LOW="$JVMARGS_DEFAULT $JVMARGS_LOWMEM"
JVMARGS_HIGH="$JVMARGS_DEFAULT $JVMARGS_HIGHMEM"

CLASSES_HIGHMEM="
BRC00_Empty
BRC01_Baseline
BRC100_DirectTempWrite
BRC10_NoStream
BRC12_SplitRemoved
BRC14_NewParseDouble
BRC15_ParseDoubleSimpler1
BRC16_NoExtraString
BRC17_ParseDoubleSimpler2
BRC20_IntegerValue
BRC21_LooplessParsing
BRC22_LooplessParsing2
BRC25_MutateData
BRC27_SizedMap
BRC30_OpenMap
BRC31_OpenMapLessCasting
BRC33_SimplerLambda
BRC35_NoLambda
BRC36_TurnedIfs
BRC37_LessNullChecks
BRC40_ASetIsGoodEnough
BRC41_DirectUpdate
BRC42_NoSplittedString
BRC45_KeepChars
BRC47_IntOnly
BRC48_ImprovedStringHandling
BRC49_ManualMinMax
BRC50_LargerSet"

CLASSES_LOWMEM="
BRC60_ReadingBytes
BRC61_RandomAccessFile
BRC63_OneAddLess
BRC65_OneLoopLess
BRC67_ParseDifferently
BRC68_InlineParsing
BRC70_EqualsCity
BRC72_ReadDirectNotViaBuffer
BRC75_LargeByteBuffer
BRC77_LessAdditions
BRC78_CalculateEarlier
BRC79_RunWithoutByteBuffer
BRC80_JDKArrayUtils
BRC81_LoopUnroll
BRC82_VectorSearchOnlyForLargeArray
BRC83_MainLoop
BRC90_MemorySegment_VOID
BRC91_Reviewed83
BRC92_ParseIntegerLessBranches_VOID
BRC93_ParseIntegerLessCode_VOID
BRC94_Reviewed91
BRC95_Hash
BRC96_ReadInt_VOID
BRC97_EqualsCity
BRC98_ParseTemperature_VOID
BRC99_ArrayAccess
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
    #perf stat -o $c.perf.txt
    java $JVMARGS_HIGH org.rschwietzke.again26.$c -f $1 -wc $2 -mc $3 --batchMode ""
done

for c in $CLASSES_LOWMEM
do
    #perf stat -o $c.perf.txt
    java $JVMARGS_LOW org.rschwietzke.again26.$c -f $1 -wc $2 -mc $3 --batchMode ""
done
