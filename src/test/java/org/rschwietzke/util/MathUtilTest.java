package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MathUtilTest
{
    @Test
    void doubleToString()
    {
        assertEquals("1.0", String.valueOf(MathUtil.round(1d)));
        assertEquals("-1.0", String.valueOf(MathUtil.round(-1d)));
        assertEquals("1.0", String.valueOf(MathUtil.round(1.0d)));
        assertEquals("-1.0", String.valueOf(MathUtil.round(-1.0d)));
        assertEquals("1.0", String.valueOf(MathUtil.round(1.02d)));
        assertEquals("111.4", String.valueOf(MathUtil.round(111.42d)));
        assertEquals("111.5", String.valueOf(MathUtil.round(111.46d)));
        assertEquals("111.4", String.valueOf(MathUtil.round(111.449d)));
        assertEquals("111.5", String.valueOf(MathUtil.round(111.451d)));
    }

    @Test
    void doubleToDoubleString()
    {
        assertEquals("1.0", String.valueOf(MathUtil.round(10)));
        assertEquals("-1.0", String.valueOf(MathUtil.round(-10)));
        assertEquals("1.1", String.valueOf(MathUtil.round(11)));
        assertEquals("-1.1", String.valueOf(MathUtil.round(-11)));
        assertEquals("10.2", String.valueOf(MathUtil.round(102)));
    }

    @Test
    void toMeanDoubleString()
    {
        assertEquals("0.5", String.valueOf(MathUtil.meanAndRound(10, 2)));
        assertEquals("5.0", String.valueOf(MathUtil.meanAndRound(100, 2)));
        assertEquals("5.0", String.valueOf(MathUtil.meanAndRound(100, 2)));
        assertEquals("3.3", String.valueOf(MathUtil.meanAndRound(100, 3)));
        assertEquals("2.5", String.valueOf(MathUtil.meanAndRound(100, 4)));
        assertEquals("2.0", String.valueOf(MathUtil.meanAndRound(100, 5)));
        assertEquals("25.0", String.valueOf(MathUtil.meanAndRound(1000, 4)));
    }
}
