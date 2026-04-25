#!/bin/bash

echo "Starting benchmark matrix run"

TOTAL_RUNS=${totalRuns}
CURRENT_RUN=0
START_TIME=$(date +%s)

export ITERATIONS=${iterations}

source $HOME/.sdkman/bin/sdkman-init.sh 2>/dev/null || true

echo "Capturing system information..."
SYSINFO_FILE="data/benchmark-history/${timestamp}-sysinfo.txt"
echo "Kernel: $(uname -r)" > $SYSINFO_FILE
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "OS: $PRETTY_NAME" >> $SYSINFO_FILE
fi
echo "CPU: $(lscpu | grep 'Model name' | awk -F ':' '{print $2}' | xargs)" >> $SYSINFO_FILE
echo "CPU Cores: $(nproc)" >> $SYSINFO_FILE
echo "Memory: $(free -h | awk '/^Mem:/ {print $2}')" >> $SYSINFO_FILE

echo "JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys" > data/benchmark-history/${timestamp}.csv

<#list jdkBlocks as block>
echo "=================================================="
echo "JDK Block: ${block.jdk.label}"
echo "=================================================="

<#if block.jdk.isSdkman>
sdk install java ${block.sdkVersion}
sdk use java ${block.sdkVersion}
export JAVA_HOME="$SDKMAN_DIR/candidates/java/${block.sdkVersion}"
<#else>
export JAVA_HOME="${block.jdk.pathOrSdkman}"
</#if>
export PATH="$JAVA_HOME/bin:$PATH"

echo "Compiling for JDK release ${block.releaseVersion}"
${block.mavenCmd}

<#list block.combos as combo>
export CLASS="${combo.className}"
export DATA="${combo.data}"
export JVM_OPTS="${combo.jvmOpts}"
export BINDING="${combo.binding}"
export PROG_OPTS="${combo.progOpts}"
export JFR_FILE="${combo.jfrFile}"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running ${combo.simpleClassName} (Run: ${combo.runName}) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running ${combo.simpleClassName} (Run: ${combo.runName}) - ETA: calculating..."
fi

echo -n "${combo.jdkLabel},\"${combo.gcOpts}\",\"${combo.vmOpts}\",\"${combo.progOpts}\",\"${combo.binding}\",${combo.dataLabel},${timestamp}," >> data/benchmark-history/${timestamp}.csv
./execute-scenario.sh >> data/benchmark-history/${timestamp}.csv

</#list>
</#list>

echo "Analyzing results"
mvn -q exec:java -pl benchmark-harness -Dexec.mainClass="org.onebrc.benchmark.BenchmarkMatrix" -Dexec.args="analyze ${timestamp}"
