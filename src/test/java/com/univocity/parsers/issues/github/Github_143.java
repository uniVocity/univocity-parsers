/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/143
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_143 {


	@DataProvider
	Object[][] data() {
		return new Object[][]{
				{"AA'BB"},      // 1 quote char (OK without escapeEscape option)
				{"AA|'BB"},     // 1 escape char and 1 quote char
				{"AA||'BB"},    // 2 escape char and 1 quote char
				{"AA''BB"},     // 2 quote char (OK without escapeEscape option)
				{"AA|'|'BB"},   // (1 escape char anc 1 quote char) * 2
				{"AA||'||'BB"}, // (2 escape char and 1 quote char) * 2

		};
	}

	@Test
	public void testParseOfUnescapedSequence() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setEscapeUnquotedValues(true);
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('|');

		String result;

		result = new CsvParser(settings).parseLine("AA||'||'BB")[0];
		assertEquals(result, "AA|'|'BB");

		result = new CsvParser(settings).parseLine("AA||'||'||'BB")[0];
		assertEquals(result, "AA|'|'|'BB");

		result = new CsvParser(settings).parseLine("AA||'z||'BB")[0];
		assertEquals(result, "AA|'z|'BB");

		result = new CsvParser(settings).parseLine("AA|||'z||'BB")[0];
		assertEquals(result, "AA|'z|'BB");

		result = new CsvParser(settings).parseLine("AA|||'||'BB")[0];
		assertEquals(result, "AA|'|'BB");

		result = new CsvParser(settings).parseLine("AA|||'z|||'BB")[0];
		assertEquals(result, "AA|'z|'BB");
	}

	@Test(dataProvider = "data")
	public void testEscapeParsing(String input) {
		System.out.println("\n--------------[ " + input + " ]---------");
		int i = 0;
		for (char escape : new char[]{'\'', '|'}) {
			CsvParserSettings settings = new CsvParserSettings();
			settings.setEscapeUnquotedValues(true);
			settings.getFormat().setQuote('\'');
			settings.getFormat().setQuoteEscape(escape);

			System.out.print(++i + ") Escape: " + escape + ":   ");
			System.out.print(new CsvParser(settings).parseLine(input)[0]);

			try {
				settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.RAISE_ERROR);
				new CsvParser(settings).parseLine(input);
			} catch (Exception e) {
				System.out.print("   << error");
			}
			System.out.println();
		}
	}


	@Test(dataProvider = "data")
	public void testEscapeWriting(String input) {
		System.out.println("\n--------------[ " + input + " ]---------");
		int i = 0;

		List<String> expected = new ArrayList<String>();

		for (char escape : new char[]{'\'', '|'}) {
			CsvWriterSettings settings = new CsvWriterSettings();
			settings.setQuoteAllFields(true);
			settings.getFormat().setQuote('\'');
			settings.getFormat().setQuoteEscape(escape);

			System.out.print(++i + ") Escape: " + escape + ":   ");
			String result = new CsvWriter(settings).writeRowToString(input);

			CsvParserSettings parserSettings = new CsvParserSettings();
			settings.setEscapeUnquotedValues(true);
			settings.getFormat().setQuote('\'');
			settings.getFormat().setQuoteEscape(escape);
			String parsed = new CsvParser(parserSettings).parseLine(input)[0];

			System.out.println(result + (parsed.equals(input) ? "" : " << BUG!"));

			assertEquals(parsed, input);
			expected.add(result);
		}
	}

	@DataProvider
	Object[][] dataToWrite() {
		return new Object[][]{
				{"AA'BB", '\'', "'AA''BB'"},      // 1 quote char (OK without escapeEscape option)
				{"AA'BB", '|', "'AA|'BB'"},      // 1 quote char (OK without escapeEscape option)
				{"AA|'BB", '\'', "'AA|''BB'"},     // 1 escape char and 1 quote char
				{"AA|'BB", '|', "'AA|||'BB'"},     // 1 escape char and 1 quote char
				{"AA||'BB", '\'', "'AA||''BB'"},    // 2 escape char and 1 quote char
				{"AA||'BB", '|', "'AA|||||'BB'"},    // 2 escape char and 1 quote char
				{"AA''BB", '\'', "'AA''''BB'"},     // 2 quote char (OK without escapeEscape option)
				{"AA''BB", '|', "'AA|'|'BB'"},     // 2 quote char (OK without escapeEscape option)
				{"AA|'|'BB", '\'', "'AA|''|''BB'"},   // (1 escape char anc 1 quote char) * 2
				{"AA|'|'BB", '|', "'AA|||'|||'BB'"},   // (1 escape char anc 1 quote char) * 2
				{"AA||'||'BB", '\'', "'AA||''||''BB'"}, // (2 escape char and 1 quote char) * 2
				{"AA||'||'BB", '|', "'AA|||||'|||||'BB'"}, // (2 escape char and 1 quote char) * 2
		};
	}

	@Test(dataProvider = "dataToWrite")
	public void testEscapeWritingQuoteEscapeEnabled(String input, char escape, String expected) {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setQuoteEscapingEnabled(true);
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape(escape);

		String result = new CsvWriter(settings).writeRowToString(input);
		assertEquals(result, expected);
	}

	@Test(dataProvider = "dataToWrite")
	public void testEscapeWritingQuotationTriggers(String input, char escape, String expected) {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setQuotationTriggers('\'');
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape(escape);

		String result = new CsvWriter(settings).writeRowToString(input);
		assertEquals(result, expected);
	}

	@Test(dataProvider = "dataToWrite")
	public void testEscapeWritingNoQuotesButEscapeEnabled(String input, char escape, String expected) {
		expected = expected.substring(1, expected.length() - 1); //no quotes
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setEscapeUnquotedValues(true);
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape(escape);

		String result = new CsvWriter(settings).writeRowToString(input);
		assertEquals(result, expected);
	}

	@Test(dataProvider = "dataToWrite")
	public void testEscapeWritingEscapeEnabledTriggers(String input, char escape, String expected) {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setEscapeUnquotedValues(true);
		settings.setQuotationTriggers('\'');
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape(escape);

		String result = new CsvWriter(settings).writeRowToString(input);
		assertEquals(result, expected);
	}
}
