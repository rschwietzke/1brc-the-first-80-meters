package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BRC93_ParseIntegerLessCode_Test 
{
    private byte[] backingArray;

    private int parseInteger(int totalRead)
    {
        // we inlined the parse integer here as well to make it more inlineable
        int value;
        int sign;

        byte b1 = this.backingArray[totalRead++];
        if (b1 != '-')
        {
            // 9.9 or 99.9, store 9 and the sign +1
            value = b1 & 15;
            sign = 1;
        }
        else
        {
            // we are -9.9 or -99.9, store 0, because we skip this data and set sign -1
            value = this.backingArray[totalRead++] & 15;
            sign = -1;
        }
        
        // .9 or 9.9 left
        byte b2 = this.backingArray[totalRead++];
        if (b2 == '.')
        {
            // it is .9, we just need to read the 9
            byte b3 = this.backingArray[totalRead];
            value = value * 10 + (b3 & 15);
        }
        else
        {
            // it is 9.9 
            // add the b2
            value = value * 10 + (b2 & 15);
            
            // jump the . that comes now
            byte b3 = this.backingArray[++totalRead];
            value = value * 10 + (b3 & 15);
        }
        
        // fix up the sign
        return sign * value;    
    }

    @Test
    public void testParseInteger()
    {
        this.backingArray = "-99.9".getBytes();
        assertEquals(-999, this.parseInteger(0));

        this.backingArray = "9.9".getBytes();
        assertEquals(99, this.parseInteger(0));

        this.backingArray = "99.9".getBytes();
        assertEquals(999, this.parseInteger(0));

        this.backingArray = "-9.9".getBytes();
        assertEquals(-99, this.parseInteger(0));
    }

}
