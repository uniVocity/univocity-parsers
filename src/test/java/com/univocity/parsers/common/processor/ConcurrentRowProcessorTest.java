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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class ConcurrentRowProcessorTest {

	private final int LINES = 5000;
	private String input;

	@BeforeClass
	public void init() throws Exception {
		StringBuilder bigInput = new StringBuilder("A,B,C,D,E,F,G\n");

		for (int i = 0; i < LINES; i++) {
			bigInput.append("A").append(i);
			bigInput.append(",B").append(i);
			bigInput.append(",C").append(i);
			bigInput.append(",D").append(i);
			bigInput.append(",E").append(i);
			bigInput.append(",F").append(i);
			bigInput.append(",G").append(i);
			bigInput.append("\n");
		}
		input = bigInput.toString();

	}

	@DataProvider
	private Object[][] getLimits() {
		return new Object[][]{
				{-1},
				{0},
				{1},
				{2},
				{5},
				{10},
				{100}
		};
	}

	@Test(dataProvider = "getLimits")
	public void concurrentRowProcessorTest(int limit) throws Exception {
		ColumnProcessor processor = new ColumnProcessor();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setColumnReorderingEnabled(true);

		Reader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);

		ConcurrentRowProcessor concurrentRowProcessor = new ConcurrentRowProcessor(processor, limit);
		settings.setProcessor(concurrentRowProcessor);

		CsvParser parser = new CsvParser(settings);

		//long start = System.currentTimeMillis();
		parser.parse(reader);

		List<List<String>> columnValues = processor.getColumnValuesAsList();
		//System.out.println("Concurrently processed " + LINES + " lines in " + (System.currentTimeMillis() - start) + "ms with limit of " + limit);

		assertEquals(columnValues.size(), 7);
		for (int i = 0; i < 7; i++) {
			assertEquals(columnValues.get(i).size(), LINES);
		}
	}

	@Test
	public void ensureContextIsPreserved() throws Exception {

		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setColumnReorderingEnabled(true);

		Reader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);

		final StringBuilder out = new StringBuilder("A,B,C,D,E,F,G\n");

		RowProcessor myProcessor = new AbstractRowProcessor(){
			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				out.append(context.currentParsedContent());
			}

			@Override
			public void processEnded(ParsingContext context) {
				assertEquals(out.toString(), input);
			}
		};

		ConcurrentRowProcessor concurrent = new ConcurrentRowProcessor(myProcessor);
		concurrent.setContextCopyingEnabled(true);
		settings.setProcessor(concurrent);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);
	}
}
