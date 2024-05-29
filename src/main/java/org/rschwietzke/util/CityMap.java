package org.rschwietzke.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rschwietzke.st.Example36_IfInMapGetDifferent.City;
import org.rschwietzke.st.Example36_IfInMapGetDifferent.LineInfo;
import org.rschwietzke.st.Example36_IfInMapGetDifferent.Temperatures;

/**
 * This replaces the hashmap and the hash calculation
 * with a fancy lookup.
 */
public class CityMap
{

	private List<City> cities = new ArrayList<>(500);

	public static class City implements Comparable<City>
	{
		public String city;
		public Temperatures temperatures = new Temperatures();

		public int hashCode()
		{
			return city.hashCode();
		}

		public boolean equals(final City o)
		{
			return o.city.equals(this.city);
		}

		public City(final String city)
		{
			this.city = city;
			this.temperatures = new Temperatures();
		}

		/**
		 * Only for debugging
		 */
		@Override
		public String toString()
		{
			return city;
		}

		@Override
		public int compareTo(City o)
		{
			return CharSequence.compare(city, o.city);
		}
	}

	public static class Temperatures
	{
		private int min = Integer.MAX_VALUE;
		private int max = Integer.MIN_VALUE;
		private long total = 0L;
		private int count = 0;

		private double round(final double value)
		{
			return Math.round(value / 10.0 * 10.0) / 10.0;
		}

		public Temperatures add(final int value)
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
