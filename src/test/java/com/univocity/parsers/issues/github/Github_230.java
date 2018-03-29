/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/230
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_230 {

	@Test
	public void testQuotedTrim() {
		CsvParserSettings s = new CsvParserSettings();
		s.setEmptyValue("X");
		s.trimQuotedValues(true);
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		final CsvParser parser = new CsvParser(s);

		String[] row = parser.parseLine("' a', ' b ', ' c'");
		assertEquals(row[0], "a");
		assertEquals(row[1], "b");
		assertEquals(row[2], "c");

		row = parser.parseLine("'', ' '");
		assertEquals(row[0], "X");
		assertEquals(row[1], "X");

		row = parser.parseLine("' ', ''");
		assertEquals(row[0], "X");
		assertEquals(row[1], "X");

		row = parser.parseLine("' a', ' b ', ' c '");
		assertEquals(row[0], "a");
		assertEquals(row[1], "b");
		assertEquals(row[2], "c");

		row = parser.parseLine("' a   ', '  b ', 'c '");
		assertEquals(row[0], "a");
		assertEquals(row[1], "b");
		assertEquals(row[2], "c");

		row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		assertEquals(row[0], "'a '");
		assertEquals(row[1], "' b'");
		assertEquals(row[2], "c'");

	}

	@Test
	public void testQuotedTrimKeepQuotes() {
		CsvParserSettings s = new CsvParserSettings();
		s.setKeepQuotes(true);
		s.trimQuotedValues(true);
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		final CsvParser parser = new CsvParser(s);

		String[] row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		assertEquals(row[0], "' 'a '  '");
		assertEquals(row[1], "' ' b' '");
		assertEquals(row[2], "'c' '");

	}

	@Test
	public void testQuotedTrimKeepEscape() {
		CsvParserSettings s = new CsvParserSettings();
		s.trimQuotedValues(true);
		s.setKeepEscapeSequences(true);
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		final CsvParser parser = new CsvParser(s);

		String[] row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		assertEquals(row[0], "''a ''");
		assertEquals(row[1], "'' b''");
		assertEquals(row[2], "c''");

	}

	@Test
	public void testQuotedTrimKeepEscapeKeepQuote() {
		CsvParserSettings s = new CsvParserSettings();
		s.trimQuotedValues(true);
		s.setKeepQuotes(true);
		s.setKeepEscapeSequences(true);
		s.getFormat().setQuote('\'');
		s.getFormat().setQuoteEscape('\'');
		final CsvParser parser = new CsvParser(s);

		String[] row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		row = parser.parseLine("' \'\'a \'\'  ', ' \'\' b\'\' ', 'c\'\' '");
		assertEquals(row[0], "' ''a ''  '");
		assertEquals(row[1], "' '' b'' '");
		assertEquals(row[2], "'c'' '");

	}
}
