/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/353
 */
public class Github_353 {


	@Test
	public void testEmptyValuesSpacesAndQuoting() {
		CsvWriterSettings s = new CsvWriterSettings();
		CsvFormat format = s.getFormat();
		format.setLineSeparator("\n");
		format.setDelimiter(',');
		format.setQuote('\'');
		format.setQuoteEscape('\\');
		format.setComment('\u0000');

		s.setIgnoreLeadingWhitespaces(true);
		s.setIgnoreTrailingWhitespaces(true);
		s.setNullValue("");
		s.setEmptyValue("''");
		s.setSkipEmptyLines(true);
		s.setQuoteAllFields(false);
		s.setQuoteEscapingEnabled(true);
		s.setErrorContentLength(1000);
		CsvWriter w = new CsvWriter(s);

		String result = w.writeRowToString(1, "\n");
		assertEquals(result, "1,''");
	}

	@Test
	public void testParsingEmptyValuesSpacesAndQuoting() {
		CsvParserSettings s = new CsvParserSettings();
		CsvFormat format = s.getFormat();
		format.setLineSeparator("\n");
		format.setDelimiter(',');
		format.setQuote('\'');
		format.setQuoteEscape('\\');
		format.setComment('\u0000');

		s.setIgnoreLeadingWhitespaces(true);
		s.setIgnoreTrailingWhitespaces(true);
		s.setNullValue("");
		s.setEmptyValue("''");
		s.setSkipEmptyLines(true);
		s.setErrorContentLength(1000);
		CsvParser parser = new CsvParser(s);

		String[] result = parser.parseLine("1,\\'\\'");
		assertEquals(result.length, 2);
		assertEquals(result[0], "1");
		assertEquals(result[1], "\\'\\'");
	}
}