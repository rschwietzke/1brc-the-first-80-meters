#!/bin/bash
# 1BRC Test Execution and Profiling Script
#
# Supported Magic Hints in .java files:
#
#   // ignore
#       Skips the execution of the benchmark entirely.
#
#   // JVM_OPTS: <options>
#       Fallback legacy hint. Overrides the default JVM_OPTS for the file.
#       Example: // JVM_OPTS: -Xms2g -Xmx2g
#
#   // exec <assignments>
#       Defines a specific execution run. A file can have multiple `// exec` lines,
#       and each will be executed independently.
#       Supported variables inside assignments:
#         NAME    : Name of the run (appears in CSV). Defaults to "default".
#         JVM_OPTS: JVM arguments for the run.
#         PARAMS  : Command line arguments to pass to the Java program.
#                   Use $DEFAULT to refer to the script's arguments.
#         TASKSET : Taskset parameters for CPU pinning (e.g., "-c 0-7" or "0-7").
#                   If defined, the Java process is launched with `taskset`.
#         ROUNDS  : Number of evaluation runs to take the median from. Defaults to 3.
#
#       Examples of `// exec`:
#         // exec JVM_OPTS="-Xms2g"; PARAMS="-f 10M.txt -mc 0"
#         // exec NAME="high-mem"; JVM_OPTS=$HIGH_MEM; PARAMS=$DEFAULT; TASKSET="-c 0,2,3"
#

# Set the configured number of operational rounds (defaults to 3 if not present in env variables)
GLOBAL_ROUNDS="${ROUNDS:-3}"

# Parse script arguments
FILE_REGEX=""
OUTPUT_CSV=""
while [[ "$#" -gt 0 ]]; do
    case $1 in
        --rounds)
            GLOBAL_ROUNDS="$2"
            shift 2
            ;;
        --regex)
            FILE_REGEX="$2"
            shift 2
            ;;
        --output|-o)
            OUTPUT_CSV="$2"
            shift 2
            ;;
        -*)
            echo "Unknown option: $1"
            echo "Usage: $0 [--rounds N] [--regex PATTERN] --output <file.csv> <src_dir|file.java> [additional_args...]"
            exit 1
            ;;
        *)
            break
            ;;
    esac
done

if [ -z "$OUTPUT_CSV" ]; then
    echo "Error: Output file must be specified using --output <file.csv>"
    echo "Usage: $0 [--rounds N] [--regex PATTERN] --output <file.csv> <src_dir|file.java> [additional_args...]"
    exit 1
fi

if [ -z "$1" ]; then
    echo "Error: The first parameter must be the source directory or a specific Java file."
    echo "Usage: $0 [--rounds N] [--regex PATTERN] --output <file.csv> <src_dir|file.java> [additional_args...]"
    exit 1
fi

TARGET="$1"
shift
DEFAULT="$@"

# Default configuration
CLASSPATH="1brc-implementations/target/classes"

# JVM parameters defined as requested (easy to adjust, same for all)
JVM_OPTS=""
LOW_MEM="-Xmx10m -XX:+UnlockExperimentalVMOptions -XX:+UseEpsilonGC $JVM_OPTS"
HIGH_MEM="-Xms2g -Xmx2g $JVM_OPTS"

# Prepare CSV Header
echo "Class,Name,JVM_OPTS,PARAMS,TASKSET,MedianRuntimeMs,Checksum,Instructions,Cycles,Branches,BranchMisses,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys" > "$OUTPUT_CSV"

echo "Command line arguments passed: $DEFAULT"
echo "Default JVM parameters: $JVM_OPTS"
echo "Results will be saved to $OUTPUT_CSV"

# Determine file array based on whether target is a file or directory
if [ -f "$TARGET" ]; then
    FILES=("$TARGET")
elif [ -d "$TARGET" ]; then
    if [ -n "$FILE_REGEX" ]; then
        FILES=()
        for f in "$TARGET"/*.java; do
            [ -e "$f" ] || continue
            basename=$(basename "$f")
            if [[ "$basename" =~ $FILE_REGEX ]]; then
                FILES+=("$f")
            fi
        done
        if [ ${#FILES[@]} -eq 0 ]; then
            echo "Error: No files matched regex '$FILE_REGEX' in $TARGET"
            exit 1
        fi
    else
        FILES=("$TARGET"/*.java)
    fi
else
    echo "Target $TARGET is neither a file nor a directory. Please run this script from the project root."
    exit 1
fi

run_benchmark() {
    local fqcn="$1"
    local classname="$2"
    local run_name="$3"
    local jvm_opts="$4"
    local task_opts="$5"
    local params="$6"
    local rounds="$7"

    echo "============================================================"
    echo "Executing $fqcn [$run_name]"
    if [ -n "$task_opts" ]; then
        echo "Taskset: $task_opts"
    fi
    echo "============================================================"

    declare -a runtimes
    local last_checksum=""
    local runtime_error=""

    for i in $(seq 1 "$rounds"); do
        echo "  Run $i/$rounds (Measuring Runtime)..."
        local cmd=(java $jvm_opts -cp "$CLASSPATH" "$fqcn" $params)
        if [ -n "$task_opts" ]; then
            cmd=(taskset $task_opts "${cmd[@]}")
        fi
        
        local output
        output=$("${cmd[@]}" 2>&1)
        
        # Extract checksum and runtime from the output
        local line
        line=$(echo "$output" | grep "^Measurement Runtime" | tail -n 1)
        if [ -z "$line" ]; then
            echo "  [Error] Measurement Runtime not found in output."
            echo "          Check the output below:"
            echo "$output"
            runtime_error="RUNTIME_NOT_FOUND"
            continue
        fi

        local checksum
        checksum=$(echo "$line" | grep -oP '\(\K[^)]+')
        local runtime
        runtime=$(echo "$line" | grep -oP ':\s*\K\d+')

        echo "    Runtime: ${runtime} ms, Checksum: $checksum"

        if [ "$i" -gt 1 ] && [ "$checksum" != "$last_checksum" ]; then
            echo "  [Error] Checksum mismatch! Previous: $last_checksum, Current: $checksum"
            runtime_error="CHECKSUM_MISMATCH"
        fi
        last_checksum="$checksum"
        runtimes+=("$runtime")
    done

    # Check if we got exactly the requested runtimes and no checksum errors
    local median
    if [ ${#runtimes[@]} -eq "$rounds" ] && [ "$runtime_error" != "CHECKSUM_MISMATCH" ] && [ "$runtime_error" != "RUNTIME_NOT_FOUND" ]; then
        # Sort best (lowest) to worst (highest) and take the middle.
        local sorted=( $(printf "%s\n" "${runtimes[@]}" | sort -n) )
        if [ $((rounds % 2)) -ne 0 ]; then
            median="${sorted[$((rounds / 2))]}"
        else
            local mid1="${sorted[$((rounds / 2 - 1))]}"
            local mid2="${sorted[$((rounds / 2))]}"
            median=$(( (mid1 + mid2) / 2 ))
        fi
        echo "  Median Runtime: $median ms"
    else
        echo "  [Warning] Failed to collect $rounds valid runs or checksum mismatch."
        median="ERROR"
    fi

    echo "  Run 4 (perf stat)..."
    # execute with perf stat
    local cmd_base=(java $jvm_opts -cp "$CLASSPATH" "$fqcn" $params)
    if [ -n "$task_opts" ]; then
        cmd_base=(taskset $task_opts "${cmd_base[@]}")
    fi
    local perf_file=$(mktemp)
    local time_file=$(mktemp)
    local perf_cmd=(env LC_ALL=C perf stat -x, -o "$perf_file" -e instructions,cycles,branches,branch-misses,task-clock,context-switches,cpu-migrations "${cmd_base[@]}")
    
    # Capture metrics natively without JVM stdout disruption
    /usr/bin/time -o "$time_file" -f "%e,%U,%S" "${perf_cmd[@]}" >/dev/null 2>&1

    # Parse perf output robustly via CSV mode
    local instructions=$(awk -F, '$3 == "instructions" {print $1}' "$perf_file")
    local cycles=$(awk -F, '$3 == "cycles" {print $1}' "$perf_file")
    local branches=$(awk -F, '$3 == "branches" {print $1}' "$perf_file")
    local branch_misses=$(awk -F, '$3 == "branch-misses" {print $1}' "$perf_file")
    local task_clock=$(awk -F, '$3 == "task-clock" {print $1}' "$perf_file")
    local context_switches=$(awk -F, '$3 == "context-switches" {print $1}' "$perf_file")
    local cpu_migrations=$(awk -F, '$3 == "cpu-migrations" {print $1}' "$perf_file")
    
    local ipc=$(awk -F, '/insn per cycle/ {print $6}' "$perf_file")
    
    local seconds_elapsed=""
    local seconds_user=""
    local seconds_sys=""
    if [ -f "$time_file" ]; then
        local time_out=$(cat "$time_file")
        seconds_elapsed=$(echo "$time_out" | cut -d, -f1)
        seconds_user=$(echo "$time_out" | cut -d, -f2)
        seconds_sys=$(echo "$time_out" | cut -d, -f3)
    fi

    # Cleanup temp files
    rm -f "$perf_file" "$time_file"

    # Save to CSV
    echo "\"$classname\",\"$run_name\",\"$jvm_opts\",\"$params\",\"$task_opts\",$median,$last_checksum,$instructions,$cycles,$branches,$branch_misses,$task_clock,$context_switches,$cpu_migrations,$ipc,$seconds_elapsed,$seconds_user,$seconds_sys" >> "$OUTPUT_CSV"
}

for file in "${FILES[@]}"; do
    # Extract the class name from file path
    basename=$(basename "$file")
    classname="${basename%.java}"
    pkg=$(grep -m 1 '^\s*package ' "$file" | awk '{print $2}' | tr -d ';\r')
    if [ -z "$pkg" ]; then
        fqcn="$classname"
    else
        fqcn="$pkg.$classname"
    fi

    # Check for ignore hint
    if grep -q -i -m 1 '^\s*//\s*ignore' "$file"; then
        echo "============================================================"
        echo "Skipping $fqcn (ignore hint found)"
        echo "============================================================"
        continue
    fi

    # Check for magic JVM args (fallback legacy hint)
    CLASS_JVM_OPTS_RAW=$(grep -m 1 '^\s*//\s*JVM_OPTS:' "$file" | sed 's|^\s*//\s*JVM_OPTS:\s*||' | tr -d '\r')
    if [ -n "$CLASS_JVM_OPTS_RAW" ]; then
        # eval is used to expand variables like $LOW_MEM
        CURRENT_JVM_OPTS=$(eval echo "$CLASS_JVM_OPTS_RAW")
        echo "  Found fallback custom JVM_OPTS: $CURRENT_JVM_OPTS"
    else
        CURRENT_JVM_OPTS="$JVM_OPTS"
    fi

    # Read all exec hints into an array cleanly
    EXEC_LINES=()
    while IFS= read -r line; do
        [[ -n "$line" ]] && EXEC_LINES+=("$line")
    done < <(grep '^\s*//\s*exec\b' "$file" | sed 's|^\s*//\s*exec\s*||' | tr -d '\r')

    if [ ${#EXEC_LINES[@]} -gt 0 ]; then
        for exec_line in "${EXEC_LINES[@]}"; do
            (
                # Run mapping safely within a subshell so configs don't leak between lines
                NAME="default"
                TASKSET=""
                PARAMS="$DEFAULT"
                JVM_OPTS="$CURRENT_JVM_OPTS"
                ROUNDS="$GLOBAL_ROUNDS"
                
                eval "$exec_line"
                
                run_benchmark "$fqcn" "$classname" "$NAME" "$JVM_OPTS" "$TASKSET" "$PARAMS" "$ROUNDS"
            )
        done
    else
        # No exec hints, do standard fallback
        run_benchmark "$fqcn" "$classname" "default" "$CURRENT_JVM_OPTS" "" "$DEFAULT" "$GLOBAL_ROUNDS"
    fi

    # Reset vars
    runtime_error=""

done

echo "============================================================"
echo "Done. All results saved to $OUTPUT_CSV"
