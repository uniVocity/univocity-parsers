package com.univocity.parsers.issues.support;

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.csv.*;

public class Ticket_3 {

	@Test
	public void testEscapeError() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.getFormat().setQuoteEscape('\\');
		settings.setHeaderExtractionEnabled(false);
		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(new InputStreamReader(Ticket_3.class.getResourceAsStream("/issues/ticket_3/input.csv"), "UTF-8"));

		/*
		"0\",\"0"     ==> [0","0]
		"1\\",\"1"    ==> [1\","1]
		"2\\\",\"2"   ==> [2\\",2]
		 */
		String[] expected0 = new String[] { "0\",\"0" };
		String[] expected1 = new String[] { "1\\\",\"1" };
		String[] expected2 = new String[] { "2\\\\\",\"2" };

		assertEquals(allRows.get(0), expected0);
		assertEquals(allRows.get(1), expected1);
		assertEquals(allRows.get(2), expected2);
	}

	@Test
	public void testEscapeEscape() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.getFormat().setQuoteEscape('\\');
		settings.getFormat().setCharToEscapeQuoteEscaping('\\');
		settings.setParseUnescapedQuotes(false);
		settings.setHeaderExtractionEnabled(false);
		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(new InputStreamReader(Ticket_3.class.getResourceAsStream("/issues/ticket_3/input.csv"), "UTF-8"));

		/*
		"0\",\"0"     ==> [0","0]
		"1\\",\"1"    ==> [1\],[\"1"]
		"2\\\",\"2"   ==> [2\","2]
		 */
		String[] expected0 = new String[] { "0\",\"0" };
		String[] expected1 = new String[] { "1\\", "\\\"1\"" };
		String[] expected2 = new String[] { "2\\\",\"2" };

		assertEquals(allRows.get(0), expected0);
		assertEquals(allRows.get(1), expected1);
		assertEquals(allRows.get(2), expected2);
	}

}
