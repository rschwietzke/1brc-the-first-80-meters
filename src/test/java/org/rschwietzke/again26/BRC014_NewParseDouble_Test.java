package org.rschwietzke.again26;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class BRC014_NewParseDouble_Test 
{
    @Test
    public void parseDouble()
    {
        String s = "";

        s = "0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "0.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "-1.5"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "0.1"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "0.000008765"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1.0000087171"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);

        s = "2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "32"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "423"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "5234"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "12345"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "223456"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "5234567"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);

        s = "1.1"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "12.1"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s),      0.0000000001);
        s = "123.1"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s),     0.0000000001);
        s = "1234.2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s),    0.0000000001);
        s = "12345.3"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s),   0.0000000001);
        s = "123456.4"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s),  0.0000000001);
        s = "1234567.5"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.0000000001);

        s = "1"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "1.143"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "12.111"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "123.144"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "1234.255"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "12345.322"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "123456.433"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "1234567.533"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);

        s = "1.0"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "1.001"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "0.25"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "2.50"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "25.0"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "25.25"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "25.00025"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "0.6811"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "141.001"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));

        s = "10.100000000000001"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));
        s = "-141.001"; assertTrue(Double.parseDouble(s) == BRC014_NewParseDouble.parseDouble(s));

        // 1BRC
        s = "0.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "11.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "0.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "-11.0"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));

        s = "0.2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1.4"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "11.4"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "-0.2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s));
        s = "1.2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "-1.2"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);
        s = "-11.1"; assertEquals(Double.parseDouble(s), BRC014_NewParseDouble.parseDouble(s), 0.00000000001);

    }
}
