#!/bin/bash

echo "Starting benchmark matrix run"

TOTAL_RUNS=204
CURRENT_RUN=0
START_TIME=$(date +%s)

export ITERATIONS=3

source $HOME/.sdkman/bin/sdkman-init.sh 2>/dev/null || true

echo "Capturing system information..."
SYSINFO_FILE="data/benchmark-history/20260425-111521-sysinfo.txt"
echo "Kernel: $(uname -r)" > $SYSINFO_FILE
if [ -f /etc/os-release ]; then
    . /etc/os-release
    echo "OS: $PRETTY_NAME" >> $SYSINFO_FILE
fi
echo "CPU: $(lscpu | grep 'Model name' | awk -F ':' '{print $2}' | xargs)" >> $SYSINFO_FILE
echo "CPU Cores: $(nproc)" >> $SYSINFO_FILE
echo "Memory: $(free -h | awk '/^Mem:/ {print $2}')" >> $SYSINFO_FILE

echo "JDK,GC_OPTS,VM_OPTS,PROG_OPTS,BINDING,DATA,RunTimestamp,Class,MedianRuntimeMs,Checksum,PerfRuntimeMs,JfrRuntimeMs,Instructions,Cycles,Branches,BranchMisses,L1Misses,LLCMisses,PageFaults,TaskClock,ContextSwitches,CpuMigrations,IPC,SecElapsed,SecUser,SecSys" > data/benchmark-history/20260425-111521.csv

echo "=================================================="
echo "JDK Block: JDK_21_OPEN"
echo "=================================================="

FreeMarker template error (DEBUG mode; use RETHROW in production!):
For "#if" condition: Expected a boolean, but this has evaluated to a method+sequence (wrapper: f.e.b.SimpleMethodModel):
==> block.jdk.isSdkman  [in template "run-script.sh.ftl" at line 31, column 6]

----
Tip: Maybe using obj.something instead of obj.isSomething will yield the desired value.
----

----
FTL stack trace ("~" means nesting-related):
	- Failed at: #if block.jdk.isSdkman  [in template "run-script.sh.ftl" at line 31, column 1]
----

Java stack trace (for programmers):
----
freemarker.core.NonBooleanException: [... Exception message was already printed; see it above ...]
	at freemarker.core.Expression.modelToBoolean(Expression.java:195)
	at freemarker.core.Expression.evalToBoolean(Expression.java:178)
	at freemarker.core.Expression.evalToBoolean(Expression.java:163)
	at freemarker.core.IfBlock.accept(IfBlock.java:49)
	at freemarker.core.Environment.visit(Environment.java:371)
	at freemarker.core.IteratorBlock$IterationContext.executedNestedContentForCollOrSeqListing(IteratorBlock.java:321)
	at freemarker.core.IteratorBlock$IterationContext.executeNestedContent(IteratorBlock.java:271)
	at freemarker.core.IteratorBlock$IterationContext.accept(IteratorBlock.java:244)
	at freemarker.core.Environment.visitIteratorBlock(Environment.java:645)
	at freemarker.core.IteratorBlock.acceptWithResult(IteratorBlock.java:108)
	at freemarker.core.IteratorBlock.accept(IteratorBlock.java:94)
	at freemarker.core.Environment.visit(Environment.java:335)
	at freemarker.core.Environment.visit(Environment.java:341)
	at freemarker.core.Environment.process(Environment.java:314)
	at freemarker.template.Template.process(Template.java:383)
	at org.onebrc.benchmark.ScriptGenerator.generate(ScriptGenerator.java:394)
	at org.onebrc.benchmark.BenchmarkMatrix.generate(BenchmarkMatrix.java:120)
	at org.onebrc.benchmark.BenchmarkMatrix.main(BenchmarkMatrix.java:64)
	at java.base/jdk.internal.reflect.DirectMethodHandleAccessor.invoke(DirectMethodHandleAccessor.java:104)
	at java.base/java.lang.reflect.Method.invoke(Method.java:565)
	at org.codehaus.mojo.exec.AbstractExecJavaBase.executeMainMethod(AbstractExecJavaBase.java:402)
	at org.codehaus.mojo.exec.ExecJavaMojo.executeMainMethod(ExecJavaMojo.java:142)
	at org.codehaus.mojo.exec.AbstractExecJavaBase.doExecClassLoader(AbstractExecJavaBase.java:377)
	at org.codehaus.mojo.exec.AbstractExecJavaBase.lambda$execute$0(AbstractExecJavaBase.java:287)
	at java.base/java.lang.Thread.run(Thread.java:1474)
