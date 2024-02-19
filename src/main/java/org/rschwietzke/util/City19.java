package org.rschwietzke.util;

import java.util.Arrays;

import org.rschwietzke.util.LineInputStream2.LineInfo;

public class City19 implements Comparable<City19>
{
	public String city;
	public int temperature;

	// for comparison, if needed
	private byte[] buffer;
	private int mismatchPos;

	// temporary

	private int hashCode;

	public City19()
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
		final City19 o = (City19) other;
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
	public static City19 materalize(LineInfo line)
	{
		City19 c = new City19();

		c.city = line.text();
		c.buffer = c.city.getBytes();
		c.hashCode = line.hash();

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
	public int compareTo(City19 o)
	{
		// that is safe, because we use it only after we materalized it
		return CharSequence.compare(city, o.city);
	}
}
