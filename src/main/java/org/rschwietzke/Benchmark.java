package org.rschwietzke;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Supplier;

/**
 * This is an interface to be implemented for our benchmark classes to enable it to run either standalone or later in a
 * JMH like setup to warm the VM and run
 *
 * @author Rene Schwietzke
 */
public abstract class Benchmark
{
    /**
     * The interface to implement to make all implementation easily pluggable
     *
     * @param fileName
     *            the file name to use
     *
     * @return the result as string, we don't print, we let others do that
     *
     * @throws IOException
     *             in case we have an issue with the file name
     */
    public abstract String run(final String fileName) throws IOException;

    public static void run(final Class<? extends Benchmark> clazz, final String[] args)
    {
    	try
    	{
			run(clazz.getDeclaredConstructor(), args);
		}
    	catch (NoSuchMethodException | SecurityException e)
    	{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Helper for printing
     */
    static void print(final boolean batchMode, final Supplier<String> s)
    {
    	if (!batchMode)
    	{
    		System.out.print(s.get());
    	}
    }

    /**
     * Error output
     */
    private static void printError()
    {
        System.err.println("Where are the arguments?");
        System.err.println("Usage: <file> [warmUpCount] [measurementCount] [--batchmode <comment>]");
    }

    /**
     * Just runs our test and measures things
     * @throws NoSuchAlgorithmException
     */
    public static void run(final Constructor<? extends Benchmark> ctr, final String[] args)
    {
    	// did we get anything?
    	if (args.length < 3)
    	{
    		printError();
            return;
    	}

    	final String fileName = args[0];

    	// do we have an additional second and third?
    	final int warmUpRuns = Integer.valueOf(args[1]);
    	final int measurementRuns = Integer.valueOf(args[2]);

    	final boolean batchMode = args.length > 3 && args[3].equalsIgnoreCase("--batchmode");

    	final String batchComment;
    	if (batchMode && args.length < 5)
    	{
   			printError();
   			return;
    	}
    	else if (batchMode && args.length == 5)
    	{
    		batchComment = args[4];
    	}
    	else
    	{
    		batchComment = "";
    	}

    	Benchmark.print(batchMode, () -> "==== WARMUP ==================\n");
   		var results = measure(ctr, Mode.WARMUP, warmUpRuns, fileName, batchMode);

        Benchmark.print(batchMode, () -> "==== MEASUREMENT ==================\n");
        results = measure(ctr, Mode.MEASUREMENT, measurementRuns, fileName, batchMode);

        Benchmark.print(batchMode, () -> "==== RESULT ========================\n");
        long total = 0;
        for (BenchmarkResult result : results)
        {
        	total += result.runtime;
        }
        final int mean = Math.round(total / results.size());

        //  verify the checksum
        final var crcs = results.stream().map(r -> r.crc).sorted().distinct().toList();
        if (crcs.size() > 1)
        {
        	throw new RuntimeException("CRC results vary!!!");
        }

        if (batchMode)
        {
        	var clazzName = ctr.getDeclaringClass().getName();
        	System.out.print(String.format("%s - %s - %s: %d ms%n", clazzName, batchComment, crcs.get(0), mean));
        }
        else
        {
        	System.out.println(String.format("Mean Measurement Runtime: %d ms", mean));
        }
    }

    private static List<BenchmarkResult> measure(final Constructor<? extends Benchmark> ctr,
    		Mode mode, int iterationCount, String fileName,
    		final boolean batchMode)
    {
        final List<BenchmarkResult> results = new ArrayList<>();

    	for (int i = 0; i < iterationCount; i++)
    	{
    		Benchmark benchmark = null;
			try
			{
				benchmark = ctr.newInstance();
			}
			catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | SecurityException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			var result = measure(benchmark, fileName);
	        results.add(result);

	        Benchmark.print(batchMode,
	        		() -> String.format(mode == Mode.WARMUP ? "Warmup Runtime (%s): %d ms%n" : "Measurement Runtime (%s): %d ms%n", result.crc, result.runtime));
    	}

    	return results;
    }

    public static BenchmarkResult measure(final Benchmark benchmark, final String fileName)
    {
        try
        {
            final long start = System.currentTimeMillis();
            final String data = benchmark.run(fileName);
            final long end = System.currentTimeMillis();

            // checksum the data to ensure we always produce the same output
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] encodedhash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            return new BenchmarkResult(end - start, Base64.getEncoder().encodeToString(encodedhash));
        }
        catch (IOException ioe)
        {
            System.err.println("Darn... what just happened?");
            ioe.printStackTrace();
        }
        catch (NoSuchAlgorithmException alg)
        {
            System.err.println("SHA-256, where are you?");
            alg.printStackTrace();
        }

        return null;
    }

    private static record BenchmarkResult(long runtime, String crc)
    {
    }

    private enum Mode
    {
    	WARMUP, MEASUREMENT
    }
}
