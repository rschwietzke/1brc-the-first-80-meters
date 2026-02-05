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
    
    @Test 
    public void basicCtr()
    {
        BRC30_OpenMap.LightMap<String, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        assertEquals(0, map.size());
        assertEquals(0, map.keys().size());
    }
    
    @Test 
    public void basicPutGet()
    {
        BRC30_OpenMap.LightMap<String, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        
        assertEquals(1, map.get("one"));
        assertEquals(2, map.get("two"));
        assertEquals(3, map.get("three"));
        assertEquals(null, map.get("four"));
        assertEquals(3, map.size());
    }
    
    @Test
    public void customKeyPutGet()
    {
        BRC30_OpenMap.LightMap<CString, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        map.put(new CString("one", "one".hashCode()), 1);
        map.put(new CString("two", "two".hashCode()), 2);
        map.put(new CString("three", "three".hashCode()), 3);
        
        assertEquals(1, map.get(new CString("one", "one".hashCode())));
        assertEquals(2, map.get(new CString("two", "two".hashCode())));
        assertEquals(3, map.get(new CString("three", "three".hashCode())));
        assertEquals(null, map.get(new CString("four", "four".hashCode())));
        assertEquals(3, map.size());
    }
    
    @Test
    public void collisions()
    {
        BRC30_OpenMap.LightMap<CString, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        map.put(new CString("one", 21), 1);
        map.put(new CString("two", 21), 2);
        map.put(new CString("three", 21), 3);
        
        assertEquals(1, map.get(new CString("one", 21)));
        assertEquals(2, map.get(new CString("two", 21)));
        assertEquals(3, map.get(new CString("three", 21)));
        assertEquals(null, map.get(new CString("four", 21)));
        assertEquals(3, map.size());
    }
    
    @Test
    public void grow()
    {
        BRC30_OpenMap.LightMap<String, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        for (int i = 1; i <= 2000; i++)
        {
            map.put(i + "any" + i, i);
        }
        
        assertEquals(2000, map.size());

        for (int i = 1; i <= 2000; i++)
        {
            var s = i + "any" + i;
            assertEquals(i, map.get(s));
        }

        for (int i = 1; i <= 2000; i++)
        {
            map.put(i + "any2" + i, i + 5000);
        }

        for (int i = 1; i <= 2000; i++)
        {
            var s1 = i + "any" + i;
            var s2 = i + "any2" + i;
            assertEquals(i, map.get(s1));
            assertEquals(i + 5000, map.get(s2));
        }
    }
    
    @Test
    public void compute()
    {
        BRC30_OpenMap.LightMap<String, Integer> map = new BRC30_OpenMap.LightMap<>(8);
        for (int i = 1; i <= 1000; i++)
        {
            final int val = i;
            map.compute(i + "any" + i, (k, v) -> (v == null) ? val : 42 * val);
        }
        assertEquals(1000, map.size());

        for (int i = 1; i <= 1000; i++)
        {
            var s = i + "any" + i;
            assertEquals(i, map.get(s));
        }
        assertEquals(1000, map.size());

        for (int i = 1; i <= 1000; i++)
        {
            assertEquals(i + 10_000, 
                    map.compute(i + "any" + i, (k, v) -> (v == null) ? 1 : v + 10_000));
        }
        assertEquals(1000, map.size());

        for (int i = 1; i <= 1000; i++)
        {
            var s = i + "any" + i;
            assertEquals(
                    (i + 10_000), 
                    map.get(s),                    
                    String.format("Expected key: %s, %s", s, i + 10000));
        }
    }
    
    static record CString(String s, int hash) 
    {
        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            CString other = (CString) obj;
            return s.equals(other.s);
        }
        
        @Override
        public int hashCode() 
        {
            return hash;
        }
    }
}
