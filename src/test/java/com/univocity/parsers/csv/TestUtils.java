/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.csv;

import org.testng.*;

import java.text.*;
import java.util.*;

public class TestUtils {
	public static <T> void assertEquals(T[] result, T[] expected) {
		Assert.assertEquals(result, expected, "Got " + Arrays.toString(result) + "instead of " + Arrays.toString(expected));
	}

	public static <T> void assertLinesAreEqual(T[][] result, T[][] expected) {
		Assert.assertTrue(result != null && expected != null);
		Assert.assertEquals(result.length, expected.length);
		for (int i = 0; i < expected.length; i++) {
			Assert.assertEquals(result[i], expected[i], "Result: " + printArrayElements(result[i]) + "\nExpected: " + printArrayElements(expected[i]));
		}
	}

	private static <T> String printArrayElements(T[] array) {
		if (array == null) {
			return "null";
		}
		if (array.length == 0) {
			return "[]";
		}
		StringBuilder out = new StringBuilder();
		out.append('[');

		for (int i = 0; i < array.length; i++) {
			T value = array[i];
			if (out.length() != 1) {
				out.append(',');
			}
			if (value == null) {
				out.append("<null>");
			} else {
				out.append('<').append(String.valueOf(value)).append('>');
			}
		}

		out.append(']');

		return out.toString();
	}

	public static <T> void assertEquals(Collection<T> result, T[] expected) {
		assertEquals(result.toArray(), expected);
	}

	public static <T> void assertEquals(T[] result, Collection<T> expected) {
		assertEquals(result, expected.toArray());
	}

	public static String formatDate(java.util.Date date) {
		return formatDate(date, "dd-MMM-yyyy HH:mm:ss");
	}

	public static String formatDate(java.util.Date date, String format) {
		if (date == null) {
			return "null";
		}

		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
		String formatted = formatter.format(date);
		return formatted;
	}
}
