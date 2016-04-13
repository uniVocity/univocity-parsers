/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
 * From: https://github.com/uniVocity/univocity-parsers/issues/61
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_61 {

	@Test
	public void testUnescapedQuotes1(){
		CsvParserSettings settings = new CsvParserSettings();
		settings.setParseUnescapedQuotesUntilDelimiter(false);
		settings.getFormat().setDelimiter(';');
		settings.getFormat().setQuoteEscape('\0');

		CsvParser parser = new CsvParser(settings);

		String line[] = parser.parseLine("example;\"this is a \"super example\" \";example3");
		assertEquals(line[0], "example");
		assertEquals(line[1], "this is a \"super example\" ");
		assertEquals(line[2], "example3");
	}

	@Test
	public void testUnescapedQuotes2(){
		CsvParserSettings settings = new CsvParserSettings();
		settings.setParseUnescapedQuotesUntilDelimiter(false);
		settings.getFormat().setDelimiter(';');
		settings.getFormat().setQuoteEscape('\0');

		CsvParser parser = new CsvParser(settings);

		String line[] = parser.parseLine("example;\"this is a \"super example\"\";example3");
		assertEquals(line[0], "example");
		assertEquals(line[1], "this is a \"super example\"");
		assertEquals(line[2], "example3");
	}
}
