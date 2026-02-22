package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class BRC091_ParseInteger_Test 
{
    private byte[] backingArray;

    private int parseInteger(int totalRead)
    {
        byte b = this.backingArray[totalRead++];

        // are we negative? we need some setup for a later bit flip
        // compensate for the bit flip later
        int negative = b == '-' ? -1 : 0;

        // to save a branch, we just add 1 to the total read, so we are at the right position for the next read, no matter if we had a - or not
        // compensate for the later add
        int value = -negative - 1; // when negative we need 0 here, -1 otherwise
        totalRead += value; // -1 when positive, 0 when negative

        // ok, 9.9 or 99.9
        // first is always a number, we might have to reread it if positive
        byte b0 = this.backingArray[totalRead++];
        b0 &= 15;

        // next is either . or another number
        byte b1 = this.backingArray[totalRead++];
        if (b1 != '.')
        {
            b1 &= 15;

            // must be 99.9

            // skip the ., we just read a number

            // the part after the .
            byte b2 = this.backingArray[++totalRead];
            value = 100 * b0 + 10 * b1 + (b2 & 15) + value;
        }
        else
        {
            // skip .

            // it is 9.9
            // the part after the .
            byte b2 = this.backingArray[totalRead];
            value = 10 * b0 + (b2 & 15) + value;
        }

        // flip the bits to get a negative value
        // int negative = (x ^ -1) + 1
        // negative is either 0 or -1, so we flip all bits when negative and add 1 to compensate for the earlier compensation
        // if 0, we just add 1 and that was already accounted for before with value = -negative - 1 
        return (value ^ negative) + 1;
    }

    @Test
    public void testParseInteger()
    {
        this.backingArray = "9.9".getBytes();
        assertEquals(99, this.parseInteger(0));

        this.backingArray = "99.9".getBytes();
        assertEquals(999, this.parseInteger(0));

        this.backingArray = "-9.9".getBytes();
        assertEquals(-99, this.parseInteger(0));

        this.backingArray = "-99.9".getBytes();
        assertEquals(-999, this.parseInteger(0));
    }

}
