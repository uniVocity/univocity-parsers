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
package com.univocity.parsers.fixed;

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
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
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(getFieldLengths());
		settings.getFormat().setLineSeparator(lineSeparator);

		settings.setNullValue("?");

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

		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);
		settings.getFormat().setPadding('-');

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

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new InputStreamReader(this.getClass().getResourceAsStream("/examples/bean_test.csv"), "UTF-8"));

		String[] headers = rowProcessor.getHeaders();
		List<Object[]> rows = rowProcessor.getRows();

		rows.get(0)[2] = "  " + rows.get(0)[2] + "  ";

		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();
		FixedWidthWriterSettings writerSettings = new FixedWidthWriterSettings(new FixedWidthFieldLengths(11, 15, 10, 10, 20));
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
	public void testWritingWithPaddingsPerField(){
		List<Le> tofLes = new ArrayList<Le>();
		for (int i =0; i<2;i++) {
			Le le = new Le();
			le.plzV=i;
			le.plzB=i+10;
			le.ziel="ziel"+i;
			tofLes.add(le);
		}

		FixedWidthFieldLengths fieldLengths = new FixedWidthFieldLengths(20,8);
		fieldLengths.setPadding('0', 1);
		fieldLengths.setAlignment(FieldAlignment.RIGHT, 1);
		FixedWidthWriterSettings fwws = new FixedWidthWriterSettings(fieldLengths);
		fwws.getFormat().setPadding('_');
		fwws.getFormat().setLineSeparator("\n");
		fwws.setDefaultAlignmentForHeaders(FieldAlignment.CENTER);
		fwws.setHeaders("ziel","plzV");
		fwws.setHeaderWritingEnabled(true);
		BeanWriterProcessor<Le> rowWriterProcessor = new BeanWriterProcessor<Le>(Le.class);
		fwws.setRowWriterProcessor(rowWriterProcessor);

		StringWriter writer = new StringWriter();
		new FixedWidthWriter(writer,fwws).processRecordsAndClose(tofLes);

		assertEquals(writer.toString(), "________ziel__________plzV__\nziel0_______________00000000\nziel1_______________00000001\n");

	}
}
