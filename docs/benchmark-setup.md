# Benchmark Setup

This document explains how to provision the environment and build the multi-module project.

## Requirements
- `perf`, `taskset`, `awk` installed.
- Maven installed (`mvn`).
- JDK 25 or SDKman installed.

## Multi-Module Structure
- `1brc-implementations`: Contains the actual solutions to the One Billion Row Challenge. It has zero external dependencies.
- `benchmark-harness`: Contains the Java tooling to generate scripts, merge CSVs, parse JFR, and generate HTML/Markdown reports.

## Setup
1. Clone the repository.
2. Run `mvn clean compile` to build both modules.
3. Copy the `.conf.example` files to `.conf` files and customize them.
