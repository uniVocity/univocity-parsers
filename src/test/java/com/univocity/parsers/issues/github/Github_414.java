package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/414
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_414 {
	@Test
	public void detectedFormatTest() {
		char[] detectableDelimiters = {',', '|', ';', ':', ' ', '\t'};
		CsvParserSettings settings = new CsvParserSettings();

		settings.getFormat().setDelimiter(',');
		settings.getFormat().setQuote('\"');
		settings.getFormat().setQuoteEscape('\\');
		settings.detectFormatAutomatically(detectableDelimiters);

		CsvParser parser = new CsvParser(settings);

		parser.parseAll(new StringReader("header1;header2;header3;header4\n1;2.2;a;b\n"), 2);
		CsvFormat csvFormat = parser.getDetectedFormat();

		assertEquals(csvFormat.getDelimiter(), ';', "incorrect delimiter");
		assertEquals(csvFormat.getQuote(), '\"', "incorrect quote");
		assertEquals(csvFormat.getQuoteEscape(), '\\', "incorrect quote escape");
	}
}