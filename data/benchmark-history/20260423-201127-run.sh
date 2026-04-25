#!/bin/bash

echo "Starting benchmark matrix run"

TOTAL_RUNS=192
CURRENT_RUN=0
START_TIME=$(date +%s)

source $HOME/.sdkman/bin/sdkman-init.sh 2>/dev/null || true

echo "Capturing system information..."
SYSINFO_FILE="data/benchmark-history/20260423-201127-sysinfo.txt"
echo "Kernel: $(uname -r)" > $SYSINFO_FILE
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "OS: $PRETTY_NAME" >> $SYSINFO_FILE
fi
echo "CPU: $(lscpu | grep 'Model name' | awk -F ':' '{print $2}' | xargs)" >> $SYSINFO_FILE
echo "CPU Cores: $(nproc)" >> $SYSINFO_FILE
echo "Memory: $(free -h | awk '/^Mem:/ {print $2}')" >> $SYSINFO_FILE

echo "JDK,GC_OPTS,VM_OPTS,PROG_OPTS,TASKSET,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,Instructions,Cycles,Branches,BranchMisses,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys" > data/benchmark-history/20260423-201127.csv

echo "=================================================="
echo "JDK Block: JDK_21_OPEN"
echo "=================================================="
sdk install java 21.0.6-tem
sdk use java 21.0.6-tem
export JAVA_HOME="$SDKMAN_DIR/candidates/java/21.0.6-tem"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Compiling for JDK release 21"
mvn clean compile -pl 1brc-implementations -Dmaven.compiler.source=21 -Dmaven.compiler.target=21 -Pjdk21-preview

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_21) - ETA: calculating..."
fi
echo -n "JDK_21_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

echo "=================================================="
echo "JDK Block: JDK_25_OPEN"
echo "=================================================="
sdk install java 25.0.2-tem
sdk use java 25.0.2-tem
export JAVA_HOME="$SDKMAN_DIR/candidates/java/25.0.2-tem"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Compiling for JDK release 25"
mvn clean compile -pl 1brc-implementations -Dmaven.compiler.source=25 -Dmaven.compiler.target=25

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC000_Empty"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC000_Empty (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC001_Baseline"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC001_Baseline (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC010_NoStream"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC010_NoStream (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC012_SplitRemoved"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC012_SplitRemoved (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC014_NewParseDouble"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC014_NewParseDouble (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC015_ParseDoubleSimpler1"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC015_ParseDoubleSimpler1 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC016_NoExtraString"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC016_NoExtraString (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseZGC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseZGC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms4g -Xmx4g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms4g -Xmx4g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms2g -Xmx2g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms2g -Xmx2g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="1000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",1k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

export CLASS="org.onebrc.again26.BRC017_ParseDoubleSimpler2"
export DATA="10000.txt"
export JVM_OPTS="-XX:+UseG1GC -Xms1g -Xmx1g"
export TASKSET="-c 0-7"
export PROG_OPTS="-mc 1 -wc 0 -t 8"
CURRENT_RUN=$((CURRENT_RUN + 1))
ELAPSED=$(($(date +%s) - START_TIME))
if [ $CURRENT_RUN -gt 1 ]; then
    AVG_TIME=$((ELAPSED / (CURRENT_RUN - 1)))
    REMAINING_RUNS=$((TOTAL_RUNS - CURRENT_RUN + 1))
    ETA_SEC=$((AVG_TIME * REMAINING_RUNS))
    ETA_MIN=$((ETA_SEC / 60))
    ETA_S=$((ETA_SEC % 60))
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: ${ETA_MIN}m ${ETA_S}s"
else
    echo "[${CURRENT_RUN}/${TOTAL_RUNS}] Running BRC017_ParseDoubleSimpler2 (Run: DEFAULT_25) - ETA: calculating..."
fi
echo -n "JDK_25_OPEN,\"-XX:+UseG1GC\",\"-Xms1g -Xmx1g\",\"-mc 1 -wc 0 -t 8\",\"-c 0-7\",10k,20260423-201127," >> data/benchmark-history/20260423-201127.csv
./execute-scenario.sh >> data/benchmark-history/20260423-201127.csv

echo "Analyzing results"
mvn -q exec:java -pl benchmark-harness -Dexec.mainClass="org.onebrc.benchmark.BenchmarkMatrix" -Dexec.args="analyze 20260423-201127"
