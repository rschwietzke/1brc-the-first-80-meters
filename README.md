# 1BRC for Learners

Welcome to the 1BRC (One Billion Row Challenge) for Learners repository. This project was originally built around a [presentation given at Devoxx Poland 2024](https://training.xceptance.com/java/450-the-first-80p-of-1BRC-2024.html) (and other conferences) to demonstrate step-by-step performance optimization techniques in Java.

It has since evolved to include not only the learning-focused implementations but also a comprehensive benchmarking and regression testing harness.

## Project Structure

This repository is divided into several main components:

*   **[1BRC Implementations](./1brc-implementations/)**: Contains the source code for the different 1BRC solutions. This tracks the evolutionary journey from a basic, naive approach to highly optimized, multi-threaded versions. The original Devoxx presentation examples reside here. See its [README](./1brc-implementations/README.md) for instructions on compiling, data generation, and running native image builds.
*   **[Benchmark Harness](./benchmark-harness/)**: A robust Java-based benchmarking and regression-testing harness. It is designed to automatically evaluate the various 1BRC implementations across different JDK versions, data sizes, and execution scenarios, complete with JFR profiling support.
*   **Root Scripts**: The root directory contains scripts to orchestrate the benchmarking process (e.g., `benchmark.sh`, `benchmark-matrix.sh`, `execute-scenario.sh`, `measure.sh`).

## Getting Started

1.  **Read the Implementation Guide**: Head over to the [1BRC Implementations](./1brc-implementations/) folder to learn about the project's core code and how to generate the required dataset (such as 10 million or 1 billion rows).
2.  **Generate Data**: You must create test data files before running the application or benchmarks. Instructions are in the [1brc-implementations README](./1brc-implementations/README.md#create-data---413-cities).
3.  **Run Benchmarks**: If you want to evaluate performance across JDKs, GC configurations, VM memory profiles, and other dimensions, explore the `benchmark-harness/` and use the root shell scripts to execute test matrices. See the detailed [Benchmarking Guide](./docs/benchmark-running.md) for how to define the execution matrix.

## License

This codebase is available under the Apache License, version 2.0. It also contains some code from the original [1brc repository](https://github.com/gunnarmorling/1brc) by Gunnar Morling under the same license.
