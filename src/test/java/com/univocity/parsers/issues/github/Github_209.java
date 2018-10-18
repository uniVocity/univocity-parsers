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

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/209
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_209 {

	@DataProvider
	public Object[][] delimiterProvider() {
		return new Object[][]{
				{"#|#"},
				{"##"},
				{". ."},
				{". "}
		};
	}

	@DataProvider
	public Object[][] whiteDelimiterProvider() {
		return new Object[][]{
				{"\t\t"},
				{" . "},
				{" ."}
		};
	}

	private void validate(String delimiter, String input, String values) {
		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setDelimiter(delimiter);
		s.getFormat().setLineSeparator("\n");
		s.getFormat().setComment('\n');
		s.setSkipEmptyLines(false);
		s.setNullValue("null");
		if(delimiter.charAt(0) <= ' '){
			s.trimValues(false);
		}

		String[] row = new CsvParser(s).parseLine(input);
		String[] expected = values.split(",");
		assertEquals(row.length, expected.length);
		for (int i = 0; i < expected.length; i++) {
			assertEquals(row[i], expected[i]);
		}
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotes(String delimiter) {
		validate(delimiter, "A" + delimiter + "B" + delimiter + "C", "A,B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesTrim(String delimiter) {
		validate(delimiter, " A " + delimiter + " B " + delimiter + " C", "A,B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesTrimBlank(String delimiter) {
		validate(delimiter, " A " + delimiter + "  " + delimiter + " C", "A,null,C");
		validate(delimiter, " A " + delimiter + "  " + delimiter + " ", "A,null,null");
		validate(delimiter, "   " + delimiter + "  " + delimiter + " ", "null,null,null");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesPartial(String delimiter) {
		String partial = delimiter.substring(0, delimiter.length() - 1);
		validate(delimiter, "A" + partial + "B" + delimiter + "C", "A" + partial + "B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesEmpty(String delimiter) {
		validate(delimiter, "A" + delimiter + delimiter + "C", "A,null,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesEmptyValues(String delimiter) {
		validate(delimiter, " ", "null");
		validate(delimiter, delimiter, "null,null");
		validate(delimiter, delimiter + delimiter, "null,null,null");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesEOF(String delimiter) {
		validate(delimiter, "A" + delimiter + delimiter, "A,null,null");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterNoQuotesPartialEOF(String delimiter) {
		String partial = delimiter.substring(0, delimiter.length() - 1);
		validate(delimiter, partial, partial);
		validate(delimiter, "A" + delimiter + partial, "A," + partial);
		validate(delimiter, partial + "A" + delimiter, partial + "A,null");
	}

	//delimiters with whitespace
	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotes(String delimiter) {
		validate(delimiter, "A" + delimiter + "B" + delimiter + "C", "A,B,C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesNoTrim(String delimiter) {
		validate(delimiter, " A " + delimiter + " B " + delimiter + " C", " A , B , C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesNoTrimBlank(String delimiter) {
		validate(delimiter, " A " + delimiter + "  " + delimiter + " C", " A ,  , C");
		validate(delimiter, " A " + delimiter + "  " + delimiter + " ", " A ,  , ");
		validate(delimiter, "   " + delimiter + "  " + delimiter + " ", "   ,  , ");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesPartial(String delimiter) {
		String partial = delimiter.substring(0, delimiter.length() - 1);
		validate(delimiter, "A" + partial + "B" + delimiter + "C", "A" + partial + "B,C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesEmpty(String delimiter) {
		validate(delimiter, "A" + delimiter + delimiter + "C", "A,null,C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesEmptyValues(String delimiter) {
		validate(delimiter, " ", " ");
		validate(delimiter, delimiter, "null,null");
		validate(delimiter, delimiter + delimiter, "null,null,null");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesEOF(String delimiter) {
		validate(delimiter, "A" + delimiter + delimiter, "A,null,null");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterNoQuotesPartialEOF(String delimiter) {
		String partial = delimiter.substring(0, delimiter.length() - 1);
		validate(delimiter, partial, partial);
		validate(delimiter, "A" + delimiter + partial, "A," + partial);
		validate(delimiter, partial + "A" + delimiter, partial + "A,null");
	}
}




