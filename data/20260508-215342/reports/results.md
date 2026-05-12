# 1BRC Benchmark Report

**Run Timestamp:** 20260508-215342

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 86 ms | 92 ms |
| org.onebrc.again26.BRC010_NoStream | 81 ms | 83 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 66 ms | 77 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 54 ms | 59 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 56 ms | 57 ms |
| org.onebrc.again26.BRC016_NoExtraString | 53 ms | 86 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 74 ms | 69 ms |
| org.onebrc.again26.BRC020_IntegerValue | 59 ms | 108 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 53 ms | 52 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 52 ms | 62 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 54 ms |
| org.onebrc.again26.BRC027_SizedMap | 61 ms | 54 ms |
| org.onebrc.again26.BRC030_OpenMap | 53 ms | 74 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 65 ms | 44 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 57 ms | 89 ms |
| org.onebrc.again26.BRC035_NoLambda | 51 ms | 74 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 91 ms | 44 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 91 ms | 49 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 47 ms | 40 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 47 ms | 53 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 50 ms | 48 ms |
| org.onebrc.again26.BRC045_KeepChars | 59 ms | 66 ms |
| org.onebrc.again26.BRC047_IntOnly | 53 ms | 50 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 48 ms | 66 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 48 ms | 62 ms |
| org.onebrc.again26.BRC050_LargerSet | 50 ms | 85 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 48 ms | 50 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 35 ms | 44 ms |
| org.onebrc.again26.BRC063_OneAddLess | 37 ms | 27 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 39 ms | 28 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 37 ms | 35 ms |
| org.onebrc.again26.BRC068_InlineParsing | 41 ms | 41 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 36 ms | 29 ms |
| org.onebrc.again26.BRC070_EqualsCity | 41 ms | 34 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 36 ms | 35 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 37 ms | 27 ms |
| org.onebrc.again26.BRC077_LessAdditions | 42 ms | 44 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 41 ms | 49 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 36 ms | 25 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 41 ms | 35 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 36 ms | 33 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 51 ms | 32 ms |
| org.onebrc.again26.BRC083_MainLoop | 60 ms | 33 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 88 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 40 ms | 33 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 39 ms | 35 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 50 ms | 33 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 47 ms | 30 ms |
| org.onebrc.again26.BRC095_Hash | 46 ms | 31 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 48 ms | 36 ms |
| org.onebrc.again26.BRC097_EqualsCity | 60 ms | 33 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 38 ms | 31 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 46 ms | 29 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 34 ms | 39 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 33 ms | 30 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 34 ms | 47 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 42 ms | 31 ms |
| org.onebrc.again26.BRC107_ParseTemp | 35 ms | 28 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 34 ms | 26 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 35 ms | 27 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 34 ms | 27 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 42 ms | 26 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 36 ms | 28 ms |
| org.onebrc.again26.BRC125_Refined_121 | 63 ms | 31 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 46 ms | 32 ms |

