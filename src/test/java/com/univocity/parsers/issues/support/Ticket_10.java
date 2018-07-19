package com.univocity.parsers.issues.support;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Ticket_10 {

	@Test
	public void parseUnescapedInput() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.STOP_AT_CLOSING_QUOTE);
		CsvParser parser = new CsvParser(settings);

		String s = "1,2,\"\"Unescaped\n" +
				"multiline string\",\"\"Multiline string\n" +
				"with paired quotes\"\",\"Another string\",6";

		List<String[]> strings = parser.parseAll(new StringReader(s));


		assertEquals(strings.size(), 1);
		String[] values = strings.get(0);
		assertEquals(values.length, 6);

		assertEquals(values[0], "1");
		assertEquals(values[1], "2");
		assertEquals(values[2], "\"Unescaped\nmultiline string");
		assertEquals(values[3], "\"Multiline string\nwith paired quotes\"");
		assertEquals(values[4], "Another string");
		assertEquals(values[5], "6");

	}

}
