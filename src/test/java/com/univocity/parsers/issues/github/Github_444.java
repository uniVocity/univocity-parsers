package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/444
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_444 {

	@Test
	public void parseCSV() {
		String rawData;
		rawData = "" +
				"\"\"\"quoted\"\"\n" +
				")\"\t\n";

		final CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically('\t', ';', ',');
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setSkipEmptyLines(false);

		settings.setNullValue("");


		final CsvParser parser = new CsvParser(settings);

		String[] row = parser.parseAll(new StringReader(rawData)).get(0);
		assertEquals(row[0], "\"quoted\"\n)");
		assertEquals(row[1], "");

		assertEquals(parser.getDetectedFormat().getQuoteEscape(), '"');
	}
}