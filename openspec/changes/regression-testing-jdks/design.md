## Context

`benchmark.sh` already provides a solid benchmarking harness for a single JDK: it compiles optional magic hints (`// exec`, `// ignore`), runs each BRC class N times, takes the median, runs `perf stat`, and writes to a structured CSV. That output feeds the presentations and comparison tables.

The missing dimension is the **JDK × JVM profile matrix**: same implementations, different runtimes. Key variables of interest:
- **JDK distribution & version**: Temurin 21, Temurin 25, GraalVM CE, etc. — JIT quality, intrinsics, and internal APIs differ.
- **GC strategy**: Epsilon (no GC, lowest pause/overhead), G1 (default), ZGC (low-latency), Shenandoah, Serial.
- **Heap size**: LOW_MEM vs HIGH_MEM already exist in `benchmark.sh`; this matrix extends that concept.
- **Optional flags**: `--enable-preview`, `--add-exports`, experimental VM options.

The new tooling must produce output that is **directly comparable** to `benchmark.sh` results — same CSV schema with additional JDK/Profile columns — so that all results can be imported into the same analysis/presentation toolchain.

## Goals / Non-Goals

**Goals:**
- A thin `benchmark-matrix.sh` launcher (~10 lines) that invokes the Java tool; Java handles everything else.
- A **Java tool** (`BenchmarkMatrix`) that generates the run script, merges CSVs, manages history, detects regressions, and generates HTML/Markdown reports.
- **SDKman integration**: JDKs declared by SDKman identifier in config; a setup phase installs missing ones. Supports fresh/ephemeral machines.
- **Per-class exclusions**: source file header comments (`// -JDK:21`) declare what combinations a class *cannot* support; the `ScriptGenerator` only generates valid combinations.
- **Multiple datasets**: benchmark runs against multiple named data files (e.g., 100M rows, 1B rows); dataset is tracked as a dimension.
- Output CSV with new leading columns: `JDK`, `SCENARIO`, `DATA`, `RunTimestamp`.
- Paired per-run archive: `<timestamp>.csv` + `<timestamp>.html` + `<timestamp>.md` + `<timestamp>-run.sh`, all committed to git.
- **New execution script**: A dedicated `execute-scenario.sh` to wrap `perf stat` around the JVM, leaving the old `benchmark.sh` untouched.
- **Extensive Documentation**: Dedicated guides for Setup, Running, and Evaluation.

**Non-Goals:**
- Replacing `benchmark.sh` (it remains the single-JDK, single-dataset tool).
- Changing the CSV schema used by `benchmark.sh` itself.
- JUnit test correctness checking.

## Decisions

### Java generates the benchmark run script from configs and templates

**Decision**: Instead of a hand-written shell script that interprets configs at runtime, the Java tool generates a **concrete, flat shell script** for each run — fully expanded with actual JDK paths, actual profile flags, and actual `benchmark.sh` invocations. A minimal launcher (`benchmark-matrix.sh`, ~10 lines) is the only hand-written shell: it invokes the Java generator, then executes the generated script (unless `--dry-run` is passed).

The generated script is saved to `data/benchmark-history/<timestamp>-run.sh` alongside the CSV and MD, so there is a complete record of exactly what was executed.

**Benefits**:
- **Auditability**: generated script is human-readable and inspectable before execution; reviewer can verify exactly what will run.
- **Dry run**: `--dry-run` generates the script without executing it — useful for validation and CI preview.
- **Git record**: the generated script committed alongside the results documents the exact invocation that produced them.
- **No shell config parsing**: the launcher never needs to read config files; Java owns the full config → script pipeline.
- **Simplest possible launcher**: `benchmark-matrix.sh` is ~10 lines and unlikely to ever need changing.

**Template approach**: Java uses simple string templating (no external dependency needed — `String.format` or a small `StringBuilder`-based template is sufficient) to emit the shell script. The template produces one `benchmark.sh` invocation block per JDK × profile combination.

**Alternative considered**: Static hand-written shell script that reads configs at runtime — rejected because it requires shell config parsing, is harder to audit, and can't produce a dry-run preview.

### Java tool handles all analysis, reporting, and script generation

**Decision**: A dedicated Java main class (`org.onebrc.benchmark.BenchmarkMatrix`) is the primary tool. Its responsibilities:
- Load `benchmark-jdks.conf` and `benchmark-profiles.conf`
- **Generate the concrete run shell script** from a template (the main pre-run action)
- Parse and merge per-combination temp CSVs into the master result (post-run analysis)
- Collect machine fingerprint (via `ProcessBuilder` / `/proc` files)
- Manage the `data/benchmark-history/` archive (read, write, list)
- Compare current results against a historical archive (sparse column union, delta calculation, machine fingerprint diff)
- Generate the Markdown evaluation report
- Handle `--list-runs` and `--compare-run` flags as standalone operations

**Rationale**: Keeps all complex logic in a language the project already knows, with access to proper data structures, unit-testable components, and standard libraries. The Java tool can be compiled and run by any of the tested JDKs.

### Per-JDK recompilation

**Decision**: The generated execution script will group all benchmark runs by JDK. At the start of each JDK block, the script will set `JAVA_HOME` to that JDK and run `mvn clean compile` before executing the benchmarks.

**Rationale**: Because the project explores cutting-edge features (e.g., JDK 25 previews), bytecode generated by JDK 25 cannot be executed by JDK 21. Even for compatible bytecode, compiling with the same JDK that executes it ensures accurate performance measurements for that specific JDK's compiler optimizations.

### Environmental Prevalidation

**Decision**: A `prevalidate.sh` script will be executed prior to any matrix generation or benchmarking to ensure all required OS-level tools (`perf`, `taskset`, `awk`, `mvn`, `sdk` if applicable) are available and system parameters are correct.

**Rationale**: The benchmarking suite is highly dependent on Linux performance tooling. Failing fast before starting a multi-hour matrix run saves significant time and prevents incomplete data collection.

### Maven Multi-Module Architecture

**Decision**: The repository will be refactored into a Maven multi-module project to isolate dependencies.
- `1brc-implementations`: Contains all the 1BRC classes. Zero external dependencies. Recompiled iteratively for each target JDK (e.g. 21, 25) during the benchmark run using `-pl 1brc-implementations`.
- `benchmark-harness`: Contains the matrix generator, CSV merger, and reporter tools. Strictly compiled and executed using **JDK 25** (the latest) to support modern APIs like JFR parsing.

**Rationale**: Adding dependencies like templating engines (e.g., FreeMarker or Mustache) to the main project would pollute the benchmark classpath and violate the spirit of the challenge. Furthermore, by restricting the reporting/harness module to JDK 25, we can use the latest Java features for tooling without worrying about backwards compatibility, while perfectly simulating legacy runtimes (e.g., JDK 21) exclusively for the implementation code.

### Template-Driven Reporting

**Decision**: The HTML and Markdown reports will be generated using a formal templating engine (e.g., FreeMarker or Mustache) instead of manual Java string building.

**Rationale**: Simplifies generating complex HTML structures, dynamic ECharts JSON, and Markdown tables. Templates (e.g., `report.ftl`) can be edited easily without recompiling the Java logic.

### Multi-KPI Anomaly Detection & Highlights Summary

**Decision**: The analysis phase will calculate anomalies not just for **Runtime**, but across all hardware KPIs collected via `perf` (e.g., Instructions, Cycles, IPC, Branch Misses). These will be presented in a "Highlights / Anomalies Dashboard".

**Rationale**: A class might have the same runtime but suddenly execute 15% more instructions (bloat) or suffer an IPC drop (worse pipeline utilization) under a new JDK. By analyzing multiple KPIs, the engine can flag structural performance regressions before they visibly impact wall-clock runtime.

### JFR Recording & Automated Evaluation

**Decision**: The tool will support an optional `--jfr` flag. When enabled:
1. `execute-scenario.sh` will inject `-XX:StartFlightRecording=filename=...,settings=profile` to record a `.jfr` file for each run with detailed profiling overhead.
2. The Java tool will use the `jdk.jfr.consumer` API to automatically parse these `.jfr` files post-run, extracting deep JVM metrics (e.g., total GC pause time, allocation rate, JIT compilation time) and appending them to the CSV and Anomaly Dashboard.
3. The raw `.jfr` files are stored in `data/benchmark-jfr/` (which is added to `.gitignore`) so they can be manually inspected in Java Mission Control (JMC) without bloating the git repo.

**Rationale**: `perf` tells us *what* the hardware did (IPC, instructions), but JFR tells us *why* the JVM did it. Automatically parsing JFR metrics allows the tool to highlight internal shifts (like increased GC pressure or longer JIT warmup) that explain performance anomalies.

**JFR Observer Effect**: Because the matrix produces comparable CSVs whether JFR is enabled or not, a developer can compare a `--jfr` run against a standard baseline run to precisely quantify the overhead introduced by the `settings=profile` recording.

### File structure and configuration formats

**Config file format**: Both `benchmark-jdks.conf` and `benchmark-profiles.conf` use a simple `KEY=VALUE` (Java Properties-compatible) or similar line format readable by both shell `source` and Java `Properties`/custom parser. The shell script needs only basic reading; Java does the full parsing.

### Three orthogonal benchmark dimensions (preventing combination explosion)

**Decision**: Multiplying too many independent dimensions leads to an unmanageable number of test runs. To prevent combination explosion, tightly coupled variables (JVM tuning, CPU pinning, application args) are grouped together into a `SCENARIO`. The matrix has exactly three dimensions:

| Dimension | Config file | Example values |
|-----------|-------------|----------------|
| `JDK` | `benchmark-jdks.conf` | `TEMURIN21`, `TEMURIN25`, `GRAAL25` |
| `SCENARIO` | `benchmark-scenarios.conf` | `DEFAULT`, `8_CORES`, `HIGH_MEM` |
| `DATA` | `benchmark-datasets.conf` | `100M`, `1000M` |

A `SCENARIO` definition in the config file encapsulates all execution parameters:
`SCENARIO_8_CORES="JVM_OPTS=-Xmx8g -XX:+UseZGC; TASKSET=-c 0-7; ARGS=--threads 8"`

The full matrix is the cross-product: **JDK × SCENARIO × DATA × Class**, filtered by per-class exclusions.

**Rationale**: Memory tuning, CPU allocation, and application threading are rarely independent. A ZGC high-memory run usually pairs with all cores, while a low-memory run might restrict cores. Grouping them into explicit `SCENARIO` definitions prevents testing thousands of invalid or nonsensical combinations.

**Execution Script (`execute-scenario.sh`)**: Instead of hacking `MATRIX_MODE` into the existing `benchmark.sh` (which carries legacy rules and compatibility cruft), we will create a pristine, dedicated script: `execute-scenario.sh`. It will take environment variables (`JVM_OPTS`, `TASKSET`, `DATA`, `CLASS`) and cleanly execute the JVM wrapped in `perf stat` to extract metrics.

### Source Header Annotations (Exclusions & Status)

**Decision**: Each class defaults to running across **all** dimension combinations. A class annotates the dimension values it **cannot** support using a negative syntax in the source file header. Additionally, special statuses (like skipping the official SHA-512 hash) can be marked here:

```java
// -JDK:21         ← skip for any JDK whose detected major version is 21
// -JDK:GRAAL25    ← skip for the JDK labeled GRAAL25 (label match)
// -SCENARIO:8_CORES ← skip when running the 8_CORES scenario
// -DATA:1000M     ← too slow for the large dataset, omit it
// status: incomplete ← marks class as special/incomplete (e.g., skips SHA-512 hash)
// status: baseline   ← marks class as the gold standard "leader" that defines the correct SHA-512 hash
```

**Rationale**: Maintaining a central "inclusion matrix" config file is a maintenance nightmare. By using negative annotations (defaulting to "run on everything"), the system is fully decoupled. The `status: incomplete` marker is critical for the HTML report so readers know if a class is artificially fast because it skips validation. The `status: baseline` marker ensures we can visually anchor performance and correctness comparisons against the known-good implementation.

Matching rules:
- For `JDK`: match against the label (e.g., `TEMURIN21`) **or** the detected major version number (e.g., `21`).
- For `SCENARIO`, `DATA`: match against the label exactly.
- Multiple exclusion annotations on the same class are cumulative (AND logic): a combination is skipped if **any** of its dimension values is excluded by the class.
- `// ignore` (existing) still excludes the class from all combinations entirely.

**Rationale**: The exclusion model is more natural and maintainable than an inclusion list. Most classes run with most configs — defaulting to “run everything” and annotating exceptions keeps the source files clean. Adding a new JDK, PROFILE, or dataset to the config automatically includes all non-excluding classes with zero annotation changes. The JDK version-number match ensures exclusions remain valid even when JDK labels change (e.g., `TEMURIN21` → `CORRETTO21`).

### Historical results stored as timestamped paired files

**Decision**: Each completed run is archived as three files: `data/benchmark-history/YYYYMMDD_HHMMSS.csv`, `YYYYMMDD_HHMMSS.html`, and `YYYYMMDD_HHMMSS.md`. All are committed to git. The CSV includes a machine fingerprint block in comment lines (`#`). The Java tool writes all files unconditionally after a successful run.

**Machine info collected** (via `ProcessBuilder` or reading system files):
- `hostname`
- Kernel version (`uname -r` or `/proc/version`)
- OS name and version (`/etc/os-release`)
- CPU model, core count, max MHz (`/proc/cpuinfo` or `lscpu`)
- Total memory (`/proc/meminfo`)

**Rationale**: The machine is not guaranteed to be preserved between runs. A full fingerprint allows the Java tool (and human readers) to determine whether a cross-run delta is a genuine performance change or a hardware/OS change. Git-native plain text means the full history is readable without any tooling.

### Cross-run deltas are qualified by machine match

**Decision**: The Java tool compares the current machine fingerprint against the historical archive's fingerprint. Same machine (hostname + kernel + CPU all match) → deltas labeled as regressions ⚠️ / improvements 🚀. Different machine → deltas labeled as trends 📈/📉 with a prominent disclaimer.

**Rationale**: Cross-run numbers are less reliable than within-run numbers because the machine may have changed. The tool must not pretend otherwise.

### Interactive HTML Reporting with Charts & Navigation

**Decision**: The analysis phase will produce a standalone, single-file HTML report (`YYYYMMDD_HHMMSS.html`) instead of plain Markdown.

**Rationale**: A flat Markdown file becomes unreadable when hundreds of classes are tested across multiple datasets. The HTML report will feature:
1. **Sidebar Navigation**: To quickly jump between datasets and scenarios.
2. **Interactive Data Tables**: Sortable columns and baseline trend indicators (🚀/🐌).
3. **Data Visualization**: Integrated charting (e.g., Apache ECharts via CDN) to visually compare JDKs and Scenarios side-by-side for each class.

### SDKman integration for JDK lifecycle management

**Decision**: `benchmark-jdks.conf` supports two formats per entry:
- A local path: `JDK_TEMURIN21=/usr/lib/jvm/temurin-21`
- An SDKman identifier: `JDK_TEMURIN21=sdkman:21.0.3-tem`

When the Java tool encounters an SDKman identifier, it resolves the expected installation path (`$HOME/.sdkman/candidates/java/<version>`). If the path does not exist, it emits a `sdk install java <version>` call into the generated script's pre-flight section. The generated script sources SDKman (`$HOME/.sdkman/bin/sdkman-init.sh`) and performs installs before the first benchmark block.

**Rationale**: Machines may be fresh/empty. SDKman is the standard JDK version manager on Linux; integrating it removes the manual step of installing each JDK before running. The install commands are emitted into the generated script (not hidden in Java), so they are auditable and reproducible.

**Alternative considered**: Requiring all JDKs to be pre-installed — rejected for fresh-machine use cases.

---



**Rationale**: Running different data sizes reveals scalability behaviour — an implementation may be fastest on 100M but not on 1B rows. Dataset is a first-class dimension, not just a command-line argument to vary manually.

**Alternative considered**: Passing dataset as a positional argument — rejected because it would require re-running the entire matrix per dataset rather than integrating it into a single run.

## Testing Strategy

**Decision**: Test-Driven Development (TDD) will be strictly enforced for all testable Java components in the `benchmark-harness` (e.g., `SourceAnnotationParser`, `ScriptGenerator`, `CsvMerger`). Furthermore, `BRC00` (the base framework implementation) will be used as a stable baseline for end-to-end integration testing to check basic framework quality.

**Rationale**: The benchmarking harness must be mathematically perfect. A bug in the generator could silently skip hundreds of tests or misalign CSV headers. TDD ensures the parsers and generators are solid, while `BRC00` provides a fast, predictable payload to verify the end-to-end Bash execution loop.

## Risks / Trade-offs

- **`perf stat` may require root or perf_event_paranoid tuning** — inherit same failure handling from `benchmark.sh`.
- **Preview flags tied to specific JDK version**: `--enable-preview` tied to JDK major version. → Mitigation: `mvn compile` per JDK in generated script; `COMPILE_FAILURE` recorded for skipped JDKs.
- **Long total runtime**: N_jdks × N_profiles × N_datasets × N_classes × ROUNDS — significantly larger than before. → Mitigation: `--rounds` configurable (default 3); `--dry-run` to preview combination count before committing; per-class profile filtering reduces actual count.
- **SDKman not present on machine**: If SDKman itself is not installed, the generated script's install step will fail. → Mitigation: detect SDKman presence during generation; warn if absent; document installation as a one-time prerequisite.
- **Java tool must be compiled before use**: needs `target/classes`. → Mitigation: launcher checks for `target/classes` and aborts with a clear message if missing.
- **Historical file accumulation**: → Mitigation: document manual cleanup; do not auto-delete.
- **Cross-run comparisons on different hardware are inherently less reliable**: → Mitigation: full machine fingerprint per run; warn when fingerprints differ; label cross-run deltas as trend, not regression.
