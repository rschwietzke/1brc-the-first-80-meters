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

### Warning

If you play with the large files, your hard disk or SSD or NVM might be the limiting factor. So, run it twice at least. The OS might cache it fully, if your RAM permits and the subsequent runs will be faster.

Be aware that laptops and virtual cloud machines are very unstable measurement platforms. If you want to do serious tuning, get yourself a hardware box you fully control.

## License

This code base is available under the Apache License, version 2 and also contains some code from https://github.com/gunnarmorling/1brc under the same license.


