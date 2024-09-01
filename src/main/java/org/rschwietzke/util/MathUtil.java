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
}
