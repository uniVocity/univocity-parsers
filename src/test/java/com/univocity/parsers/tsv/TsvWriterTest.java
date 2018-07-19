/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
package com.univocity.parsers.tsv;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

import static org.testng.Assert.*;

public class TsvWriterTest extends TsvParserTest {

	@DataProvider
	public Object[][] lineSeparatorProvider() {
		return new Object[][]{
			{ new char[]{'\n'}},
			{ new char[]{'\r', '\n'}},
			{ new char[]{'\n'}},
			{ new char[]{'\r', '\n'}},
		};
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeTest(char[] lineSeparator) throws Exception {
		TsvWriterSettings settings = new TsvWriterSettings();

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);

		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		TsvWriter writer = new TsvWriter(new OutputStreamWriter(tsvResult, "UTF-8"), settings);

		Object[][] expectedResult = new Object[][]{
			{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
			{"1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"},
			{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
			{"1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"},
			{null, null, "Venture \"Extended Edition\"", null, "4900.00"},
			{null, null, null, null, null},
			{null, null, null, null, null},
			{null, null, "5", null, null},
			{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
			{"1997", "Ford", "E350", " ac, abs, moon ", "3000.00"},
			{"1997", "Ford", "E350", " ac, abs, moon ", "3000.00"},
			{"19 97", "Fo rd", "E350", " ac, abs, moon ", "3000.00"},
			{null, " ", null, "  ", "30 00.00"},
			{"1997", "Ford", "E350", " \" ac, abs, moon \" ", "3000.00"},
			{"1997", "Ford", "E350", "\" ac, abs, moon \" ", "3000.00"},
		};

		writer.writeHeaders();

		for (int i = 0; i < 4; i++) {
			writer.writeRow(expectedResult[i]);
		}
		writer.writeRow("-->skipping this line (10) as well");
		for (int i = 4; i < expectedResult.length; i++) {
			writer.writeRow(expectedResult[i]);
		}
		writer.close();

		String result = tsvResult.toString();
		result = "This line and the following should be skipped. The third is ignored automatically because it is blank\n\n\n".replaceAll("\n", new String(lineSeparator)) + result;

		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setRowProcessor(processor);
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);

		TsvParser parser = new TsvParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
		} catch (Error e) {
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeSelectedColumnOnly(char[] lineSeparator) throws Exception {
		TsvWriterSettings settings = new TsvWriterSettings();

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);
		settings.selectFields("Model", "Price");

		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		TsvWriter writer = new TsvWriter(new OutputStreamWriter(tsvResult, "UTF-8"), settings);

		Object[][] input = new Object[][]{
			{"E350", "3000.00"},
			{"Venture \"Extended Edition\"", "4900.00"},
			{"Grand Cherokee", "4799.00"},
			{"Venture \"Extended Edition, Very Large\"", "5000.00"},
			{"Venture \"Extended Edition\"", "4900.00"},
			{null, null},
			{"5", null},
			{"E350", "3000.00"},
		};
		writer.writeHeaders();
		writer.writeRowsAndClose(input);

		Object[][] expectedResult = new Object[][]{
			{null, null, "E350", null, "3000.00"},
			{null, null, "Venture \"Extended Edition\"", null, "4900.00"},
			{null, null, "Grand Cherokee", null, "4799.00"},
			{null, null, "Venture \"Extended Edition, Very Large\"", null, "5000.00"},
			{null, null, "Venture \"Extended Edition\"", null, "4900.00"},
			{null, null, null, null, null},
			{null, null, "5", null, null},
			{null, null, "E350", null, "3000.00"},
		};

		String result = tsvResult.toString();

		RowListProcessor rowList = new RowListProcessor();
		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setRowProcessor(rowList);
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);

		TsvParser parser = new TsvParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(rowList, expectedHeaders, expectedResult);
		} catch (Error e) {
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeSelectedColumnOnlyToString(char[] lineSeparator) throws Exception {
		TsvWriterSettings settings = new TsvWriterSettings();

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);
		settings.selectFields("Model", "Price");

		TsvWriter writer = new TsvWriter(settings);

		Object[][] input = new Object[][]{
			{"E350", "3000.00"},
			{"Venture \"Extended Edition\"", "4900.00"},
			{"Grand Cherokee", "4799.00"},
			{"Venture \"Extended Edition, Very Large\"", "5000.00"},
			{"Venture \"Extended Edition\"", "4900.00"},
			{null, null},
			{"5", null},
			{"E350", "3000.00"},
		};

		String headers = writer.writeHeadersToString();
		assertEquals(headers, "Year	Make	Model	Description	Price");

		List<String> rowList = writer.writeRowsToString(input);
		assertEquals(rowList.get(0), "		E350		3000.00");
		assertEquals(rowList.get(1), "		Venture \"Extended Edition\"		4900.00");
		assertEquals(rowList.get(2), "		Grand Cherokee		4799.00");
		assertEquals(rowList.get(3), "		Venture \"Extended Edition, Very Large\"		5000.00");
		assertEquals(rowList.get(4), "		Venture \"Extended Edition\"		4900.00");
		assertEquals(rowList.get(5), "				");
		assertEquals(rowList.get(6), "		5		");
		assertEquals(rowList.get(7), "		E350		3000.00");
	}

	@Test
	public void parseWithLineJoining(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.setLineJoiningEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.trimValues(false);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A","B","\nC");
		writer.writeRow("1","2","\n3\\");

		writer.close();

		assertEquals(out.toString(), "A	B	\\\nC\n" +
				"1	2	\\\n" +
				"3\\\\\n");
	}

	@Test
	public void parseWithConstructorUsingFile() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreLeadingWhitespaces(false);
		File file = File.createTempFile("test", "tsv");

		TsvWriter writer = new TsvWriter(file, settings);
		writer.writeRow("A","B","\nC");
		writer.close();

		assertEquals(readFileContent(file), "A\tB\t\\nC\n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsString() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "tsv");

		TsvWriter writer = new TsvWriter(file, "UTF-8", settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã\té\n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsCharset() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "tsv");

		TsvWriter writer = new TsvWriter(file, Charset.forName("UTF-8"), settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã\té\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStream() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "tsv");
		FileOutputStream outputStream = new FileOutputStream(file);

		TsvWriter writer = new TsvWriter(outputStream, settings);
		writer.writeRow("A","B","\nC");
		writer.close();

		assertEquals(readFileContent(file), "A\tB\tC\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsString() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "tsv");
		FileOutputStream outputStream = new FileOutputStream(file);

		TsvWriter writer = new TsvWriter(outputStream, "UTF-8", settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã\té\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsCharset() throws IOException {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "tsv");
		FileOutputStream outputStream = new FileOutputStream(file);

		TsvWriter writer = new TsvWriter(outputStream, Charset.forName("UTF-8"), settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã\té\n");
	}

	@Test
	public void appendEscapeT(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(true);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A ", "\\\t");
		writer.close();

		assertEquals(out.toString(), "A\t\\\\\\t\n");
	}

	@Test
	public void appendEscapeTNotIgnoringWhitespaces(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(false);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A ", "\\\t");
		writer.close();

		assertEquals(out.toString(), "A \t\\\\\\t\n");
	}

	@Test
	public void appendEscapeR(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(true);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A ", "\\\r");
		writer.close();

		assertEquals(out.toString(), "A\t\\\\\\r\n");
	}

	@Test
	public void appendEscapeRNotIgnoringWhitespaces(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(false);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A ", "\\\r");
		writer.close();

		assertEquals(out.toString(), "A \t\\\\\\r\n");
	}

	@Test
	public void appendEscapeSlash(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(true);

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);

		writer.writeRow("A", "\\\\");
		writer.close();

		assertEquals(out.toString(), "A\t\\\\\\\\\n");
	}

	@Test
	public void appendNullValueWhenThereIsNoContent(){
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setNullValue("null");

		StringWriter out = new StringWriter();
		TsvWriter writer = new TsvWriter(out, settings);
		writer.writeRow("A", " ");
		writer.close();

		assertEquals(out.toString(), "A\tnull\n");
	}

	@Test
	public void testBitsAreNotDiscardedWhenWriting() {
		TsvWriterSettings settings = new TsvWriterSettings();
		settings.setSkipBitsAsWhitespace(false);

		TsvWriter writer = new TsvWriter(settings);
		String line;

		line = writer.writeRowToString(new String[]{"\0 a", "b"});
		assertEquals(line, "\0 a\tb");

		line = writer.writeRowToString(new String[]{"\0 a ", " b\1 "});
		assertEquals(line, "\0 a\tb\1");

		line = writer.writeRowToString(new String[]{"\2 a ", " b\2"});
		assertEquals(line, "a\tb");
	}

}
