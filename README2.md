what is
rules and example
best solution, shipilev
adjusted rules for learning
 warmup of desired
 rounds of desired
 startup not measured
 checksuming for checking
Pure (singe, multi) - 8 core
show the poor scale
basic analysis
Unroll
understand (profiler)

go the simple single threaded mode first for easier tuning

clean the code (04)
profile
 split is cpu and alloc intensive
 indexOf
 gained XXX

BRC05_ReplaceSplitST, split -> indexOf

BRC06_NewDoubleParsing (simple handcrafted version)

BRC07_NoCopyForDoubleST, no string for double

BRC08_GoIntST, int instead of double, we know the format!!!

BRC09_NoMergeST, get rid of merging and go classic

BRC10_MutateST, mutate to reduce operations

BRC11_SizedMapST, size hashmap

BRC12_NewMapST, simpler map

BRC13_HardcodedSetST, brute force map optimizations

BRC14_ReadBytesST, avoid string conversion when reading data
- coding mistake, called read too often

java -agentpath:/home/rschwietzke/bin/async-profiler/async-profiler-2.9-linux-x64/build/libasyncProfiler.so=start,flamegraph,file=f.html  -cp target/classes/ org.rschwietzke.st.Example08_SimpleMap  measurements-10m.txt

## DO 16C, 32 GB - Intel Dedicated

Seed 424242, JDK 21.0.3-tem

### Serial

#### 1000m 0 1
default, 206572
default, 200041
8g,

### Parallel
#### 16 cores, 0 1
java  -cp target/classes/ dev.morling.onebrc.BaselineParallel measurements-1000m.txt 0 1
98486
98486

java  -Djava.util.concurrent.ForkJoinPool.common.parallelism=8 -cp target/classes/ dev.morling.onebrc.BaselineParallel measurements-1000m.txt 0 1
96130

#### 8 cores, 0 1
default, 105961
8g, 107086

perf stat -e branches,branch-misses,cache-references,cache-misses,cycles,instructions,L1-dcache-loads,L1-dcache-load-misses,branch-loads,branch-load-misses,l2_cache_hits_from_dc_misses,l2_cache_misses_from_dc_misses