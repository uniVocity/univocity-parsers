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
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;
import java.util.Map.*;

public class CsvParserExamples extends Example {

	@Test
	public void example001ParseAll() throws Exception {
		//##CODE_START

		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);

		// parses all rows in one go.
		List<String[]> allRows = parser.parseAll(getReader("/examples/example.csv"));

		//##CODE_END
		printAndValidate(null, allRows);
	}

	@Test
	public void example002ReadSimpleCsv() throws Exception {
		StringBuilder out = new StringBuilder();

		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);

		// call beginParsing to read records one by one, iterator-style.
		parser.beginParsing(getReader("/examples/example.csv"));

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
	public void example003ReadCsvWithRowProcessor() throws Exception {
		//##CODE_START

		// The settings object provides many configuration options
		CsvParserSettings parserSettings = new CsvParserSettings();

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
		CsvParser parser = new CsvParser(parserSettings);

		// the 'parse' method will parse the file and delegate each parsed row to the RowProcessor you defined
		parser.parse(getReader("/examples/example.csv"));

		// get the parsed records from the RowListProcessor here.
		// Note that different implementations of RowProcessor will provide different sets of functionalities.
		String[] headers = rowProcessor.getHeaders();
		List<String[]> rows = rowProcessor.getRows();

		//##CODE_END

		printAndValidate(headers, rows);
	}

	@Test
	public void example004ReadCsvAndConvertValues() throws Exception {
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

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);

		//the rowProcessor will be executed here.
		parser.parse(getReader("/examples/example.csv"));

		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example005UsingAnnotations() throws Exception {
		//##CODE_START
		// BeanListProcessor converts each parsed row to an instance of a given class, then stores each instance into a list.
		BeanListProcessor<TestBean> rowProcessor = new BeanListProcessor<TestBean>(TestBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader("/examples/bean_test.csv"));

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

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setHeaderExtractionEnabled(true);

		// Set the RowProcessor to the masterRowProcessor.
		parserSettings.setProcessor(masterRowProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader("/examples/master_detail.csv"));

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
	public void example007Columns() throws Exception {
		//##CODE_START
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		// To get the values of all columns, use a column processor
		ColumnProcessor rowProcessor = new ColumnProcessor();
		parserSettings.setProcessor(rowProcessor);

		CsvParser parser = new CsvParser(parserSettings);

		//This will kick in our column processor
		parser.parse(getReader("/examples/example.csv"));

		//Finally, we can get the column values:
		Map<String, List<String>> columnValues = new TreeMap<String, List<String>>(rowProcessor.getColumnValuesAsMapOfNames());

		//##CODE_END
		StringBuilder out = new StringBuilder();
		for (Entry<String, List<String>> e : columnValues.entrySet()) {
			List<String> values = e.getValue();
			String columnName = e.getKey();
			println(out, columnName + " -> " + values);
		}

		printAndValidate(out);
	}

	@Test
	public void example008CustomConversionAnnotation() throws Exception {
		StringBuilder out = new StringBuilder();

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		//##CODE_START
		BeanListProcessor<Car> rowProcessor = new BeanListProcessor<Car>(Car.class);
		parserSettings.setProcessor(rowProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader("/examples/example.csv"));

		//Let's get our cars
		List<Car> cars = rowProcessor.getBeans();
		for (Car car : cars) {
			// Let's get only those cars that actually have some description
			if (!car.getDescription().isEmpty()) {
				println(out, car.getDescription() + " - " + car.toString());
			}
		}
		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example009ParallelProcessing() throws Exception {
		StringBuilder out = new StringBuilder();

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setHeaderExtractionEnabled(true);

		BeanListProcessor<Car> rowProcessor = new BeanListProcessor<Car>(Car.class);

		//##CODE_START
		parserSettings.setProcessor(new ConcurrentRowProcessor(rowProcessor));
		//##CODE_END

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(getReader("/examples/example.csv"));

		//Let's get our cars
		List<Car> cars = rowProcessor.getBeans();
		for (Car car : cars) {
			// Let's get only those cars that actually have some description
			if (!car.getDescription().isEmpty()) {
				println(out, car.getDescription() + " - " + car.toString());
			}
		}

		printAndValidate(out);
	}

	@Test
	public void example010Escaping() throws Exception {
		StringBuilder out = new StringBuilder();

		CsvParserSettings settings = new CsvParserSettings();
		//##CODE_START
		// quotes inside quoted values are escaped as \"
		settings.getFormat().setQuoteEscape('\\');

		// but if two backslashes are found before a quote symbol they represent a single slash.
		settings.getFormat().setCharToEscapeQuoteEscaping('\\');
		//##CODE_END

		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(new InputStreamReader(getClass().getResourceAsStream("/examples/escape.csv"), "UTF-8"));
		for (String[] row : allRows) {
			for (String col : row) {
				print(out, "[" + col + "]\t");
			}
			println(out);
		}

		printAndValidate(out);
	}

	@Test
	public void example011ErrorHandling() throws Exception {
		final StringBuilder out = new StringBuilder();

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START
		BeanListProcessor<AnotherTestBean> beanProcessor = new BeanListProcessor<AnotherTestBean>(AnotherTestBean.class);
		settings.setProcessor(beanProcessor);

		//Let's set a RowProcessorErrorHandler to log the error. The parser will keep running.
		settings.setProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				println(out, "Error processing row: " + Arrays.toString(inputRow));
				println(out, "Error details: column '" + error.getColumnName() + "' (index " + error.getColumnIndex() + ") has value '" + inputRow[error.getColumnIndex()] + "'");
			}
		});

		CsvParser parser = new CsvParser(settings);
		parser.parse(getReader("/examples/bean_test.csv"));

		println(out);
		println(out, "Printing beans that could be parsed");
		println(out);
		for (AnotherTestBean bean : beanProcessor.getBeans()) {
			println(out, bean); //should print just one bean here
		}
		//##CODE_END

		printAndValidate(out);
	}

	@Test
	public void example012FormatAutodetection() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();

		//##CODE_START
		//turns on automatic detection of line separators, column separators, quotes & quote escapes
		settings.detectFormatAutomatically();

		CsvParser parser = new CsvParser(settings);

		List<String[]> rows;
		//First, CSV we've been using to demonstrate all examples.
		println("Data in /examples/example.csv:");
		rows = parser.parseAll(getReader("/examples/example.csv"));
		printRows(rows, false);

		//Then, the same data but in European style (column separator is ; and decimals are separated by ,). We also escaped quotes with \ instead of using double quotes
		println("\nData in /examples/european.csv:");
		rows = parser.parseAll(getReader("/examples/european.csv"));
		printRows(rows, false);

		//Let's see the detected format:
		println("\nFormat detected in /examples/european.csv:");
		CsvFormat detectedFormat = parser.getDetectedFormat();
		println(detectedFormat);

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example013ParseKeepingEscapeSequences() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);

		//let's parse the file seen in the previous example, where quotes are escaped using \
		settings.detectFormatAutomatically();

		//##CODE_START

		//now we want to keep the escape sequences. We should see the slash before the quotes.
		settings.setKeepEscapeSequences(true);

		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(getReader("/examples/european.csv"));

		//##CODE_END
		printAndValidate(null, allRows);
	}

	@Test
	public void example014ParseMultipleBeansInSingleRow() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START

		// The MultiBeanProcessor allows multiple bean instances to be created from a single input record.
		// in this example, we will create instances of TestBean and AnotherTestBean.
		// Here we use a MultiBeanListProcessor which is a convenience class that implements the
		// abstract beanProcessed() method of MultiBeanProcessor to add each instance to a list.;
		MultiBeanListProcessor processor = new MultiBeanListProcessor(TestBean.class, AnotherTestBean.class);

		// one of the records in the input won't be compatible with AnotherTestBean: the field "pending"
		// only accepts 'y' or 'n' as valid representations of true or false. We want to continue processing, let's ignore the error.
		settings.setProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				//ignore the error.
			}
		});

		// we also need to grab the headers from our input file
		settings.setHeaderExtractionEnabled(true);

		//let's configure the parser to use our MultiBeanProcessor
		settings.setProcessor(processor);

		CsvParser parser = new CsvParser(settings);

		//and parse everything.
		parser.parse(getReader("/examples/bean_test.csv"));

		// we can get all beans parsed from the input as a map, where the keys are the bean type associated
		// with a list of corresponding bean instances
		Map<Class<?>, List<?>> beans = processor.getBeans();

		// or we can get the lists of beans processed individually by providing the type:
		List<TestBean> testBeans = processor.getBeans(TestBean.class);
		List<AnotherTestBean> anotherTestBeans = processor.getBeans(AnotherTestBean.class);

		//Let's have a look:
		println("TestBeans\n----------------");
		for (TestBean testBean : testBeans) {
			println(testBean);
		}

		//We expect one of the instances here to be null
		println("\nAnotherTestBeans\n----------------");
		for (AnotherTestBean anotherTestBean : anotherTestBeans) {
			println(anotherTestBean);
		}
		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example015QuoteAndEscapeHandling() {
		CsvParserSettings settings = new CsvParserSettings();
		//##CODE_START
		settings.setParseUnescapedQuotesUntilDelimiter(false);
		settings.getFormat().setLineSeparator("\r\n");

		//let's quote values with single quotes
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('\'');

		//Line separators are normalized by default. This means they are all converted to \n, including line separators found within quoted values.
		parse("value 1,'line 1\r\nline 2',value 3", settings, "Normalizing line endings");

		//You can disable this behavior to keep the original line separators in parsed values.
		settings.setNormalizeLineEndingsWithinQuotes(false);
		parse("value 1,'line 1\r\nline 2',value 3", settings, "Without normalized line endings");

		//Values that contain a quote character, but are not enclosed within quotes, are read as-is
		parse("value 1,I'm NOT a quoted value,value 3", settings, "Value with a quote, not enclosed");

		//But if your input comes with escaped quotes, and is not enclosed within quotes you'll get the escape sequence
		parse("value 1,I''m NOT a quoted value,value 3", settings, "Value with quote, escaped, not enclosed");

		//Turn on the escape unquoted values to correctly unescape this sort of input
		settings.setEscapeUnquotedValues(true);
		parse("value 1,I''m NOT a quoted value,value 3", settings, "Value with quote, escaped, not enclosed, processing escape");

		//As usual, when you parse values that have escaped characters, such as the quote, you get the unescaped result.
		parse("value 1,'I''m a quoted value',value 3", settings, "Enclosed value, quote escaped");

		//But in some cases you might want to get the original text, character by character, including the original escape sequence
		settings.setKeepEscapeSequences(true);
		parse("value 1,'I''m a quoted value',value 3", settings, "Enclosed value, quote escaped, keeping escape sequences");

		//By default, the parser handles broken quote escapes, so it won't complain about "I'm" not being escaped properly (should be "I''m").
		parse("value 1,'I'm a broken quoted value',value 3", settings, "Enclosed value, broken quote escape");

		//But you can disable this and get exceptions instead.
		settings.setParseUnescapedQuotes(false);
		try {
			parse("value 1,'Hey, I'm a broken quoted value',value 3", settings, "This will blow up");
		} catch (TextParsingException exception) {
			//The exception will give you better details about what went wrong, and where.
			println("Quote escape error. Parser stopped after reading: [" + exception.getParsedContent() + "] of column " + exception.getColumnIndex());
		}
		//##CODE_END

		printAndValidate();
	}

	@Test
	public void example016ParseReadingComments() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START
		// This configures the parser to store all comments found in the input.
		// You'll be able to retrieve the last parsed comment or all comments parsed at
		// any given time during the parsing.
		settings.setCommentCollectionEnabled(true);
		CsvParser parser = new CsvParser(settings);

		parser.beginParsing(getReader("/examples/example.csv"));
		String[] row;
		while ((row = parser.parseNext()) != null) {
			// using the getContext method we have access to the parsing context, from where the comments found so far can be accessed
			// let's get the last parsed comment and print it out in front of each parsed row.
			String comment = parser.getContext().lastComment();
			if (comment == null || comment.trim().isEmpty()) {
				comment = "No relevant comments yet";
			}
			println("Comment: " + comment + ". Parsed: " + Arrays.toString(row));
		}

		// We can also get all comments parsed.
		println("\nAll comments found:\n-------------------");
		//The comments() method returns a map of line numbers associated with the comments found in them.
		Map<Long, String> comments = parser.getContext().comments();
		for (Entry<Long, String> e : comments.entrySet()) {
			long line = e.getKey();
			String commentAtLine = e.getValue();
			println("Line: " + line + ": '" + commentAtLine + "'");
		}
		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example017ParseMultiSchema() {

		//##CODE_START
		//Here's a master-detail schema, with rows using different formats. Rows starting with MASTER are master rows
		//and they are followed by one or more car records.
		StringReader input = new StringReader("" +
				"MASTER,Value 1,Value 2\n" +
				"2012,Toyota,Camry,10000\n" +
				"2014,Toyota,Camry,12000\n" +
				"MASTER,Value 3\n" +
				"1982,Volkswagen,Beetle,3500"
		);

		//We are going to collect all MASTER records using a processor for lists of Object[]
		final ObjectRowListProcessor masterElementProcessor = new ObjectRowListProcessor();
		//And rows with car information will be parsed into a list of Car objects.
		final BeanListProcessor<Car> carProcessor = new BeanListProcessor(Car.class);

		//We create a switch based on the first column of the input. Values of each column of each row
		//will be compared against registered processors that "know" how to parse the input row.
		InputValueSwitch inputSwitch = new InputValueSwitch(0) {

			// the input value switch allows you to override the "rowProcessorSwitched" so you can handle
			// what should happen when a different type of row is found in the input.
			public void rowProcessorSwitched(RowProcessor from, RowProcessor to) {
				//let's add associate the list of parsed Car instances to the MASTER record parsed before.
				//when the current row processor is not the "carProcessor" anymore, it means we parsed all cars under a MASTER record
				if (from == carProcessor) {

					List<Object[]> masterRows = masterElementProcessor.getRows();
					int lastMasterRowIndex = masterRows.size() - 1;
					Object[] lastMasterRow = masterRows.get(lastMasterRowIndex);

					//let's expand the last master row
					Object[] masterRowWithCars = Arrays.copyOf(lastMasterRow, 3);

					//and add our parsed car list in the last element:
					masterRowWithCars[2] = new ArrayList<Car>(carProcessor.getBeans());

					masterRows.set(lastMasterRowIndex, masterRowWithCars);

					//clear the list of cars so it's empty for the next batch of cars under another master row.
					carProcessor.getBeans().clear();
				}
			}
		};

		//Rows whose value at column 0 is "MASTER" will be processed using the masterElementProcessor.
		inputSwitch.addSwitchForValue("MASTER", masterElementProcessor, 1, 2); //we are not interested in the "MASTER" value at column 0, let's select which columns to get values from

		//Car records don't have an identifier in their rows. In this case the input switch will use a "default" processor
		//to handle the input. All records that don't start with "SUPER" are Car records, we use the car processor as the default.
		inputSwitch.setDefaultSwitch(carProcessor, "year", "make", "model", "price"); //the header list here matches the fields annotated in the Car class.

		//All we need now is to set row processor of our parser, which is the input switch.
		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(inputSwitch);

		settings.getFormat().setLineSeparator("\n");

		//and parse!
		CsvParser parser = new CsvParser(settings);
		parser.parse(input);

		//Let's have a look at our master rows which contain lists of cars:
		List<Object[]> rows = masterElementProcessor.getRows();

		for (Object[] row : rows) {
			println(Arrays.toString(row));
		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example018ParseAllRecords() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		//##CODE_START
		// configure to grab headers from file. We want to use these names to get values from each record.
		settings.setHeaderExtractionEnabled(true);
		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);

		// parses all records in one go.
		List<Record> allRecords = parser.parseAllRecords(getReader("/examples/example.csv"));
		for(Record record : allRecords){
			print("Year: " + record.getValue("year", 2000)); //defaults year to 2000 if value is null.
			print(", Model: " + record.getString("model"));
			println(", Price: " + record.getBigDecimal("price"));
		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example019IterateOverRecords() throws Exception {

		CsvParserSettings settings = new CsvParserSettings();
		//the file used in the example uses '\n' as the line separator sequence.
		//the line separator sequence is defined here to ensure systems such as MacOS and Windows
		//are able to process this file correctly (MacOS uses '\r'; and Windows uses '\r\n').
		settings.getFormat().setLineSeparator("\n");

		// configure to grab headers from file. We want to use these names to get values from each record.
		settings.setHeaderExtractionEnabled(true);

		// creates a CSV parser
		CsvParser parser = new CsvParser(settings);

		//##CODE_START
		// call beginParsing to read records one by one, iterator-style.
		parser.beginParsing(getReader("/examples/example.csv"));

		//among many other things, we can set default values of one ore more columns in the record metadata.
		//Let's again set year to 2000 if it comes as null.
		parser.getRecordMetadata().setDefaultValueOfColumns(2000, "year");

		Record record;
		while ((record = parser.parseNextRecord()) != null) {
			print("Year: " + record.getInt("year"));
			print(", Model: " + record.getString("model"));
			println(", Price: " + record.getBigDecimal("price"));
		}
		//##CODE_END
		// The resources are closed automatically when the end of the input is reached,
		// or when an error happens, but you can call stopParsing() at any time.

		// You only need to use this if you are not parsing the entire content.
		// But it doesn't hurt if you call it anyway.
		parser.stopParsing();

		printAndValidate();
	}

	private void parse(String input, CsvParserSettings settings, String message) {
		String[] values = null;

		values = new CsvParser(settings).parseLine(input);
		values = displayLineSeparators(values);
		println("\n" + message + ":\n\t" + Arrays.toString(values));
	}

	private void printRows(List<String[]> rows, boolean expand) {
		println("Printing " + rows.size() + " rows");
		int rowCount = 0;
		for (String[] row : rows) {
			println("Row " + ++rowCount + " (length " + row.length + "): " + Arrays.toString(row));
			if (expand) {
				int valueCount = 0;
				for (String value : row) {
					println("\tvalue " + ++valueCount + ": " + value);
				}
			}
		}
	}
}
