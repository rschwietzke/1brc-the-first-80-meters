# Running Benchmarks

This document explains how to set up the configuration files and execute `benchmark-matrix.sh` to run the 1BRC performance benchmarks.

## Configuration Setup

Before running benchmarks, you need to define the test matrix by setting up the configuration file. An example file is provided in the root directory. Copy it and customize it to your environment.

```bash
cp benchmark.conf.example benchmark.conf
```

### `benchmark.conf` Structure
The configuration file uses an INI-style format with explicit sections for defining properties and a `[RUNS]` section for defining the execution matrix.

**Dimensions:**
- `[JDKS]`: Define the JDK versions you want to test (e.g., `JDK_21_OPEN = sdkman:21.0.2-open`).
- `[GC_OPTS]`: JVM Garbage Collector flags (e.g., `ZGC = -XX:+UseZGC`).
- `[VM_OPTS]`: JVM memory limits and other flags (e.g., `MEM_2G = -Xms2g -Xmx2g`).
- `[PROG_OPTS]`: Application arguments passed to the 1BRC program.
- `[TASKSETS]`: Arguments for the Linux `taskset` command for CPU pinning (e.g., `CORES_8 = -c 0-7`).
- `[DATASETS]`: Target data files (e.g., `DATASET_10M = 10000000.txt`).

**Combinable Parameters (Runs):**
Instead of a generic cross-product matrix, you explicitly define what combinations to run in the `[RUNS]` section.

```ini
[RUNS]
# Format: RUN_NAME = JDK_LABEL ; GC_LABEL ; VM_LABEL ; TASKSET_LABEL ; PROG_LABEL ; DATASET_LABEL ; CLASS_FILTER
NIGHTLY_ZGC = JDK_21_OPEN ; ZGC ; MEM_2G ; CORES_8 ; DEFAULT ; DATASET_10M ; *
DEBUG_PKG   = JDK_21_OPEN ; G1 ; DEFAULT ; ALL ; DEFAULT ; DATASET_10M ; org.rschwietzke.st.*
ALL_MATRIX  = * ; * ; * ; * ; * ; * ; *
```
- `RUN_NAME`: An internal identifier for the run. This name can be used in class annotations to restrict a class to this run.
- `CLASS_FILTER`: A specific class name (`BRC13_HardcodedSetST`), a package prefix (`org.rschwietzke.st.*`), or `*` for all classes.
- Wildcards (`*`): In any parameter slot, a `*` evaluates to all defined properties in that category.

### Class Annotations for Exclusions/Inclusions
You can restrict when a specific class is executed by adding special comments inside the `.java` file:
- `// RUN: NIGHTLY_ZGC` - The class will ONLY run when evaluating the `NIGHTLY_ZGC` run.
- `// -RUN: NIGHTLY_ZGC` - The class will skip the `NIGHTLY_ZGC` run.
- `// -JDK:21` - The class will skip any run that uses a JDK starting with `21`.

## How Configurations Evolve into Tests

### Generation Cycle
1. `ScriptGenerator` scans `1brc-implementations` for target classes.
2. It parses exclusions and inclusions (`// RUN:`, `// -RUN:`, `// -JDK:`, `// ignore`).
3. It iterates over the explicitly defined `[RUNS]` in `benchmark.conf`, expanding any wildcards.
4. It outputs a master execution script to `data/benchmark-history/<timestamp>-run.sh`.
5. The `benchmark-matrix.sh` script immediately runs this generated script.
6. The generated script calls `execute-scenario.sh` for each valid combination, progressively building `data/benchmark-history/<timestamp>.csv`. The CSV logs the raw parameters (GC, VM, PROG_OPTS, TASKSET) used.
7. Finally, `CsvMerger` and Report Writers run to aggregate historical data and generate interactive HTML/MD reports.

## Execution

Once your `.conf` files are set up, start the benchmark matrix:

```bash
./benchmark-matrix.sh
```

**Arguments:**
- `--dry-run`: Generates the execution script (`<timestamp>-run.sh`) but does not execute it. Useful for verifying your configuration matrix.
- `--jfr`: Enables Java Flight Recorder during benchmarking, saving `.jfr` profiles for each run.
