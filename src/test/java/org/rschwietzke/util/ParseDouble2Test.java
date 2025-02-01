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
public class ParseDouble2Test
{
    // ================================================================
    // Double

    @Test
    public void parseDouble()
    {
        String s = "";

        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "-1.5"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "0.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));

        s = "1.1"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble2(s));
        s = "12.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s),      0.0000000001);
        s = "123.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s),     0.0000000001);
        s = "1234.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s),    0.0000000001);
        s = "12345.3"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s),   0.0000000001);
        s = "123456.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s),  0.0000000001);
        s = "1234567.5"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s), 0.0000000001);

        s = "1.0"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble2(s));
        s = "25.0"; assertTrue(Double.parseDouble(s) == ParseDouble.parseDouble2(s));

        // 1BRC
        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "11.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "0.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "-1.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "-11.0"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));

        s = "0.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "1.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s), 0.00000000001);
        s = "11.4"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "-0.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s));
        s = "1.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s), 0.00000000001);
        s = "-1.2"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s), 0.00000000001);
        s = "-11.1"; assertEquals(Double.parseDouble(s), ParseDouble.parseDouble2(s), 0.00000000001);

    }

}
