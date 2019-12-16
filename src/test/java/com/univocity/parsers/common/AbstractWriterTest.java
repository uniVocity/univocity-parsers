/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
package com.univocity.parsers.common;

import com.univocity.parsers.ParserTestCase;
import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import com.univocity.parsers.fixed.*;
import com.univocity.parsers.fixed.FixedWidthFields;
import org.testng.annotations.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertEqualsNoOrder;

public class AbstractWriterTest extends ParserTestCase {

	@Test
	public void testWriteRowWithObjectCollection() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(4, 4));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FixedWidthWriter writer = new FixedWidthWriter(file, settings);

		Collection<Object> objects = new ArrayList<Object>();
		objects.add("A");
		objects.add("B");

		writer.writeRow(objects);
		writer.close();

		assertEquals(readFileContent(file), "A   B   \n");
	}

	@Test
	public void testWriteRowWithNullObjectCollection() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(4, 4));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FixedWidthWriter writer = new FixedWidthWriter(file, settings);

		Collection<Object> objects = null;
		writer.writeRow(objects);
		writer.close();

		assertEquals(readFileContent(file), "");
	}

	@Test
	public void testWriteStringRows() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(4, 4));
		settings.getFormat().setLineSeparator("\n");
		settings.addFormatForLookahead("MASTER", new FixedWidthFields(3, 3, 3, 3));

		File file = File.createTempFile("test", "csv");
		FixedWidthWriter writer = new FixedWidthWriter(file, settings);

		List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(Arrays.asList("A", "B"));
		rows.add(Arrays.asList("C", "D"));
		writer.writeStringRows(rows);
		writer.close();

		assertEquals(readFileContent(file), "A   B   \nC   D   \n");
	}

	@Test
	public void testWriteBufferedWriter() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(3, 3));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

		FixedWidthWriter writer = new FixedWidthWriter(bufferedWriter, settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã  É  \n");
	}

	@Test
	public void testRowExpansion() {
		StringWriter output = new StringWriter();

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setExpandIncompleteRows(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(true);
		settings.setHeaders("A", "B", "C", "D", "E", "F");

		CsvWriter writer = new CsvWriter(output, settings);
		writer.writeRow();
		writer.writeRow("V1", "V2", "V3");
		writer.writeRow("V1", "V2", "V3", 4, 5);
		writer.writeRow("V1", "V2", "V3", 4, 5, 6);

		writer.close();

		assertEquals(output.toString(), "A,B,C,D,E,F\n,,,,,\nV1,V2,V3,,,\nV1,V2,V3,4,5,\nV1,V2,V3,4,5,6\n");
	}

	@Test
	public void testSelectFields() {
		StringWriter output = new StringWriter();

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setHeaderWritingEnabled(true);
		settings.setColumnReorderingEnabled(true);
		settings.setHeaders("A", "B", "C");
		settings.selectFields("A", "C");

		CsvWriter writer = new CsvWriter(output, settings);
		writer.writeRow("V1", "V2", "V3");
		writer.writeRow("V1", "V2", "V3");
		writer.writeRow("V1", "V2", "V3");

		writer.close();

		assertEquals(output.toString(), "A,C\nV1,V3\nV1,V3\nV1,V3\n");
	}

	@Test
	public void testExcludeFields() {
		StringWriter output = new StringWriter();

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setHeaderWritingEnabled(true);
		settings.setColumnReorderingEnabled(true);
		settings.setHeaders("A", "B", "C");
		settings.excludeFields("B");

		CsvWriter writer = new CsvWriter(output, settings);
		writer.writeRow("V1", "V2", "V3");
		writer.writeRow("V1", "V2", "V3");
		writer.writeRow("V1", "V2", "V3");

		writer.close();

		assertEquals(output.toString(), "A,C\nV1,V3\nV1,V3\nV1,V3\n");
	}
}