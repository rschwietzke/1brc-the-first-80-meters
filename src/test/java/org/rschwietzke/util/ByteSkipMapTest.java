package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.rschwietzke.util.bsm.IByteSkipMap;
import org.rschwietzke.util.bsm.RootByteSkipMap;

class ByteSkipMapTest
{
    @Test
    void firstEmpty()
    {
        var bsm = new RootByteSkipMap<String>();
        var n = bsm.getNext((byte)0);
        assertNotNull(n);
        assertNull(n.getValue());
    }

    @Test
    void firstKnown()
    {
        var bsm = new RootByteSkipMap<String>();
        var n1 = bsm.getNext((byte)0);
        n1.setValue("A");

        var n2 = bsm.getNext((byte)0);
        assertEquals("A", n2.getValue());
    }


    @Test
    void firstTwoDifferent()
    {
        var bsm = new RootByteSkipMap<String>();
        var n1 = bsm.getNext((byte)0);
        n1.setValue("A");

        var n2 = bsm.getNext((byte)1);
        assertNull(n2.getValue());
        n2.setValue("B");

        assertEquals("A", bsm.getNext((byte)0).getValue());
        assertEquals("B", bsm.getNext((byte)1).getValue());
    }

    @Test
    void twoIdenticalString()
    {
        var root = new RootByteSkipMap<String>();
        IByteSkipMap<String> pos = root;

        for (byte b : "ABCD".getBytes())
        {
            pos = pos.getNext(b);
        }
        pos.setValue("ABCD");

        pos = root;
        for (byte b : "ABCD".getBytes())
        {
            pos = pos.getNext(b);
        }
        assertEquals("ABCD", pos.getValue());

        for (byte b : "ABCDE".getBytes())
        {
            pos = pos.getNext(b);
        }
        assertNull(pos.getValue());
    }

    @Test
    void unicode()
    {
        var root = new RootByteSkipMap<String>();
        IByteSkipMap<String> pos = root;

        for (byte b : "Tromsø".getBytes())
        {
            pos = pos.getNext(b);
        }
        pos.setValue("ABCD");

        pos = root;
        for (byte b : "Tromsø".getBytes())
        {
            pos = pos.getNext(b);
        }
        assertEquals("ABCD", pos.getValue());

        for (byte b : "Wrocław".getBytes())
        {
            pos = pos.getNext(b);
        }
        assertNull(pos.getValue());
    }
}
