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

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

public class TsvParserTest extends ParserTestCase {

	@DataProvider(name = "tsvProvider")
	public Object[][] tsvProvider() {
		return new Object[][] {
				{ "/tsv/essential.tsv", new char[] { '\n' } },
				{ "/tsv/essential-dos.tsv", new char[] { '\r', '\n' } },
				{ "/tsv/essential.tsv", null },
				{ "/tsv/essential-dos.tsv", null },
				{ "/tsv/essential-mac.tsv", null }
		};
	}

	@Test(enabled = true, dataProvider = "tsvProvider")
	public void parseIgnoringWhitespaces(String tsvFile, char[] lineSeparator) throws Exception {
		TsvParserSettings settings = newTsvInputSettings(lineSeparator);
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);

		TsvParser parser = new TsvParser(settings);
		parser.parse(newReader(tsvFile));

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };

		String[][] expectedResult = new String[][] {
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00" },
				{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00" },
				{ null, null, "Venture \"Extended Edition\"", null, "4900.00" },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, "5", null, null },
				{ "1997", "Ford", "E350", "ac, abs, moon", "\"3000.00\"" },
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "19 97", "Fo rd", "E350", "ac, abs, moon", "3000.00" },
				{ null, null, null, "\"  \"", "30 00.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00" },
		};

		assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	protected TsvParserSettings newTsvInputSettings(char[] lineSeparator) {
		TsvParserSettings out = new TsvParserSettings();
		if (lineSeparator == null) {
			out.setLineSeparatorDetectionEnabled(true);
		} else {
			out.getFormat().setLineSeparator(lineSeparator);
		}
		return out;
	}

	@Test(enabled = true, dataProvider = "tsvProvider")
	public void parseUsingWhitespaces(String tsvFile, char[] lineSeparator) throws Exception {
		TsvParserSettings settings = newTsvInputSettings(lineSeparator);
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.setNullValue("?????");
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);

		TsvParser parser = new TsvParser(settings);
		parser.parse(newReader(tsvFile));

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };

		String[][] expectedResult = new String[][] {
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition\"", "?????", "4900.00" },
				{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition, Very Large\"", "?????", "5000.00" },
				{ "?????", "?????", "Venture \"Extended Edition\"", "?????", "4900.00" },
				{ "?????", "?????", "?????", "?????", "?????" },
				{ " ", " ", " ", " ", " " },
				{ "?????", "?????", " 5 ", "?????", "?????" },
				{ "  " },
				{ "1997 ", " Ford ", "E350", "ac, abs, moon\t", " \"3000.00\" \t" },
				{ "1997", " Ford ", "E350", " ac, abs, moon \t", "3000.00  \t" },
				{ "  1997", " Ford ", "E350", " ac, abs, moon \t", "3000.00" },
				{ "    19 97 ", " Fo rd ", "E350", " ac, abs, moon \t", "3000.00" },
				{ "\t\t", " ", "  ", " \"  \"\t", "30 00.00\t" },
				{ "1997", "Ford", "E350", " \" ac, abs, moon \" ", "3000.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \" ", "3000.00" },
		};

		assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	@Test(enabled = true, dataProvider = "tsvProvider")
	public void parseColumns(String tsvFile, char[] lineSeparator) throws Exception {
		TsvParserSettings settings = newTsvInputSettings(lineSeparator);
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.selectFields("Year");
		settings.setColumnReorderingEnabled(false);

		TsvParser parser = new TsvParser(settings);
		parser.parse(newReader(tsvFile));

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };

		String[][] expectedResult = new String[][] {
				{ "1997", null, null, null, null },
				{ "1999", null, null, null, null },
				{ "1996", null, null, null, null },
				{ "1999", null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ "1997", null, null, null, null },
				{ "1997", null, null, null, null },
				{ "1997", null, null, null, null },
				{ "19 97", null, null, null, null },
				{ null, null, null, null, null },
				{ "1997", null, null, null, null },
				{ "1997", null, null, null, null },
		};

		assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}

	private String[] process(String input, Integer[] indexesToExclude, Integer[] indexesToSelect, String[] fieldsToExclude, String[] fieldsToSelect) {
		RowListProcessor processor = new RowListProcessor();
		StringReader reader = new StringReader(input);
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(fieldsToExclude != null || fieldsToSelect != null);

		if (indexesToExclude != null) {
			settings.excludeIndexes(indexesToExclude);
		} else if (fieldsToExclude != null) {
			settings.excludeFields(fieldsToExclude);
		} else if (indexesToSelect != null) {
			settings.selectIndexes(indexesToSelect);
		} else if (fieldsToSelect != null) {
			settings.selectFields(fieldsToSelect);
		}

		TsvParser parser = new TsvParser(settings);
		parser.parse(reader);

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);
		return rows.get(0);
	}

	@Test(enabled = true)
	public void columnSelectionTest() {
		String[] result;
		String input = "a	b	c	d	e";

		Integer[] indexesToExclude = new Integer[] { 0, 4 };
		result = process(input, indexesToExclude, null, null, null);
		assertEquals(result, new String[] { "b", "c", "d" });

		Integer[] indexesToSelect = new Integer[] { 0, 4 };
		result = process(input, null, indexesToSelect, null, null);
		assertEquals(result, new String[] { "a", "e" });

		input = "ha	hb	hc	hd	he\na	b	c	d	e";

		String[] fieldsToExclude = new String[] { "hb", "hd" };
		result = process(input, null, null, fieldsToExclude, null);
		assertEquals(result, new String[] { "a", "c", "e" });

		String[] fieldsToSelect = new String[] { "hb", "hd" };
		result = process(input, null, null, null, fieldsToSelect);
		assertEquals(result, new String[] { "b", "d" });
	}

	@Override
	protected RowListProcessor newRowListProcessor() {
		return new RowListProcessor() {
			@Override
			public void processStarted(ParsingContext context) {
				super.processStarted(context);
				context.skipLines(2);
			}

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				super.rowProcessed(row, context);

//				 for (int i = 0; i < row.length; i++) {
//				 row[i] = ">>" + row[i] + "<<";
//				 }
//				 System.out.println(context.currentLine() + " => " + Arrays.toString(row));

				if (context.currentLine() == 8) {
					context.skipLines(1);
				}
			}
		};
	}

	@Test(enabled = true, dataProvider = "tsvProvider")
	public void parseOneByOne(String tsvFile, char[] lineSeparator) throws Exception {
		TsvParserSettings settings = newTsvInputSettings(lineSeparator);
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setHeaders("YR", "MK", "MDL", "DSC", "PRC");

		List<Object[]> results = new ArrayList<Object[]>();
		TsvParser parser = new TsvParser(settings);
		try {
			parser.beginParsing(newReader(tsvFile));

			Object[] row = null;
			while ((row = parser.parseNext()) != null) {
				if (row.length == 5) {
					results.add(row);
				}
			}
		} finally {
			parser.stopParsing();
		}

		String[] expectedHeaders = new String[] { "YR", "MK", "MDL", "DSC", "PRC" };

		String[][] expectedResult = new String[][] {
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00" },
				{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00" },
				{ null, null, "Venture \"Extended Edition\"", null, "4900.00" },
				{ null, null, null, null, null },
				{ null, null, null, null, null },
				{ null, null, "5", null, null },
				{ "1997", "Ford", "E350", "ac, abs, moon", "\"3000.00\"" },
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "19 97", "Fo rd", "E350", "ac, abs, moon", "3000.00" },
				{ null, null, null, "\"  \"", "30 00.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00" },
				{ "1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00" },
		};

		Object[] headers = processor.getHeaders();
		TestUtils.assertEquals(headers, expectedHeaders);

		assertEquals(results.size(), expectedResult.length);

		for (int i = 0; i < expectedResult.length; i++) {
			Object[] result = results.get(i);
			String[] expectedRow = expectedResult[i];
			assertEquals(result, expectedRow);
		}
	}

	@Test(enabled = true, dataProvider = "tsvProvider")
	public void parse3Records(String tsvFile, char[] lineSeparator) throws Exception {
		TsvParserSettings settings = newTsvInputSettings(lineSeparator);
		settings.setRowProcessor(processor);
		settings.setHeaderExtractionEnabled(true);
		settings.setIgnoreLeadingWhitespaces(true);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setNumberOfRecordsToRead(3);

		TsvParser parser = new TsvParser(settings);
		parser.parse(newReader(tsvFile));

		String[] expectedHeaders = new String[] { "Year", "Make", "Model", "Description", "Price" };

		String[][] expectedResult = new String[][] {
				{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
				{ "1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00" },
				{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
		};

		assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}
}
