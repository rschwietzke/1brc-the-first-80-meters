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

import org.junit.jupiter.api.Test;

/**
 * Test for parsing longs and ints.
 *
 * @author René Schwietzke (Xceptance Software Technologies GmbH)
 */
public class ParseIntegerReverseTest
{
	@Test
	public void integer()
	{
		String s = "";

		// 1BRC
		s = "0.0"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 2));
		s = "1.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  ParseDouble.parseIntegerReverse(s.getBytes(), 0, 2));
		s = "11.0"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "-0.0"; assertEquals(Integer.parseInt(s.replace(".", "")),  ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "-1.0"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "-11.0"; assertEquals(Integer.parseInt(s.replace(".", "")),ParseDouble.parseIntegerReverse(s.getBytes(), 0, 4));

		s = "0.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  ParseDouble.parseIntegerReverse(s.getBytes(), 0, 2));
		s = "1.4"; assertEquals(Integer.parseInt(s.replace(".", "")),  ParseDouble.parseIntegerReverse(s.getBytes(), 0, 2));
		s = "11.4"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "-0.2"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "1.2"; assertEquals(Integer.parseInt(s.replace(".", "")),  ParseDouble.parseIntegerReverse(s.getBytes(), 0, 2));
		s = "-1.2"; assertEquals(Integer.parseInt(s.replace(".", "")), ParseDouble.parseIntegerReverse(s.getBytes(), 0, 3));
		s = "-11.1"; assertEquals(Integer.parseInt(s.replace(".", "")),ParseDouble.parseIntegerReverse(s.getBytes(), 0, 4));
	}
}

