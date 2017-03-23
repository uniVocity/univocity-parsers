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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.math.*;
import java.util.*;

public class FixedWidthParserExamples extends Example {

	@Test
	public void example001ParseAll() throws Exception {
		//##CODE_START

		// creates the sequence of field lengths in the file to be parsed
		FixedWidthFields lengths = new FixedWidthFields(4, 5, 40, 40, 8);

		// creates the default settings for a fixed width parser
		FixedWidthParserSettings settings = new FixedWidthParserSettings(lengths);

		//sets the character used for padding unwritten spaces in the file
		settings.getFormat().setPadding('_');
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
		FixedWidthFields lengths = new FixedWidthFields(4, 5, 40, 40, 8);

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

		FixedWidthFields lengths = new FixedWidthFields(4, 5, 40, 40, 8);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');

		//You can configure the parser to automatically detect what line separator sequence is in the input
		parserSettings.setLineSeparatorDetectionEnabled(true);

		//set the RowProcessor that will process the values of each parsed row.
		//You can create your own or use any pre-defined RowProcessor
		//in the 'com.univocity.parsers.common.processor' package
		parserSettings.setProcessor(rowProcessor);

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

		FixedWidthFields lengths = new FixedWidthFields(4, 5, 40, 40, 8);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.getFormat().setPadding('_');
		parserSettings.setProcessor(rowProcessor);
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
		FixedWidthFields lengths = new FixedWidthFields(11, 15, 10, 10, 20);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);
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

		FixedWidthFields lengths = new FixedWidthFields(12, 7);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.setHeaderExtractionEnabled(true);

		// Set the RowProcessor to the masterRowProcessor.
		parserSettings.setProcessor(masterRowProcessor);

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

	@Test
	public void example007BatchedColumns() throws Exception {
		final StringBuilder out = new StringBuilder();

		FixedWidthParserSettings settings = new FixedWidthParserSettings(new FixedWidthFields(4, 5, 40, 40, 8));
		settings.setHeaderExtractionEnabled(true);
		settings.getFormat().setPadding('_');
		settings.getFormat().setLineSeparator("\n");
		//##CODE_START

		//To process larger inputs, we can use a batched column processor.
		//Here we set the batch size to 3, meaning we'll get the column values of at most 3 rows in each batch.
		settings.setProcessor(new BatchedColumnProcessor(3) {

			@Override
			public void batchProcessed(int rowsInThisBatch) {
				List<List<String>> columnValues = getColumnValuesAsList();

				println(out, "Batch " + getBatchesProcessed() + ":");
				int i = 0;
				for (List<String> column : columnValues) {
					println(out, "Column " + (i++) + ":" + column);
				}
			}
		});

		FixedWidthParser parser = new FixedWidthParser(settings);
		parser.parse(getReader("/examples/example.txt"));

		//##CODE_END
		printAndValidate(out);
	}

	@Test
	public void example008BeanListToStringList() throws Exception {
		// Let's use the code we had before to load a list of TestBeans
		BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);
		FixedWidthFields lengths = new FixedWidthFields(11, 15, 10, 10, 20);
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		parserSettings.getFormat().setPadding('_');
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(getReader("/examples/bean_test.txt"));

		List<TestBean> beans = rowProcessor.getBeans();

		//##CODE_START
		BeanWriterProcessor<TestBean> writerProcessor = new BeanWriterProcessor<TestBean>(TestBean.class);

		LinkedHashMap<String, Integer> fieldsAndLengths = new LinkedHashMap<String, Integer>();
		fieldsAndLengths.put("amount", 15);
		fieldsAndLengths.put("date", 11);
		fieldsAndLengths.put("pending", 10);
		fieldsAndLengths.put("quantity", 10);
		fieldsAndLengths.put("comments", 20);

		FixedWidthWriterSettings writerSettings = new FixedWidthWriterSettings(new FixedWidthFields(fieldsAndLengths));
		writerSettings.getFormat().setPadding('_');
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setRowWriterProcessor(writerProcessor);

		//note that we are not passing in an instanceof java.io.Writer here.
		FixedWidthWriter writer = new FixedWidthWriter(writerSettings);

		//let's see how the headers will appear
		println(writer.writeHeadersToString());

		List<String> rows = writer.processRecordsToString(beans); //beans is just a List of TestBean

		//each row should have data of a TestBean:
		for (String row : rows) {
			println(row);
		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example009ParseWithLookahead() throws Exception {
		//##CODE_START
		FixedWidthParserSettings settings = new FixedWidthParserSettings();
		settings.getFormat().setLineSeparator("\n");

		//We are going to parse the multi_schema.txt file, with a lookahead value in front of each record
		//Let's define the format used to store clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 2); //here we will store the look ahead value in a column
		clientFields.addField("ClientID", 7, FieldAlignment.RIGHT, '0');
		clientFields.addField("Name", 20);

		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 7, FieldAlignment.RIGHT, '0'); //here the account ID will be prefixed by the lookahead value
		accountFields.addField("Bank", 4);
		accountFields.addField("AccountNumber", 10);
		accountFields.addField("Swift", 7);

		//If a record starts with C#, it's a client record, so we associate "C#" with the client format
		settings.addFormatForLookahead("C#", clientFields);

		//And here we associate "A#" with the account format
		settings.addFormatForLookahead("A#", accountFields);

		//We can now parse all rows
		FixedWidthParser parser = new FixedWidthParser(settings);
		List<String[]> rows = parser.parseAll(getReader("/examples/multi_schema.txt"));
		//##CODE_END

		printAndValidate(rows);
	}

	@Test
	public void example010ParseWithDefaultAndLookahead() throws Exception {
		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 5, FieldAlignment.RIGHT, '0'); //now, the account fields won't have a lookahead value.
		accountFields.addField("Bank", 4);
		accountFields.addField("AccountNumber", 10);
		accountFields.addField("Swift", 7);

		//##CODE_START
		//In some cases the input records might not have a lookahead value. On the multi_schema2.txt file,
		//only client records have a lookahead. If no other lookahead is matched, the parser will switch back to
		//the default field format. Here, the format used by account records will be used as default.
		FixedWidthParserSettings settings = new FixedWidthParserSettings(accountFields);
		settings.getFormat().setLineSeparator("\n");

		//Let's again define the format used to store clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 2); //here we will store the look ahead value in a column
		clientFields.addField("ClientID", 7, FieldAlignment.RIGHT, '0');
		clientFields.addField("Name", 20);

		//If a record starts with C#, it's a client record, so we associate "C#" with the client format.
		//Any other record will be parsed using the default format
		settings.addFormatForLookahead("?#", clientFields);

		//Let's parse all rows now
		FixedWidthParser parser = new FixedWidthParser(settings);
		List<String[]> rows = parser.parseAll(getReader("/examples/multi_schema2.txt"));
		//##CODE_END

		printAndValidate(rows);
	}

	@Test
	public void example011ParseWithLookbehind() throws Exception {
		FixedWidthParserSettings settings = new FixedWidthParserSettings();
		settings.getFormat().setLineSeparator("\n");

		//Here's the format used for client accounts:
		FixedWidthFields accountFields = new FixedWidthFields();
		accountFields.addField("ID", 5, FieldAlignment.RIGHT, '0'); //the account fields won't have a lookahead value.
		accountFields.addField("Bank", 4);
		accountFields.addField("AccountNumber", 10);
		accountFields.addField("Swift", 7);


		//Let's again define the format used to store clients' records
		FixedWidthFields clientFields = new FixedWidthFields();
		clientFields.addField("Lookahead", 2); //here we will store the look ahead value in a column
		clientFields.addField("ClientID", 7, FieldAlignment.RIGHT, '0');
		clientFields.addField("Name", 20);

		//##CODE_START
		//We can also specify a lookbehind value to determine which format to use when parsing the input.

		//If a record starts with C#, it's a client record, so we associate "C#" with the client format.
		settings.addFormatForLookahead("C#", clientFields);
		//If a record parsed previously has a C#, but the current doesn't, then we are processing accounts. Let's use the account format.
		settings.addFormatForLookbehind("?#", accountFields);

		//Let's parse all rows now
		FixedWidthParser parser = new FixedWidthParser(settings);
		List<String[]> rows = parser.parseAll(getReader("/examples/multi_schema2.txt"));
		//##CODE_END

		printAndValidate(rows);
	}
}
