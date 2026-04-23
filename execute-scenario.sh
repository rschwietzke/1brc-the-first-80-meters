#!/bin/bash
# execute-scenario.sh
# Expects env vars: CLASS, DATA, JVM_OPTS, TASKSET, PROG_OPTS

if [ -z "$CLASS" ] || [ -z "$DATA" ]; then
    echo "Error: CLASS and DATA must be set."
    exit 1
fi

ITERATIONS=${ITERATIONS:-3}
CLASSPATH="1brc-implementations/target/classes"

cmd=(java $JVM_OPTS -cp "$CLASSPATH" "$CLASS" "$DATA" $PROG_OPTS)
if [ -n "$BINDING" ]; then
    cmd=($BINDING "${cmd[@]}")
fi

# 1. OS Cache Warming (Run 3 times as requested)
for i in {1..3}; do
    cat "$DATA" > /dev/null
done

# 2. Clean Iterations (P50 Median)
runtimes=()
checksum="ERROR"
for (( i=1; i<=$ITERATIONS; i++ )); do
    out=$(/usr/bin/time -f "%e,%U,%S" "${cmd[@]}" 2>&1)
    line=$(echo "$out" | grep "^Measurement Runtime" | tail -n 1)
    if [ -n "$line" ]; then
        rt=$(echo "$line" | grep -oP ':\s*\K\d+')
        chk=$(echo "$line" | grep -oP '\(\K[^)]+')
        runtimes+=($rt)
        checksum=$chk
    fi
done

if [ ${#runtimes[@]} -eq 0 ]; then
    median_runtime=0
else
    sorted=($(for r in "${runtimes[@]}"; do echo "$r"; done | sort -n))
    mid=$(( ${#sorted[@]} / 2 ))
    if (( ${#sorted[@]} % 2 == 0 )); then
        median_runtime=$(( (sorted[mid-1] + sorted[mid]) / 2 ))
    else
        median_runtime=${sorted[mid]}
    fi
fi

# 3. Dedicated Perf Stat Run
perf_file=$(mktemp)
time_file=$(mktemp)
perf_cmd=(env LC_ALL=C perf stat -x, -o "$perf_file" -e instructions,cycles,branches,branch-misses,L1-dcache-load-misses,cache-misses,page-faults,task-clock,context-switches,cpu-migrations "${cmd[@]}")

out_perf=$(/usr/bin/time -o "$time_file" -f "%e,%U,%S" "${perf_cmd[@]}" 2>&1)
line_perf=$(echo "$out_perf" | grep "^Measurement Runtime" | tail -n 1)
perf_runtime=0
if [ -n "$line_perf" ]; then
    perf_runtime=$(echo "$line_perf" | grep -oP ':\s*\K\d+')
fi

instructions=$(awk -F, '$3 == "instructions" {print $1}' "$perf_file")
cycles=$(awk -F, '$3 == "cycles" {print $1}' "$perf_file")
branches=$(awk -F, '$3 == "branches" {print $1}' "$perf_file")
branch_misses=$(awk -F, '$3 == "branch-misses" {print $1}' "$perf_file")
l1_misses=$(awk -F, '$3 == "L1-dcache-load-misses" {print $1}' "$perf_file")
llc_misses=$(awk -F, '$3 == "cache-misses" {print $1}' "$perf_file")
page_faults=$(awk -F, '$3 == "page-faults" {print $1}' "$perf_file")
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

# 4. Dedicated JFR Run (Always On if JFR_FILE is set)
jfr_runtime=0
if [ -n "$JFR_FILE" ]; then
    jfr_cmd=(java $JVM_OPTS "-XX:StartFlightRecording=filename=$JFR_FILE,settings=profile" -cp "$CLASSPATH" "$CLASS" "$DATA" $PROG_OPTS)
    if [ -n "$BINDING" ]; then
        jfr_cmd=($BINDING "${jfr_cmd[@]}")
    fi
    out_jfr=$(/usr/bin/time -f "%e,%U,%S" "${jfr_cmd[@]}" 2>&1)
    line_jfr=$(echo "$out_jfr" | grep "^Measurement Runtime" | tail -n 1)
    if [ -n "$line_jfr" ]; then
        jfr_runtime=$(echo "$line_jfr" | grep -oP ':\s*\K\d+')
    fi
fi

rm -f "$perf_file" "$time_file"

# Output raw CSV line
echo "$CLASS,$median_runtime,$checksum,$perf_runtime,$jfr_runtime,$instructions,$cycles,$branches,$branch_misses,$l1_misses,$llc_misses,$page_faults,$task_clock,$context_switches,$cpu_migrations,$ipc,$seconds_elapsed,$seconds_user,$seconds_sys"
