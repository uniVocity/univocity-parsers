package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/416
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_416 {
	@Test
	public void testSelectedHeaders() {
		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setLineSeparator("\n");
		s.setHeaderExtractionEnabled(true);
		s.selectFields("a", "c", "e");

		CsvParser p = new CsvParser(s);
		p.beginParsing(new StringReader("a,b,c,d,e\n1,2,3,4,5"));
		assertEquals(p.getContext().selectedHeaders(), new String[]{"a", "c", "e"});
		assertEquals(p.getContext().parsedHeaders(), new String[]{"a", "b", "c", "d", "e"});
		assertEquals(p.parseNext(), new String[]{"1", "3", "5"});
	}
}