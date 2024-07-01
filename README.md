# 1BRC, the first 80% of the Way

## Presentation

This repository belongs to a [presentation given at Devoxx Poland 2024](https://training.xceptance.com/java/450-the-first-80p-of-1BRC-2024.html) and other conferences.

## How to Work With It

Please pay attention to the `org.rschwietzke.devoxxpl24` package. It contains all examples from the presentation.

### Build

You can of course build all with `mvn compile`.

### Create Data

You first need data before you can measure anything. The data files can be created like this:

#### 10 million rows
```
java -cp target/classes/ dev.morling.onebrc.CreateMeasurements 10000000 measurements-10m.txt
```

#### 1 billion rows
```
java -cp target/classes/ dev.morling.onebrc.CreateMeasurements 1000000000 measurements-1000m.txt
```

Attention: You need 13 GB of space for a 1 billion row file and some patience. The creation process is not performance optimized.

You have to specify the line count and the file you want to write it to. The original code by Gunnar Morling was slightly modified by me to allow for identical files when running the creation twice. This also makes all files identical in the sense of their first rows aka the first 10 million rows of a 100 million row file are similar to a 10 million row file.


So if you want to different files, change the hardcoded seed:

```
var r = RandomGeneratorFactory.of("Xoroshiro128PlusPlus").create(424242L);
```

### Measure

```
java  -cp target/classes/ org.rschwietzke.devoxxpl24.BRC13_HardcodedSetST measurements-10m.txt 0 1
```

You have to point to a file with the data, you have to specify the number of warmup rounds and the number of measurement rounds. While warmup and measurements are important for small data files, you can easily just say `0 1` when running the large 1 billion row file.

## Native Image

A very simple build file for a native image is provided that takes a compiled class and compiles it into a native image. It will create two versions: a standard optimized version and a PGO version.

For the PGO version, we compile an instrumented version first and run it, later we use the profiling output and compile again.

For both versions, we will run a standard Java execution first to enable the build agent of Graal native image to collect reflection data and write it down.

```
./create_native.sh BRC13_HardcodedSetST measurements-10m.txt 0 1
```

Specify the short class name for what you want to compile first (omit the package) and specify the test data and iterations as you normally do. This is used for the reflection data generation and the training runs for PGO.

After the build, you will find the binaries in the main directory and you can run them like that:

```
./org.rschwietzke.devoxxpl24.brc13_hardcodedsetst.best measurements-10m.txt 0 1
./org.rschwietzke.devoxxpl24.brc13_hardcodedsetst measurements-10m.txt 0 1
```

The version with `.best` is using a regular compile with optimization heuristics. The config has been borrowed from here: [prepare_thomaswue.sh](https://github.com/gunnarmorling/1brc/blob/main/prepare_thomaswue.sh).

## Warning

If you play with the large files, your hard disk or SSD or NVM might be the limiting factor. So, run it twice at least. The OS might cache it fully, if your RAM permits and the subsequent runs will be faster.

Be aware that laptops and virtual cloud machines are very unstable measurement platforms. If you want to do serious tuning, get yourself a hardware box you fully control.

## License

This code base is available under the Apache License, version 2 and also contains some code from https://github.com/gunnarmorling/1brc under the same license.


