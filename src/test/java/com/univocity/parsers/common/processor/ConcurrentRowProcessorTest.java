/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.processor;

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

	@Test
	public void concurrentRowProcessorTest() throws Exception {
		ColumnProcessor processor = new ColumnProcessor();

		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setColumnReorderingEnabled(true);

		Reader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(new ConcurrentRowProcessor(processor));

		CsvParser parser = new CsvParser(settings);

		long start = System.currentTimeMillis();
		parser.parse(reader);

		List<List<String>> columnValues = processor.getColumnValuesAsList();
		System.out.println("Concurrently processed " + LINES + " lines in " + (System.currentTimeMillis() - start) + "ms");

		assertEquals(columnValues.size(), 7);
		for (int i = 0; i < 7; i++) {
			assertEquals(columnValues.get(i).size(), LINES);
		}
	}

}
