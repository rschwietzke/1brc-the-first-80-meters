#!/bin/bash
# prevalidate.sh

deps=("perf" "taskset" "awk" "mvn")
for cmd in "${deps[@]}"; do
    if ! command -v "$cmd" &> /dev/null; then
        echo "Error: Required command '$cmd' is not installed."
        exit 1
    fi
done

if [ ! -d "1brc-implementations/target/classes" ] || [ ! -d "benchmark-harness/target/classes" ]; then
    echo "Error: target/classes directories do not exist. Please run 'mvn compile' first."
    exit 1
fi

exit 0
