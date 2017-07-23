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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class ColumnProcessorTest {

	private static final String INPUT = "" +
		"A,B,C" +
		"\n1A,1B,1C" +
		"\n2A,2B" +
		"\n3A,3B,3C" +
		"\n4A,4B,4C,4D";

	@Test
	public void testColumnValues() {
		ColumnProcessor processor = new ColumnProcessor();
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);
		settings.getFormat().setLineSeparator("\n");

		new CsvParser(settings).parse(new StringReader(INPUT));

		String[][] expectedValues = new String[][]{
			{"1A", "2A", "3A", "4A"},
			{"1B", "2B", "3B", "4B"},
			{"1C", null, "3C", "4C"},
			{null, null, null, "4D"}
		};

		List<List<String>> columnValues = processor.getColumnValuesAsList();
		Map<Integer, List<String>> columnsByIndex = processor.getColumnValuesAsMapOfIndexes();

		assertEquals(columnValues.size(), expectedValues.length);

		int i = 0;
		for (List<String> column : columnValues) {
			assertEquals(column.toArray(new String[0]), expectedValues[i]);
			assertEquals(columnsByIndex.get(i).toArray(new String[0]), expectedValues[i]);
			i++;
		}

		try {
			processor.getColumnValuesAsMapOfNames();
			fail("Expected exception. No name defined for 4th column");
		} catch (Exception e) {
			//OK
		}

		assertEquals(processor.getHeaders(), new String[]{"A", "B", "C"});
	}
}
