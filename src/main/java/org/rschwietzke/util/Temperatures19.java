package org.rschwietzke.util;

public class Temperatures19
{
	private int min;
	private int max;
	private long total;
	private long count = 1;

	public Temperatures19(final int value)
	{
		this.total = value;
		this.max = value;
		this.min = value;
	}

	private double round(final double value)
	{
		return Math.round(value / 10.0 * 10.0) / 10.0;
	}

	public Temperatures19 add(final int value)
	{
		min = Math.min(min, value);
		max = Math.max(max, value);
		total += value;
		count++;

		return this;
	}

	public String toString()
	{
		return round(min) + "," + round((double)total / count) + "," + round(max);
	}
}


