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
import java.util.*;

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
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		s.getFormat().setLineSeparator("\n");
		s.getFormat().setComment('\n');
		s.setSkipEmptyLines(false);
		s.setNullValue("null");
		s.setEmptyValue("null");
		if (delimiter.charAt(0) <= ' ') {
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

	//QUOTED
	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuoted(String delimiter) {
		validate(delimiter, "'A'" + delimiter + "'B'" + delimiter + "'C'", "A,B,C");
		validate(delimiter, "'A" + delimiter + "B'" + delimiter + "C", "A" + delimiter + "B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedTrim(String delimiter) {
		validate(delimiter, " A " + delimiter + " 'B' " + delimiter + " C", "A,B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedTrimBlank(String delimiter) {
		validate(delimiter, " A " + delimiter + "''" + delimiter + " C", "A,null,C");
		validate(delimiter, " A " + delimiter + "''''" + delimiter + " ", "A,',null");
		validate(delimiter, "   " + delimiter + "'  " + delimiter + " '", "null,  " + delimiter + " ");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedPartial(String delimiter) {
		String partial = delimiter.substring(0, delimiter.length() - 1);
		validate(delimiter, "'A" + partial + "B'" + delimiter + "C", "A" + partial + "B,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedEmpty(String delimiter) {
		validate(delimiter, "'A'" + delimiter + "''" + delimiter + "'C'", "A,null,C");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedEmptyValues(String delimiter) {
		validate(delimiter, "''", "null");
		validate(delimiter, "''" + delimiter + "''", "null,null");
		validate(delimiter, "''" + delimiter + "''" + delimiter + "''", "null,null,null");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterQuotedEOF(String delimiter) {
		validate(delimiter, "'A" + delimiter + "\n" + delimiter + "'", "A" + delimiter + "\n" + delimiter);
		validate(delimiter, "'A" + delimiter + "\n" + delimiter, "A" + delimiter + "\n" + delimiter);
	}

	//delimiters with whitespace
	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterQuoted(String delimiter) {
		validate(delimiter, "'A'" + delimiter + "'B'" + delimiter + "'C'", "A,B,C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterQuotedNoTrim(String delimiter) {
		validate(delimiter, "'A " + delimiter + " B' " + delimiter + " C", "A " + delimiter + " B, C");
	}

	@Test(dataProvider = "whiteDelimiterProvider")
	public void testMultiWhiteDelimiterQuotedNoTrimBlank(String delimiter) {
		validate(delimiter, " ' '  " + delimiter + "' " + delimiter + " '", " ' '  , " + delimiter + " ");
	}

	@DataProvider
	public Object[][] config() {
		return new Object[][]{
				{false, UnescapedQuoteHandling.STOP_AT_DELIMITER, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{false, UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, "INCOME\".\"Taxable", "EXPENSES\".\"TotalExpenses", "EXPENSES\".\"Exceptional"},
				{false, UnescapedQuoteHandling.SKIP_VALUE, null, null, null},
				{true, UnescapedQuoteHandling.STOP_AT_DELIMITER, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{true, UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE, "\"INCOME\".\"Taxable\"", "\"EXPENSES\".\"TotalExpenses\"", "\"EXPENSES\".\"Exceptional\""},
				{true, UnescapedQuoteHandling.SKIP_VALUE, null, null, null},
		};
	}

	@Test(dataProvider = "config")
	public void testWithKeepQuotes(boolean keepQuotes, UnescapedQuoteHandling handling, String first, String second, String third) {
		String input = "" +
				"PAL :: PAL :: NF :: \"INCOME\".\"Taxable\"\n" +
				"PAL :: PAL :: NF :: \"EXPENSES\".\"TotalExpenses\"\n" +
				"PAL :: PAL :: NF :: \"EXPENSES\".\"Exceptional\"";

		CsvParserSettings settings = new CsvParserSettings();
		settings.setKeepQuotes(keepQuotes);
		settings.setUnescapedQuoteHandling(handling);

		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setDelimiter("::");

		CsvParser parser = new CsvParser(settings);
		List<String[]> rows = parser.parseAll(new StringReader(input));
		assertEquals(rows.size(), 3);
		assertEquals(rows.get(0)[3], first);
		assertEquals(rows.get(1)[3], second);
		assertEquals(rows.get(2)[3], third);
	}

	private String write(String delimiter, String[] values) {
		CsvWriterSettings s = new CsvWriterSettings();
		s.getFormat().setLineSeparator("\n");
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		s.getFormat().setDelimiter(delimiter);
		s.trimValues(false);
		CsvWriter w = new CsvWriter(s);
		String line = w.writeRowToString(values);
		return line;
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterWritingNoQuotes(String delimiter) {
		String line = write(delimiter, new String[]{"A", "B", "C"});
		assertEquals(line, "A" + delimiter + "B" + delimiter + "C");
	}


	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterWritingInValues(String delimiter) {
		String line = write(delimiter, new String[]{"A" + delimiter + "a", "B" + delimiter, delimiter + "C"});
		assertEquals(line, "'A" + delimiter + "a'" + delimiter + "'B" + delimiter + "'" + delimiter + "'" + delimiter + "C'");
	}

	@Test(dataProvider = "delimiterProvider")
	public void testMultiDelimiterWritingInValuesWithQuotes(String delimiter) {
		String line = write(delimiter, new String[]{"A'a", "B" + delimiter + "'", delimiter + "'C"});
		assertEquals(line, "A'a" + delimiter + "'B" + delimiter + "'''" + delimiter + "'" + delimiter + "''C'");
	}
}




