## Why

The project contains a growing library of BRC implementations, each optimized in different ways. Their performance is not just a function of the algorithm — JDK version and distribution (Temurin, GraalVM, OpenJDK), garbage collector choice (G1, ZGC, Shenandoah, Epsilon), and other runtime flags all have measurable impact on throughput and latency. Currently `benchmark.sh` captures performance for a single JDK and a single set of JVM options. There is no automated way to (1) sweep the same implementations across a matrix of JDKs and JVM configurations, or (2) track how those results change across time so that regressions introduced by a new JDK release or a code change are automatically detected.

## What Changes

- A thin **`benchmark-matrix.sh`** shell script whose only job is process orchestration: iterate JDK × profile combinations, set `JAVA_HOME`, invoke `benchmark.sh` per combination, then hand off collected temp CSVs to the Java tool.
- A **Java analysis tool** (`org.onebrc.benchmark.BenchmarkMatrix`) that handles everything analytical: loading JDK and profile configs, merging CSVs, collecting machine fingerprint, managing the history archive, detecting regressions, and generating Markdown reports.
- A **matrix-aware CSV** with `JDK`, `Profile`, and `RunTimestamp` columns prepended to the existing `benchmark.sh` schema.
- A **historical results store** in `data/benchmark-history/` — each run produces a paired `<timestamp>.csv` + `<timestamp>.md`, both committed to git as plain text.
- Documentation in `README.md` on usage.

## Capabilities

### New Capabilities

- `jdk-profile-matrix-runner`: Script that iterates over a matrix of JDK installations × named JVM profiles, running the full benchmark suite for each BRC implementation per combination, and collecting all results into a single timestamped CSV.
- `historical-results-store`: Automatically archives each run's CSV into a persistent directory (`data/benchmark-history/`) so results accumulate across runs and can be retrieved for comparison.
- `matrix-comparison-report`: Produces a Markdown comparison table showing performance across JDK+profile combinations within the current run and, optionally, against a previous archived run — flagging regressions in both dimensions.

### Modified Capabilities

*(none — no existing spec-level requirements are changing)*

## Impact

- **New file**: `benchmark-matrix.sh` at project root (wraps / reuses logic from `benchmark.sh`).
- **New file**: `benchmark-profiles.conf.example` — example JVM profile definitions.
- **New directory**: `data/benchmark-history/` — archived CSVs from past runs (one file per run, timestamped).
- **Modified file**: `README.md` with documentation of the new matrix benchmarking workflow.
- **No pom.xml changes required** — this is a shell-level harness over the existing compiled classes.
- **Dependencies**: Requires compiled `target/classes/`, `mvn` on path, and at least one valid JDK home.
