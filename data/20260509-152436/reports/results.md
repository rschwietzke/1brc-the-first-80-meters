# 1BRC Benchmark Report

**Run Timestamp:** 20260509-152436

## Dataset: 10k

| Class | JDK_21_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 | JDK_25_OPEN | -XX:+UseG1GC | -Xms2g -Xmx2g | -wc 0 -mc 1 -t 8 | taskset -c 0-7 |
|---|---|---|
| org.onebrc.again26.BRC000_Empty | 0 ms | 0 ms |
| org.onebrc.again26.BRC001_Baseline | 23,387 ms | 25,669 ms |
| org.onebrc.again26.BRC010_NoStream | 25,410 ms | 27,901 ms |
| org.onebrc.again26.BRC012_SplitRemoved | 21,484 ms | 21,343 ms |
| org.onebrc.again26.BRC014_NewParseDouble | 16,026 ms | 17,205 ms |
| org.onebrc.again26.BRC015_ParseDoubleSimpler1 | 17,363 ms | 16,856 ms |
| org.onebrc.again26.BRC016_NoExtraString | 14,689 ms | 14,682 ms |
| org.onebrc.again26.BRC017_ParseDoubleSimpler2 | 14,490 ms | 14,594 ms |
| org.onebrc.again26.BRC020_IntegerValue | 14,342 ms | 14,865 ms |
| org.onebrc.again26.BRC021_LooplessParsing | 15,465 ms | 13,650 ms |
| org.onebrc.again26.BRC022_LooplessParsing2 | 13,911 ms | 13,918 ms |
| org.onebrc.again26.BRC025_MutateData | 0 ms | 12,471 ms |
| org.onebrc.again26.BRC027_SizedMap | 12,920 ms | 12,468 ms |
| org.onebrc.again26.BRC030_OpenMap | 12,802 ms | 13,044 ms |
| org.onebrc.again26.BRC031_OpenMapLessCasting | 12,214 ms | 12,167 ms |
| org.onebrc.again26.BRC033_SimplerLambda | 11,894 ms | 11,801 ms |
| org.onebrc.again26.BRC035_NoLambda | 11,699 ms | 11,417 ms |
| org.onebrc.again26.BRC036_TurnedIfs | 11,485 ms | 11,220 ms |
| org.onebrc.again26.BRC037_LessNullChecks | 11,406 ms | 11,031 ms |
| org.onebrc.again26.BRC040_ASetIsGoodEnough | 12,133 ms | 11,220 ms |
| org.onebrc.again26.BRC041_DirectUpdate | 13,099 ms | 11,162 ms |
| org.onebrc.again26.BRC042_NoSplittedString | 11,600 ms | 11,467 ms |
| org.onebrc.again26.BRC045_KeepChars | 11,097 ms | 11,471 ms |
| org.onebrc.again26.BRC047_IntOnly | 10,951 ms | 13,150 ms |
| org.onebrc.again26.BRC048_ImprovedStringHandling | 10,791 ms | 12,237 ms |
| org.onebrc.again26.BRC049_ManualMinMax | 13,239 ms | 11,117 ms |
| org.onebrc.again26.BRC050_LargerSet | 12,828 ms | 11,202 ms |
| org.onebrc.again26.BRC060_ReadingBytes | 15,188 ms | 16,283 ms |
| org.onebrc.again26.BRC061_RandomAccessFile | 5,990 ms | 5,699 ms |
| org.onebrc.again26.BRC063_OneAddLess | 5,670 ms | 5,666 ms |
| org.onebrc.again26.BRC065_OneLoopLess | 4,965 ms | 4,965 ms |
| org.onebrc.again26.BRC067_ParseDifferently | 5,263 ms | 5,302 ms |
| org.onebrc.again26.BRC068_InlineParsing | 5,311 ms | 4,877 ms |
| org.onebrc.again26.BRC069_InlineParsing_65 | 5,000 ms | 4,931 ms |
| org.onebrc.again26.BRC070_EqualsCity | 4,791 ms | 4,882 ms |
| org.onebrc.again26.BRC072_ReadDirectNotViaBuffer | 3,939 ms | 4,073 ms |
| org.onebrc.again26.BRC075_LargeByteBuffer | 3,883 ms | 3,788 ms |
| org.onebrc.again26.BRC077_LessAdditions | 3,785 ms | 5,236 ms |
| org.onebrc.again26.BRC078_CalculateEarlier | 3,985 ms | 3,843 ms |
| org.onebrc.again26.BRC079_RunWithoutByteBuffer | 3,493 ms | 3,624 ms |
| org.onebrc.again26.BRC080_JDKArrayUtils | 3,536 ms | 3,795 ms |
| org.onebrc.again26.BRC081_LoopUnroll | 3,446 ms | 3,654 ms |
| org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray | 3,734 ms | 3,565 ms |
| org.onebrc.again26.BRC083_MainLoop | 3,714 ms | 3,852 ms |
| org.onebrc.again26.BRC090_MemorySegment_VOID | 0 ms | 4,323 ms |
| org.onebrc.again26.BRC091_Reviewed83 | 3,230 ms | 3,422 ms |
| org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID | 3,209 ms | 3,323 ms |
| org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID | 3,259 ms | 3,336 ms |
| org.onebrc.again26.BRC094_Reviewed91 | 3,235 ms | 3,379 ms |
| org.onebrc.again26.BRC095_Hash | 3,205 ms | 3,397 ms |
| org.onebrc.again26.BRC096_ReadInt_VOID | 3,602 ms | 3,620 ms |
| org.onebrc.again26.BRC097_EqualsCity | 3,230 ms | 3,261 ms |
| org.onebrc.again26.BRC098_ParseTemperature_VOID | 4,180 ms | 4,496 ms |
| org.onebrc.again26.BRC099_ArrayAccess | 3,298 ms | 3,388 ms |
| org.onebrc.again26.BRC100_DirectTempWrite | 3,380 ms | 3,444 ms |
| org.onebrc.again26.BRC101_ParseFrom65_VOID | 4,771 ms | 3,294 ms |
| org.onebrc.again26.BRC105_ParseTemp_95 | 3,008 ms | 3,414 ms |
| org.onebrc.again26.BRC106_ParseTemp_105 | 3,131 ms | 3,359 ms |
| org.onebrc.again26.BRC107_ParseTemp | 3,211 ms | 3,389 ms |
| org.onebrc.again26.BRC110_EqualsCitySplit | 3,197 ms | 3,391 ms |
| org.onebrc.again26.BRC111_EqualsCitySplit_Reverse | 3,240 ms | 3,505 ms |
| org.onebrc.again26.BRC112_EqualsCityMismatch | 0 ms | 0 ms |
| org.onebrc.again26.BRC113_EqualsCityMismatchSimple | 0 ms | 0 ms |
| org.onebrc.again26.BRC120_OnlyHashing_From95 | 2,321 ms | 2,231 ms |
| org.onebrc.again26.BRC121_UpdatedLoops | 2,403 ms | 2,382 ms |
| org.onebrc.again26.BRC123_121plus105_VOID | 2,372 ms | 2,381 ms |
| org.onebrc.again26.BRC125_Refined_121 | 2,359 ms | 2,340 ms |
| org.onebrc.again26.BRC127_ParsingTempAI_VOID | 2,426 ms | 2,416 ms |

