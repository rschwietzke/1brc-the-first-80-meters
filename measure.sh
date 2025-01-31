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
BRC00_Empty.java
BRC01_BaselineMT.java
BRC01_BaselineST.java
BRC02_NoGroupingMT.java
BRC02_NoGroupingST.java
BRC03_NoStreamMT.java
BRC03_NoStreamST.java
BRC04_CleanupST.java
BRC05_ReplaceSplitST.java
BRC06_NewDoubleParsingST.java
BRC07_NoCopyForDoubleST.java
BRC08_GoIntST.java
BRC09_NoMergeST.java
BRC10_MutateST.java
BRC11_SizedMapST.java
BRC12_NewMapST.java
BRC13_HardcodedSetST.java"

CLASSES_LOWMEM="
BRC14a_ReadBytesBroken.java
BRC14b_ReadBytesFixed.java
BRC15_ParseDoubleFixedST.java
BRC20_UseArrayNoBufferST.java
BRC21_ManualMinMaxST.java
BRC22_EarlyHashCodeST.java
BRC23a_NoMulSplitST.java
BRC23_NoMulST.java
BRC24_DifferentBranchInHashCodeST.java
BRC25_SmallAddReordingST.java
BRC26_MoreMapSpaceST.java
BRC27_SmallPutST.java
BRC28_FineTuningST.java
BRC29a_ParseDoubleTuningST.java
BRC29c_ArrayCopyInMethod.java
BRC29d_EqualsNotBoolean.java
BRC29e_EarlyIntResolution.java
BRC29f_LessDataForTempResolution.java
BRC29g_FixedIntParsing.java
BRC30_DangerNoEqualsST.java
BRC40a_NoChannel.java
BRC40b_ReturnInstead.java
BRC40c_UnrollTempParsing.java
BRC40d_LongLoop.java
BRC40e_NoReloadSub.java
BRC40f_DoWhile.java
BRC40g_Put.java
BRC40h_ManualMismatch.java
BRC40i_SmallerSemicolonLoop.java
BRC40j_LessStateInSetEquals.java
BRC40_NoChannel.java
BRC41a_FixedFastHashSet.java
BRC41b_ReorderedLineFields.java
BRC41c_LargerBuffer.java
BRC42a_WhileTrue.java
BRC42b_NoReturnBranch.java
BRC43_NoSubClass.java
BRC45_DoubleTheSetSize.java
BRC46_TunedHashSet.java
BRC47_LeanerPut.java
BRC48_FixedFactor.java
BRC49_OffsetSubtraction.java
BRC50_Short.java
BRC51_TempParsingLessBranches.java
BRC52_TempParsingBitSubtraction.java
BRC53_SetEqualsNoLocalAssignment.java
BRC54_LoopVariableBackInLoop.java
BRC55_SimplerPutCall.java
BRC56_MainLoopAsWhile.java
BRC57_SimplerHashing_VOID.java
BRC58_UnnoticedCharSkipping.java
BRC59_ByteBufferDirect.java
BRC60_FeedCPUUnrollSemicolonLoop.java
BRC61_BitShiftMul10Void.java
BRC62_IsEqualsWithADifferentBranchApproach.java
BRC63b_Equals_MainLoop.java
BRC63_Equals.java
BRC64_CombinePuts.java
BRC65_OneMainLoopMethod.java
BRC66_HashCode.java
BRC67_StoreArrayLength.java
BRC68_RemoveNewLinePos.java
BRC69_RemovedExtraAdd.java
BRC70_RemovedPutReturn.java"

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
