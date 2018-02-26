/*******************************************************************************
 * Copyright 2018 uniVocity Software Pty Ltd
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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/188
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_188 {


	public static class T {
		private List<String> values = new ArrayList<String>();

		@Parsed
		private int id;

		@Parsed(field = "field")
		public void addValue(String value) {
			if (value != null) {
				values.add(value);
			}
		}
	}

	@Test
	public void testRepeatedHeaders() {
		String input = "" +
				"id,id,field,field\n" +
				"1,2,value1,value2";

		List<T> ts = new CsvRoutines().parseAll(T.class, new StringReader(input));
		T t = ts.get(0);
		assertEquals(t.values.size(), 2);
		assertEquals(t.values.get(0), "value1");
		assertEquals(t.values.get(1), "value2");
		assertEquals(t.id, 2);
	}

}
