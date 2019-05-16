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
package com.univocity.parsers.csv;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import org.testng.annotations.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

import static org.testng.Assert.*;

public class CsvWriterTest extends CsvParserTest {

	@DataProvider
	public Object[][] lineSeparatorProvider() {
		return new Object[][]{
				{false, new char[]{'\n'}},
				{true, new char[]{'\r', '\n'}},
				{true, new char[]{'\n'}},
				{false, new char[]{'\r', '\n'}},
		};
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeTest(boolean quoteAllFields, char[] lineSeparator) throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};
		settings.setQuoteAllFields(quoteAllFields);
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);

		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

		CsvWriter writer = new CsvWriter(new OutputStreamWriter(csvResult, "UTF-8"), settings);

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

		String result = csvResult.toString();
		result = "This line and the following should be skipped. The third is ignored automatically because it is blank\n\n\n".replaceAll("\n", new String(lineSeparator)) + result;

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setRowProcessor(processor);
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
		} catch (Error e) {
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeSelectedColumnOnly(boolean quoteAllFields, char[] lineSeparator) throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};
		settings.setQuoteAllFields(quoteAllFields);
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);
		settings.selectFields("Model", "Price");

		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

		CsvWriter writer = new CsvWriter(new OutputStreamWriter(csvResult, "UTF-8"), settings);

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

		String result = csvResult.toString();

		RowListProcessor rowList = new RowListProcessor();
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setRowProcessor(rowList);
		parserSettings.getFormat().setLineSeparator(lineSeparator);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setIgnoreLeadingWhitespaces(false);
		parserSettings.setIgnoreTrailingWhitespaces(false);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(result));

		try {
			assertHeadersAndValuesMatch(rowList, expectedHeaders, expectedResult);
		} catch (Error e) {
			System.out.println("FAILED:\n===\n" + result + "\n===");
			throw e;
		}
	}

	@Test
	public void testWritingQuotedValuesWithTrailingWhistespaces() throws Exception {
		Object[] row = new Object[]{1, "Line1\nLine2 "};

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\r\n");
		settings.setIgnoreTrailingWhitespaces(false);

		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
		CsvWriter writer = new CsvWriter(new OutputStreamWriter(csvResult, "UTF-8"), settings);
		writer.writeRow(row);
		writer.close();

		String expected = "1,\"Line1\r\nLine2 \"\r\n";

		assertEquals(csvResult.toString(), expected);
	}

	@Test
	public void testWritingQuotedValuesIgnoringTrailingWhistespaces() throws Exception {
		Object[] row = new Object[]{1, "Line1\nLine2 "};

		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\r\n");
		settings.setIgnoreTrailingWhitespaces(true);

		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();
		CsvWriter writer = new CsvWriter(new OutputStreamWriter(csvResult, "UTF-8"), settings);
		writer.writeRow(row);
		writer.close();

		String expected = "1,\"Line1\r\nLine2\"\r\n";

		assertEquals(csvResult.toString(), expected);
	}

	@Test
	public void testWriteToString() throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\r\n");
		settings.setIgnoreTrailingWhitespaces(true);

		CsvWriter writer = new CsvWriter(settings);
		String result = writer.writeRowToString(new Object[]{1, "Line1\nLine2 "});

		String expected = "1,\"Line1\r\nLine2\"";

		assertEquals(result, expected);
	}

	@DataProvider
	public Object[][] escapeHandlingParameterProvider() {
		return new Object[][]{
				{false, false, "A|\"", "\",B|||\"\""},    //default: escapes only the quoted value
				{false, true, "A|||\"", "\",B|||\"\""},    //escape the unquoted value
				{true, false, "A|\"", "\",B|\"\""},        //assumes input is already escaped and won't change it. Quotes introduced around value with delimiter
				{true, true, "A|\"", "\",B|\"\""}        //same as above, configured to escape the unquoted value but assumes input is already escaped.
		};
	}

	@Test(dataProvider = "escapeHandlingParameterProvider")
	public void testHandlingOfEscapeSequences(boolean inputEscaped, boolean escapeUnquoted, String expected1, String expected2) throws Exception {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setInputEscaped(inputEscaped);
		settings.setEscapeUnquotedValues(escapeUnquoted);
		settings.getFormat().setCharToEscapeQuoteEscaping('|');
		settings.getFormat().setQuoteEscape('|');

		String[] line1 = new String[]{"A|\""};
		String[] line2 = new String[]{",B|\""}; // will quote because of the column separator

		CsvWriter writer = new CsvWriter(settings);
		String result1 = writer.writeRowToString(line1);
		String result2 = writer.writeRowToString(line2);

		//System.out.println(result1);
		//System.out.println(result2);
		assertEquals(result1, expected1);
		assertEquals(result2, expected2);
	}

	@Test
	public void testWritingWithIndexSelection() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.selectIndexes(4, 1);

		CsvWriter writer = new CsvWriter(settings);
		String result1 = writer.writeRowToString(1, 2);

		writer.updateFieldSelection(0, 3, 5);

		String result2 = writer.writeRowToString('A', 'B', 'C');

		//System.out.println(result1);
		//System.out.println(result2);

		assertEquals(result1, ",2,,,1");
		assertEquals(result2, "A,,,B,,C");
	}

	@Test
	public void testWritingWithIndexExclusion() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setMaxColumns(8);
		settings.excludeIndexes(4, 1);

		CsvWriter writer = new CsvWriter(settings);
		String result1 = writer.writeRowToString(1, 2, 3, 4, 5, 6);
		writer.updateFieldExclusion(1, 3, 5, 7);
		String result2 = writer.writeRowToString(7, 8, 9, 10);

//		System.out.println(result1);
//		System.out.println(result2);

		assertEquals(result1, "1,,2,3,,4,5,6");
		assertEquals(result2, "7,,8,,9,,10,");
	}

	@DataProvider
	public Object[][] blanksProvider() {
		return new Object[][]{
				{false, "--", "//,//,//,\"\"\"\"\"\",--"},
				{true, "//", "\"//\",\"//\",\"//\",\"\"\"\"\"\",\"//\""},
				{false, null, "//,//,//,\"\"\"\"\"\","},
				{true, null, "\"//\",\"//\",\"//\",\"\"\"\"\"\",\"\""},
		};
	}

	@Test(dataProvider = "blanksProvider")
	public void testWriteBlanks(boolean quoteAllFields, String nullValue, String expectedResult) {
		CsvWriterSettings s = new CsvWriterSettings();
		s.setQuoteAllFields(quoteAllFields);
		s.getFormat().setLineSeparator("\n");
		s.setNullValue(nullValue);
		s.setEmptyValue("//");
		CsvWriter w = new CsvWriter(s);

		CsvParserSettings ps = new CsvParserSettings();
		ps.setNullValue(nullValue);
		ps.setEmptyValue("//");
		CsvParser p = new CsvParser(ps);

		String result = w.writeRowToString("   ", " ", "", "\"\"", null);

		assertEquals(result, expectedResult);

		String[] row = p.parseLine(result);
		assertEquals(row[0], "//");
		assertEquals(row[1], "//");
		assertEquals(row[2], "//");
		assertEquals(row[3], "\"\"");
		if (quoteAllFields) {
			assertEquals(row[4], "//");
		} else {
			assertEquals(row[4], nullValue);
		}
	}

	@Test
	public void testWriteWithArrayExpansion() {
		StringBuilder longText = new StringBuilder(1000000);
		for (int i = 0; i < 1000000; i++) {
			longText.append(i % 10);
		}

		CsvWriterSettings s = new CsvWriterSettings();
		s.setMaxCharsPerColumn(2);
		CsvWriter w = new CsvWriter(s);

		w.addValue(longText);
		String value = w.writeValuesToString().trim();
		assertEquals(value.length(), longText.length());
		assertEquals(value, longText.toString());

		w.addValue(longText);
		w.addValue(longText);
		value = w.writeValuesToString().trim();
		assertEquals(value.length(), longText.length() * 2 + 1);
		assertEquals(value, longText.toString() + "," + longText.toString());
	}

	@Test
	public void testLineEndingsAreNotModified() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setNormalizeLineEndingsWithinQuotes(false);
		settings.getFormat().setLineSeparator("\r\n");
		settings.trimValues(false);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		writer.writeRow(new String[]{"1", " Line1 \r\n Line2 \r Line3 \n Line4 \n\r "});
		writer.writeRow(new String[]{"2", " Line10 \r\n Line11 "});
		writer.close();

		String result = output.toString();

		assertEquals(result, "1,\" Line1 \r\n Line2 \r Line3 \n Line4 \n\r \"\r\n" +
				"2,\" Line10 \r\n Line11 \"\r\n");

	}

	@Test
	public void testEscapeQuoteInValues() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.trimValues(false);
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('\'');
		settings.getFormat().setCharToEscapeQuoteEscaping('\'');
		settings.setQuoteEscapingEnabled(true);

		CsvWriter writer = new CsvWriter(settings);

		assertEquals(writer.writeRowToString(new String[]{"my 'precious' value"}), "'my ''precious'' value'");
		assertEquals(writer.writeRowToString(new String[]{"'"}), "''''");
		assertEquals(writer.writeRowToString(new String[]{" '"}), "' '''");
		assertEquals(writer.writeRowToString(new String[]{" ' "}), "' '' '");
	}

	@Test
	public void testQuotationTriggers() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.trimValues(false);
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('\'');
		settings.getFormat().setCharToEscapeQuoteEscaping('\'');
		settings.setQuotationTriggers(' ', '\t', 'Z');
		settings.setQuoteEscapingEnabled(false);

		CsvWriter writer = new CsvWriter(settings);

		assertEquals(writer.writeRowToString(new String[]{"my 'precious' value"}), "'my ''precious'' value'"); //quotes because of the spaces
		assertEquals(writer.writeRowToString(new String[]{"my'precious'value"}), "my'precious'value"); //no triggers here, no quotation applied
		assertEquals(writer.writeRowToString(new String[]{"lulz"}), "lulz");
		assertEquals(writer.writeRowToString(new String[]{"lulZ"}), "'lulZ'"); //uppercase Z is a trigger
		assertEquals(writer.writeRowToString(new String[]{"I'm\ta\tTSV!"}), "'I''m\ta\tTSV!'");
	}

	@Test
	public void parseWithConstructorUsingFile() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		CsvWriter writer = new CsvWriter(file, settings);
		writer.writeRow("A", "B", "\nC");
		writer.close();

		assertEquals(readFileContent(file), "A,B,C\n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsString() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		File file = File.createTempFile("test", "csv");

		CsvWriter writer = new CsvWriter(file, "UTF-8", settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã,é\n");
	}

	@Test
	public void parseWithConstructorUsingFileAndEncodingAsCharset() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		File file = File.createTempFile("test", "csv");

		CsvWriter writer = new CsvWriter(file, Charset.forName("UTF-8"), settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã,é\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStream() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreLeadingWhitespaces(false);

		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		CsvWriter writer = new CsvWriter(outputStream, settings);
		writer.writeRow("A", "B", "\nC");
		writer.close();

		assertEquals(readFileContent(file), "A,B,\"\nC\"\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsString() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		CsvWriter writer = new CsvWriter(outputStream, "UTF-8", settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã,é\n");
	}

	@Test
	public void parseWithConstructorUsingOutputStreamAndEncodingAsCharset() throws IOException {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		File file = File.createTempFile("test", "csv");
		FileOutputStream outputStream = new FileOutputStream(file);

		CsvWriter writer = new CsvWriter(outputStream, Charset.forName("UTF-8"), settings);
		writer.writeRow("ã", "é");
		writer.close();

		assertEquals(readFileContent(file), "ã,é\n");
	}

	@Test
	public void appendEscapeEscape() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setEscapeUnquotedValues(true);
		settings.getFormat().setCharToEscapeQuoteEscaping('\\');
		settings.getFormat().setQuoteEscape('\'');

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);
		writer.writeRow("A", "B\'");
		writer.close();

		assertEquals(output.toString(), "A,B\\'\n");
	}

	@Test
	public void testErrorMessageRestrictions() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setErrorContentLength(0);

		java.lang.Object bomb = new Object() {
			public String toString() {
				throw new UnsupportedOperationException("boom!");
			}
		};

		try {
			new CsvWriter(settings).writeRowToString(new Object[]{bomb});
			fail("Expecting an exception here");
		} catch (TextWritingException ex) {
			assertNull(ex.getRecordData());
		}

		settings.setErrorContentLength(2);
		try {
			new CsvWriter(settings).writeRowToString(new Object[]{bomb});
			fail("Expecting an exception here");
		} catch (TextWritingException ex) {
			assertEquals(ex.getRecordData()[0], bomb);
		}
	}

	@Test
	public void testWriteEmptyValue() {
		CsvWriterSettings s = new CsvWriterSettings();
		s.setNullValue("NULL");
		s.setEmptyValue("EMPTY");
		CsvWriter w = new CsvWriter(s);

		String result = w.writeRowToString(new String[]{null, "", " ", "", "  "});
		assertEquals(result, "NULL,EMPTY,EMPTY,EMPTY,EMPTY");
	}

	@DataProvider
	public Object[][] nullAndEmptyValueProvider() {
		return new Object[][]{
				{"\"\"", "\"\"", false, false, "\"\"", "\"\""},
				{"\"\"", "\"\"", true, false, "\"\"", "\"\""},
				{"\"", "\"", false, false, "\"\"\"\"", "\"\"\"\""},
				{"\"", "\"", true, false, "\"\"\"\"", "\"\"\"\""},
				{"a", "b", false, false, "a", "b"},
				{"a", "b", true, false, "\"a\"", "\"b\""},
				{null, null, false, false, "", ""},
				{null, null, true, false, "\"\"", "\"\""},
				{"", "", false, false, "", ""},
				{"", "", true, false, "\"\"", "\"\""},
				{"\"a", "\"b", false, false, "\"\"\"a\"", "\"\"\"b\""},
				{"\"a", "\"b", true, false, "\"\"\"a\"", "\"\"\"b\""},
				{"\"a\"", "\"b\"", false, false, "\"a\"", "\"b\""},
				{"\"a\"", "\"b\"", true, false, "\"a\"", "\"b\""},
				//quote escaping enabled.
				{"\"\"", "\"\"", false, true, "\"\"", "\"\""},
				{"\"\"", "\"\"", true, true, "\"\"", "\"\""},
				{"\"", "\"", false, true, "\"\"\"\"", "\"\"\"\""},
				{"\"", "\"", true, true, "\"\"\"\"", "\"\"\"\""},
				{"a", "b", false, true, "a", "b"},
				{"a", "b", true, true, "\"a\"", "\"b\""},
				{null, null, false, true, "", ""},
				{null, null, true, true, "\"\"", "\"\""},
				{"", "", false, true, "", ""},
				{"", "", true, true, "\"\"", "\"\""},
				{"\"a", "\"b", false, true, "\"\"\"a\"", "\"\"\"b\""},
				{"\"a", "\"b", true, true, "\"\"\"a\"", "\"\"\"b\""},
				{"\"a\"", "\"b\"", false, true, "\"a\"", "\"b\""},
				{"\"a\"", "\"b\"", true, true, "\"a\"", "\"b\""},
		};

	}

	@Test(dataProvider = "nullAndEmptyValueProvider")
	public void testWriteNullValueAsEmptyQuotes(String nullValue, String emptyValue, boolean quoteAllFields, boolean quoteEscapingEnabled, String expectedNullValue, String expectedEmptyValue) {
		CsvWriterSettings s = new CsvWriterSettings();
		s.setNullValue(nullValue);
		s.setEmptyValue(emptyValue);
		s.setQuoteAllFields(quoteAllFields);
		s.setQuoteEscapingEnabled(quoteEscapingEnabled);

		String result;
		result = new CsvWriter(s).writeRowToString(new String[]{null, ""});
		assertEquals(result, expectedNullValue + ',' + expectedEmptyValue);
	}

	@Test
	public void testBitsAreNotDiscardedWhenWriting() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.setSkipBitsAsWhitespace(false);

		CsvWriter writer = new CsvWriter(settings);
		String line;

		line = writer.writeRowToString(new String[]{"\0 a", "b"});
		assertEquals(line, "\0 a,b");

		line = writer.writeRowToString(new String[]{"\0 a ", " b\1"});
		assertEquals(line, "\0 a,b\1");

		line = writer.writeRowToString(new String[]{"\2 a ", " b\2"});
		assertEquals(line, "a,b");
	}

	@Test
	public void testCollectionWriting() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		StringWriter out = new StringWriter();

		CsvWriter writer = new CsvWriter(out, settings);

		List<String> row = new ArrayList<String>();
		row.add("value 1");
		row.add("value 2");
		writer.writeRow(row);

		writer.close();

		assertEquals(out.toString(), "value 1,value 2\n");
	}

}
