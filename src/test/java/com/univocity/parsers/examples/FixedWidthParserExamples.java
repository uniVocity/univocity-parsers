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
package com.univocity.parsers.examples;

import java.math.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.fixed.*;

public class FixedWidthParserExamples extends Example {

	@Test
	public void example001ParseAll() throws Exception {
		//##CODE_START

		// creates the sequence of field lengths in the file to be parsed
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(4, 5, 40, 40, 8);

		// creates the default settings for a fixed width parser
		FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);

		//sets the character used for padding unwritten spaces in the file
		settings.getFormat().setPadding('_');

		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		// creates a fixed-width parser with the given settings
		FixedWidthParser parser = new FixedWidthParser(settings);

		// parses all rows in one go.
		List<String[]> allRows = parser.parseAll(getReader("/examples/example.txt"));

		//##CODE_END

		printAndValidate(null, allRows);
	}

	@Test
	public void example002ReadSimpleFixedWidth() throws Exception {
		StringBuilder out = new StringBuilder();
		//##CODE_START
		// creates the sequence of field lengths in the file to be parsed
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(4, 5, 40, 40, 8);

		// creates the default settings for a fixed width parser
		FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);
		settings.getFormat().setLineSeparator("\n");

		//sets the character used for padding unwritten spaces in the file
		settings.getFormat().setPadding('_');

		// creates a fixed-width parser with the given settings
		FixedWidthParser parser = new FixedWidthParser(settings);

		// call beginParsing to read records one by one, iterator-style.
		parser.beginParsing(getReader("/examples/example.txt"));

		String[] row;
		while ((row = parser.parseNext()) != null) {
			println(out, Arrays.toString(row));
		}

		// Resources are closed automatically when the end of the input is reached,
		// but you can call stopParsing() at any time.

		//You only need to use this if you are not parsing the entire content.
		//It doesn't hurt if you call it anyway. 
		parser.stopParsing();

		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example003ReadFixedWidthWithRowProcessor() throws Exception {
		//##CODE_START
		//A RowListProcessor stores each parsed row in a List.
		RowListProcessor rowProcessor = new RowListProcessor();

		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(4, 5, 40, 40, 8);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');
		parserSettings.getFormat().setLineSeparator("\n");

		//set the RowProcessor that will process the values of each parsed row.
		//You can create your own or use any pre-defined RowProcessor 
		//in the 'com.univocity.parsers.common.processor' package 
		parserSettings.setRowProcessor(rowProcessor);

		// flag to consider the first parsed row as the headers of each column in the file. 
		parserSettings.setHeaderExtractionEnabled(true);

		// creates a parser instance with the given settings
		FixedWidthParser parser = new FixedWidthParser(parserSettings);

		// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
		parser.parse(getReader("/examples/example.txt"));

		// get the parsed records from the RowListProcessor here. 
		// Note that different implementations of RowProcessor will provide different sets of functionalities.
		String[] headers = rowProcessor.getHeaders();
		List<String[]> rows = rowProcessor.getRows();

		//##CODE_END

		printAndValidate(headers, rows);
	}

	@Test
	public void example004ReadFixedWidthAndConvertValues() throws Exception {
		final StringBuilder out = new StringBuilder();

		//##CODE_START

		// ObjectRowProcessor converts the parsed values and gives you the resulting row.  
		ObjectRowProcessor rowProcessor = new ObjectRowProcessor() {
			@Override
			public void rowProcessed(Object[] row, ParsingContext context) {
				//here is the row. Let's just print it to the standard output.
				println(out, Arrays.toString(row));
			}
		};

		// converts values in the "Price" column (index 4) to BigDecimal
		rowProcessor.convertIndexes(Conversions.toBigDecimal()).set(4);

		// converts the values in columns "Make, Model and Description" to lower case, and sets the value "chevy" to null.
		rowProcessor.convertFields(Conversions.toLowerCase(), Conversions.toNull("chevy")).set("Make", "Model", "Description");

		// converts the values at index 0 (year) to BigInteger. Nulls are converted to BigInteger.ZERO.
		rowProcessor.convertFields(new BigIntegerConversion(BigInteger.ZERO, "0")).set("year");

		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(4, 5, 40, 40, 8);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setPadding('_');
		parserSettings.setRowProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(getReader("/examples/example.txt"));

		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example005UsingAnnotations() throws Exception {
		//##CODE_START
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(11, 15, 10, 10, 20);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setRowProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(getReader("/examples/bean_test.txt"));

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

		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(12, 7);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.setHeaderExtractionEnabled(true);

		// Set the RowProcessor to the masterRowProcessor.
		parserSettings.setRowProcessor(masterRowProcessor);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(getReader("/examples/master_detail.txt"));

		List<MasterDetailRecord> rows = masterRowProcessor.getRecords();
		MasterDetailRecord masterRecord = rows.get(0);

		// The master record has one master row and multiple detail rows.
		Object[] masterRow = masterRecord.getMasterRow();
		List<Object[]> detailRows = masterRecord.getDetailRows();
		//##CODE_END
		printAndValidate(masterRow, detailRows);
	}
}
