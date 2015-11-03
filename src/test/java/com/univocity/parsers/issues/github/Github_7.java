/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.issues.github;

import com.univocity.parsers.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/7
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_7 extends ParserTestCase {

	@DataProvider
	private Object[][] readerProvider() throws Exception {
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

		String[] expectedHeaders = new String[]{"A", "B", "C"};
		String[][] expectedResult = new String[][]{
			{"1", "2", "3"},
		};

		this.assertHeadersAndValuesMatch(processor, expectedHeaders, expectedResult);
	}
}
