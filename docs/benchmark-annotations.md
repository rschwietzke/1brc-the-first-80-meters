# Benchmark Source Annotations

The 1BRC benchmarking harness parses special comments directly from your `.java` source files to control execution and reporting. 

These "Source Annotations" can be placed anywhere in the file as standard single-line `//` comments.

## 1. Execution Control Filters
These annotations define exactly when the harness is allowed to run the class. They override the filters defined in `benchmark.conf`.

*   `// RUN: RUN_NAME` — **Whitelist**. The class will *only* execute for the run named `RUN_NAME` (e.g., `// RUN: NIGHTLY_ZGC`). If a class has any whitelist annotations, it automatically skips all other runs.
*   `// -RUN: RUN_NAME` (or `// exclude-run:`) — **Blacklist**. The class will specifically skip the run named `RUN_NAME`.
*   `// -JDK: VERSION` (or `// exclude-jdk:`) — Skips execution if the JDK label or version starts with this string (e.g., `// -JDK: 21` or `// -JDK: JDK_21_OPEN`).
*   `// -DATA: LABEL` (or `// exclude-data:`) — Skips execution for a specific dataset label defined in your `[DATASETS]` config (e.g., `// -DATA: DATASET_1k`).

## 2. Status & Evaluation Markers
These annotations control how the class is processed and how it is visually represented in the generated HTML and Markdown reports.

*   `// ignore` (or `// status: ignore`) — The harness will completely ignore this class. It won't be compiled, executed, or included in any reports. Useful for broken code or scratch files.
*   `// baseline` (or `// status: baseline`) — Marks the class as the canonical reference implementation. In the HTML report, this class receives a 👑 crown badge and is used as the baseline for performance comparison charts.
*   `// incomplete` (or `// status: incomplete`) — Marks the class as a Work-In-Progress or failing validation. It will still execute, but it receives a ⚠️ warning badge in the reports so reviewers know the results might be partial or broken.
