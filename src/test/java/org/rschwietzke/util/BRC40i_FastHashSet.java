package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.TreeMap;

/**
 * this is a test version of the set to verify its correctness.
 * some extra stats and some things are open for checking
 */
public class BRC40i_FastHashSet
{
    private static final Temperatures FREE_KEY = null;
    /** Fill factor, must be between (0 and 1) */
    private static final float m_fillFactor = 0.5f;

    /** Keys and values */
    public Temperatures[] m_data;

    /** Current map size */
    private int m_size;
    /** Mask to calculate the original position */
    private int m_mask;
    /** We will resize a map once it reaches this size */
    private int m_threshold;

    /**
     * Count how often we searched, 0 is perfect
     */
    public int effort;
    public boolean firstSlotWasFree;
    public boolean hadToResize;

    /**
     * Holds our temperature data without the station, because the
     * map already knows that
     */
    static class Temperatures
    {
        private int min;
        private int max;
        private int total;
        private int count;
        private final byte[] data;
        private final int hashCode;

        public Temperatures(final byte[] city, final int hashCode, final int value)
        {
            this.data = city;
            this.hashCode = hashCode;
            this.min = value;
            this.max = value;
            this.total = value;
            this.count = 1;
        }

        /**
         * Combine two temperatures
         *
         * @param value the temperature to add
         */
        public void add(final int value)
        {
            if (value < this.min)
            {
                this.min = value;
            }
            else if (value > this.max)
            {
                this.max = value;
            }
            this.total += value;
            this.count++;
        }

        @Override
        public int hashCode()
        {
            return hashCode;
        }

        public int customEquals(final byte[] other)
        {
            return Arrays.mismatch(
                    data, 0, data.length,
                    other, 0, other.length);
        }

        @Override
        public boolean equals(Object o)
        {
            if (o instanceof Temperatures other)
            {
                return Arrays.mismatch(
                        data, 0, data.length,
                        other.data, 0, other.data.length) == -1;
            }
            else
            {
                return false;
            }
        }

        /**
         * 1BRC wants to have one decimal digits
         * @param value the value to transform
         * @return the rounded value
         */
        private double round(double value)
        {
            return Math.round(value) / 10.0;
        }

        public String getCity()
        {
            return new String(data, 0, data.length);
        }

        public double getTotalTemperature()
        {
            return round(this.total);
        }

        /**
         * Our final printing format
         */
        public String toString()
        {
            return round(min) + "," + round(((double)total / (double)count)) + "," + round(max);
        }
    }

    static class Line
    {
        private static int MIN_BUFFERSIZE = 500_000;

        private final byte[] data = new byte[MIN_BUFFERSIZE];

        int newlinePos = -1;

        int lineStartPos = 0;
        int semicolonPos = -1;

        int hashCode = 0;
        int temperature = 0;

        /**
        *
        * @param s the data to use
        * @param offset into the buffer
        * @param hashCode to overwrite for testing
        */
       public Line(final String s, int offset, int hashCode)
       {
           this(s, offset);
           this.hashCode = hashCode;
       }

        /**
         *
         * @param s the data to use
         * @param offset into the buffer
         */
        public Line(final String s, int offset)
        {
            // ok, for testing, we want to get the
            // data fit in
            var b = s.getBytes();

            // find ;
            int i = 0;
            for (i = 0; i < b.length; i++)
            {
                if (b[i] == ';')
                {
                    break;
                }
                hashCode = 31 * hashCode + b[i];
            }
            // have a ; and some data before
            assertTrue(i > 0);
            assertTrue(hashCode != 0);

            // last should be \n
            assertTrue(b[b.length - 1] == '\n');

            // set things
            System.arraycopy(b, 0, data, offset,b.length);
            lineStartPos = offset;
            newlinePos = offset + b.length - 1;
            semicolonPos = offset + i;

            // temperature
            i++;
            int multiplier = 1;
            for (; i < b.length - 1; i++)
            {
                if (b[i] == '-')
                {
                    multiplier = -1;
                }
                else if (b[i] == '.')
                {
                }
                else
                {
                    temperature = temperature * 10 + b[i] - 48;
                }
            }
            this.temperature *= multiplier;
        }
    }


    public BRC40i_FastHashSet( final int size, final float fillFactor )
    {
        final int capacity = arraySize(size, fillFactor);
        m_mask = capacity - 1;

        m_data = new Temperatures[capacity];
        m_threshold = (int) (capacity * fillFactor);
    }

    public void putOrUpdate(final Line line)
    {
        // reset stat
        this.effort = 0;
        this.hadToResize = false;
        this.firstSlotWasFree = false;

        final int ptr = line.hashCode & m_mask;
        Temperatures k = m_data[ ptr ];

        if ( k != FREE_KEY )
        {
            int l = line.semicolonPos - line.lineStartPos;
            byte[] data = k.data;

            // was
            // if (Arrays.equals(data, 0, data.length, line.data, line.lineStartPos, line.semicolonPos))
            // replaced to have less checks in the mix

            // check length first
            if (l == data.length)
            {
                // iterate old fashioned
                int start = line.lineStartPos;
                int i = 0;
                for (; i < l; i++)
                {
                    if (data[i] != line.data[start + i])
                    {
                        break;
                    }
                }
                if (i == l)
                {
                    k.add(line.temperature);
                    return;
                }
            }
        }
        else
        {
            this.firstSlotWasFree = true;

            // have to do a proper put to avoid filling up the map
            // without resizing
            put(new Temperatures(
                    Arrays.copyOfRange(
                            line.data,
                            line.lineStartPos, line.semicolonPos),
                        line.hashCode, line.temperature));

            return;
        }

        putOrUpdateSlow(line, ptr);
    }

    private void putOrUpdateSlow( final Line line, int ptr)
    {
        while ( true )
        {
            this.effort++;

            ptr = (ptr + 1) & m_mask; //that's next index
            Temperatures k = m_data[ ptr ];
            if ( k == FREE_KEY )
            {
                put(new Temperatures(
                        Arrays.copyOfRange(line.data, line.lineStartPos, line.semicolonPos),
                        line.hashCode, line.temperature));
                return;
            }
            else if (Arrays.mismatch(k.data, 0, k.data.length, line.data, line.lineStartPos, line.semicolonPos) == -1)
            {
                k.add(line.temperature);
                return;
            }
        }
    }

    private Temperatures put(final Temperatures key)
    {
        final int hash = key.hashCode();
        int ptr = hash & m_mask;
        Temperatures k = m_data[ptr];

        if ( k == FREE_KEY ) //end of chain already
        {
            m_data[ ptr ] = key;
            if ( m_size >= m_threshold )
                rehash( m_data.length * 2 ); //size is set inside
            else
                ++m_size;
            return null;
        }
        else if (k.customEquals( key.data ) == -1)
        {
            final Temperatures ret = m_data[ptr];
            m_data[ptr] = key;
            return ret;
        }

        while ( true )
        {
            ptr = (ptr + 1) & m_mask; //that's next index calculation
            k = m_data[ ptr ];
            if ( k == FREE_KEY )
            {
                m_data[ ptr ] = key;
                if ( m_size >= m_threshold )
                    rehash( m_data.length * 2 ); //size is set inside
                else
                    ++m_size;
                return null;
            }
            else if ( k.customEquals( key.data ) == -1)
            {
                final Temperatures ret = m_data[ptr];
                m_data[ptr] = key;
                return ret;
            }
        }
    }

    public int size()
    {
        return m_size;
    }

    private void rehash( final int newcapacity )
    {
        this.hadToResize = true;

        m_threshold = (int) (newcapacity * m_fillFactor);
        m_mask = newcapacity - 1;

        final int oldcapacity = m_data.length;
        final Temperatures[] oldData = m_data;

        m_data = new Temperatures[newcapacity];

        m_size = 0;

        for ( int i = 0; i < oldcapacity; i++ )
        {
            final Temperatures oldKey = oldData[ i ];
            if( oldKey != FREE_KEY)
            {
                put(oldKey);
            }
        }
    }

    /**
     * Returns a list of all values
     *
     * @return
     */
    public TreeMap<String, Temperatures> toTreeMap()
    {
        final var result = new TreeMap<String, Temperatures>();

        final int length = m_data.length;
        for (int i = 0; i < length; i++)
        {
            final Temperatures t = m_data[i];
            if (t != FREE_KEY)
            {
                result.put(t.getCity(), t);
            }
        }

        return result;
    }

    /**
     * Clears the map, reuses the data structure by clearing it out.
     * It won't shrink the underlying array!
     */
    public void clear()
    {
        this.m_size = 0;
        Arrays.fill(m_data, FREE_KEY);
    }

    /** Return the least power of two greater than or equal to the specified value.
     *
     * <p>Note that this function will return 1 when the argument is 0.
     *
     * @param x a long integer smaller than or equal to 2<sup>62</sup>.
     * @return the least power of two greater than or equal to the specified value.
     */
    public static long nextPowerOfTwo( long x ) {
        if ( x == 0 ) return 1;
        x--;
        x |= x >> 1;
        x |= x >> 2;
        x |= x >> 4;
        x |= x >> 8;
        x |= x >> 16;
        return ( x | x >> 32 ) + 1;
    }

    /** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to <code>Math.ceil( expected / f )</code>.
     *
     * @param expected the expected number of elements in a hash table.
     * @param f the load factor.
     * @return the minimum possible size for a backing array.
     * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
     */
    public static int arraySize( final int expected, final float f ) {
        final long s = Math.max( 2, nextPowerOfTwo(expected));
        if ( s > (1 << 30) )
        {
            throw new IllegalArgumentException( "Too large (" + expected + " expected elements with load factor " + f + ")" );
        }
        return (int)s;
    }
}
