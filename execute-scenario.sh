#!/bin/bash
# execute-scenario.sh
# Expects env vars: CLASS, DATA, JVM_OPTS, TASKSET, PROG_OPTS

if [ -z "$CLASS" ] || [ -z "$DATA" ]; then
    echo "Error: CLASS and DATA must be set."
    exit 1
fi

CLASSPATH="1brc-implementations/target/classes"

cmd=(java $JVM_OPTS -cp "$CLASSPATH" "$CLASS" "$DATA" $PROG_OPTS)
if [ -n "$TASKSET" ]; then
    cmd=(taskset $TASKSET "${cmd[@]}")
fi

perf_file=$(mktemp)
time_file=$(mktemp)

perf_cmd=(env LC_ALL=C perf stat -x, -o "$perf_file" -e instructions,cycles,branches,branch-misses,task-clock,context-switches,cpu-migrations "${cmd[@]}")

# Capture stdout to parse Runtime and Checksum, run time wrapped around it
output=$(/usr/bin/time -o "$time_file" -f "%e,%U,%S" "${perf_cmd[@]}" 2>&1)

line=$(echo "$output" | grep "^Measurement Runtime" | tail -n 1)
if [ -n "$line" ]; then
    checksum=$(echo "$line" | grep -oP '\(\K[^)]+')
    runtime=$(echo "$line" | grep -oP ':\s*\K\d+')
else
    checksum="ERROR"
    runtime="0"
fi

instructions=$(awk -F, '$3 == "instructions" {print $1}' "$perf_file")
cycles=$(awk -F, '$3 == "cycles" {print $1}' "$perf_file")
branches=$(awk -F, '$3 == "branches" {print $1}' "$perf_file")
branch_misses=$(awk -F, '$3 == "branch-misses" {print $1}' "$perf_file")
task_clock=$(awk -F, '$3 == "task-clock" {print $1}' "$perf_file")
context_switches=$(awk -F, '$3 == "context-switches" {print $1}' "$perf_file")
cpu_migrations=$(awk -F, '$3 == "cpu-migrations" {print $1}' "$perf_file")
ipc=$(awk -F, '/insn per cycle/ {print $6}' "$perf_file")

seconds_elapsed=""
seconds_user=""
seconds_sys=""
if [ -f "$time_file" ]; then
    time_out=$(cat "$time_file")
    seconds_elapsed=$(echo "$time_out" | cut -d, -f1)
    seconds_user=$(echo "$time_out" | cut -d, -f2)
    seconds_sys=$(echo "$time_out" | cut -d, -f3)
fi

rm -f "$perf_file" "$time_file"

# Output raw CSV line
echo "$CLASS,$runtime,$checksum,$instructions,$cycles,$branches,$branch_misses,$task_clock,$context_switches,$cpu_migrations,$ipc,$seconds_elapsed,$seconds_user,$seconds_sys"
