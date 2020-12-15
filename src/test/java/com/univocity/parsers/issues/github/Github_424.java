package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/424
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_424 {
	@Test
	public void testReadInputOnSeparateThreadIssue() {
		CsvParserSettings s = new CsvParserSettings();

		s.setReadInputOnSeparateThread(false);
		s.setHeaderExtractionEnabled(true);
		s.getFormat().setLineSeparator("\r");

		List<String[]> rows = new CsvParser(s).parseAll(getClass().getResourceAsStream("/issues/github_194/uk-500.csv"));
		assertEquals(rows.size(), 500);
	}
}