#!/bin/bash
# benchmark-matrix.sh

if command -v java >/dev/null; then
    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(command -v java))))
fi

COMMAND=$1
if [ -z "$COMMAND" ]; then
    echo "Usage: benchmark-matrix.sh <command> [args]"
    echo "Commands:"
    echo "  run                   Generate and execute the benchmark matrix"
    echo "  dry-run               Generate the script without executing it"
    echo "  analyze [timestamp]   Regenerate HTML/MD reports for all runs, or a specific run"
    echo "  compare <t1> <t2>     Compare two benchmark runs (To be implemented)"
    exit 1
fi

shift # Remove command from arguments

if [ "$COMMAND" = "analyze" ] || [ "$COMMAND" = "compare" ] || [ "$COMMAND" = "compare-run" ]; then
    echo "==> Compiling benchmark harness..."
    mvn -q clean compile -pl benchmark-harness
    mvn -q exec:exec -pl benchmark-harness -Dexec.workingdir="$(pwd)" -Dexec.executable="java" -Dexec.args="-cp %classpath org.onebrc.benchmark.BenchmarkMatrix $COMMAND $*"
    exit $?
fi

if [ "$COMMAND" = "run" ] || [ "$COMMAND" = "dry-run" ]; then
    echo "==> Running pre-validations..."
    ./prevalidate.sh || exit 1

    echo "==> Compiling benchmark harness..."
    mvn -q clean compile -pl benchmark-harness

    echo "==> Booting matrix generator..."
    
    ARGS="generate"
    if [ "$COMMAND" = "dry-run" ]; then
        ARGS="generate --dry-run"
    fi
    
    # Safely append remaining args while preserving quotes (e.g. --comment "my comment")
    for arg in "$@"; do
        ARGS="$ARGS $(printf "%q" "$arg")"
    done

    OUTPUT=$(mvn -q exec:exec -pl benchmark-harness -Dexec.workingdir="$(pwd)" -Dexec.executable="java" -Dexec.args="-cp %classpath org.onebrc.benchmark.BenchmarkMatrix $ARGS")
    RET=$?

    if [ $RET -ne 0 ]; then
        echo "Error generating script:"
        echo "$OUTPUT"
        exit $RET
    fi

    SCRIPT_PATH=$(echo "$OUTPUT" | tail -n 1 | tr -d '\r')

    if [ "$COMMAND" = "dry-run" ] || [[ " $* " == *" --dry-run "* ]]; then
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
else
    echo "Unknown command: $COMMAND"
    exit 1
fi
