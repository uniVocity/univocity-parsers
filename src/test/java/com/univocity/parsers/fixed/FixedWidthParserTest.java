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

import com.univocity.parsers.*;
import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import org.testng.annotations.*;

import java.io.StringReader;
import java.util.*;

import static org.testng.Assert.*;

public class FixedWidthParserTest extends ParserTestCase {

	@DataProvider(name = "fileProvider")
	public Object[][] csvProvider() {
		return new Object[][]{
				{".txt", new char[]{'\n'}},
				{"-dos.txt", new char[]{'\r', '\n'}},
				{"-mac.txt", new char[]{'\r'}},
				{".txt", null},
				{"-dos.txt", null},
				{"-mac.txt", null}
		};
	}

	protected FixedWidthFields getFieldLengths() {
		return new FixedWidthFields(new int[]{11, 38, 20, 8});
	}

	private FixedWidthParserSettings newSettings(FixedWidthFields lengths, char[] lineSeparator) {
		FixedWidthParserSettings settings = new FixedWidthParserSettings(getFieldLengths());
		if (lineSeparator == null) {
			settings.setLineSeparatorDetectionEnabled(true);
		} else {
			settings.getFormat().setLineSeparator(lineSeparator);
		}
		return settings;
	}

	@Test(enabled = true, dataProvider = "fileProvider")
	public void testFixedWidthParser(String fileExtension, char[] lineSeparator) throws Exception {
		FixedWidthParserSettings settings = newSettings(getFieldLengths(), lineSeparator);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);
		FixedWidthParser parser = new FixedWidthParser(settings);

		parser.parse(this.newReader("/fixed/essential" + fileExtension));

		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		String[][] expectedResult = new String[][]{
				{"2013-FEB-28", "Harry Dong", "15000.99", "8.786",},
				{"2013-JAN-1", "Billy Rubin", "15100.99", "5",},
				{"2012-SEP-1", "Willie Stroker", "15000.00", "6",},
				{"2012-JAN-11", "Mike Litoris", "15000", "4.86",},
				{"2010-JUL-01", "Gaye Males", "1", "8.6",},
		};

		this.assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	@Override
	protected RowListProcessor newRowListProcessor() {
		return new RowListProcessor();
	}

	@Test(enabled = true, dataProvider = "fileProvider")
	public void testFixedWidthParserSkippingUntilNewLine(String fileExtension, char[] lineSeparator) throws Exception {
		FixedWidthParserSettings settings = newSettings(getFieldLengths(), lineSeparator);
		settings.setSkipTrailingCharsUntilNewline(true);
		settings.setRecordEndsOnNewline(true);
		settings.setHeaderExtractionEnabled(false);
		settings.setRowProcessor(processor);

		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		settings.setHeaders(expectedHeaders);
		FixedWidthParser parser = new FixedWidthParser(settings);

		parser.parse(this.newReader("/fixed/essential_1" + fileExtension));

		String[][] expectedResult = new String[][]{
				{"2013-FEB-28", "Harry Dong", "15000.99", "8.786",},
				{"2013-JAN-1", "Billy Rubin", "15100.99", "5",},
				{"2012-SEP-1", "Willie Stroker"},
				{"2012-JAN-11", "Mike Litoris", "15000", "4.86",},
				{"2010-JUL-01", "Gaye Males", "1", "8.6",},
		};

		this.assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	@Test(enabled = true, dataProvider = "fileProvider")
	public void testFixedWidthParserWithPadding(String fileExtension, char[] lineSeparator) throws Exception {
		FixedWidthParserSettings settings = newSettings(getFieldLengths(), lineSeparator);
		settings.getFormat().setPadding('_');
		settings.setSkipTrailingCharsUntilNewline(true);
		settings.setRecordEndsOnNewline(true);
		settings.setHeaderExtractionEnabled(false);
		settings.setRowProcessor(processor);

		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		settings.setHeaders(expectedHeaders);
		FixedWidthParser parser = new FixedWidthParser(settings);

		parser.parse(this.newReader("/fixed/essential_3" + fileExtension));

		String[][] expectedResult = new String[][]{
				{"2013-FEB-28", "Harry Dong", "15000.99", "8.786",},
				{"2013-JAN-1", "Billy Rubin", "15100.99", "5",},
				{"2012-SEP-1", "Willie Stroker"},
				{"2012-JAN-11", "Mike Litoris", "15000", "4.86",},
				{"2010-JUL-01", "Gaye Males", "1", "8.6",},
		};

		this.assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	@Test(enabled = true, dataProvider = "fileProvider")
	public void testFixedWidthParserWithPaddingAndNoTrimming(String fileExtension, char[] lineSeparator) throws Exception {
		FixedWidthParserSettings settings = newSettings(getFieldLengths(), lineSeparator);
		settings.getFormat().setPadding('_');
		settings.setSkipTrailingCharsUntilNewline(true);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setRecordEndsOnNewline(true);
		settings.setHeaderExtractionEnabled(false);
		settings.setRowProcessor(processor);

		String[] expectedHeaders = new String[]{
				"DATE", "NAME", "OWED", "INTEREST",
		};

		settings.setHeaders(expectedHeaders);
		FixedWidthParser parser = new FixedWidthParser(settings);

		parser.parse(this.newReader("/fixed/essential_2" + fileExtension));

		String[][] expectedResult = new String[][]{
				{"2013-FEB-28", "  Harry Dong  ", "15000.99", "  8.786",},
				{"2013-JAN-1", "Billy Rubin  ", "15100.99", "5",},
				{"2012-SEP-1", " Willie Stroker"},
				{"2012-JAN-11", "Mike Litoris ", "15000", "4.86",},
				{"2010-JUL-01", " Gaye Males ", " 1 ", "8.6  ",},
		};

		this.assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	@Test
	public void testParsingWithPaddingPerField() {
		FixedWidthFields fieldLengths = new FixedWidthFields(20, 8);
		fieldLengths.setPadding('0', 1);
		fieldLengths.setAlignment(FieldAlignment.RIGHT, 1);
		FixedWidthParserSettings fwws = new FixedWidthParserSettings(fieldLengths);

		fwws.getFormat().setPadding('_');
		fwws.getFormat().setLineSeparator("\n");
		fwws.setHeaderExtractionEnabled(true);

		FixedWidthParser parser = new FixedWidthParser(fwws);
		parser.beginParsing(new StringReader("ziel____________________plzV\nziel0_______________00000000\nziel1_______________00000001\n"));

		assertEquals(parser.parseNext(), new String[]{"ziel0", null});
		assertEquals(parser.parseNext(), new String[]{"ziel1", "1"});
		assertEquals(parser.getContext().headers(), new String[]{"ziel", "plzV"});
	}

	@Test
	public void testParsingWithoutRecordBreaks() {
		int[] length = new int[]{2, 2, 2};
		FixedWidthFields lengths = new FixedWidthFields(length);
		FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);

		FixedWidthParser parser = new FixedWidthParser(settings);
		parser.beginParsing(new StringReader("abcdefghijkl"));

		String[] data;

		data = parser.parseNext();
		assertEquals(data[0], "ab");
		assertEquals(data[1], "cd");
		assertEquals(data[2], "ef");

		data = parser.parseNext();
		assertEquals(data[0], "gh");
		assertEquals(data[1], "ij");
		assertEquals(data[2], "kl");
	}

	@Test
	public void testBitsAreNotDiscardedWhenParsing() {
		FixedWidthFields lengths = new FixedWidthFields(3, 3);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');
		parserSettings.setSkipBitsAsWhitespace(false);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		String[] line;

		line = parser.parseLine("\0 a_b_");
		assertEquals(line.length, 2);
		assertEquals(line[0], "\0 a");
		assertEquals(line[1], "b");

		line = parser.parseLine("\1_ab \0");
		assertEquals(line.length, 2);
		assertEquals(line[0], "\1_a");
		assertEquals(line[1], "b \0");

		line = parser.parseLine("_\2ab\1_");
		assertEquals(line.length, 2);
		assertEquals(line[0], "a");
		assertEquals(line[1], "b\1");

		line = parser.parseLine("\2_ab\1_");
		assertEquals(line.length, 2);
		assertEquals(line[0], "a");
		assertEquals(line[1], "b\1");
	}

	public static class X {

		public X() {
		}

		public X(int a, String b) {
			this.a = a;
			this.b = b;
		}

		@Parsed
		@FixedWidth(4)
		int a;

		@Parsed
		@FixedWidth(10)
		String b;
	}

	@Test
	public void testFixedWidthAnnotation() throws Exception {
		BeanListProcessor<X> rowProcessor = new BeanListProcessor<X>(X.class);

		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(new StringReader("12  some text \n71  more text "));


		List<X> beans = rowProcessor.getBeans();
		assertEquals(beans.size(), 2);

		assertEquals(beans.get(0).a, 12);
		assertEquals(beans.get(0).b, "some text");

		assertEquals(beans.get(1).a, 71);
		assertEquals(beans.get(1).b, "more text");

	}
}
