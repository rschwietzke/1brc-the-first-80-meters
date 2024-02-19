package org.rschwietzke.util;

import java.util.Arrays;

import org.rschwietzke.util.LineInputStream2.LineInfo;

public class City20 implements Comparable<City20>
{
	public String city;
	public int temperature;

	// for comparison, if needed
	private byte[] buffer;
	private int mismatchPos;

	// temporary

	private int hashCode;

	public Temperatures20 temperatures;

	public City20()
	{
	}

	public void data(final LineInfo line)
	{
		this.buffer = line.buffer();
		this.hashCode = line.hash();
		this.mismatchPos = line.separator();
		this.temperature = ParseDouble.parseInteger(line.buffer(), line.separator() + 1, line.end());
	}

	public int hashCode()
	{
		return this.hashCode;
	}

	@Override
	public boolean equals(final Object other)
	{
		final City20 o = (City20) other;
		if (o.hashCode != hashCode)
		{
			return false;
		}

		// compare the byte arrays
		final int pos = Arrays.mismatch(buffer, o.buffer);

		// the difference should be the splitpos upwards or no difference
		if (pos == -1)
		{
			return true;
		}
		return pos >= this.mismatchPos;
	}

	/**
	 * Create a copy for hashmap storing, rare event
	 * @return
	 */
	public static City20 materalize(final LineInfo line)
	{
		final City20 c = new City20();

		c.city = line.text();
		c.buffer = c.city.getBytes(); // needed for hashing
		c.hashCode = line.hash(); // to avoid recalculation
		c.temperatures = new Temperatures20();

		return c;
	}

	/**
	 * Only for debugging
	 */
	@Override
	public String toString()
	{
		return city != null ? city : "N/A";
	}

	@Override
	public int compareTo(City20 o)
	{
		// that is safe, because we use it only after we materalized it
		return CharSequence.compare(city, o.city);
	}

	public static class Temperatures20
	{
		private long min = Integer.MAX_VALUE;
		private long max = Integer.MIN_VALUE;
		private long total = 0L;
		private long count = 1L;

		private double round(final double value)
		{
			return Math.round(value / 10.0 * 10.0) / 10.0;
		}

		public Temperatures20 add(final int value)
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



}
