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
package com.univocity.parsers.examples;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.math.*;
import java.util.*;
import java.util.Map.*;

public class TsvParserExamples extends Example {

	@Test
	public void example001ParseAll() throws Exception {
		//##CODE_START

		TsvParserSettings settings = new TsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		// creates a TSV parser
		TsvParser parser = new TsvParser(settings);

		// parses all rows in one go.
		List<String[]> allRows = parser.parseAll(getReader("/examples/example.tsv"));

		//##CODE_END
		printAndValidate(null, allRows);
	}

	@Test
	public void example002ReadSimpleTsv() throws Exception {
		StringBuilder out = new StringBuilder();

		TsvParserSettings settings = new TsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START

		// creates a TSV parser
		TsvParser parser = new TsvParser(settings);

		// call beginParsing to read records one by one, iterator-style.
		parser.beginParsing(getReader("/examples/example.tsv"));

		String[] row;
		while ((row = parser.parseNext()) != null) {
			println(out, Arrays.toString(row));
		}

		// The resources are closed automatically when the end of the input is reached,
		// or when an error happens, but you can call stopParsing() at any time.

		// You only need to use this if you are not parsing the entire content.
		// But it doesn't hurt if you call it anyway.
		parser.stopParsing();

		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example003ReadTsvWithRowProcessor() throws Exception {
		//##CODE_START

		// The settings object provides many configuration options
		TsvParserSettings parserSettings = new TsvParserSettings();

		//You can configure the parser to automatically detect what line separator sequence is in the input
		parserSettings.setLineSeparatorDetectionEnabled(true);

		// A RowListProcessor stores each parsed row in a List.
		RowListProcessor rowProcessor = new RowListProcessor();

		// You can configure the parser to use a RowProcessor to process the values of each parsed row.
		// You will find more RowProcessors in the 'com.univocity.parsers.common.processor' package, but you can also create your own.
		parserSettings.setProcessor(rowProcessor);

		// Let's consider the first parsed row as the headers of each column in the file.
		parserSettings.setHeaderExtractionEnabled(true);

		// creates a parser instance with the given settings
		TsvParser parser = new TsvParser(parserSettings);

		// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
		parser.parse(getReader("/examples/example.tsv"));

		// get the parsed records from the RowListProcessor here.
		// Note that different implementations of RowProcessor will provide different sets of functionalities.
		String[] headers = rowProcessor.getHeaders();
		List<String[]> rows = rowProcessor.getRows();

		//##CODE_END

		printAndValidate(headers, rows);
	}

	@Test
	public void example004ReadTsvAndConvertValues() throws Exception {
		final StringBuilder out = new StringBuilder();

		//##CODE_START

		// ObjectRowProcessor converts the parsed values and gives you the resulting row.
		ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
			@Override
			public void rowProcessed(Object[] row, ParsingContext context) {
				//here is the row. Let's just print it.
				println(out, Arrays.toString(row));
			}
		};

		// converts values in the "Price" column (index 4) to BigDecimal
		rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(4);

		// converts the values in columns "Make, Model and Description" to lower case, and sets the value "chevy" to null.
		rowProcessor.convertFields(Conversions.toLowerCase(), Conversions.toNull("chevy")).set("Make", "Model", "Description");

		// converts the values at index 0 (year) to BigInteger. Nulls are converted to BigInteger.ZERO.
		rowProcessor.convertFields(new BigIntegerConversion(BigInteger.ZERO, "0")).set("year");

		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		TsvParser parser = new TsvParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(getReader("/examples/example.tsv"));

		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example005UsingAnnotations() throws Exception {
		//##CODE_START
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);

		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		TsvParser parser = new TsvParser(parserSettings);
		parser.parse(getReader("/examples/bean_test.tsv"));

		// The BeanListProcessor provides a list of objects extracted from the input.
		List<TestBean> beans = rowProcessor.getBeans();

		//##CODE_END

		printAndValidate(beans.toString());
	}

	@Test
	public void example006MasterDetail() throws Exception {
		//##CODE_START
		// 1st, Create a RowProcessor to process all "detail" elements
		ObjectRowListProcessor detailProcessor = new ObjectRowListProcessor();

		// converts values at in the "Amount" column (position 1 in the file) to integer.
		detailProcessor.convertIndexes(Conversions.toInteger()).set(1);

		// 2nd, Create MasterDetailProcessor to identify whether or not a row is the master row.
		// the row placement argument indicates whether the master detail row occurs before or after a sequence of "detail" rows.
		MasterDetailListProcessor masterRowProcessor = new MasterDetailListProcessor(RowPlacement.BOTTOM, detailProcessor) {
			@Override
			protected boolean isMasterRecord(String[] row, ParsingContext context) {
				//Returns true if the parsed row is the master row.
				//In this example, rows that have "Total" in the first column are master rows.
				return "Total".equals(row[0]);
			}
		};
		// We want our master rows to store BigIntegers in the "Amount" column
		masterRowProcessor.convertIndexes(Conversions.toBigInteger()).set(1);

		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);

		// Set the RowProcessor to the masterRowProcessor.
		parserSettings.setProcessor(masterRowProcessor);

		TsvParser parser = new TsvParser(parserSettings);
		parser.parse(getReader("/examples/master_detail.tsv"));

		// Here we get the MasterDetailRecord elements.
		List<MasterDetailRecord> rows = masterRowProcessor.getRecords();
		MasterDetailRecord masterRecord = rows.get(0);

		// The master record has one master row and multiple detail rows.
		Object[] masterRow = masterRecord.getMasterRow();
		List<Object[]> detailRows = masterRecord.getDetailRows();
		//##CODE_END
		printAndValidate(masterRow, detailRows);
	}

	@Test
	public void example007ConvertColumns() throws Exception {
		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		//##CODE_START

		// ObjectColumnProcessor converts the parsed values and stores them in columns
		// Use BatchedObjectColumnProcessor to process columns in batches
		ObjectColumnProcessor rowProcessor = new ObjectColumnProcessor();

		// converts values in the "Price" column (index 4) to BigDecimal
		rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(4);

		// converts the values in columns "Make, Model and Description" to lower case, and sets the value "chevy" to null.
		rowProcessor.convertFields(Conversions.toLowerCase(), Conversions.toNull("chevy")).set("Make", "Model", "Description");

		// converts the values at index 0 (year) to BigInteger. Nulls are converted to BigInteger.ZERO.
		rowProcessor.convertFields(new BigIntegerConversion(BigInteger.ZERO, "0")).set("year");

		parserSettings.setProcessor(rowProcessor);

		TsvParser parser = new TsvParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(getReader("/examples/example.tsv"));

		//Let's get the column values:
		Map<Integer, List<Object>> columnValues = rowProcessor.getColumnValuesAsMapOfIndexes();

		//##CODE_END
		StringBuilder out = new StringBuilder();
		for (Entry<Integer, List<Object>> e : columnValues.entrySet()) {
			List<Object> values = e.getValue();
			Integer columnIndex = e.getKey();
			println(out, columnIndex + " -> " + values);
		}

		printAndValidate(out);
	}

	@Test
	public void example008ParseLine() throws Exception {
		StringBuilder out = new StringBuilder();
		//##CODE_START
		// creates a TSV parser
		TsvParser parser = new TsvParser(new TsvParserSettings());

		String[] line;
		line = parser.parseLine("A	B	C");
		println(out, Arrays.toString(line));

		line = parser.parseLine("1	2	3	4");
		println(out, Arrays.toString(line));
		//##CODE_END
		printAndValidate(out);
	}

	@Test
	public void example009ParseJoinedLines() throws Exception {
		//##CODE_START

		//Let's write 3 values to a TSV, one of them has a line break.
		String []values = new String[]{"Value 1",	"Breaking [\n] here", "Value 3"};

		TsvWriterSettings writerSettings = new TsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");

		// In TSV, we can have line separators escaped with a slash before a line break. In this case the current
		// line will be joined with the next line.
		writerSettings.setLineJoiningEnabled(true);

		//Let's write the values and see how the data looks like:
		String writtenLine = new TsvWriter(writerSettings).writeRowToString(values);
		println("Written data\n------------\n" + writtenLine);

		// To parse, we just use the same confiuration:
		TsvParserSettings parserSettings = new TsvParserSettings();
		parserSettings.setLineJoiningEnabled(true);
		parserSettings.getFormat().setLineSeparator("\n");

		TsvParser parser = new TsvParser(parserSettings);

		//Let's parse the contents we've just written:
		values = parser.parseLine(writtenLine);

		println("\nParsed elements\n---------------");
		println("First: " + values[0]);
		println("Second: " + values[1]);
		println("Third: " + values[2]);

		//##CODE_END
		printAndValidate();
	}


}
