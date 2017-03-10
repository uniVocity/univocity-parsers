/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

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
		for (char escape : new char[]{'\'', '|'}) {
			CsvWriterSettings settings = new CsvWriterSettings();
			settings.setEscapeUnquotedValues(true);
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
		}
	}

}
