# 1BRC Benchmark Report

**Run Timestamp:** 20260511-111208

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 122 ms | 103 ms |
| org.onebrc.again26.BRC010_NoStream | 83 ms | 91 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 97 ms | 95 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 62 ms | 53 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 83 ms | 79 ms |
| org.onebrc.again26.BRC016_NoExtraString | 71 ms | 86 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 62 ms | 50 ms |
| org.onebrc.again26.BRC020_IntegerValue | 86 ms | 45 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 98 ms | 54 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 76 ms | 52 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 54 ms |
| org.onebrc.again26.BRC027_SizedMap | 114 ms | 49 ms |
| org.onebrc.again26.BRC030_OpenMap | 78 ms | 48 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 97 ms | 48 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 89 ms | 58 ms |
| org.onebrc.again26.BRC035_NoLambda | 94 ms | 49 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 65 ms | 46 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 63 ms | 42 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 58 ms | 44 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 76 ms | 69 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 78 ms | 67 ms |
| org.onebrc.again26.BRC045_KeepChars | 59 ms | 42 ms |
| org.onebrc.again26.BRC047_IntOnly | 58 ms | 40 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 61 ms | 40 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 84 ms | 67 ms |
| org.onebrc.again26.BRC050_LargerSet | 59 ms | 70 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 53 ms | 35 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 46 ms | 25 ms |
| org.onebrc.again26.BRC063_OneAddLess | 39 ms | 26 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 69 ms | 25 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 67 ms | 31 ms |
| org.onebrc.again26.BRC068_InlineParsing | 43 ms | 28 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 41 ms | 45 ms |
| org.onebrc.again26.BRC070_EqualsCity | 42 ms | 58 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 38 ms | 26 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 38 ms | 24 ms |
| org.onebrc.again26.BRC077_LessAdditions | 39 ms | 26 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 39 ms | 29 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 70 ms | 24 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 52 ms | 34 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 46 ms | 34 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 63 ms | 27 ms |
| org.onebrc.again26.BRC083_MainLoop | 60 ms | 33 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 80 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 55 ms | 27 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 44 ms | 29 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 51 ms | 29 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 44 ms | 46 ms |
| org.onebrc.again26.BRC095_Hash | 46 ms | 27 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 47 ms | 32 ms |
| org.onebrc.again26.BRC097_EqualsCity | 50 ms | 32 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 45 ms | 29 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 47 ms | 28 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 46 ms | 32 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 53 ms | 30 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 67 ms | 32 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 49 ms | 32 ms |
| org.onebrc.again26.BRC107_ParseTemp | 45 ms | 30 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 43 ms | 32 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 77 ms | 31 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 47 ms | 26 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 43 ms | 30 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 45 ms | 38 ms |
| org.onebrc.again26.BRC125_Refined_121 | 41 ms | 27 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 51 ms | 30 ms |

