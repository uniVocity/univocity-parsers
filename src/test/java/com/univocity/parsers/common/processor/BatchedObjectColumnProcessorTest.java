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

import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class BatchedObjectColumnProcessorTest {

	private static final String INPUT = "" +
		"A,B,C" +
		"\n1,true,C" +
		"\n2,false" +
		"\n3,,C" +
		"\n4,false,C,55.4";

	@Test
	public void testColumnValues() {
		final Object[][] expectedValueOnFirstBatch = new Object[][]{
			{1, 2},
			{true, false},
			{'C', null},
		};

		final Object[][] expectedValueOnSecondBatch = new Object[][]{
			{3, 4},
			{null, false},
			{'C', 'C'},
			{null, new BigDecimal("55.4")}
		};

		BatchedObjectColumnProcessor processor = new BatchedObjectColumnProcessor(2) {
			@Override
			public void batchProcessed(int rowsInThisBatch) {
				List<List<Object>> columnValues = getColumnValuesAsList();
				Map<Integer, List<Object>> columnsByIndex = getColumnValuesAsMapOfIndexes();

				Object[][] expectedValues = getBatchesProcessed() == 0 ? expectedValueOnFirstBatch : expectedValueOnSecondBatch;

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

		processor.convertFields(Conversions.toInteger()).add("A");
		processor.convertFields(Conversions.toBoolean()).add("B");
		processor.convertFields(Conversions.toChar()).add("C");
		processor.convertIndexes(Conversions.toBigDecimal()).add(3);

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader(INPUT));
	}
}
