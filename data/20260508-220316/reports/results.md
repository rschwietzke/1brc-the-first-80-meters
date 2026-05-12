# 1BRC Benchmark Report

**Run Timestamp:** 20260508-220316

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 78 ms | 139 ms |
| org.onebrc.again26.BRC010_NoStream | 74 ms | 85 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 70 ms | 63 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 59 ms | 45 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 69 ms | 45 ms |
| org.onebrc.again26.BRC016_NoExtraString | 59 ms | 46 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 59 ms | 85 ms |
| org.onebrc.again26.BRC020_IntegerValue | 56 ms | 88 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 57 ms | 41 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 62 ms | 48 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 42 ms |
| org.onebrc.again26.BRC027_SizedMap | 77 ms | 78 ms |
| org.onebrc.again26.BRC030_OpenMap | 57 ms | 85 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 52 ms | 45 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 55 ms | 51 ms |
| org.onebrc.again26.BRC035_NoLambda | 57 ms | 44 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 58 ms | 43 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 53 ms | 41 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 52 ms | 42 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 63 ms | 42 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 68 ms | 42 ms |
| org.onebrc.again26.BRC045_KeepChars | 51 ms | 55 ms |
| org.onebrc.again26.BRC047_IntOnly | 53 ms | 46 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 95 ms | 49 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 93 ms | 48 ms |
| org.onebrc.again26.BRC050_LargerSet | 80 ms | 46 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 46 ms | 29 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 45 ms | 26 ms |
| org.onebrc.again26.BRC063_OneAddLess | 47 ms | 34 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 40 ms | 27 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 38 ms | 26 ms |
| org.onebrc.again26.BRC068_InlineParsing | 37 ms | 27 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 39 ms | 33 ms |
| org.onebrc.again26.BRC070_EqualsCity | 38 ms | 47 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 36 ms | 54 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 79 ms | 23 ms |
| org.onebrc.again26.BRC077_LessAdditions | 52 ms | 26 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 36 ms | 26 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 46 ms | 23 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 45 ms | 30 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 52 ms | 28 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 40 ms | 46 ms |
| org.onebrc.again26.BRC083_MainLoop | 60 ms | 52 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 67 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 51 ms | 25 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 51 ms | 25 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 45 ms | 27 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 38 ms | 27 ms |
| org.onebrc.again26.BRC095_Hash | 53 ms | 24 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 45 ms | 28 ms |
| org.onebrc.again26.BRC097_EqualsCity | 58 ms | 25 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 43 ms | 25 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 36 ms | 25 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 43 ms | 31 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 39 ms | 26 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 40 ms | 61 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 36 ms | 39 ms |
| org.onebrc.again26.BRC107_ParseTemp | 50 ms | 41 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 48 ms | 45 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 46 ms | 37 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 37 ms | 24 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 34 ms | 25 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 63 ms | 23 ms |
| org.onebrc.again26.BRC125_Refined_121 | 39 ms | 23 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 32 ms | 22 ms |

