package org.onebrc.util;

public class MathUtil
{
    // three digits are 0.1 aka 10.0d
    private static final double ROUND_FACTOR = 10.0d;
    
    /**
     * A double adjusted to one decimal point (properly rounded)
     * @return a result as double with one decimal digit
     */
    public static double round(final double value)
    {
        return Math.round(value * ROUND_FACTOR) / ROUND_FACTOR;
    }

    /**
     * We assume we use an int to store double with only one decimal digit
     *
     * @param value
     * @return a result as double with one decimal digit
     */
    public static double roundFromIntDouble(final double value)
    {
        return Math.round(value) / ROUND_FACTOR;
    }

    /**
     * We assume we use an int to store double with with only one decimal digit
     *
     * @param value
     * @return a result as double with one decimal digit
     */
    public static double round(final int value)
    {
        return value / ROUND_FACTOR;
    }

    /**
     *
     * @param total A total as int but represents a one decimal digit double
     * @param count the total occurrences
     * @return a result as double with one decimal digit
     */
    public static double meanAndRound(final long total, final int count)
    {
        return Math.round((double)total / (double)count) / ROUND_FACTOR;
    }

    /**
     * Standard formatter to ensure we get the same everywhere
     */
    public static String toString(double sum, int count, double min, double max)
    {
        final double mean = sum / (double)count;
        return count + "/" + round(min) + "/" + round(mean) + "/" + round(max);
    }

    /**
     * Standard formatter to ensure we get the same everywhere
     */
    public static String toString(double sum, long count, double min, double max)
    {
        final double mean = sum / (double)count;
        return count + "/" + round(min) + "/" + round(mean) + "/" + round(max);
    }
    
    /**
     * Standard formatter to ensure we get the same everywhere. We assume that we
     * store doubles as int with one decimal digit precision.
     */
    public static String toStringFromInteger(long sum, long count, int min, int max)
    {
        final double mean = (double)(sum / 10d) / (double)count;
        return count + "/" + round(min / 10d) + "/" + round(mean) + "/" + round(max / 10d);
    }

    /**
     * Standard formatter to ensure we get the same everywhere. We assume that we
     * store doubles as int with one decimal digit precision.
     */
    public static String toStringFromInteger(int sum, int count, int min, int max)
    {
        final double mean = (double)(sum / 10d) / (double)count;
        return count + "/" + round(min / 10d) + "/" + round(mean) + "/" + round(max / 10d);
    }

    /**
     * Standard formatter to ensure we get the same everywhere. We assume that we
     * store doubles as int with one decimal digit precision.
     */
    public static String toString(int sum, int count, int min, int max)
    {
        final double mean = (double)(sum / 10) / (double)count;
        return count + "/" + round(min) + "/" + round(mean) + "/" + round(max);
    }

    /**
     * Standard formatter to ensure we get the same everywhere
     */
    public static String toString(int count, double min, double max, double mean)
    {
        return count + "/" + round(min) + "/" + round(mean) + "/" + round(max);
    }
}
