/*
 * Copyright (c) 2005-2023 Xceptance Software Technologies GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rschwietzke.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test for parsing longs and ints.
 *
 * @author René Schwietzke (Xceptance Software Technologies GmbH)
 */
public class ParseDoubleTest
{
    // ================================================================
    // Double

    @Test
    public void parseDouble()
    {
        String s = "";

        s = "0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "-1.5"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "0.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "0.000008765"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1.0000087171"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);

        s = "2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "32"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "423"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "5234"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "12345"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "223456"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "5234567"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);

        s = "1.1"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "12.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s),      0.0000000001);
        s = "123.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s),     0.0000000001);
        s = "1234.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s),    0.0000000001);
        s = "12345.3"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s),   0.0000000001);
        s = "123456.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s),  0.0000000001);
        s = "1234567.5"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.0000000001);

        s = "1"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "1.143"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "12.111"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "123.144"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "1234.255"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "12345.322"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "123456.433"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "1234567.533"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);

        s = "1.0"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "1.001"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "0.25"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "2.50"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "25.0"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "25.25"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "25.00025"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "0.6811"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "141.001"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));

        s = "10.100000000000001"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));
        s = "-141.001"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble(s));

        // 1BRC
        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "11.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "-11.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));

        s = "0.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "11.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "-0.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s));
        s = "1.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "-1.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);
        s = "-11.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble(s), 0.00000000001);

    }

}
