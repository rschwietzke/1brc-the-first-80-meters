# 1BRC Benchmark Report

**Run Timestamp:** 20260508-213732

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 122 ms | 147 ms |
| org.onebrc.again26.BRC010_NoStream | 110 ms | 68 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 127 ms | 84 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 140 ms | 51 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 91 ms | 45 ms |
| org.onebrc.again26.BRC016_NoExtraString | 78 ms | 49 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 68 ms | 49 ms |
| org.onebrc.again26.BRC020_IntegerValue | 81 ms | 52 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 83 ms | 42 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 80 ms | 42 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 80 ms |
| org.onebrc.again26.BRC027_SizedMap | 67 ms | 81 ms |
| org.onebrc.again26.BRC030_OpenMap | 70 ms | 56 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 59 ms | 60 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 77 ms | 64 ms |
| org.onebrc.again26.BRC035_NoLambda | 57 ms | 53 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 53 ms | 46 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 53 ms | 64 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 77 ms | 76 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 53 ms | 44 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 51 ms | 52 ms |
| org.onebrc.again26.BRC045_KeepChars | 54 ms | 42 ms |
| org.onebrc.again26.BRC047_IntOnly | 50 ms | 40 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 59 ms | 40 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 58 ms | 38 ms |
| org.onebrc.again26.BRC050_LargerSet | 55 ms | 49 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 54 ms | 30 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 48 ms | 25 ms |
| org.onebrc.again26.BRC063_OneAddLess | 37 ms | 41 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 66 ms | 41 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 65 ms | 45 ms |
| org.onebrc.again26.BRC068_InlineParsing | 41 ms | 39 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 46 ms | 29 ms |
| org.onebrc.again26.BRC070_EqualsCity | 44 ms | 35 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 56 ms | 34 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 51 ms | 41 ms |
| org.onebrc.again26.BRC077_LessAdditions | 36 ms | 30 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 42 ms | 41 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 38 ms | 31 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 38 ms | 31 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 44 ms | 29 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 37 ms | 26 ms |
| org.onebrc.again26.BRC083_MainLoop | 38 ms | 47 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 91 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 45 ms | 26 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 42 ms | 30 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 42 ms | 36 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 42 ms | 30 ms |
| org.onebrc.again26.BRC095_Hash | 51 ms | 26 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 39 ms | 49 ms |
| org.onebrc.again26.BRC097_EqualsCity | 40 ms | 47 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 39 ms | 27 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 63 ms | 30 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 38 ms | 32 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 38 ms | 30 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 36 ms | 27 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 36 ms | 28 ms |
| org.onebrc.again26.BRC107_ParseTemp | 55 ms | 55 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 41 ms | 50 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 50 ms | 26 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 45 ms | 23 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 48 ms | 29 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 34 ms | 25 ms |
| org.onebrc.again26.BRC125_Refined_121 | 46 ms | 23 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 43 ms | 22 ms |

