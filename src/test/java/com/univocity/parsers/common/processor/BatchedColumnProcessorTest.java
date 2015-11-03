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

public class BatchedColumnProcessorTest {

	private static final String INPUT = "" +
		"A,B,C" +
		"\n1A,1B,1C" +
		"\n2A,2B" +
		"\n3A,3B,3C" +
		"\n4A,4B,4C,4D";

	@Test
	public void testColumnValues() {

		final String[][] expectedValueOnFirstBatch = new String[][]{
			{"1A", "2A"},
			{"1B", "2B"},
			{"1C", null},
		};

		final String[][] expectedValueOnSecondBatch = new String[][]{
			{"3A", "4A"},
			{"3B", "4B"},
			{"3C", "4C"},
			{null, "4D"}
		};

		BatchedColumnProcessor processor = new BatchedColumnProcessor(2) {
			@Override
			public void batchProcessed(int rowsInThisBatch) {
				List<List<String>> columnValues = getColumnValuesAsList();
				Map<Integer, List<String>> columnsByIndex = getColumnValuesAsMapOfIndexes();

				String[][] expectedValues = getBatchesProcessed() == 0 ? expectedValueOnFirstBatch : expectedValueOnSecondBatch;

				assertEquals(columnValues.size(), expectedValues.length);

				for (int i = 0; i < expectedValues.length; i++) {
					assertEquals(columnValues.get(i).size(), rowsInThisBatch);
					assertEquals(columnValues.get(i).toArray(), expectedValues[i]);
					assertEquals(columnsByIndex.get(i).toArray(), expectedValues[i]);
				}

				if (expectedValues.length == 4) {
					try {
						getColumnValuesAsMapOfNames();
						fail("Expected exception. No name defined for 4th column");
					} catch (Exception e) {
						//OK
					}
				}
				assertEquals(getHeaders(), new String[]{"A", "B", "C"});
			}
		};

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader(INPUT));
	}
}
