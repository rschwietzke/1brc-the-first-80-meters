package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class BRC67_ParseDifferently_Test 
{
    private static final int parseInteger(String s)
    {
        ByteBuffer b = ByteBuffer.allocate(100);
        b.put(s.getBytes());
        b.position(0);
        
        return BRC67_ParseDifferently.parseInteger(b);
    }
    
    @Test
    public void integer()
    {
        String s = "";

        // 1BRC
        s = "19.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "11.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-11.0"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "-12.0"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "61.1"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "21.1"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "-21.1"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "-9.9"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "9.9"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "-99.9"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "99.9"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
        s = "1.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "0.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-0.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "-1.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));

        s = "0.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "1.4"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "11.4"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-0.2"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "1.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "-1.2"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-11.1"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
    }
}
