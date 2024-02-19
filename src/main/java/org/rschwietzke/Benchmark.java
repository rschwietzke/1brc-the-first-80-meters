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

/**
 * This is an interface to be implemented for our benchmark classes to enable it to run either standalone or later in a
 * JMH like setup to warm the VM and run
 *
 * @author Rene Schwietzke
 */
public abstract class Benchmark
{
	/**
	 * How often?
	 */
	private static int WARMUP_RUNS = 1;
	private static int MEASUREMENT_RUNS = 1;

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
     * Just runs our test and measures things
     * @throws NoSuchAlgorithmException
     */
    public static void run(final Constructor<? extends Benchmark> ctr, final String[] args)
    {
    	// did we get anything?
    	if (args.length == 0)
    	{
            System.err.println("Where is the argument? Need the file");
            return;
    	}

    	final String fileName = args[0];

        System.out.println("==== WARMUP =======================");
        var results = measure(ctr, Mode.WARMUP, WARMUP_RUNS, fileName);

        System.out.println("==== MEASUREMENT ==================");
        results = measure(ctr, Mode.MEASUREMENT, MEASUREMENT_RUNS, fileName);

        System.out.println("==== RESULT ========================");
        long total = 0;
        for (BenchmarkResult result : results)
        {
        	total += result.runtime;
        }
        System.out.println(String.format("Mean Measurement Runtime: %d ms", Math.round(total / results.size())));
    }

    private static List<BenchmarkResult> measure(final Constructor<? extends Benchmark> ctr, Mode mode, int iterationCount, String fileName)
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

	        System.out.println(String.format(mode == Mode.WARMUP ? "Warmup Runtime: %d ms" : "Measurement Runtime: %d ms", result.runtime));
	        System.out.println(String.format("Checksum: %s", result.data));
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

//            System.out.println(data);

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

    private static record BenchmarkResult(long runtime, String data)
    {
    }

    private enum Mode
    {
    	WARMUP, MEASUREMENT
    }
}
