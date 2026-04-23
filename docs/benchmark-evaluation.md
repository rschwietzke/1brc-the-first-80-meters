# Evaluating Benchmarks

This document explains how to read the HTML and Markdown reports.

## HTML Report
The standalone HTML report provides a comprehensive view of the benchmark run.
- **Executive Anomaly Dashboard**: Shows the biggest regressions and improvements for Runtime, Instructions, IPC, and Branch Misses.
- **Data Tables**: Matrices of `Class` vs `JDK / SCENARIO`, grouped by Dataset.
- **Visual Badges**: Marks `status: baseline` classes with a crown and `status: incomplete` classes with a warning.
- **Interactive Charts**: Apache ECharts visualizes the performance of different implementations.

## Markdown Report
The `.md` report provides condensed tables ideal for AI ingestion and version control diffs.

## Compare Runs
Use `BenchmarkMatrix compare-run <timestamp>` (to be implemented) to cross-compare results between distinct runs, factoring in `MachineFingerprint`.
