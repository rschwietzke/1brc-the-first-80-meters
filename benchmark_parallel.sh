#!/bin/bash
# 1BRC Parallel Test Execution and Profiling Script

# Default configuration
SRC_DIR="src/main/java/org/rschwietzke/parallel"
CLASSPATH="target/classes"
OUTPUT_CSV="results.csv"

# JVM parameters defined as requested (easy to adjust, same for all)
JVM_OPTS="-Xms2g -Xmx2g -XX:+AlwaysPreTouch"

# Prepare CSV Header
echo "Class,MedianRuntimeMs,Checksum,Instructions,Cycles,Branches,BranchMisses,TaskClock,ContextSwitches,IPC,SecElapsed,SecUser,SecSys" > "$OUTPUT_CSV"

echo "Command line arguments passed: $@"
echo "JVM parameters: $JVM_OPTS"
echo "Results will be saved to $OUTPUT_CSV"

# Make sure to run inside the root of the project
if [ ! -d "$SRC_DIR" ]; then
    echo "Directory $SRC_DIR does not exist. Please run this script from the project root."
    exit 1
fi

for file in "$SRC_DIR"/*.java; do
    # Extract the class name from file path
    basename=$(basename "$file")
    classname="${basename%.java}"
    fqcn="org.rschwietzke.parallel.$classname"

    echo "============================================================"
    echo "Executing $fqcn"
    echo "============================================================"

    declare -a runtimes
    last_checksum=""
    runtime_error=""

    for i in {1..3}; do
        echo "  Run $i/3 (Measuring Runtime)..."
        output=$(java $JVM_OPTS -cp "$CLASSPATH" "$fqcn" "$@" 2>&1)
        
        # Extract checksum and runtime from the output
        line=$(echo "$output" | grep "Measurement Runtime")
        if [ -z "$line" ]; then
            echo "  [Error] Measurement Runtime not found in output."
            echo "          Check the output below:"
            echo "$output"
            runtime_error="RUNTIME_NOT_FOUND"
            continue
        fi

        checksum=$(echo "$line" | grep -oP '\(\K[^)]+')
        runtime=$(echo "$line" | grep -oP ':\s*\K\d+')

        echo "    Runtime: ${runtime} ms, Checksum: $checksum"

        if [ "$i" -gt 1 ] && [ "$checksum" != "$last_checksum" ]; then
            echo "  [Error] Checksum mismatch! Previous: $last_checksum, Current: $checksum"
            runtime_error="CHECKSUM_MISMATCH"
        fi
        last_checksum="$checksum"
        runtimes+=("$runtime")
    done

    # Check if we got exactly 3 runtimes and no checksum errors
    if [ ${#runtimes[@]} -eq 3 ] && [ "$runtime_error" != "CHECKSUM_MISMATCH" ] && [ "$runtime_error" != "RUNTIME_NOT_FOUND" ]; then
        # Sort best (lowest) to worst (highest) and take the middle.
        sorted=( $(printf "%s\n" "${runtimes[@]}" | sort -n) )
        median="${sorted[1]}"
        echo "  Median Runtime: $median ms"
    else
        echo "  [Warning] Failed to collect 3 valid runs or checksum mismatch."
        median="ERROR"
    fi

    echo "  Run 4 (perf stat)..."
    # execute with perf stat
    # -B prints without thousands separators. LC_ALL=C ensures decimal dots instead of commas.
    perf_out=$(LC_ALL=C perf stat -B -e instructions,cycles,branches,branch-misses,task-clock,context-switches java $JVM_OPTS -cp "$CLASSPATH" "$fqcn" "$@" 2>&1)

    # Parse perf output robustly
    instructions=$(echo "$perf_out" | awk '$2 == "instructions" || $3 == "instructions" {print $1}' | head -n 1)
    cycles=$(echo "$perf_out" | awk '$2 == "cycles" || $3 == "cycles" {print $1}' | head -n 1)
    branches=$(echo "$perf_out" | awk '$2 == "branches" || $3 == "branches" {print $1}' | head -n 1)
    branch_misses=$(echo "$perf_out" | awk '$2 == "branch-misses" || $3 == "branch-misses" {print $1}' | head -n 1)
    task_clock=$(echo "$perf_out" | awk '$2 == "task-clock" || $3 == "task-clock" {print $1}' | head -n 1)
    context_switches=$(echo "$perf_out" | awk '$2 == "context-switches" || $3 == "context-switches" {print $1}' | head -n 1)
    
    ipc=$(echo "$perf_out" | awk '/insn per cycle/ {for(i=1;i<=NF;i++) if($i=="insn") print $(i-1)}')
    seconds_elapsed=$(echo "$perf_out" | awk '/seconds time elapsed/ {print $1}')
    seconds_user=$(echo "$perf_out" | awk '/seconds user/ {print $1}')
    seconds_sys=$(echo "$perf_out" | awk '/seconds sys/ {print $1}')

    # Save to CSV
    echo "$classname,$median,$last_checksum,$instructions,$cycles,$branches,$branch_misses,$task_clock,$context_switches,$ipc,$seconds_elapsed,$seconds_user,$seconds_sys" >> "$OUTPUT_CSV"

    # Reset arrays and vars for the next class
    unset runtimes
    runtime_error=""

done

echo "============================================================"
echo "Done. All results saved to $OUTPUT_CSV"
