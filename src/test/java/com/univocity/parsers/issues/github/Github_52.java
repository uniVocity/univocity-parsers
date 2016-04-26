/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/52
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_52 {

	@DataProvider
	public Object[][] inputProvider() {
		return new Object[][]{
				{4, "\n", "A\n1\n2\n"},
				{3, "\n", "A\n1\n2"},
				{4, "\r\n", "A\r\n1\r\n2\r\n"},
				{3, "\r\n", "A\r\n1\r\n2"},
				{3, "\r\n", "A\r\n1\r\n2\r"}, //I want to see the world burn
		};
	}

	@Test(dataProvider = "inputProvider")
	public void testRowCount(final int expectedLineCount, String lineSeparator, String input) {
		RowProcessor rowProcessor = new AbstractRowProcessor() {
			long lastIndex = 0;

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				assertEquals(context.currentLine(), ++lastIndex);
			}

			@Override
			public void processEnded(ParsingContext context) {
				assertEquals(context.currentLine(), expectedLineCount);
			}
		};

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setRowProcessor(rowProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(input));

	}
}
