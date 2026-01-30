package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BRC30_OpenMap_Test 
{
    @Test
    public void nextPowerOfTwo()
    {
        assertEquals(1, BRC30_OpenMap.LightMap.nextPowerOfTwo(1));
        assertEquals(2, BRC30_OpenMap.LightMap.nextPowerOfTwo(2));
        assertEquals(4, BRC30_OpenMap.LightMap.nextPowerOfTwo(3));
        assertEquals(4, BRC30_OpenMap.LightMap.nextPowerOfTwo(4));
        assertEquals(8, BRC30_OpenMap.LightMap.nextPowerOfTwo(5));
        assertEquals(8, BRC30_OpenMap.LightMap.nextPowerOfTwo(6));
        assertEquals(8, BRC30_OpenMap.LightMap.nextPowerOfTwo(7));
        assertEquals(8, BRC30_OpenMap.LightMap.nextPowerOfTwo(8));
        assertEquals(16, BRC30_OpenMap.LightMap.nextPowerOfTwo(9));
        assertEquals(1, BRC30_OpenMap.LightMap.nextPowerOfTwo(0));
        assertEquals(512, BRC30_OpenMap.LightMap.nextPowerOfTwo(413));
        assertEquals(512, BRC30_OpenMap.LightMap.nextPowerOfTwo(512));
    }
}
