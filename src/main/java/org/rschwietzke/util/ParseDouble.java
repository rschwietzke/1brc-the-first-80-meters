package org.rschwietzke.util;

/**
 * This is a small helper class for parsing char sequences and converting them into int, long, and double. This implementation is optimized for
 * speed not functionality. It is only able to parse plain numbers with base 10, e.g. 100828171. In case of parsing problems it will fall
 * back to the JDK but will lose the speed advantage of course.
 *
 * @author René Schwietzke
 */
public final class ParseDouble
{
    private static final int DIGITOFFSET = 48;

    private static final double[] multipliers = {
        1, 1, 0.1, 0.01, 0.001, 0.000_1, 0.000_01, 0.000_001, 0.000_000_1, 0.000_000_01,
        0.000_000_001, 0.000_000_000_1, 0.000_000_000_01, 0.000_000_000_001, 0.000_000_000_000_1,
        0.000_000_000_000_01, 0.000_000_000_000_001, 0.000_000_000_000_000_1, 0.000_000_000_000_000_01};

    /**
     * Parses the chars and returns the result as double. Raises a NumberFormatException in case of an non-convertible
     * char set. Due to conversion limitations, the result might be different from Double.parseDouble aka precision.
     * We also drop negative numbers and fallback to Double.parseDouble.
     *
     * @param s
     *            the characters to parse
     * @return the converted string as double
     * @throws java.lang.NumberFormatException
     */
    public static double parseDouble(final String s, final int offset, final int end)
    {
        final int negative = s.charAt(offset) == '-' ? offset + 1 : offset;

        long value = 0;
        int decimalPos = end;

        for (int i = negative; i <= end; i++)
        {
            final int d = s.charAt(i);
            if (d == '.')
            {
                decimalPos = i;
                continue;
            }
            final int v = d - DIGITOFFSET;
            value = ((value << 3) + (value << 1));
            value += v;
        }

        // adjust the decimal places
        value = negative != offset ? -value : value;
        return value * multipliers[end - decimalPos + 1];
    }

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseInteger(final String s, final int offset, final int end)
    {
        final int negative = s.charAt(offset) == '-' ? offset + 1 : offset;

        int value = 0;

        for (int i = negative; i <= end; i++)
        {
            final int d = s.charAt(i);
            if (d == '.')
            {
                continue;
            }
            final int v = d - DIGITOFFSET;
            value = ((value << 3) + (value << 1));
            value += v;
        }

        value = negative != offset ? -value : value;
        return value;
    }

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseInteger(final byte[] b, final int offset, final int end)
    {
        final int negative = b[offset] == (byte)'-' ? offset + 1 : offset;

        int value = 0;

        for (int i = negative; i <= end; i++)
        {
            final byte d = b[i];
            if (d == (byte)'.')
            {
                continue;
            }
            final int v = d - DIGITOFFSET;
            value = ((value << 3) + (value << 1));
            value += v;
        }

        value = negative != offset ? -value : value;
        return value;
    }

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseIntegerReverse(final byte[] b, final int offset, final int end)
    {
        int value = 0;

        // we know the first three pieces already 9.9
        value += (b[end] - DIGITOFFSET);
        value += (b[end - 2] - DIGITOFFSET) * 10;

        final int negative = b[offset] == (byte)'-' ? offset + 1 : offset;

        for (int i = end - 3; i >= negative; i--)
        {
            final byte d = b[i];
            final int v = (d - DIGITOFFSET) * 100;
            value += v;
        }

        value = negative != offset ? -value : value;
        return value;
    }

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseIntegerFixed(final byte[] b, final int offset, final int end)
    {
        final int length = end - offset; // one is missing, we care for that later

    	// we know the first three pieces already 9.9
    	int p0 = b[end];
    	int p1 = b[end - 2] * 10;
    	int value = p0 + p1 - (DIGITOFFSET + DIGITOFFSET * 10);

    	// we are 9.9
        if (length == 2)
        {
        	return value;
        }

        // ok, we are either -9.9 or 99.9 or -99.9
        if (b[offset] != (byte)'-')
        {
        	// we are 99.9
        	value += b[end - 3] * 100 - DIGITOFFSET * 100;
        	return value;
        }

        // we are either -99.9 or -9.9
        if (length == 3)
        {
        	// -9.9
        	return -value;
        }

        // -99.9
    	value += b[end - 3] * 100 - DIGITOFFSET * 100;
        return -value;
    }

    /**
     * Parses a double but ends up with an int, only because we know
     * the format of the results -99.9 to 99.9
     */
    public static int parseDoubleAsInt29g(final byte[] b, int pos, final int newlinePos)
    {
        int l = newlinePos - pos;
        if (b[pos] == (byte)'-')
        {
            // -9.9
            if (l == 4)
            {
                var p1 = b[pos + 1];
                var p0 = b[pos + 3];

                var value =
                        ((p1 - DIGITOFFSET) * 10) +
                        (p0 - DIGITOFFSET);
                return -value;
            }
            else
            {
                // -99.9
                var p2 = b[pos + 1];
                var p1 = b[pos + 2];
                var p0 = b[pos + 4];

                var value =
                        ((p2 - DIGITOFFSET) * 100) +
                        ((p1 - DIGITOFFSET) * 10) +
                        (p0 - DIGITOFFSET);
                return -value;
            }
        }
        else
        {
            // 9.9
            if (l == 3)
            {
                var p1 = b[pos];
                var p0 = b[pos + 2];

                var value =
                        ((p1 - DIGITOFFSET) * 10) +
                        (p0 - DIGITOFFSET);
                return value;
            }
            else
            {
                // 99.9
                var p2 = b[pos];
                var p1 = b[pos + 1];
                var p0 = b[pos + 3];

                var value =
                        ((p2 - DIGITOFFSET) * 100) +
                        ((p1 - DIGITOFFSET) * 10) +
                        (p0 - DIGITOFFSET);
                return value;
            }
        }
    }

    /**
     * In case we want to parse the full string
     */
    public static double parseDouble(final String s)
    {
    	return parseDouble(s, 0, s.length() - 1);
    }
}