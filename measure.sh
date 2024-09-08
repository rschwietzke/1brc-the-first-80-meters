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

JVMARGS_LOWMEM="-Xmx50m -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC"
JVMARGS_HIGHMEM="-Xms1g -Xmx1g "

JVMARGS_LOW="$JVMARGS_DEFAULT $JVMARGS_LOWMEM"
JVMARGS_HIGH="$JVMARGS_DEFAULT $JVMARGS_HIGHMEM"

CLASSES_HIGHMEM="
BRC01_BaselineMT
BRC01_BaselineST
BRC02_NoGroupingST
BRC03_NoStreamMT
BRC03_NoStreamST
BRC04_CleanupST
BRC05_ReplaceSplitST
BRC06_NewDoubleParsingST
BRC07_NoCopyForDoubleST
BRC08_GoIntST
BRC09_NoMergeST
BRC10_MutateST
BRC11_SizedMapST
BRC12_NewMapST
BRC13_HardcodedSetST"

CLASSES_LOWMEM="
BRC14a_ReadBytesBroken
BRC14b_ReadBytesFixed
BRC15_ParseDoubleFixedST
BRC20_UseArrayNoBufferST
BRC21_ManualMinMaxST
BRC22_EarlyHashCodeST
BRC23a_NoMulSplitST
BRC23_NoMulST
BRC24_DifferentBranchInHashCodeST
BRC25_SmallAddReordingST
BRC26_MoreMapSpaceST
BRC27_SmallPutST
BRC28_FineTuningST
BRC29a_ParseDoubleTuningST
BRC29c_ArrayCopyInMethod
BRC29d_EqualsNotBoolean
BRC29e_EarlyIntResolution
BRC29f_LessDataForTempResolution
BRC29g_FixedIntParsing
BRC30_DangerNoEqualsST
BRC40a_NoChannel
BRC40b_ReturnInstead
BRC40c_UnrollTempParsing
BRC40d_LongLoop
BRC40e_NoReloadSub
BRC40f_DoWhile
BRC40g_Put
BRC40h_ManualMismatch
BRC40i_SmallerSemicolonLoop
BRC40j_LessStateInSetEquals
BRC41a_FixedFastHashSet
BRC41b_ReorderedLineFields
BRC41c_LargerBuffer
BRC42a_WhileTrue
BRC42b_NoReturnBranch
BRC43_NoSubClass
BRC45_DoubleTheSetSize
BRC46_TunedHashSet
BRC47_LeanerPut
BRC48_FixedFactor
BRC49_OffsetSubtraction
BRC50_Short"

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
    java $JVMARGS_HIGH org.rschwietzke.devoxxpl24.$c $1 $2 $3 --batchMode ""
done

for c in $CLASSES_LOWMEM
do
    #perf stat -o $c.perf.txt
    java $JVMARGS_LOW org.rschwietzke.devoxxpl24.$c $1 $2 $3 --batchMode ""
done
