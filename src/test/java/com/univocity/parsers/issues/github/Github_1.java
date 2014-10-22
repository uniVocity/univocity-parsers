package com.univocity.parsers.issues.github;

import java.io.*;

import org.testng.annotations.*;

import com.univocity.parsers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/1
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_1 extends ParserTestCase {

	@Test
	public void parseFourthColumn() throws Exception {
		Reader reader = newReader("/issues/github_1/input.csv");

		RowListProcessor processor = new RowListProcessor();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		//settings.setHeaders("column1", "column2", "column3", "column4");
		settings.excludeIndexes(1);
		settings.setColumnReorderingEnabled(false);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		String[] expectedHeaders = new String[] { "column 1", "column 2", "column 3" };
		String[][] expectedResult = new String[][] {
				{ "first", null, "third", "fourth" },
				{ "1", null, "3", "4" }
		};

		this.assertHeadersAndValuesMatch(processor, expectedHeaders, expectedResult);
	}
}
