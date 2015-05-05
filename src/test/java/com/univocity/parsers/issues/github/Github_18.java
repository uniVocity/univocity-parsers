package com.univocity.parsers.issues.github;

import static org.testng.Assert.*;

import org.testng.annotations.*;

import com.univocity.parsers.csv.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/18
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_18 {

	@Test
	public void testEscapingOnUnquotedValues() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setQuoteEscape('|');
		parserSettings.getFormat().setCharToEscapeQuoteEscaping('|');
		parserSettings.setEscapeUnquotedValues(true);

		CsvParser parser = new CsvParser(parserSettings);
		String[] result;
		
		result = parser.parseLine("|\"thing");
		assertEquals(result[0], "\"thing");
		
		result = parser.parseLine("||\"thing");
		assertEquals(result[0], "|\"thing");
		
		result = parser.parseLine("A,B,C");
		assertEquals(result, new String[]{"A", "B", "C"});
	}
}
