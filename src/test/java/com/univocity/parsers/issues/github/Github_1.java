/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
 * From: https://github.com/univocity/univocity-parsers/issues/1
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_1 extends ParserTestCase {

	@Test
	public void parseFourthColumn() throws Exception {
		Reader reader = newReader("/issues/github_1/input.csv");

		RowListProcessor processor = new RowListProcessor();

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderExtractionEnabled(true);
		//settings.setHeaders("column1", "column2", "column3", "column4");
		settings.excludeIndexes(1);
		settings.setColumnReorderingEnabled(false);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		String[] expectedHeaders = new String[]{"column 1", "column 2", "column 3"};
		String[][] expectedResult = new String[][]{
			{"first", null, "third", "fourth"},
			{"1", null, "3", "4"}
		};

		this.assertHeadersAndValuesMatch(processor, expectedHeaders, expectedResult);
	}
}
