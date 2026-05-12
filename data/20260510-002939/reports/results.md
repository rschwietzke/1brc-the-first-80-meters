# 1BRC Benchmark Report

**Run Timestamp:** 20260510-002939

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 23,343 ms | 27,477 ms |
| org.onebrc.again26.BRC010_NoStream | 29,529 ms | 32,358 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 18,265 ms | 27,743 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 17,914 ms | 18,184 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 15,917 ms | 18,244 ms |
| org.onebrc.again26.BRC016_NoExtraString | 17,959 ms | 20,125 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 16,069 ms | 20,007 ms |
| org.onebrc.again26.BRC020_IntegerValue | 15,443 ms | 17,988 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 15,729 ms | 16,063 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 15,724 ms | 16,293 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 18,079 ms |
| org.onebrc.again26.BRC027_SizedMap | 14,788 ms | 16,950 ms |
| org.onebrc.again26.BRC030_OpenMap | 13,937 ms | 16,915 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 14,486 ms | 13,045 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 13,134 ms | 13,962 ms |
| org.onebrc.again26.BRC035_NoLambda | 13,729 ms | 11,562 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 13,280 ms | 11,777 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 12,870 ms | 11,755 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 12,835 ms | 11,635 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 12,514 ms | 11,867 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 13,075 ms | 12,439 ms |
| org.onebrc.again26.BRC045_KeepChars | 12,900 ms | 12,148 ms |
| org.onebrc.again26.BRC047_IntOnly | 13,793 ms | 11,989 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 12,720 ms | 11,966 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 13,560 ms | 11,920 ms |
| org.onebrc.again26.BRC050_LargerSet | 13,746 ms | 12,172 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 16,899 ms | 16,982 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 5,987 ms | 5,995 ms |
| org.onebrc.again26.BRC063_OneAddLess | 6,005 ms | 6,127 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 5,066 ms | 5,765 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 5,390 ms | 5,537 ms |
| org.onebrc.again26.BRC068_InlineParsing | 5,439 ms | 5,225 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 5,170 ms | 5,897 ms |
| org.onebrc.again26.BRC070_EqualsCity | 4,782 ms | 5,032 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 4,253 ms | 4,319 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 3,886 ms | 4,167 ms |
| org.onebrc.again26.BRC077_LessAdditions | 3,818 ms | 4,214 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 4,101 ms | 4,095 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 3,586 ms | 3,931 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 3,678 ms | 4,184 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 3,437 ms | 4,126 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 3,880 ms | 3,688 ms |
| org.onebrc.again26.BRC083_MainLoop | 3,591 ms | 3,763 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 4,644 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 3,369 ms | 3,575 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 3,389 ms | 3,657 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 3,416 ms | 3,550 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 3,924 ms | 3,600 ms |
| org.onebrc.again26.BRC095_Hash | 3,228 ms | 3,594 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 3,607 ms | 3,814 ms |
| org.onebrc.again26.BRC097_EqualsCity | 3,302 ms | 3,497 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 3,243 ms | 3,525 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 3,431 ms | 3,475 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 3,517 ms | 4,147 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 3,179 ms | 3,321 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 3,184 ms | 3,493 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 3,145 ms | 3,391 ms |
| org.onebrc.again26.BRC107_ParseTemp | 3,389 ms | 3,430 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 3,418 ms | 3,511 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 3,400 ms | 3,547 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 2,413 ms | 2,492 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 2,436 ms | 2,485 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 3,794 ms | 2,584 ms |
| org.onebrc.again26.BRC125_Refined_121 | 2,391 ms | 2,506 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 2,494 ms | 2,568 ms |

