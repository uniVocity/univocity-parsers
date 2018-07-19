/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;
import org.w3c.dom.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/18
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_18 {

	@Test
	public void testEscapingOnUnquotedValues() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setQuoteEscape('|');
		parserSettings.getFormat().setCharToEscapeQuoteEscaping('|');
		parserSettings.setEscapeUnquotedValues(true);

		CsvParser parser = new CsvParser(parserSettings);
		String[] result;

		result = parser.parseLine("|\"thing");
		assertEquals(result[0], "\"thing");

		result = parser.parseLine("||\"thing");
		assertEquals(result[0], "|\"thing");

		result = parser.parseLine("|||\"thing");
		assertEquals(result[0], "|\"thing");

		result = parser.parseLine("A,B,C");
		assertEquals(result, new String[]{"A", "B", "C"});

		parserSettings.setParseUnescapedQuotes(false);
		parser = new CsvParser(parserSettings);

		result = parser.parseLine("|||\"thing");
		assertEquals(result[0], "|\"thing");

		try {
			parser.parseLine("||\"thing");
			fail("Expected unescaped quote to cause error");
		} catch(TextParsingException ex){
			//success
		}

		result = parser.parseLine("|||\"thing");
		assertEquals(result[0], "|\"thing");
	}
}
