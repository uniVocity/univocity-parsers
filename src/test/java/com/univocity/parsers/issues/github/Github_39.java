/*
 * Copyright (c) 2015. uniVocity Software Pty Ltd
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
 */

package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Github_39 {

	@Test
	public void testSimpleMapWriting() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");

		CsvWriter writer = new CsvWriter(settings);

		Map<String, Integer> row = new TreeMap<String, Integer>();
		row.put("B", 2);
		row.put("A", 1);

		String result = writer.writeRowToString(row);
		assertEquals(result, "1,2");

		row.remove("A");
		result = writer.writeRowToString(row);
		assertEquals(result, ",2");

		row.remove("B");

		result = writer.writeRowToString(row);
		assertEquals(result, ",");

		assertEquals(writer.writeHeadersToString(), "A,B");
	}

	@Test
	public void testSimpleMapWritingWithHeader() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");

		CsvWriter writer = new CsvWriter(settings);

		Map<Integer, String> headerMap = new TreeMap<Integer, String>();
		headerMap.put(1, "Z");
		headerMap.put(2, "C");

		Map<Integer, String> row = new TreeMap<Integer, String>();
		row.put(1, "A");
		row.put(2, "B");

		String result = writer.writeRowToString(headerMap, row);
		assertEquals(result, "A,B");

		row.remove(1);
		result = writer.writeRowToString(headerMap, row);
		assertEquals(result, ",B");

		row.remove(2);

		result = writer.writeRowToString(headerMap, row);
		assertEquals(result, ",");

		assertEquals(writer.writeHeadersToString(), "Z,C");
	}

	@Test
	public void testMapWriting() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(true);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		Map<String, String> headerMap = new TreeMap<String, String>();
		headerMap.put("A", "HA");
		headerMap.put("B", "HB");
		headerMap.put("C", "HC");
		headerMap.put("D", "HD");
		headerMap.put("E", "HE");

		Map<String, String[]> rows = new TreeMap<String, String[]>();
		rows.put("A", new String[]{"a1", "a2"});
		rows.put("B", new String[]{"b1", "b2", "b3"});
		rows.put("D", new String[]{null, "d1"});

		writer.writeStringRowsAndClose(headerMap, rows);

		assertEquals(out.toString(), "HA,HB,HC,HD,HE\n" +
				"a1,b1,,,\n" +
				"a2,b2,,d1,\n" +
				",b3,,,\n");
	}

	@Test
	public void testMapWritingWithRowProcessor() {
		CsvWriterSettings settings = new CsvWriterSettings();

		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		processor.convertAll(Conversions.toNull("!"));
		processor.convertFields(Conversions.toUpperCase()).add("HB");
		settings.setRowWriterProcessor(processor);

		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(true);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		Map<String, String> headerMap = new TreeMap<String, String>();
		headerMap.put("A", "HA");
		headerMap.put("B", "HB");
		headerMap.put("C", "HC");
		headerMap.put("D", "HD");
		headerMap.put("E", "HE");

		Map<String, Object[]> rows = new TreeMap<String, Object[]>();
		rows.put("A", new String[]{"a1", "a2"});
		rows.put("B", new String[]{"b1", "b2", "b3"});
		rows.put("D", new String[]{null, "d1"});

		writer.processObjectRecordsAndClose(headerMap, rows);

		assertEquals(out.toString(),"HA,HB,HC,HD,HE\n" +
				"a1,B1,!,!,!\n" +
				"a2,B2,!,d1,!\n" +
				"!,B3,!,!,!\n");
	}
}

