/*
 * Copyright (c) 2005-2023 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rschwietzke.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastHashSet20
{
	// we need only the reference, not the content
    private static final City20 FREE_KEY = new City20();
    private static final City20 REMOVED_KEY = new City20();

    /** Keys and values */
    private City20[] m_data;

    /** Fill factor, must be between (0 and 1) */
    private final float m_fillFactor;
    /** We will resize a map once it reaches this size */
    private int m_threshold;
    /** Current map size */
    private int m_size;
    /** Mask to calculate the original position */
    private int m_mask;

    public FastHashSet20()
    {
        this(13, 0.5f);
    }

    public FastHashSet20( final int size, final float fillFactor )
    {
        if ( fillFactor <= 0 || fillFactor >= 1 )
            throw new IllegalArgumentException( "FillFactor must be in (0, 1)" );
        if ( size <= 0 )
            throw new IllegalArgumentException( "Size must be positive!" );
        final int capacity = arraySize(size, fillFactor);
        m_mask = capacity - 1;
        m_fillFactor = fillFactor;

        m_data = new City20[capacity];
        Arrays.fill( m_data, FREE_KEY );

        m_threshold = (int) (capacity * fillFactor);
    }

    public City20 get( final City20 key )
    {
        int ptr = key.hashCode() & m_mask;
        City20 k = m_data[ ptr ];

        if ( k == FREE_KEY )
        {
            return null;  //end of chain already
        }

        if ( k.hashCode() == key.hashCode() && k.equals( key ) )
        {
            return k;
        }

        while ( true )
        {
            ptr = (ptr + 1) & m_mask; //that's next index
            k = m_data[ ptr ];
            if ( k == FREE_KEY )
            {
                return null;
            }
            if (k.hashCode() == key.hashCode() && k.equals( key ))
            {
                return k;
            }
        }
    }

    public City20 put(final City20 key)
    {
        int ptr = key.hashCode() & m_mask;
        City20 k = m_data[ptr];

        if ( k == FREE_KEY ) //end of chain already
        {
            m_data[ ptr ] = key;
            if ( m_size >= m_threshold )
                rehash( m_data.length * 2 ); //size is set inside
            else
                ++m_size;
            return null;
        }
        else if (k.hashCode() == key.hashCode() && k.equals( key ))
        {
            final City20 ret = m_data[ptr];
            m_data[ptr] = key;
            return ret;
        }

        int firstRemoved = -1;
        if ( k == REMOVED_KEY )
            firstRemoved = ptr; //we may find a key later

        while ( true )
        {
            ptr = (ptr + 1) & m_mask; //that's next index calculation
            k = m_data[ ptr ];
            if ( k == FREE_KEY )
            {
                if ( firstRemoved != -1 )
                    ptr = firstRemoved;
                m_data[ ptr ] = key;
                if ( m_size >= m_threshold )
                    rehash( m_data.length * 2 ); //size is set inside
                else
                    ++m_size;
                return null;
            }
            else if ( k.hashCode() == key.hashCode() && k.equals( key ) )
            {
                final City20 ret = m_data[ptr];
                m_data[ptr] = key;
                return ret;
            }
            else if ( k == REMOVED_KEY )
            {
                if ( firstRemoved == -1 )
                    firstRemoved = ptr;
            }
        }
    }

    public int size()
    {
        return m_size;
    }

    private void rehash( final int newcapacity )
    {
        m_threshold = (int) (newcapacity * m_fillFactor);
        m_mask = newcapacity - 1;

        final int oldcapacity = m_data.length;
        final City20[] oldData = m_data;

        m_data = new City20[newcapacity];
        Arrays.fill( m_data, FREE_KEY );

        m_size = 0;

        for ( int i = 0; i < oldcapacity; i++ )
        {
            final City20 oldKey = oldData[ i ];
            if( oldKey != FREE_KEY && oldKey != REMOVED_KEY )
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
    public List<City20> keys()
    {
        final List<City20> result = new ArrayList<>(this.m_size);

        final int length = m_data.length;
        for (int i = 0; i < length; i++)
        {
            final City20 o = m_data[i];
            if (o != FREE_KEY && o != REMOVED_KEY)
            {
                result.add(o);
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
       final long s = Math.max( 2, nextPowerOfTwo( (long)Math.ceil( expected / f ) ) );
       if ( s > (1 << 30) ) throw new IllegalArgumentException( "Too large (" + expected + " expected elements with load factor " + f + ")" );
       return (int)s;
   }
}