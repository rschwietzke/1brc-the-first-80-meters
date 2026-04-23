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

**Global Variables:**
You can define arbitrary variables at the top of the file (before any `[SECTION]` headers) and reuse them throughout your configuration using `${VAR_NAME}` or `$VAR_NAME`.

```ini
# --- Global Variables ---
MY_CLASSES = org.onebrc.*
# ------------------------

[RUN:DEFAULT]
CLASS_FILTER = ${MY_CLASSES}
```

**Combinable Parameters (Runs):**
Instead of a generic cross-product matrix, you explicitly define what combinations to run using `[RUN:NAME]` blocks.

```ini
[RUN:NIGHTLY_ZGC]
JDK_FILTER = JDK_21_OPEN
GC_FILTER = ZGC
VM_FILTER = MEM_2G
TASKSET_FILTER = CORES_8
PROG_FILTER = DEFAULT
DATA_FILTER = 10M
CLASS_FILTER = *

[RUN:DEBUG_PKG]
JDK_FILTER = JDK_21_OPEN
GC_FILTER = G1
DATA_FILTER = 10M
CLASS_FILTER = org.rschwietzke.st.*

[RUN:ALL_MATRIX]
# Empty run block inherits '*' for all filters
```
- `[RUN:NAME]`: The internal identifier for the run. This name can be used in class annotations to restrict a class to this run.
- `CLASS_FILTER`: A specific class name (`BRC13_HardcodedSetST`), a package prefix (`org.rschwietzke.st.*`), a regular expression (`.*FileOnly$`), or `*` for all classes.
- **Regex Support**: All `_FILTER` properties natively support Java Regular Expressions. If a filter string doesn't exactly match an available key or class, it is compiled as a regex. For example, `DATA_FILTER = .*k$` will match both `DATASET_1k` and `DATASET_10k`.
- Omitted filters automatically default to `*`.

### Class Annotations for Exclusions/Inclusions
You can restrict when a specific class is executed, or mark it with special badges (like `baseline` or `incomplete`), by adding special comments inside the `.java` file. 

For a complete reference of all supported annotations (e.g. `// RUN:`, `// ignore`, `// baseline`), please see the dedicated [Benchmark Source Annotations](benchmark-annotations.md) documentation.

For a comprehensive breakdown of what happens during a test execution and exactly what hardware/JVM metrics are captured, please see the [Benchmark Execution & Metrics](benchmark-metrics.md) documentation.

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

Once your `.conf` files are set up, you must provide an explicit command to start the benchmark matrix:

```bash
./benchmark-matrix.sh run
```

### Available Commands:
*   `run`: Generates the execution script and immediately executes the full matrix.
*   `dry-run`: Generates the dry-run console output to test your matrix filters, but stops before writing or executing anything.
*   `analyze [timestamp]`: Regenerates the HTML and Markdown reports. If no timestamp is provided, it scans the entire history folder and automatically regenerates all historical reports and global dashboards.
*   `compare <t1> <t2>`: Compares two distinct benchmark runs (To be implemented).

### Additional Arguments
When using `run`, you can also append additional flags to the end of the command:
*   `--jfr`: Enables Java Flight Recorder during benchmarking, saving `.jfr` profiles for each executed class configuration.
