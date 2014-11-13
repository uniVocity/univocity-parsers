package com.univocity.parsers.issues.github;

import java.io.*;

import org.testng.annotations.*;

import com.univocity.parsers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/7
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_7 extends ParserTestCase {

	@DataProvider
	private Object[][] readerProvider() throws Exception{
		return new Object[][]{
				{new StringReader("# this is a comment line\nA,B,C\n1,2,3\n")},
				{newReader("/issues/github_7/input.csv")}
		};
	}
	
	@Test(dataProvider = "readerProvider")
	public void parseStringWithSlashN(Reader input) throws Exception {

		RowListProcessor processor = new RowListProcessor();
		CsvParserSettings settings = new CsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setIgnoreLeadingWhitespaces(false);
		
		CsvParser parser = new CsvParser(settings);
		parser.parse(input);

		String[] expectedHeaders = new String[] { "A", "B", "C" };
		String[][] expectedResult = new String[][] {
				{ "1", "2", "3" },
		};

		this.assertHeadersAndValuesMatch(processor, expectedHeaders, expectedResult);
	}
}
