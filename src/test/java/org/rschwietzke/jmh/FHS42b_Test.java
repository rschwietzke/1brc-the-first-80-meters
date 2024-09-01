package org.rschwietzke.jmh;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.rschwietzke.jmh.FHS42b.FHS42b_FastHashSet;
import org.rschwietzke.jmh.FHS42b.Line;
import org.rschwietzke.jmh.FHS42b.Temperatures;

class FHS42b_Test
{
    @Test
    void ctrSmall()
    {
        var set = new FHS42b_FastHashSet(5, 0.5f);
        assertEquals(0, set.size());
        assertArrayEquals(new Temperatures[16], set.m_data);
    }

    @Test
    void ctrLarge()
    {
        var set = new FHS42b_FastHashSet(500, 0.5f);
        assertEquals(0, set.size());
        assertArrayEquals(new Temperatures[1024], set.m_data);
    }

    /**
     * Put simple
     */
    @Test
    void putSimple1()
    {
        var set = new FHS42b_FastHashSet(2, 0.5f);
        assertEquals(4, set.m_data.length);

        set.putOrUpdate(new Line("Foo;12.0\n", 0, 11));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[3].getCity());
        assertEquals(12.0d, set.m_data[3].getTotalTemperature());

        set.putOrUpdate(new Line("Bar;42.1\n", 0, 8));
        assertEquals(2, set.size());
        assertEquals("Bar", set.m_data[0].getCity());
        assertEquals(42.1d, set.m_data[0].getTotalTemperature());
    }

    /**
     * Put simple
     */
    @Test
    void putUpdate()
    {
        var set = new FHS42b_FastHashSet(2, 0.5f);
        assertEquals(4, set.m_data.length);

        set.putOrUpdate(new Line("Foo;12.0\n", 0, 11));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[3].getCity());
        assertEquals(12.0d, set.m_data[3].getTotalTemperature());

        set.putOrUpdate(new Line("Foo;12.1\n", 0, 11));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[3].getCity());
        assertEquals(24.1d, set.m_data[3].getTotalTemperature());
    }

    /**
     * Put simple with offset
     */
    @Test
    void putUpdateOffset()
    {
        var set = new FHS42b_FastHashSet(2, 0.5f);
        assertEquals(4, set.m_data.length);

        set.putOrUpdate(new Line("Foo;12.0\n", 100, 11));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[3].getCity());
        assertEquals(12.0d, set.m_data[3].getTotalTemperature());

        set.putOrUpdate(new Line("Foo;12.1\n", 33, 11));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[3].getCity());
        assertEquals(24.1d, set.m_data[3].getTotalTemperature());
    }

    /**
     * Put with a collision
     */
    @Test
    void putPutCollision()
    {
        var set = new FHS42b_FastHashSet(2, 0.5f);
        assertEquals(4, set.m_data.length);

        set.putOrUpdate(new Line("Foo;12.0\n", 0, 2));
        assertEquals(1, set.size());
        assertEquals("Foo", set.m_data[2].getCity());
        assertEquals(12.0d, set.m_data[2].getTotalTemperature());

        set.putOrUpdate(new Line("Bar;1.0\n", 110, 10));
        assertEquals(2, set.size());
        assertEquals("Bar", set.m_data[3].getCity());
        assertEquals(1.0d, set.m_data[3].getTotalTemperature());
    }

    /**
     * Resize
     */
    @Test
    void putWithResize()
    {
        var set = new FHS42b_FastHashSet(2, 0.5f);

        set.putOrUpdate(new Line("Foo;12.0\n", 0, 1));
        set.putOrUpdate(new Line("Bar;1.0\n", 110, 2));
        set.putOrUpdate(new Line("Mark;2.0\n", 330, 3));

        assertEquals(8, set.m_data.length);
        assertEquals(3, set.size());
        assertEquals("Foo", set.m_data[1].getCity());
        assertEquals(12.0d, set.m_data[1].getTotalTemperature());
        assertEquals("Bar", set.m_data[2].getCity());
        assertEquals(1.0d, set.m_data[2].getTotalTemperature());
        assertEquals("Mark", set.m_data[3].getCity());
        assertEquals(2.0d, set.m_data[3].getTotalTemperature());
    }
}
