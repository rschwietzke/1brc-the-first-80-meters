package org.onebrc.jmh;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.onebrc.again26.BRC125_Refined_121;
import org.onebrc.devoxxpl24.BRC67_StoreArrayLength;
import org.onebrc.devoxxpl24.BRC68_RemoveNewLinePos;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
public class Basic_Benchmark
{
//    @Benchmark
//    public void measure67()
//    {
//        BRC67_StoreArrayLength.main(new String[]{"data-1000m.txt", "0", "1"});
//    }
//
//    @Benchmark
//    public void measure68()
//    {
//        BRC68_RemoveNewLinePos.main(new String[]{"data-1000m.txt", "0", "1"});
//    }

    @Benchmark
    public void measure125() throws NoSuchMethodException, SecurityException
    {
        BRC125_Refined_121.main(new String[]{"-f", "data-1000m.txt", "-wc", "0", "-mc", "1"});
    }
    
    public static void main(String[] args) throws RunnerException
    {
        Options opt = new OptionsBuilder()
                .include(Basic_Benchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}