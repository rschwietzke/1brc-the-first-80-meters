package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BRC21_LooplessParsing_Test 
{
    private static final int parseInteger(String s)
    {
        return BRC21_LooplessParsing.parseInteger(s, 0, s.length());
    }
    
    @Test
    public void integer()
    {
        String s = "";

        // 1BRC
        s = "1.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "0.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "11.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-0.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "-1.0"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-11.0"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));

        s = "0.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "1.4"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "11.4"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-0.2"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "1.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  parseInteger(s));
        s = "-1.2"; assertEquals(Integer.parseInt(s.replace(".", "")), parseInteger(s));
        s = "-11.1"; assertEquals(Integer.parseInt(s.replace(".", "")),parseInteger(s));
    }
}
