## 0. Maven Multi-Module Refactoring

- [ ] 0.1 Create a parent `pom.xml` at the root. Set compiler target to JDK 25.
- [ ] 0.2 Move existing 1BRC code into a new `1brc-implementations` module; ensure its `pom.xml` has zero external dependencies.
- [ ] 0.3 Refactor existing implementation packages from `org.rschwietzke.*` to `org.onebrc.*`. This sets us up to support other topics/concepts (e.g. `org.onebrc.parallel`, `org.onebrc.memory`) cleanly in the future.
- [ ] 0.4 Create a new `benchmark-harness` module for the Java tooling. Add dependencies for a templating engine (e.g., `freemarker` or `mustache`) and any CSV parsers. This module strictly requires **JDK 25** to run.
- [ ] 0.5 Update compilation scripts to target the multi-module structure appropriately.

## 1. Shell Launcher & Execution

- [ ] 1.1 Create `prevalidate.sh` to verify system dependencies: `perf`, `taskset`, `awk`, `mvn`, and that `target/classes` exists. Exit with 1 if missing.
- [ ] 1.2 Create `execute-scenario.sh`: A clean bash script (no legacy 1BRC cruft) that takes `JVM_OPTS`, `TASKSET`, `DATA`, and `CLASS` environment variables, executes the JVM wrapped in `perf stat`, and outputs the raw CSV line.
- [ ] 1.3 Create `benchmark-matrix.sh` (~12 lines): call `./prevalidate.sh`, invoke `BenchmarkMatrix generate [args...]`, then execute the generated script (unless `--dry-run` was passed)
- [ ] 1.4 Make `prevalidate.sh`, `execute-scenario.sh`, and `benchmark-matrix.sh` executable (`chmod +x`)
- [ ] 1.5 The launcher passes all arguments through to the Java tool unchanged.

## 2. Java Tool — Script Generation

- [ ] 2.1 Implement `SourceAnnotationParser`: scan all target `.java` files for `// -DIMENSION:value` exclusions, `// ignore`, and status markers like `// status: incomplete` and `// status: baseline`. Build a `ClassConfig` per file.
- [ ] 2.2 Implement `ScriptGenerator`: compute full cross-product JDK × SCENARIO × DATA; filter out combinations matching exclusions; group valid runs by JDK
- [ ] 2.3 Generated script structure: For each JDK block, set `JAVA_HOME=$JDK_PATH`, run `mvn clean compile -pl 1brc-implementations -Dmaven.compiler.release=$JDK_MAJOR_VERSION`, then for each valid combination invoke `execute-scenario.sh`. If `--jfr` is passed, inject `-XX:StartFlightRecording=filename=...,settings=profile` into `JVM_OPTS`.
- [ ] 2.4 JDK exclusion matching: for `// -JDK:21`, match against the JDK label AND the detected major version number so version-based exclusions work across distributions
- [ ] 2.5 SDKman pre-flight in generated script: emit `source $HOME/.sdkman/bin/sdkman-init.sh` and `sdk install java <version>` for any `sdkman:` JDK entries that are not yet installed
- [ ] 2.6 Print combination summary at generation time: `N classes × M JDKs × S SCENARIOs × D DATA = K blocks`
- [ ] 2.7 Implement `--dry-run`: generate script and print summary, do not execute
- [ ] 2.8 Save generated script to `data/benchmark-history/<timestamp>-run.sh`; make it executable
- [ ] 2.9 After all `benchmark.sh` runs complete, invoke analysis phase with temp CSV paths and their dimension metadata (JDK, SCENARIO, DATA)

## 3. Java Tool — Project Structure

- [ ] 3.1 Create package `org.onebrc.benchmark` under `src/main/java/` in the `benchmark-harness` module.
- [ ] 3.2 Create main class `BenchmarkMatrix.java` with subcommands: `generate` (config → script), `analyze` (temp CSVs → archive + report), `list-runs`, `compare-run`
- [ ] 3.3 Ensure the Java tool compiles as part of normal `mvn compile` (no separate module needed)

## 4. Java Tool — Config Loading

- [ ] 4.1 Implement `JdkConfig` reader: parse `benchmark-jdks.conf`; support local path and `sdkman:<version>` format
- [ ] 4.2 Implement `ScenarioConfig` reader: parse `benchmark-scenarios.conf` (`SCENARIO_<LABEL>="JVM_OPTS=...; TASKSET=...; ARGS=..."`)
- [ ] 4.3 Implement `DatasetConfig` reader: parse `benchmark-datasets.conf` (`DATASET_<LABEL>=<path>`)
- [ ] 4.4 Detect each JDK’s major version by running `$JDK/bin/java -version`; store alongside label for use in exclusion matching

## 5. Java Tool — CSV Merging

- [ ] 5.1 Implement `CsvMerger`: read each temp CSV, strip header, prepend `JDK,SCENARIO,DATA,RunTimestamp` columns using the combination’s dimension labels
- [ ] 5.2 Implement `JfrConsumer`: If `--jfr` is enabled, use `jdk.jfr.consumer` to parse the generated `.jfr` files, extract total GC pause time, allocation rate, and JIT compilation time.
- [ ] 5.3 Write the merged master CSV with header: `JDK,SCENARIO,DATA,RunTimestamp,` + original `perf` columns + new JFR columns (if applicable)
- [ ] 5.4 Validate each temp CSV header matches the expected schema; log a warning and skip on mismatch

## 6. Java Tool — Machine Fingerprint

- [ ] 6.1 Implement `MachineFingerprint` collector using `ProcessBuilder` and/or reading `/proc/` files: hostname, kernel (`uname -r` or `/proc/version`), OS (`/etc/os-release`), CPU model + cores + max MHz (`/proc/cpuinfo`), total memory (`/proc/meminfo`)
- [ ] 6.2 Fall back gracefully if any individual field is unavailable (log a note, continue)
- [ ] 6.3 Implement fingerprint comparison: given two `MachineFingerprint` objects, return a `MachineMatch` enum (`SAME`, `DIFFERENT`) based on hostname + kernel + CPU model

## 7. Java Tool — Historical Archive Management

- [ ] 7.1 Create `data/benchmark-history/` and `data/benchmark-jfr/` if absent. Add `data/benchmark-jfr/` to `.gitignore`.
- [ ] 7.2 Archive the four paired files per run: `<timestamp>.csv`, `<timestamp>.html`, `<timestamp>.md`, and `<timestamp>-run.sh`
- [ ] 7.3 If `--jfr` is enabled, move all `.jfr` files to `data/benchmark-jfr/` indexed by timestamp.
- [ ] 7.3 Implement `--list-runs`: scan `data/benchmark-history/` for `*-run.sh` files, sort newest first, show presence of paired CSV/HTML/MD, then exit
- [ ] 7.4 Implement `--compare-run <timestamp>`: load the matching `.csv` archive for fingerprint and data comparison
- [ ] 7.5 Auto-select most recent prior archive when `--compare-run` is not specified; skip cross-run comparison if none exists

## 8. Java Tool — Regression Detection and Report Generation

- [ ] 8.1 Implement `ResultMatrix`: in-memory structure keyed by `(JDK, SCENARIO, DATA, className) → medianRuntimeMs`; built from merged master CSV
- [ ] 8.2 Group results by `DATA` for reporting: each dataset gets its own within-run comparison table
- [ ] 8.3 Within-run comparison per dataset: columns are JDK × SCENARIO combinations; if `--baseline` specified, compute deltas and flag regressions ⚠️ / improvements ✅
- [ ] 8.4 Cross-run comparison: build dimension-union of current + historical matrices per dataset; label new combinations `(new)`, missing `(retired)`, sparse cells `—`
- [ ] 8.5 Apply machine match qualification: same machine → regression/improvement labels; different machine → trend labels 📈/📉 with disclaimer
- [ ] 8.6 Implement `HtmlReportWriter`: use a templating engine (e.g., FreeMarker) to produce a standalone HTML file containing:
  - **Executive Anomaly Dashboard**: Compute and display the top 3 biggest regressions/improvements not just for Runtime, but across all hardware KPIs (Instructions, IPC, Branch Misses).
  - Sticky sidebar navigation between dataset/scenario views
  - Data tables showing runtime with emojis (🚀/🐌) for cross-run or baseline deltas
  - Visual Badges: Clearly mark classes with `status: incomplete` (⚠️) or `status: baseline` (👑).
  - Integrated Apache ECharts (via CDN) to visually compare JDKs/Scenarios for each class
- [ ] 8.7 Implement `MarkdownReportWriter`: use a templating engine to produce a plain `.md` file with condensed tables, intended specifically for automated AI ingestion/analysis
- [ ] 8.8 Write reports unconditionally to `data/benchmark-history/`; copy to `--output-report` if specified

## 9. Config File Examples and Documentation

- [ ] 9.1 Create `benchmark-jdks.conf.example` with local paths and SDKman identifiers
- [ ] 9.2 Create `benchmark-scenarios.conf.example` defining typical groupings (e.g., `ZGC_8_CORES`)
- [ ] 9.3 Create `benchmark-datasets.conf.example` defining standard test paths
- [ ] 9.4 Create `docs/benchmark-setup.md`: extensive documentation on how to provision the environment and build the multi-module project
- [ ] 9.5 Create `docs/benchmark-running.md`: extensive documentation on how to execute `benchmark-matrix.sh` and what the generation cycle does
- [ ] 9.6 Create `docs/benchmark-evaluation.md`: extensive documentation explaining how to read the HTML/Markdown reports, interpret the ECharts, and understand the multi-KPI Anomaly Dashboard (IPC, Instructions, Branch Misses vs Runtime)
- [ ] 9.7 Verify `data/benchmark-history/` is not in `.gitignore`

## 10. Integration Testing & TDD

- [ ] 10.1 Enforce TDD for `SourceAnnotationParser` (verify it correctly extracts exclusions and status markers).
- [ ] 10.2 Enforce TDD for `ScriptGenerator` (verify JDK grouping, `mvn -pl` injection, and exclusion filtering).
- [ ] 10.3 Enforce TDD for `CsvMerger` (verify matrix logic).
- [ ] 10.4 Run an end-to-end integration test of `benchmark-matrix.sh` using the `BRC00` base framework class to verify the complete loop (generation -> execution -> JFR parsing -> HTML reporting).
- [ ] 10.6 Run a second time; confirm cross-run delta appears and sparse handling works for any new/retired dimension values
