/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/271
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_271 {

	@DataProvider
	public Object[][] delimiterProvider() {
		return new Object[][]{
				{","},
				{"#|#"},
				{"##"},
				{". ."},
				{". "}
		};
	}

	@Test(dataProvider = "delimiterProvider")
	public void testUnescapedHandling(String delimiter) {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setDelimiter(delimiter);
		parserSettings.setUnescapedQuoteHandling(UnescapedQuoteHandling.BACK_TO_DELIMITER);
		parserSettings.setReadInputOnSeparateThread(true);
		parserSettings.trimValues(true);
		CsvParser lineParser = new CsvParser(parserSettings);

		List<String[]> rows = lineParser.parseAll(new StringReader("" +
				"\"name\"" + delimiter + "\"description\"" + delimiter + "\"digit\"" + delimiter + "\"other\"\n" +
				" \"test one\"" + delimiter + "\"test description with \"\"" + delimiter + "\"1\"" + delimiter + "\"other one\"" +
				"\n" +
				"\"test two\"" + delimiter + "\"test description without a quote\"" + delimiter + "\"2\"" + delimiter + "\"other two\""));

		assertEquals(rows.size(), 3);
		String[] row;

		row = rows.get(0);
		assertEquals(row.length, 4);
		assertEquals(row[0], "name");
		assertEquals(row[1], "description");
		assertEquals(row[2], "digit");
		assertEquals(row[3], "other");

		row = rows.get(1);
		assertEquals(row.length, 4);
		assertEquals(row[0], "test one");
		assertEquals(row[1], "test description with \"");
		assertEquals(row[2], "1");
		assertEquals(row[3], "other one");

		row = rows.get(2);
		assertEquals(row.length, 4);
		assertEquals(row[0], "test two");
		assertEquals(row[1], "test description without a quote");
		assertEquals(row[2], "2");
		assertEquals(row[3], "other two");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testBackToDelimiter(String delimiter) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setDelimiter(delimiter);
		settings.getFormat().setLineSeparator("\n");
		settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.BACK_TO_DELIMITER);

		CsvParser parser = new CsvParser(settings);

		StringReader input = new StringReader("" +
				"Example Line 1" + delimiter + "some data" + delimiter + "\"good line\"" + delimiter + "processes fine" + delimiter + "happy\n" +
				"Example Line 2" + delimiter + "some data" + delimiter + "\"bad line" + delimiter + "processes poorly" + delimiter + "unhappy\n" +
				"Example Line 3" + delimiter + "some data" + delimiter + "\"good line\"" + delimiter + "dies before here" + delimiter + "unhappy");

		parser.beginParsing(input);

		String[] row;
		row = parser.parseNext();
		assertEquals(row.length, 5);
		assertEquals(row[0], "Example Line 1");
		assertEquals(row[1], "some data");
		assertEquals(row[2], "good line");
		assertEquals(row[3], "processes fine");
		assertEquals(row[4], "happy");

		row = parser.parseNext();
		assertEquals(row.length, 5);
		assertEquals(row[0], "Example Line 2");
		assertEquals(row[1], "some data");
		assertEquals(row[2], "bad line");
		assertEquals(row[3], "processes poorly");
		assertEquals(row[4], "unhappy");

		row = parser.parseNext();
		assertEquals(row.length, 5);
		assertEquals(row[0], "Example Line 3");
		assertEquals(row[1], "some data");
		assertEquals(row[2], "good line");
		assertEquals(row[3], "dies before here");
		assertEquals(row[4], "unhappy");

	}

	@Test
	public void testBackToDelimiterWithAutoDetection() {
		String input = "\"name\"|\"description\"|\"digit\"|\"other\"\n \"test one\"|\"test description with \"\"|\"1\"|\"other one\"\n \"test two\"|\"test description without a quote\"|\"2\"|\"other two\"\n";

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setUnescapedQuoteHandling(UnescapedQuoteHandling.BACK_TO_DELIMITER);

		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setDelimiterDetectionEnabled(true);
		parserSettings.setQuoteDetectionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		List<String[]> rows = parser.parseAll(new StringReader(input));
		assertEquals(rows.size(), 2);

		String[] row;
		row = rows.get(0);
		assertEquals(row.length, 4);
		assertEquals(row[0], "test one");
		assertEquals(row[1], "test description with \"");
		assertEquals(row[2], "1");
		assertEquals(row[3], "other one");

		row = rows.get(1);
		assertEquals(row.length, 4);
		assertEquals(row[0], "test two");
		assertEquals(row[1], "test description without a quote");
		assertEquals(row[2], "2");
		assertEquals(row[3], "other two");

	}


	@Test
	public void testBackToDelimiterWithKeepQuotes() {
		String input = "\"name\"|\"description\"|\"digit\"|\"other\"\n \"test one\"|\"test description with \"\"|\"1\"|\"other one\"\n \"test two\"|\"test description without a quote\"|\"2\"|\"other two\"\n";

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setUnescapedQuoteHandling(UnescapedQuoteHandling.BACK_TO_DELIMITER);

		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setDelimiterDetectionEnabled(true);
		parserSettings.setQuoteDetectionEnabled(true);
		parserSettings.setKeepQuotes(true);

		CsvParser parser = new CsvParser(parserSettings);
		List<String[]> rows = parser.parseAll(new StringReader(input));
		assertEquals(rows.size(), 2);

		String[] row;
		row = rows.get(0);
		assertEquals(row.length, 4);
		assertEquals(row[0], "\"test one\"");
		assertEquals(row[1], "\"test description with \"\"");
		assertEquals(row[2], "\"1\"");
		assertEquals(row[3], "\"other one\"");

		row = rows.get(1);
		assertEquals(row.length, 4);
		assertEquals(row[0], "\"test two\"");
		assertEquals(row[1], "\"test description without a quote\"");
		assertEquals(row[2], "\"2\"");
		assertEquals(row[3], "\"other two\"");

	}

}
