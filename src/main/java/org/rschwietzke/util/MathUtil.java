package org.rschwietzke.util;

public class MathUtil
{
    /**
     * A double adjusted to one decimal point (properly rounded)
     * @return a result as double with one decimal digit
     */
    public static double round(final double value)
    {
        return Math.round(value * 10.0) / 10.0;
    }

    /**
     * We assume we use an int to store double with with only one decimal digit
     *
     * @param value
     * @return a result as double with one decimal digit
     */
    public static double roundFromIntDouble(final double value)
    {
        return Math.round(value) / 10.0d;
    }

    /**
     * We assume we use an int to store double with with only one decimal digit
     *
     * @param value
     * @return a result as double with one decimal digit
     */
    public static double round(final int value)
    {
        return value / 10.0d;
    }

    /**
     *
     * @param total A total as int but represents a one decimal digit double
     * @param count the total occurrences
     * @return a result as double with one decimal digit
     */
    public static double meanAndRound(final long total, final int count)
    {
        return Math.round((double)total / (double)count) / 10.0;
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
