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

public class ObjectColumnProcessorTest {

	private static final String INPUT = "" +
		"A,B,C" +
		"\n1,true,C" +
		"\n2,false" +
		"\n3,,C" +
		"\n4,false,C,55.4";

	@Test
	public void testColumnValues() {
		ObjectColumnProcessor processor = new ObjectColumnProcessor();
		processor.convertFields(Conversions.toInteger()).add("A");
		processor.convertFields(Conversions.toBoolean()).add("B");
		processor.convertFields(Conversions.toChar()).add("C");
		processor.convertIndexes(Conversions.toBigDecimal()).add(3);

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader(INPUT));

		Object[][] expectedValues = new Object[][]{
			{1, 2, 3, 4},
			{true, false, null, false},
			{'C', null, 'C', 'C'},
			{null, null, null, new BigDecimal("55.4")}
		};

		List<List<Object>> columnValues = processor.getColumnValuesAsList();
		Map<Integer, List<Object>> columnsByIndex = processor.getColumnValuesAsMapOfIndexes();

		assertEquals(columnValues.size(), expectedValues.length);

		int i = 0;
		for (List<Object> column : columnValues) {
			assertEquals(column.toArray(), expectedValues[i]);
			assertEquals(columnsByIndex.get(i).toArray(), expectedValues[i]);
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
