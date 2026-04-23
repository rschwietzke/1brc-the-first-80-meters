#!/bin/bash
# benchmark-matrix.sh

./prevalidate.sh || exit 1

# Compile benchmark-harness (and optionally 1brc-implementations)
mvn -q clean compile

# Execute BenchmarkMatrix to generate the run script
OUTPUT=$(mvn -q exec:java -pl benchmark-harness -Dexec.mainClass="org.onebrc.benchmark.BenchmarkMatrix" -Dexec.args="generate $*")
RET=$?

if [ $RET -ne 0 ]; then
    echo "Error generating script:"
    echo "$OUTPUT"
    exit $RET
fi

# We expect the Java tool to output the path to the generated script as the final line
SCRIPT_PATH=$(echo "$OUTPUT" | tail -n 1 | tr -d '\r')

if [[ " $* " == *" --dry-run "* ]]; then
    echo "$OUTPUT"
    exit 0
fi

echo "$OUTPUT"

if [ -x "$SCRIPT_PATH" ]; then
    echo "Executing generated script: $SCRIPT_PATH"
    "$SCRIPT_PATH"
else
    echo "Error: Generated script '$SCRIPT_PATH' is not executable or not found."
    exit 1
fi
