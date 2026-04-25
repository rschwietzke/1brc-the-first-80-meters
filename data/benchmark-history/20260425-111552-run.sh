#!/bin/bash

echo "Starting benchmark matrix run"

TOTAL_RUNS=204
CURRENT_RUN=0
START_TIME=$(date +%s)

export ITERATIONS=3

source $HOME/.sdkman/bin/sdkman-init.sh 2>/dev/null || true

echo "Capturing system information..."
SYSINFO_FILE="data/benchmark-history/20260425-111552-sysinfo.txt"
echo "Kernel: $(uname -r)" > $SYSINFO_FILE
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "OS: $PRETTY_NAME" >> $SYSINFO_FILE
fi
echo "CPU: $(lscpu | grep 'Model name' | awk -F ':' '{print $2}' | xargs)" >> $SYSINFO_FILE
echo "CPU Cores: $(nproc)" >> $SYSINFO_FILE
echo "Memory: $(free -h | awk '/^Mem:/ {print $2}')" >> $SYSINFO_FILE

echo "JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys" > data/benchmark-history/20260425-111552.csv

echo "=================================================="
echo "JDK Block: JDK_21_OPEN"
echo "=================================================="

sdk install java 21.0.6-tem
sdk use java 21.0.6-tem
export JAVA_HOME="$SDKMAN_DIR/candidates/java/21.0.6-tem"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Compiling for JDK release 21"
mvn clean compile -pl 1brc-implementations -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 -Pjdk21-preview

export CLASS="org.onebrc.again26.BRC100_DirectTempWrite"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC100_DirectTempWrite-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC100_DirectTempWrite"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC100_DirectTempWrite-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC100_DirectTempWrite"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC100_DirectTempWrite-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC100_DirectTempWrite (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC101_ParseFrom65_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC101_ParseFrom65_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC101_ParseFrom65_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC101_ParseFrom65_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC101_ParseFrom65_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC101_ParseFrom65_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC101_ParseFrom65_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC105_ParseTemp_95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC105_ParseTemp_95-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC105_ParseTemp_95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC105_ParseTemp_95-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC105_ParseTemp_95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC105_ParseTemp_95-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC105_ParseTemp_95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC106_ParseTemp_105"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC106_ParseTemp_105-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC106_ParseTemp_105"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC106_ParseTemp_105-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC106_ParseTemp_105"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC106_ParseTemp_105-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC106_ParseTemp_105 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC107_ParseTemp"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC107_ParseTemp-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC107_ParseTemp"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC107_ParseTemp-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC107_ParseTemp"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC107_ParseTemp-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC107_ParseTemp (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC110_EqualsCitySplit"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC110_EqualsCitySplit-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC110_EqualsCitySplit"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC110_EqualsCitySplit-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC110_EqualsCitySplit"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC110_EqualsCitySplit-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC110_EqualsCitySplit (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC111_EqualsCitySplit_Reverse"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC111_EqualsCitySplit_Reverse-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC111_EqualsCitySplit_Reverse"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC111_EqualsCitySplit_Reverse-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC111_EqualsCitySplit_Reverse"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC111_EqualsCitySplit_Reverse-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC111_EqualsCitySplit_Reverse (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC112_EqualsCityMismatch"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC112_EqualsCityMismatch-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC112_EqualsCityMismatch"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC112_EqualsCityMismatch-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC112_EqualsCityMismatch"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC112_EqualsCityMismatch-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC112_EqualsCityMismatch (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC113_EqualsCityMismatchSimple"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC113_EqualsCityMismatchSimple-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC113_EqualsCityMismatchSimple"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC113_EqualsCityMismatchSimple-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC113_EqualsCityMismatchSimple"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC113_EqualsCityMismatchSimple-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC113_EqualsCityMismatchSimple (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC010_NoStream-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC010_NoStream-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC010_NoStream-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC010_NoStream (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC012_SplitRemoved-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC012_SplitRemoved-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC012_SplitRemoved-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC012_SplitRemoved (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC014_NewParseDouble-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC014_NewParseDouble-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC014_NewParseDouble-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC014_NewParseDouble (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC015_ParseDoubleSimpler1-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC015_ParseDoubleSimpler1-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC015_ParseDoubleSimpler1-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC015_ParseDoubleSimpler1 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC016_NoExtraString-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC016_NoExtraString-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC016_NoExtraString-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC016_NoExtraString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC017_ParseDoubleSimpler2-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC017_ParseDoubleSimpler2-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC017_ParseDoubleSimpler2-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC017_ParseDoubleSimpler2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC020_IntegerValue"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC020_IntegerValue-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC020_IntegerValue"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC020_IntegerValue-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC020_IntegerValue"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC020_IntegerValue-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC020_IntegerValue (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC021_LooplessParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC021_LooplessParsing-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC021_LooplessParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC021_LooplessParsing-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC021_LooplessParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC021_LooplessParsing-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC021_LooplessParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC022_LooplessParsing2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC022_LooplessParsing2-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC022_LooplessParsing2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC022_LooplessParsing2-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC022_LooplessParsing2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC022_LooplessParsing2-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC022_LooplessParsing2 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC025_MutateData"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC025_MutateData-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC025_MutateData"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC025_MutateData-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC025_MutateData"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC025_MutateData-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC025_MutateData (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC027_SizedMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC027_SizedMap-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC027_SizedMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC027_SizedMap-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC027_SizedMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC027_SizedMap-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC027_SizedMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC030_OpenMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC030_OpenMap-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC030_OpenMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC030_OpenMap-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC030_OpenMap"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC030_OpenMap-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC030_OpenMap (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC031_OpenMapLessCasting"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC031_OpenMapLessCasting-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC031_OpenMapLessCasting"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC031_OpenMapLessCasting-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC031_OpenMapLessCasting"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC031_OpenMapLessCasting-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC031_OpenMapLessCasting (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC033_SimplerLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC033_SimplerLambda-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC033_SimplerLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC033_SimplerLambda-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC033_SimplerLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC033_SimplerLambda-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC033_SimplerLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC035_NoLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC035_NoLambda-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC035_NoLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC035_NoLambda-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC035_NoLambda"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC035_NoLambda-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC035_NoLambda (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC036_TurnedIfs"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC036_TurnedIfs-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC036_TurnedIfs"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC036_TurnedIfs-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC036_TurnedIfs"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC036_TurnedIfs-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC036_TurnedIfs (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC037_LessNullChecks"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC037_LessNullChecks-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC037_LessNullChecks"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC037_LessNullChecks-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC037_LessNullChecks"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC037_LessNullChecks-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC037_LessNullChecks (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC040_ASetIsGoodEnough"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC040_ASetIsGoodEnough-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC040_ASetIsGoodEnough"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC040_ASetIsGoodEnough-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC040_ASetIsGoodEnough"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC040_ASetIsGoodEnough-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC040_ASetIsGoodEnough (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC041_DirectUpdate"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC041_DirectUpdate-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC041_DirectUpdate"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC041_DirectUpdate-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC041_DirectUpdate"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC041_DirectUpdate-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC041_DirectUpdate (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC042_NoSplittedString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC042_NoSplittedString-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC042_NoSplittedString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC042_NoSplittedString-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC042_NoSplittedString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC042_NoSplittedString-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC042_NoSplittedString (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC045_KeepChars"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC045_KeepChars-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC045_KeepChars"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC045_KeepChars-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC045_KeepChars"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC045_KeepChars-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC045_KeepChars (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC047_IntOnly"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC047_IntOnly-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC047_IntOnly"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC047_IntOnly-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC047_IntOnly"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC047_IntOnly-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC047_IntOnly (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC048_ImprovedStringHandling"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC048_ImprovedStringHandling-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC048_ImprovedStringHandling"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC048_ImprovedStringHandling-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC048_ImprovedStringHandling"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC048_ImprovedStringHandling-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC048_ImprovedStringHandling (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC049_ManualMinMax"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC049_ManualMinMax-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC049_ManualMinMax"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC049_ManualMinMax-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC049_ManualMinMax"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC049_ManualMinMax-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC049_ManualMinMax (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC050_LargerSet"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC050_LargerSet-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC050_LargerSet"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC050_LargerSet-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC050_LargerSet"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC050_LargerSet-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC050_LargerSet (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC060_ReadingBytes"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC060_ReadingBytes-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC060_ReadingBytes"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC060_ReadingBytes-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC060_ReadingBytes"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC060_ReadingBytes-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC060_ReadingBytes (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC061_RandomAccessFile"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC061_RandomAccessFile-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC061_RandomAccessFile"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC061_RandomAccessFile-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC061_RandomAccessFile"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC061_RandomAccessFile-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC061_RandomAccessFile (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC063_OneAddLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC063_OneAddLess-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC063_OneAddLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC063_OneAddLess-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC063_OneAddLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC063_OneAddLess-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC063_OneAddLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC065_OneLoopLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC065_OneLoopLess-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC065_OneLoopLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC065_OneLoopLess-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC065_OneLoopLess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC065_OneLoopLess-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC065_OneLoopLess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC067_ParseDifferently"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC067_ParseDifferently-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC067_ParseDifferently"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC067_ParseDifferently-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC067_ParseDifferently"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC067_ParseDifferently-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC067_ParseDifferently (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC068_InlineParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC068_InlineParsing-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC068_InlineParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC068_InlineParsing-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC068_InlineParsing"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC068_InlineParsing-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC068_InlineParsing (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC069_InlineParsing_65"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC069_InlineParsing_65-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC069_InlineParsing_65"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC069_InlineParsing_65-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC069_InlineParsing_65"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC069_InlineParsing_65-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC069_InlineParsing_65 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC070_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC070_EqualsCity-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC070_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC070_EqualsCity-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC070_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC070_EqualsCity-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC070_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC072_ReadDirectNotViaBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC072_ReadDirectNotViaBuffer-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC072_ReadDirectNotViaBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC072_ReadDirectNotViaBuffer-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC072_ReadDirectNotViaBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC072_ReadDirectNotViaBuffer-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC072_ReadDirectNotViaBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC075_LargeByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC075_LargeByteBuffer-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC075_LargeByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC075_LargeByteBuffer-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC075_LargeByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC075_LargeByteBuffer-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC075_LargeByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC077_LessAdditions"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC077_LessAdditions-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC077_LessAdditions"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC077_LessAdditions-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC077_LessAdditions"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC077_LessAdditions-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC077_LessAdditions (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC078_CalculateEarlier"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC078_CalculateEarlier-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC078_CalculateEarlier"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC078_CalculateEarlier-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC078_CalculateEarlier"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC078_CalculateEarlier-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC078_CalculateEarlier (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC079_RunWithoutByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC079_RunWithoutByteBuffer-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC079_RunWithoutByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC079_RunWithoutByteBuffer-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC079_RunWithoutByteBuffer"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC079_RunWithoutByteBuffer-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC079_RunWithoutByteBuffer (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC080_JDKArrayUtils"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC080_JDKArrayUtils-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC080_JDKArrayUtils"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC080_JDKArrayUtils-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC080_JDKArrayUtils"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC080_JDKArrayUtils-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC080_JDKArrayUtils (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC081_LoopUnroll"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC081_LoopUnroll-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC081_LoopUnroll"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC081_LoopUnroll-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC081_LoopUnroll"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC081_LoopUnroll-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC081_LoopUnroll (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC082_VectorSearchOnlyForLargeArray-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC082_VectorSearchOnlyForLargeArray-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC082_VectorSearchOnlyForLargeArray"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC082_VectorSearchOnlyForLargeArray-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC082_VectorSearchOnlyForLargeArray (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC083_MainLoop"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC083_MainLoop-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC083_MainLoop"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC083_MainLoop-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC083_MainLoop"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC083_MainLoop-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC083_MainLoop (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC090_MemorySegment_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC090_MemorySegment_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC090_MemorySegment_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC090_MemorySegment_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC090_MemorySegment_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC090_MemorySegment_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC090_MemorySegment_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC091_Reviewed83"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC091_Reviewed83-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC091_Reviewed83"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC091_Reviewed83-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC091_Reviewed83"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC091_Reviewed83-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC091_Reviewed83 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC092_ParseIntegerLessBranches_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC092_ParseIntegerLessBranches_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC092_ParseIntegerLessBranches_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC092_ParseIntegerLessBranches_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC092_ParseIntegerLessBranches_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC093_ParseIntegerLessCode_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC093_ParseIntegerLessCode_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC093_ParseIntegerLessCode_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC093_ParseIntegerLessCode_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC093_ParseIntegerLessCode_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC094_Reviewed91"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC094_Reviewed91-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC094_Reviewed91"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC094_Reviewed91-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC094_Reviewed91"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC094_Reviewed91-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC094_Reviewed91 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC095_Hash"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC095_Hash-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC095_Hash"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC095_Hash-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC095_Hash"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC095_Hash-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC095_Hash (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC096_ReadInt_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC096_ReadInt_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC096_ReadInt_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC096_ReadInt_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC096_ReadInt_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC096_ReadInt_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC096_ReadInt_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC097_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC097_EqualsCity-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC097_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC097_EqualsCity-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC097_EqualsCity"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC097_EqualsCity-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC097_EqualsCity (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC098_ParseTemperature_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC098_ParseTemperature_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC098_ParseTemperature_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC098_ParseTemperature_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC098_ParseTemperature_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC098_ParseTemperature_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC098_ParseTemperature_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC099_ArrayAccess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC099_ArrayAccess-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC099_ArrayAccess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC099_ArrayAccess-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC099_ArrayAccess"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC099_ArrayAccess-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC099_ArrayAccess (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC120_OnlyHashing_From95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC120_OnlyHashing_From95-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC120_OnlyHashing_From95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC120_OnlyHashing_From95-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC120_OnlyHashing_From95"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC120_OnlyHashing_From95-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC120_OnlyHashing_From95 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC121_UpdatedLoops"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC121_UpdatedLoops-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC121_UpdatedLoops"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC121_UpdatedLoops-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC121_UpdatedLoops"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC121_UpdatedLoops-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC121_UpdatedLoops (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC123_121plus105_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC123_121plus105_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC123_121plus105_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC123_121plus105_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC123_121plus105_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC123_121plus105_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC123_121plus105_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC125_Refined_121"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC125_Refined_121-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC125_Refined_121"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC125_Refined_121-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC125_Refined_121"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC125_Refined_121-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC125_Refined_121 (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC127_ParsingTempAI_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC127_ParsingTempAI_VOID-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC127_ParsingTempAI_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC127_ParsingTempAI_VOID-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC127_ParsingTempAI_VOID"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC127_ParsingTempAI_VOID-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC127_ParsingTempAI_VOID (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC000_Empty-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC000_Empty-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC000_Empty-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC000_Empty (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC001_Baseline-JDK_21_OPEN--XX__UseZGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC001_Baseline-JDK_21_OPEN--XX__UseG1GC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseSerialGC -Xms2g -Xmx2g"
export BINDING="taskset -c 0-7"
export PROG_OPTS="-wc 0 -mc 1 -t 8"
export JFR_FILE="data/benchmark-jfr/20260425-111552-BRC001_Baseline-JDK_21_OPEN--XX__UseSerialGC_-Xms2g_-Xmx2g_taskset_-c_0-7-10k.jfr"

CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    # Calculate ETA in pure bash using dollar-curly syntax without breaking FreeMarker
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: $ETA_MIN'm' $ETA_S's'"
else
    echo "[$CURRENT_RUN/$TOTAL_RUNS] Running BRC001_Baseline (Run: JDK21) - ETA: calculating..."
fi

echo -n "JDK_21_OPEN,\"-XX:+UseSerialGC\",\"-Xms2g -Xmx2g\",\"-wc 0 -mc 1 -t 8\",\"taskset -c 0-7\",10k,20260425-111552," >> data/benchmark-history/20260425-111552.csv
./execute-scenario.sh >> data/benchmark-history/20260425-111552.csv


echo "Analyzing results"
mvn -q exec:java -pl benchmark-harness -Dexec.mainClass="org.onebrc.benchmark.BenchmarkMatrix" -Dexec.args="analyze 20260425-111552"
