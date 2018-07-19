/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/60
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_60 {

	@Test
	public void testParseUnescapedQuotesWithStop() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setParseUnescapedQuotesUntilDelimiter(true);
		settings.getFormat().setDelimiter('\t');

		String[] values;
		values = new CsvParser(settings).parseLine("\"a\"b\tccc\tddd");
		assertEquals(values.length, 3);
		assertEquals(values[0], "\"a\"b");
		assertEquals(values[1], "ccc");
		assertEquals(values[2], "ddd");

		values = new CsvParser(settings).parseLine("\"a\"bc\tccc\tddd");
		assertEquals(values.length, 3);
		assertEquals(values[0], "\"a\"bc");
		assertEquals(values[1], "ccc");
		assertEquals(values[2], "ddd");

		values = new CsvParser(settings).parseLine("\"a\"bc\"\tccc\tddd");
		assertEquals(values.length, 3);
		assertEquals(values[0], "\"a\"bc\"");
		assertEquals(values[1], "ccc");
		assertEquals(values[2], "ddd");
	}
}
