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
package com.univocity.parsers.tsv;

import java.io.*;

import org.testng.annotations.*;

import com.univocity.parsers.common.processor.*;

public class TsvWriterTest extends TsvParserTest {

	@DataProvider
	public Object[][] lineSeparatorProvider() {
		return new Object[][] {
				{ false, new char[] { '\n' } },
				{ true, new char[] { '\r', '\n' } },
				{ true, new char[] { '\n' } },
				{ false, new char[] { '\r', '\n' } },
		};
	}

	@Test(enabled = true, dataProvider = "lineSeparatorProvider")
	public void writeTest(boolean quoteAllFields, char[] lineSeparator) throws Exception {
		TsvWriterSettings settings = new TsvWriterSettings();

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);

		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		TsvWriter writer = new TsvWriter(new OutputStreamWriter(tsvResult, "UTF-8"), settings);

		Object[][] expectedResult = new Object[][] {
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00" },
				{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00" },
				{ null, null, "Venture \"Extended Edition\"", null, "4900.00" },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, "5", null, null },
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1997", "Ford", "E350", " ac, abs, moon ", "3000.00" },
				{ "1997", "Ford", "E350", " ac, abs, moon ", "3000.00" },
				{ "19 97", "Fo rd", "E350", " ac, abs, moon ", "3000.00" },
				{ null, " ", null, "  ", "30 00.00" },
				{ "1997", "Ford", "E350", " \" ac, abs, moon \" ", "3000.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \" ", "3000.00" },
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
	public void writeSelectedColumnOnly(boolean quoteAllFields, char[] lineSeparator) throws Exception {
		TsvWriterSettings settings = new TsvWriterSettings();

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };
		settings.getFormat().setLineSeparator(lineSeparator);
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setHeaders(expectedHeaders);
		settings.selectFields("Model", "Price");

		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		TsvWriter writer = new TsvWriter(new OutputStreamWriter(tsvResult, "UTF-8"), settings);

		Object[][] input = new Object[][] {
				{ "E350", "3000.00" },
				{ "Venture \"Extended Edition\"", "4900.00" },
				{ "Grand Cherokee", "4799.00" },
				{ "Venture \"Extended Edition, Very Large\"", "5000.00" },
				{ "Venture \"Extended Edition\"", "4900.00" },
				{ null, null },
				{ "5", null },
				{ "E350", "3000.00" },
		};
		writer.writeHeaders();
		writer.writeRowsAndClose(input);

		Object[][] expectedResult = new Object[][] {
				{ null, null, "E350", null, "3000.00" },
				{ null, null, "Venture \"Extended Edition\"", null, "4900.00" },
				{ null, null, "Grand Cherokee", null, "4799.00" },
				{ null, null, "Venture \"Extended Edition, Very Large\"", null, "5000.00" },
				{ null, null, "Venture \"Extended Edition\"", null, "4900.00" },
				{ null, null, null, null, null },
				{ null, null, "5", null, null },
				{ null, null, "E350", null, "3000.00" },
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
}
