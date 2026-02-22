package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.ByteBuffer;

import org.junit.jupiter.api.Test;

public class BRC096_ParseInt_Test 
{
    private byte[] backingArray;

    private int parseInteger(int totalRead)
    {
        int value;
        ByteBuffer buffer = ByteBuffer.wrap(this.backingArray);
        int readInt = buffer.getInt(totalRead);
        
        int b = (readInt >>> 24) & 0xFF; // 1, the -
        if (b == '-')
        {
            // ok, -9.9 or -99.9
            // first is always a number
            int b0 = (readInt >>> 16) & 0xFF; // 2 a 9
            b0 &= 15;

            // next is either . or another number
            int b1 = (readInt >>> 8) & 0xFF; // 3 . or 9
            if (b1 != '.')
            {
                b1 &= 15; // was 9 

                // must be 99.9

                // skip the ., we just read a number // 4

                // the part after the . we have to read from the array
                totalRead += 4;
                byte b2 = this.backingArray[totalRead]; 
                value = -(100 * b0 + 10 * b1 + (b2 & 15));
            }
            else
            {
                // skip .

                // it is -9.9
                // the part after the .
                int b2 = readInt & 0xFF; // 4
                value = -(10 * b0 + (b2 & 15));
                totalRead += 3;
            }
        }
        else
        {
            // ok, 9.9 or 99.9
            b &= 15;

            // next is either . or another number
            int b1 = (readInt >>> 16) & 0xFF;
            if (b1 != '.')
            {
                // must be 99.9
                b1 &= 15;

                // skip the .

                int b2 = readInt & 0xFF;
                value = 100 * b + 10 * b1 + (b2 & 15);
                totalRead += 3;
            }
            else
            {
                // skip .
                // it is 9.9
                int b2 = (readInt >>> 8) & 0xFF;
                value = 10 * b + (b2 & 15);
                totalRead += 2;
            }
        }
        
        // fix up the sign
        return value;    
    }

    @Test
    public void testParseInteger()
    {
        this.backingArray = "-99.9\n".getBytes();
        assertEquals(-999, this.parseInteger(0));

        this.backingArray = "9.9\n".getBytes();
        assertEquals(99, this.parseInteger(0));

        this.backingArray = "99.9\n".getBytes();
        assertEquals(999, this.parseInteger(0));

        this.backingArray = "-9.9\n".getBytes();
        assertEquals(-99, this.parseInteger(0));
    }

}
