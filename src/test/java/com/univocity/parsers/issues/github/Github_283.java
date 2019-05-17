/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/283
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_283 {

	private static final String INPUT = "a,A, a , A ,a ,A , b \n1,2,3,4,5,6,7";

	private static final String[] ALL_FIELDS = new String[]{"a", "A", " a ", " A ", "a ", "A ", "B"};

	@DataProvider
	public Object[][] paramProvider() {
		return new Object[][]{
				{true, ALL_FIELDS},
				{false, ALL_FIELDS},

				{true, new String[]{" a ", " A ", "a ", "A ", "B"}},
				{false, new String[]{" a ", " A ", "a ", "A ", "B"}},

				{true, new String[]{"a", "A", " a ", " A ", " B"}},
				{false, new String[]{"a", "A", " a ", " A ", " B"}},
		};
	}


	@Test(dataProvider = "paramProvider")
	public void testHandlingOfSimilarHeaders(boolean trim, String[] selectedFields) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setHeaderExtractionEnabled(true);
		settings.trimValues(trim);
		settings.selectFields(selectedFields);

		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(new StringReader(INPUT));

		String[] headers = parser.getContext().headers();
		assertEquals(headers.length, 7);

		Set<String> selected = new LinkedHashSet<String>(Arrays.asList(selectedFields));

		Record record = parser.parseNextRecord();
		assertEqual(selected, "a", getValue(record, "a"), 1);
		assertEqual(selected, "A", getValue(record, "A"), 2);
		assertEqual(selected, " a ", getValue(record, " a "), 3);
		assertEqual(selected, " A ", getValue(record, " A "), 4);
		assertEqual(selected, "a ", getValue(record, "a "), 5);
		assertEqual(selected, "A ", getValue(record, "A "), 6);

		String _B = selectedFields[selectedFields.length - 1];
		assertEqual(selected, _B, getValue(record, "b"), 7);
		assertEqual(selected, _B, getValue(record, " b "), 7);
		assertEqual(selected, _B, getValue(record, "B"), 7);
		assertEqual(selected, _B, getValue(record, " B"), 7);
		assertEqual(selected, _B, getValue(record, " B   "), 7);
		parser.stopParsing();
	}

	private Integer getValue(Record record, String field) {
		try {
			return record.getInt(field);
		} catch (IllegalArgumentException e) {
			assertTrue(e.getMessage().startsWith("Header name '" + field + "' not found."));
			return null;
		}

	}

	private void assertEqual(Set<String> selectedFields, String field, Integer value, int expected) {
		if (selectedFields.contains(field)) {
			assertEquals(value.intValue(), expected);
		} else {
			assertNull(value);
		}
	}

	@Test(dataProvider = "paramProvider")
	public void testWritingSimilarHeaders(boolean trim, String[] selectedFields) {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaders(ALL_FIELDS);
		settings.trimValues(trim);
		settings.selectFields(selectedFields);
		settings.setHeaderWritingEnabled(true);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		int k = 0;
		Object[] row = new Object[selectedFields.length];
		for (int i = 0; i < selectedFields.length; i++) {
			for (int j = 0; j < ALL_FIELDS.length; j++) {
				if (ALL_FIELDS[j].equals(selectedFields[i])) {
					row[k++] = j + 1;
					break;
				}
			}
		}
		if(k < row.length && row[k] == null){
			row[k] = 7;
		}

		writer.writeRow(row);

		writer.close();

		validateWrittenOutput(output, selectedFields);
	}

	private void validateWrittenOutput(StringWriter output, String[] selectedFields) {
		Set<String> selected = new LinkedHashSet<String>(Arrays.asList(selectedFields));
		StringBuilder expectedOutput = new StringBuilder();
		for (int i = 0; i < ALL_FIELDS.length; i++) {
			if (i > 0) {
				expectedOutput.append(',');
			}
			expectedOutput.append(ALL_FIELDS[i]);
		}
		expectedOutput.append('\n');

		for (int i = 0; i < ALL_FIELDS.length; i++) {
			if (i > 0) {
				expectedOutput.append(',');
			}
			if (selected.contains(ALL_FIELDS[i]) || i == 6) /* header 6 is 'b', while selection is 'B' or ' B'. */ {
				expectedOutput.append(i + 1);
			}
		}
		expectedOutput.append('\n');

		assertEquals(output.toString(), expectedOutput.toString());
	}

	public static class A {
		@Parsed(field = "a")
		private Integer a1;
		@Parsed(field = "A")
		private Integer a2;
		@Parsed(field = " a ")
		private Integer a3;
		@Parsed(field = " A ")
		private Integer a4;
		@Parsed(field = "a ")
		private Integer a5;
		@Parsed(field = "A ")
		private Integer a6;
		@Parsed(field = "B")
		private Integer b;

	}

	@Test(dataProvider = "paramProvider")
	public void testHandlingOfSimilarHeadersInClass(boolean trim, String[] selectedFields) throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		BeanListProcessor<A> processor = new BeanListProcessor<A>(A.class);
		settings.setProcessor(processor);
		settings.setLineSeparatorDetectionEnabled(true);
		settings.setHeaderExtractionEnabled(true);
		settings.selectFields(selectedFields);
		settings.trimValues(trim);

		new CsvParser(settings).parse(new StringReader(INPUT));

		Set<String> selected = new LinkedHashSet<String>(Arrays.asList(selectedFields));

		A a = processor.getBeans().get(0);

		assertEqual(selected, "a", a.a1, 1);
		assertEqual(selected, "A", a.a2, 2);
		assertEqual(selected, " a ", a.a3, 3);
		assertEqual(selected, " A ", a.a4, 4);
		assertEqual(selected, "a ", a.a5, 5);
		assertEqual(selected, "A ", a.a6, 6);

		String _B = selectedFields[selectedFields.length - 1];
		assertEqual(selected, _B, a.b, 7);
	}

	@Test(dataProvider = "paramProvider")
	public void testWritingSimilarHeadersInClass(boolean trim, String[] selectedFields) throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.trimValues(trim);
		settings.selectFields(selectedFields);
		settings.setHeaderWritingEnabled(true);

		settings.setRowWriterProcessor(new BeanWriterProcessor<A>(A.class));

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		A a = new A();
		a.a1 = 1;
		a.a2 = 2;
		a.a3 = 3;
		a.a4 = 4;
		a.a5 = 5;
		a.a6 = 6;
		a.b = 7;

		writer.processRecord(a);

		writer.close();

		validateWrittenOutput(output, selectedFields);
	}
}
