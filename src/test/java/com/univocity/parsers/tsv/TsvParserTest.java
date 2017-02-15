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

import com.univocity.parsers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class TsvParserTest extends ParserTestCase {

	@DataProvider(name = "tsvProvider")
	public Object[][] tsvProvider() {
		return new Object[][]{
				{"/tsv/essential.tsv", new char[]{'\n'}},
				{"/tsv/essential-dos.tsv", new char[]{'\r', '\n'}},
				{"/tsv/essential.tsv", null},
				{"/tsv/essential-dos.tsv", null},
				{"/tsv/essential-mac.tsv", null}
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

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};

		String[][] expectedResult = new String[][]{
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"},
				{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
				{"1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"},
				{null, null, "Venture \"Extended Edition\"", null, "4900.00"},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, "5", null, null},
				{"1997", "Ford", "E350", "ac, abs, moon", "\"3000.00\""},
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"19 97", "Fo rd", "E350", "ac, abs, moon", "3000.00"},
				{null, null, null, "\"  \"", "30 00.00"},
				{"1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00"},
				{"1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00"},
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

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};

		String[][] expectedResult = new String[][]{
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1999", "Chevy", "Venture \"Extended Edition\"", "?????", "4900.00"},
				{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
				{"1999", "Chevy", "Venture \"Extended Edition, Very Large\"", "?????", "5000.00"},
				{"?????", "?????", "Venture \"Extended Edition\"", "?????", "4900.00"},
				{"?????", "?????", "?????", "?????", "?????"},
				{" ", " ", " ", " ", " "},
				{"?????", "?????", " 5 ", "?????", "?????"},
				{"  "},
				{"1997 ", " Ford ", "E350", "ac, abs, moon\t", " \"3000.00\" \t"},
				{"1997", " Ford ", "E350", " ac, abs, moon \t", "3000.00  \t"},
				{"  1997", " Ford ", "E350", " ac, abs, moon \t", "3000.00"},
				{"    19 97 ", " Fo rd ", "E350", " ac, abs, moon \t", "3000.00"},
				{"\t\t", " ", "  ", " \"  \"\t", "30 00.00\t"},
				{"1997", "Ford", "E350", " \" ac, abs, moon \" ", "3000.00"},
				{"1997", "Ford", "E350", "\" ac, abs, moon \" ", "3000.00"},
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

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};

		String[][] expectedResult = new String[][]{
				{"1997", null, null, null, null},
				{"1999", null, null, null, null},
				{"1996", null, null, null, null},
				{"1999", null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{"1997", null, null, null, null},
				{"1997", null, null, null, null},
				{"1997", null, null, null, null},
				{"19 97", null, null, null, null},
				{null, null, null, null, null},
				{"1997", null, null, null, null},
				{"1997", null, null, null, null},
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

		Integer[] indexesToExclude = new Integer[]{0, 4};
		result = process(input, indexesToExclude, null, null, null);
		assertEquals(result, new String[]{"b", "c", "d"});

		Integer[] indexesToSelect = new Integer[]{0, 4};
		result = process(input, null, indexesToSelect, null, null);
		assertEquals(result, new String[]{"a", "e"});

		input = "ha	hb	hc	hd	he\na	b	c	d	e";

		String[] fieldsToExclude = new String[]{"hb", "hd"};
		result = process(input, null, null, fieldsToExclude, null);
		assertEquals(result, new String[]{"a", "c", "e"});

		String[] fieldsToSelect = new String[]{"hb", "hd"};
		result = process(input, null, null, null, fieldsToSelect);
		assertEquals(result, new String[]{"b", "d"});
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

		String[] expectedHeaders = new String[]{"YR", "MK", "MDL", "DSC", "PRC"};

		String[][] expectedResult = new String[][]{
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"},
				{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
				{"1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"},
				{null, null, "Venture \"Extended Edition\"", null, "4900.00"},
				{null, null, null, null, null},
				{null, null, null, null, null},
				{null, null, "5", null, null},
				{"1997", "Ford", "E350", "ac, abs, moon", "\"3000.00\""},
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"19 97", "Fo rd", "E350", "ac, abs, moon", "3000.00"},
				{null, null, null, "\"  \"", "30 00.00"},
				{"1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00"},
				{"1997", "Ford", "E350", "\" ac, abs, moon \"", "3000.00"},
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

		String[] expectedHeaders = new String[]{"Year", "Make", "Model", "Description", "Price"};

		String[][] expectedResult = new String[][]{
				{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
				{"1999", "Chevy", "Venture \"Extended Edition\"", null, "4900.00"},
				{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
		};

		assertHeadersAndValuesMatch(expectedHeaders, expectedResult);
	}


	@Test
	public void parseWithLineJoining() {
		TsvParserSettings settings = new TsvParserSettings();
		settings.setLineJoiningEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.trimValues(false);
		TsvParser parser = new TsvParser(settings);

		List<String[]> result = parser.parseAll(new StringReader("A	B	\\\nC\n" +
				"1	2	\\\n" +
				"3\\\\"));

		assertEquals(result.get(0), new String[]{"A", "B", "\nC"});
		assertEquals(result.get(1), new String[]{"1", "2", "\n3\\"});
	}

	@Test
	public void parseIgnoreTrailingWhitespaceAppendSlash() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("\\\\"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] firstRow = rows.get(0);
		assertEquals(firstRow[0], "\\");
	}

	@Test
	public void parseIgnoreTrailingWhitespaceAppendBreakLineR() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a\\r"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] firstRow = rows.get(0);
		assertEquals(firstRow[0], "a");
	}

	@Test
	public void parseIgnoreTrailingWhitespaceJoinLines() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setLineJoiningEnabled(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a\\\nb"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] firstRow = rows.get(0);
		assertEquals(firstRow[0], "a\nb");
	}

	@Test
	public void parseIgnoreTrailingWhitespaceEscapeTab() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setLineJoiningEnabled(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a\\\tb"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] row = rows.get(0);
		assertEquals(row.length, 2);
		assertEquals(row[0], "a\\");
		assertEquals(row[1], "b");
	}

	@Test
	public void parseIgnoreTrailingWhitespaceEscapeOther() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.getFormat().setLineSeparator("\n");
		settings.setIgnoreTrailingWhitespaces(true);
		settings.setLineJoiningEnabled(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a \\\bb"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] row = rows.get(0);
		assertEquals(row.length, 1);
		assertEquals(row[0], "a \\\bb");
	}

	@Test
	public void parseNotIgnoreTrailingWhitespaceAppendBreakLineR() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(false);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a \\r"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] row = rows.get(0);
		assertEquals(row.length, 1);
		assertEquals(row[0], "a \r");
	}

	@Test
	public void parseNotIgnoreTrailingWhitespaceEscapeTab() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setLineJoiningEnabled(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a \\\tb"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] row = rows.get(0);
		assertEquals(row.length, 2);
		assertEquals(row[0], "a \\");
		assertEquals(row[1], "b");
	}

	@Test
	public void parseNotIgnoreTrailingWhitespaceEscapeOther() {
		RowListProcessor processor = new RowListProcessor();
		TsvParserSettings settings = new TsvParserSettings();
		settings.setRowProcessor(processor);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setLineJoiningEnabled(true);
		TsvParser parser = new TsvParser(settings);

		parser.parse(new StringReader("a \\\bb"));

		List<String[]> rows = processor.getRows();
		assertEquals(rows.size(), 1);

		String[] row = rows.get(0);
		assertEquals(row.length, 1);
		assertEquals(row[0], "a \\\bb");
	}

	@Test
	public void testFieldSelectionWithMismatchingNames() {
		String input = "" +
				"h1\th2\th3\n" +
				"1\t2\t3\n" +
				"4\t5\t6";

		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.selectFields("h2", "h3", "h9", "h8");
		settings.setHeaderExtractionEnabled(true);
		TsvParser parser = new TsvParser(settings);
		List<String[]> rows = parser.parseAll(new StringReader(input));
		assertEquals(rows.get(0)[0], "2");
		assertEquals(rows.get(0)[1], "3");
		assertEquals(rows.get(0)[2], null);
		assertEquals(rows.get(0)[3], null);
		assertEquals(rows.get(0).length, 4);
		assertEquals(rows.get(1)[0], "5");
		assertEquals(rows.get(1)[1], "6");
		assertEquals(rows.get(1)[2], null);
		assertEquals(rows.get(1)[3], null);
		assertEquals(rows.get(1).length, 4);
		assertEquals(rows.size(), 2);
	}

	@Test
	public void parseWithAutoExpansion() {
		TsvParserSettings settings = new TsvParserSettings();
		settings.setMaxCharsPerColumn(-1);

		StringBuilder in = new StringBuilder(100000);
		for(int i = 0; i < 100000; i++){
			in.append(i % 10);
			if(i % 10000 == 0){
				in.append('\t');
			}
		}

		String[] result = new TsvParser(settings).parseLine(in.toString());
		StringBuilder out = new StringBuilder();
		for(String value : result){
			if(out.length() > 0){
				out.append('\t');
			}
			out.append(value);
		}

		assertEquals(out.toString(), in.toString());
	}

	@Test
	public void testBitsAreNotDiscardedWhenParsing() {
		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setSkipBitsAsWhitespace(false);

		TsvParser parser = new TsvParser(parserSettings);
		String[] line;

		line = parser.parseLine("\0 a\tb");
		assertEquals(line.length, 2);
		assertEquals(line[0], "\0 a");
		assertEquals(line[1], "b");

		line = parser.parseLine("\1 a\t b \0");
		assertEquals(line.length, 2);
		assertEquals(line[0], "\1 a");
		assertEquals(line[1], "b \0");

		line = parser.parseLine("\2 a\t b\\t \1 ");
		assertEquals(line.length, 2);
		assertEquals(line[0], "a");
		assertEquals(line[1], "b\t \1");
	}
}
