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
package com.univocity.parsers.fixed;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import static org.testng.Assert.*;

public class FixedWidthWriterTest extends FixedWidthParserTest {

	@DataProvider
	public Object[][] lineSeparatorProvider() {
		return new Object[][]{
				{new char[]{'\n'}},
				{new char[]{'\r', '\n'}},
		};
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void testWriter(char[] lineSeparator) throws Exception {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(getFieldLengths());
		settings.getFormat().setLineSeparator(lineSeparator);

		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		Object[][] expectedResult = new Object[][]{
				{"2013-FEB-28", "Harry Dong", "15000.99", "8.786",},
				{"2013-JAN-1", "Billy Rubin", "15100.99", "5",},
				{"2012-SEP-1", "Willie Stroker", "15000.00", "6",},
				{"2012-JAN-11", "Mike Litoris", "15000", "4.86",},
				{"2010-JUL-01", "Gaye Males", "1", "8.6",},
		};

		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setHeaders(expectedHeaders);
		settings.getFormat().setPadding('-');

		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();

		FixedWidthWriter writer = new FixedWidthWriter(new OutputStreamWriter(fixedWidthResult, "UTF-8"), settings);
		writer.writeHeaders();
		for (int i = 0; i < 2; i++) {
			writer.writeRow(expectedResult[i]);
		}
		writer.writeEmptyRow();
		writer.commentRow("pre 2013");
		writer.writeEmptyRow();
		for (int i = 2; i < expectedResult.length; i++) {
			writer.writeRow(expectedResult[i]);
		}
		writer.close();

		String result = fixedWidthResult.toString();

		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(getFieldLengths());
		parserSettings.getFormat().setPadding('-');
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setRowProcessor(processor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
		} catch (Error e) {
			result = result.replaceAll("\r", "\\\\r");
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void testWriterWithSpacesAndOverflow(char[] lineSeparator) throws Exception {
		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		String[][] input = new String[][]{
				{null, null},
				null,
				{},
				{"2013-FEB-28", "  Harry Dong  ", "15000.99", " 8.786 ",},
				{"2013-JANUARY-1", " Billy Rubin  - Ha ", " 15100.99345345345345345345345345345345345", " - 5 - ",},

		};

		String[][] expectedResult = new String[][]{
				{"?", "?"},
				{"2013-FEB-28", "  Harry Dong  ", "15000.99", " 8.786 ",},
				{"2013-JANUAR", " Billy Rubin  - Ha ", " 15100.9934534534534", " - 5 - ",},

		};

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(getFieldLengths());
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.getFormat().setPadding('-');
		settings.setNullValue("?");

		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);


		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();

		FixedWidthWriter writer = new FixedWidthWriter(new OutputStreamWriter(fixedWidthResult, "UTF-8"), settings);
		writer.writeHeaders();
		writer.writeRowsAndClose(input);

		String result = fixedWidthResult.toString();

		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(getFieldLengths());
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.getFormat().setPadding('-');
		parserSettings.setRowProcessor(processor);
		parserSettings.setRecordEndsOnNewline(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);
		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
		} catch (Error e) {
			result = result.replaceAll("\r", "\\\\r");
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test
	public void writeFromCsv() throws Exception {
		ObjectRowListProcessor rowProcessor = new ObjectRowListProcessor();

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setRowProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/examples/bean_test.csv"), "UTF-8"));

		String[] headers = rowProcessor.getHeaders();
		List<Object[]> rows = rowProcessor.getRows();

		rows.get(0)[2] = "  " + rows.get(0)[2] + "  ";

		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();
		FixedWidthWriterSettings writerSettings = new FixedWidthWriterSettings(new FixedWidthFields(11, 15, 10, 10, 20));
		writerSettings.getFormat().setPadding('_');
		writerSettings.setIgnoreLeadingWhitespaces(false);
		writerSettings.setIgnoreTrailingWhitespaces(false);

		FixedWidthWriter writer = new FixedWidthWriter(new OutputStreamWriter(fixedWidthResult, "UTF-8"), writerSettings);
		writer.writeHeaders(headers);
		writer.writeRowsAndClose(rows);

		//System.out.println("Result 1: \n" + fixedWidthResult.toString().replaceAll("\\r", "#").replaceAll("\\n", "@"));
		int correctLength = fixedWidthResult.toString().length();

		fixedWidthResult = new ByteArrayOutputStream();
		writerSettings.setIgnoreLeadingWhitespaces(true);
		writerSettings.setIgnoreTrailingWhitespaces(true);

		writer = new FixedWidthWriter(new OutputStreamWriter(fixedWidthResult, "UTF-8"), writerSettings);
		writer.writeHeaders(headers);
		writer.writeRowsAndClose(rows);

		//System.out.println("Result 2: \n" + fixedWidthResult.toString().replaceAll("\\r", "#").replaceAll("\\n", "@"));
		int length = fixedWidthResult.toString().length();

		assertEquals(correctLength, length);
	}

	public static class Le {
		@Parsed
		private Integer plzV;
		@Parsed
		private Integer plzB;
		@Parsed
		private String ziel;
	}

	@Test
	public void testWritingWithPaddingsPerField() {
		List<Le> tofLes = new ArrayList<Le>();
		for (int i = 0; i < 2; i++) {
			Le le = new Le();
			le.plzV = i;
			le.plzB = i + 10;
			le.ziel = "ziel" + i;
			tofLes.add(le);
		}

		FixedWidthFields fieldLengths = new FixedWidthFields(20, 8);
		fieldLengths.setPadding('0', 1);
		fieldLengths.setAlignment(FieldAlignment.RIGHT, 1);
		FixedWidthWriterSettings fwws = new FixedWidthWriterSettings(fieldLengths);
		fwws.getFormat().setPadding('_');
		fwws.getFormat().setLineSeparator("\n");
		fwws.setDefaultAlignmentForHeaders(FieldAlignment.CENTER);
		fwws.setHeaders("ziel", "plzV");
		fwws.setHeaderWritingEnabled(true);
		BeanWriterProcessor<Le> rowWriterProcessor = new BeanWriterProcessor<Le>(Le.class);
		fwws.setRowWriterProcessor(rowWriterProcessor);

		StringWriter writer = new StringWriter();
		new FixedWidthWriter(writer, fwws).processRecordsAndClose(tofLes);

		assertEquals(writer.toString(), "________ziel__________plzV__\nziel0_______________00000000\nziel1_______________00000001\n");
	}

	@Test
	public void parseWithConstructorUsingFile() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(4, 4));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");

		FixedWidthWriter writer = new FixedWidthWriter(file, settings);
		writer.writeRow("A", "B");
		writer.close();

		assertEquals(readFileContent(file), "A   B   \n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsString() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(4, 4));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");

		FixedWidthWriter writer = new FixedWidthWriter(file, "UTF-8", settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã   É   \n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsCharset() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(3, 3));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");

		FixedWidthWriter writer = new FixedWidthWriter(file, Charset.forName("UTF-8"), settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã  É  \n");
	}

	@Test
	public void parseWithConstructorUsingOutputStream() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(3, 3));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		FixedWidthWriter writer = new FixedWidthWriter(outputStream, settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã  É  \n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsString() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(3, 3));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		FixedWidthWriter writer = new FixedWidthWriter(outputStream, "UTF-8", settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã  É  \n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsCharset() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(3, 3));
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		FixedWidthWriter writer = new FixedWidthWriter(outputStream, Charset.forName("UTF-8"), settings);
		writer.writeRow("Ã", "É");
		writer.close();
		assertEquals(readFileContent(file), "Ã  É  \n");
	}

	@Test
	public void testLookupCharsLengthMinorThanValue() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(8, 8, 8));
		settings.getFormat().setLineSeparator("\n");
		settings.addFormatForLookahead("MASTER", new FixedWidthFields(7, 7, 7, 7, 7));

		File file = File.createTempFile("test", "csv");
		FixedWidthWriter writer = new FixedWidthWriter(file, settings);

		writer.writeRow("MASTER", "some", "data", "for", "master1");
		writer.writeRow("DET", "first", "data");
		writer.close();

		assertEquals(readFileContent(file), "MASTER some   data   for    master1\nDET     first   data    \n");
	}

	@Test
	public void testGotTruncatedExactlyAfterOneOrMoreWhitespaces() throws IOException {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(6, 6));
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(true);
		File file = File.createTempFile("test", "csv");
		FixedWidthWriter writer = new FixedWidthWriter(file, settings);

		writer.writeRow("first..data", "other data");
		writer.close();

		assertEquals(readFileContent(file), "first.other \n");
	}

	@Test
	public void testWriteLineSeparatorAfterRecord() {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(2, 2));
		settings.getFormat().setLineSeparator("\n");
		settings.setWriteLineSeparatorAfterRecord(false);
		settings.setIgnoreTrailingWhitespaces(false);

		StringWriter out = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow("ab", "cd");
		writer.writeRow("e\n", "f"); //writes line separator as part of the value, not a record delimiter
		writer.writeRow("g", "hi");
		writer.close();

		assertEquals(out.toString(), "abcde\nf g hi");
	}

	@Test
	public void testWriteLineSeparatorAfterRandomContent() {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(2, 2));
		settings.getFormat().setLineSeparator("\n");
		settings.setWriteLineSeparatorAfterRecord(false);
		settings.setIgnoreTrailingWhitespaces(false);

		StringWriter out = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow("ab", "cd");
		writer.commentRow(">>some random comment<<");
		writer.writeEmptyRow(); //does nothing.
		writer.writeRow("data"); //writer won't validate content and will just dump it as a record.
		writer.writeRow("++", "++");
		writer.close();

		assertEquals(out.toString(), "abcd#>>some random comment<<data++++");
	}

	@Test
	public void testBitsAreNotDiscardedWhenWriting() {
		FixedWidthFields lengths = new FixedWidthFields(3, 3);
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);
		settings.getFormat().setPadding('_');
		settings.setSkipBitsAsWhitespace(false);

		FixedWidthWriter writer = new FixedWidthWriter(settings);
		String line;

		line = writer.writeRowToString(new String[]{"\0 a", "b"});
		assertEquals(line, "\0 ab__");

		line = writer.writeRowToString(new String[]{"\0 a ", " b\1 "});
		assertEquals(line, "\0 ab\1_");

		line = writer.writeRowToString(new String[]{"\2 a ", " b\2"});
		assertEquals(line, "a__b__");
	}

	@Test
	public void testWriteFixedWidthAnnotation() throws Exception {
		BeanWriterProcessor<X> rowProcessor = new BeanWriterProcessor<X>(X.class);

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowWriterProcessor(rowProcessor);

		StringWriter out = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		List<X> beans = new ArrayList<X>();
		writer.writeHeaders();
		writer.processRecordsAndClose(beans);

		assertEquals(out.toString(), "a    b        \n");

		out = new StringWriter();
		writer = new FixedWidthWriter(out, settings);
		beans.add(new X(34, "blah blah"));
		beans.add(new X(7674, "etc"));

		writer.processRecordsAndClose(beans);
		assertEquals(out.toString(), "" +
				"34   blah blah\n" +
				"7674 etc      \n");
	}

	@Test
	public void testWriteFixedWidthAnnotationAndWildcard() throws Exception {

		FixedWidthWriterSettings settings = new FixedWidthWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.addFormatForLookahead("???????1", new FixedWidthFields(3, 4, 1));
		settings.addFormatForLookahead("???????2", new FixedWidthFields(4, 3, 1));
		settings.addFormatForLookahead("???????3", new FixedWidthFields(7, 1, 2));

		StringWriter out = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(out, settings);

		writer.writeRow("101", "abcd", 1);
		writer.writeRow("1011", "abc", 2222);
		writer.writeRow("1012", "xyz", 2);
		writer.writeRow("1234567", 3, "10");

		writer.close();

		assertEquals(out.toString(), "" +
				"101abcd1\n" +
				"1011abc2\n" +
				"1012xyz2\n" +
				"1234567310\n");

	}

	@Test
	public void testFieldRanges() throws Exception {
		FixedWidthFields fields = new FixedWidthFields();
		fields.addField(5, 7).addField(10,14).addField(18, 20, '_').addField(24, 25, '.');

		FixedWidthWriterSettings s = new FixedWidthWriterSettings(fields);
		s.setExpandIncompleteRows(true);
		FixedWidthWriter w = new FixedWidthWriter(s);

		String row = w.writeRowToString("67", "1234");
		assertEquals(row, "     67   1234    __    .");
	}

	@Test
	public void writeNullRowShouldReplaceWithNullValueFromSettings() {
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(new FixedWidthFields(5));
		settings.getFormat().setLineSeparator("\n");
		settings.setNullValue("N/A");
		StringWriter sw = new StringWriter();
		FixedWidthWriter fixedWidthWriter = new FixedWidthWriter(sw, settings);
		fixedWidthWriter.writeRow(new String[] {null});
		assertEquals(sw.toString(), "N/A  \n");
	}

}
