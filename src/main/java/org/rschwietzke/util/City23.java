package org.rschwietzke.util;

import java.util.Arrays;

import org.rschwietzke.util.LineInputStream2.LineInfo;

public class City23 implements Comparable<City23>
{
	public String city;
	public int temperature;

	// for comparison, if needed
	private byte[] buffer;
	private int mismatchPos;

	// temporary

	private int hashCode;

	public Temperatures20 temperatures;

	public City23()
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

	public boolean equals(final City23 o)
	{
		// compare the byte arrays
		final int pos = Arrays.mismatch(buffer, o.buffer);

		// the difference should be the splitpos upwards or no difference
		return pos >= this.mismatchPos || pos == -1;
	}

	/**
	 * Create a copy for hashmap storing, rare event
	 * @return
	 */
	public static City23 materalize(final LineInfo line)
	{
		final City23 c = new City23();

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
	public int compareTo(City23 o)
	{
		// that is safe, because we use it only after we materalized it
		return CharSequence.compare(city, o.city);
	}

	public static class Temperatures20
	{
		private int min = Integer.MAX_VALUE;
		private int max = Integer.MIN_VALUE;
		private long total = 0L;
		private int count = 0;

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
