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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class WriterExamples extends Example {

	List<Object[]> rows = Arrays.asList(
			new Object[][]{
					{"1997", "Ford", "E350", "ac, abs, moon", "3000.00"},
					{"1999", "Chevy", "Venture \"Extended Edition\"", "", "4900.00"},
					{"1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00"},
					{},
					{"1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00"},
					{null, "", "Venture \"Extended Edition\"", null, "4900.00"},
			});

	@Test
	public void example001WriteSimpleCsv() {

		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

		// CsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(csvResult);

		//##CODE_START

		// All you need is to create an instance of CsvWriter with the default CsvWriterSettings.
		// By default, only values that contain a field separator are enclosed within quotes.
		// If quotes are part of the value, they are escaped automatically as well.
		// Empty rows are discarded automatically.
		CsvWriter writer = new CsvWriter(outputWriter, new CsvWriterSettings());

		// Write the record headers of this file
		writer.writeHeaders("Year", "Make", "Model", "Description", "Price");

		// Here we just tell the writer to write everything and close the given output Writer instance.
		writer.writeRowsAndClose(rows);

		//##CODE_END

		// Let's just print the resulting CSV
		printAndValidate(csvResult.toString());
	}

	@Test
	public void example002WriteCsvOneByOne() {

		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

		// CsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(csvResult);

		//##CODE_START
		CsvWriterSettings settings = new CsvWriterSettings();
		// Sets the character sequence to write for the values that are null.
		settings.setNullValue("?");

		//Changes the comment character to -
		settings.getFormat().setComment('-');

		// Sets the character sequence to write for the values that are empty.
		settings.setEmptyValue("!");

		// writes empty lines as well.
		settings.setSkipEmptyLines(false);

		// Creates a writer with the above settings;
		CsvWriter writer = new CsvWriter(outputWriter, settings);

		// writes the file headers
		writer.writeHeaders("a", "b", "c", "d", "e");

		// Let's write the rows one by one (the first row will be skipped)
		for (int i = 1; i < rows.size(); i++) {
			// You can write comments above each row
			writer.commentRow("This is row " + i);
			// writes the row
			writer.writeRow(rows.get(i));
		}

		// we must close the writer. This also closes the java.io.Writer you used to create the CsvWriter instance
		// note no checked exceptions are thrown here. If anything bad happens you'll get an IllegalStateException wrapping the original error.
		writer.close();
		//##CODE_END
		// Let's just print the resulting CSV
		printAndValidate(csvResult.toString());
	}

	@Test
	public void example003WriteCsvWithFieldSelection() {
		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream csvResult = new ByteArrayOutputStream();

		// CsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(csvResult);
		//##CODE_START
		CsvWriterSettings settings = new CsvWriterSettings();

		// when writing, nulls are printed using the empty value (defaults to "").
		// Here we configure the writer to print ? to describe null values.
		settings.setNullValue("?");

		// if the value is not null, but is empty (e.g. ""), the writer will can be configured to
		// print some default representation for a non-null/empty value
		settings.setEmptyValue("!");

		// Encloses all records within quotes even when they are not required.
		settings.setQuoteAllFields(true);

		// Sets the file headers (used for selection, these values won't be written automatically)
		settings.setHeaders("Year", "Make", "Model", "Description", "Price");

		// Selects which fields from the input should be written. In this case, fields "make" and "model" will be empty
		// The field selection is not case sensitive
		settings.selectFields("description", "price", "year");

		// Creates a writer with the above settings;
		CsvWriter writer = new CsvWriter(outputWriter, settings);

		// Writes the headers specified in the settings
		writer.writeHeaders();

		// writes each row providing values for the selected fields (note the values and field selection order must match)
		writer.writeRow("ac, abs, moon", 3000.00, 1997);
		writer.writeRow("", 4900.00, 1999); // NOTE: empty string will be replaced by "!" as per configured emptyQuotedValue.
		writer.writeRow("MUST SELL!\nair, moon roof, loaded", 4799.00, 1996);

		writer.close();
		//##CODE_END
		// Let's just print the resulting CSV
		printAndValidate(csvResult.toString());
	}

	@Test
	public void example004WriteFixedWidthUsingConversions() {
		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();

		// CsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(fixedWidthResult);
		//##CODE_START
		FixedWidthFields lengths = new FixedWidthFields(15, 10, 35);
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);

		// Any null values will be written as ?
		settings.setNullValue("nil");
		settings.getFormat().setPadding('_');
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);

		// Creates an ObjectRowWriterProcessor that handles annotated fields in the TestBean class.
		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		settings.setRowWriterProcessor(processor);

		// Converts objects in the "date" field using the yyyy-MMM-dd format.
		processor.convertFields(Conversions.toDate(Locale.ENGLISH," yyyy MMM dd "), Conversions.trim()).add("date");

		// Trims Strings at position 2 of the input row.
		processor.convertIndexes(Conversions.trim(), Conversions.toUpperCase()).add(2);

		// Sets the file headers so the writer knows the correct order when writing values taken from a TestBean instance
		settings.setHeaders("date", "quantity", "comments");

		// Creates a writer with the above settings;
		FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);

		// Writes the headers specified in the settings
		writer.writeHeaders();

		// writes a Fixed Width row with the values set in "bean". Notice that there's no annotated
		// attribute for the "date" column, so it will just be null (an then converted to ? a )
		writer.processRecord(new Date(0), null, "  a comment  ");
		writer.processRecord(null, 1000, "");

		writer.close();
		//##CODE_END

		// Let's just print the resulting fixed width output
		printAndValidate(fixedWidthResult.toString());
	}

	@Test
	public void example005WriteFixedWidthUsingAnnotatedBean() {
		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream fixedWidthResult = new ByteArrayOutputStream();

		// CsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(fixedWidthResult);
		//##CODE_START
		FixedWidthFields lengths = new FixedWidthFields(10, 10, 35, 10, 40);
		FixedWidthWriterSettings settings = new FixedWidthWriterSettings(lengths);

		// Any null values will be written as ?
		settings.setNullValue("?");

		// Creates a BeanWriterProcessor that handles annotated fields in the TestBean class.
		settings.setRowWriterProcessor(new BeanWriterProcessor<TestBean>(TestBean.class));

		// Sets the file headers so the writer knows the correct order when writing values taken from a TestBean instance
		settings.setHeaders("amount", "pending", "date", "quantity", "comments");

		// Creates a writer with the above settings;
		FixedWidthWriter writer = new FixedWidthWriter(outputWriter, settings);

		// Writes the headers specified in the settings
		writer.writeHeaders();

		// writes a fixed width row with empty values (as nothing was set in the TestBean instance).
		writer.processRecord(new TestBean());

		TestBean bean = new TestBean();
		bean.setAmount(new BigDecimal("500.33"));
		bean.setComments("Blah,blah");
		bean.setPending(false);
		bean.setQuantity(100);

		// writes a Fixed Width row with the values set in "bean". Notice that there's no annotated
		// attribute for the "date" column, so it will just be null (an then converted to ?, as we have settings.setNullValue("?");)
		writer.processRecord(bean);

		// you can still write rows passing in its values directly.
		writer.writeRow(BigDecimal.ONE, true, "1990-01-10", 3, null);

		writer.close();
		//##CODE_END
		// Let's just print the resulting fixed width output
		printAndValidate(fixedWidthResult.toString());
	}

	@Test
	public void example006WriteSimpleTsv() {

		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		// TsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(tsvResult);

		//##CODE_START

		// As with the CsvWriter, all you need is to create an instance of TsvWriter with the default TsvWriterSettings.
		TsvWriter writer = new TsvWriter(outputWriter, new TsvWriterSettings());

		// Write the record headers of this file
		writer.writeHeaders("Year", "Make", "Model", "Description", "Price");

		// Here we just tell the writer to write everything and close the given output Writer instance.
		writer.writeRowsAndClose(rows);

		//##CODE_END

		// Let's just print the resulting TSV
		printAndValidate(tsvResult.toString());
	}

	@Test
	public void example007WriteValues() {

		// Writing to an in-memory byte array. This will be printed out to the standard output so you can easily see the result.
		ByteArrayOutputStream tsvResult = new ByteArrayOutputStream();

		// TsvWriter (and all other file writers) work with an instance of java.io.Writer
		Writer outputWriter = new OutputStreamWriter(tsvResult);

		// As with the CsvWriter, all you need is to create an instance of TsvWriter with the default TsvWriterSettings.
		//##CODE_START
		TsvWriter writer = new TsvWriter(outputWriter, new TsvWriterSettings());

		writer.writeHeaders("A", "B", "C", "D", "E");

		//writes a value to the first column
		writer.addValue(10);

		//writes a value to the second column
		writer.addValue(20);

		//writes a value to the fourth column (index 3 represents the 4th column - the one with header "D")
		writer.addValue(3, 40);

		//overrides the value in the first column. "A" indicates the header name.
		writer.addValue("A", 100.0);

		//flushes all values to the output, creating a row.
		writer.writeValuesToRow();

		//##CODE_END
		// Here we just tell the writer to close the given output Writer instance.
		writer.close();
		// Let's just print the resulting TSV
		printAndValidate(tsvResult.toString());
	}

	@Test
	public void example008WriteWithHeaderAnnotation() {
		//##CODE_START
		TsvWriterSettings settings = new TsvWriterSettings();

		settings.setRowWriterProcessor(new BeanWriterProcessor<AnotherTestBean>(AnotherTestBean.class));

		// We didn't provide a java.io.Writer here, so all we can do is write to Strings (streaming)
		TsvWriter writer = new TsvWriter(settings);

		// Let's write the headers declared in @Headers annotation of AnotherTestBean
		String headers = writer.writeHeadersToString();

		// Now, let's create an instance of our bean
		AnotherTestBean bean = new AnotherTestBean();
		bean.setPending(true);
		bean.setDate(2012, Calendar.AUGUST, 5);

		// Calling processRecordToString will write the contents of the bean in a TSV formatted String
		String row1 = writer.processRecordToString(bean);

		// You can write whatever you need as well
		String row2 = writer.writeRowToString("Random", "Values", "Here");

		// Let's change our bean and produce another String
		bean.setPending(false);

		String row3 = writer.processRecordToString(bean);
		//##CODE_END

		println(headers);
		println(row1);
		println(row2);
		println(row3);

		printAndValidate();
	}

	@Test
	public void example009WriteMapWithTypeConversion() {
		CsvWriterSettings settings = new CsvWriterSettings();

		// Using the object row writer processor, we can apply conversions to be applied by default over specific types/
		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();

		//Strings are trimmed and lower cased by default
		processor.convertType(String.class, Conversions.trim(), Conversions.toLowerCase());

		// If a null is written to our boolean column, we want to print "N/A", otherwise Y and N for true and false.
		// Note that column-specific conversions also prevent the type-conversions to be applied.
		// The lower case conversion applied over Strings won't execute on this column.
		processor.convertFields(Conversions.toBoolean(null, "N/A", "Y", "N")).add("Boolean column");

		settings.setRowWriterProcessor(processor);

		//Let's create a CSV writer
		CsvWriter writer = new CsvWriter(settings);

		//##CODE_START
		//Creating a map of rows to write our data. Keys will be used as the headers
		//Each entry contains the values of a column
		Map<String, Object[]> rows = new LinkedHashMap<String, Object[]>();
		rows.put("String column", new Object[]{" Paid ", "   PAID", "\npaid"});
		rows.put("Boolean column", new Object[]{null, true, false});
		rows.put("Last column", new Object[]{199, 288, 11});

		//Let's write everything into a list of Strings. Each element of the list will be a new row
		List<String> writtenRows = writer.processObjectRecordsToString(rows);
		for (String row : writtenRows) {
			println(row);
		}

		//##CODE_END
		printAndValidate();
	}

	@Test
	public void example010MultiSchemaWrite() {
		//##CODE_START
		//creates a switch that will use a different row processor for writing a row, based on values at column 0.
		OutputValueSwitch writerSwitch = new OutputValueSwitch("type");

		// If the value is "SUPER", we want to use an ObjectRowWriterProcessor.
		// Field names "type", "field1" and "field2" will be associated with this row processor
		writerSwitch.addSwitchForValue("SUPER", new ObjectRowWriterProcessor(), "type", "field1", "field2");

		// If the value is "DUPER", another ObjectRowWriterProcessor will be used.
		// Field names "type", "A", "B" and "C" will be used here
		writerSwitch.addSwitchForValue("DUPER", new ObjectRowWriterProcessor(), "type", "A", "B", "C");

		CsvWriterSettings settings = new CsvWriterSettings();

		// configure the writer to use the switch
		settings.setRowWriterProcessor(writerSwitch);
		//rows with less values than expected will be expanded, i.e. empty columns will be written
		settings.setExpandIncompleteRows(true);

		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);

		Map<String, Object> duperValues = new HashMap();
		duperValues.put("type", "DUPER");
		duperValues.put("A", "value A");
		duperValues.put("B", "value B");
		duperValues.put("C", "value C");

		writer.processRecord(new Object[]{"SUPER", "Value 1", "Value 2"}); //writing an array
		writer.processRecord(duperValues); //writing a map

		duperValues.remove("A"); //no data for column "A"
		duperValues.put("B", 5555); //updating the value of B
		duperValues.put("D", null); //not included, will be ignored

		writer.processRecord(duperValues);
		writer.processRecord(new Object[]{"SUPER", "Value 3"}); //no value for column "field2", an empty column will be written

		writer.close();

		print(output.toString());
		//##CODE_END

		printAndValidate();

	}

	@Test
	public void example011MultiSchemaWriteWithBeans() {
		//##CODE_START
		//creates a switch that will use a different row processor for writing a row, based on values at column 0.
		OutputValueSwitch writerSwitch = new OutputValueSwitch("type");

		// If the value is "SUPER", we want to use an ObjectRowWriterProcessor.
		// Field names "type", "field1" and "field2" will be associated with this row processor
		writerSwitch.addSwitchForValue("SUPER", new ObjectRowWriterProcessor(), "type", "field1", "field2");

		//we are going to write instances of Car
		writerSwitch.addSwitchForType(Car.class); //you can also define specific fields to write by giving a list of header names/column indexes.

		CsvWriterSettings settings = new CsvWriterSettings();

		// configure the writer to use the switch
		settings.setRowWriterProcessor(writerSwitch);

		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderWritingEnabled(false);

		StringWriter output = new StringWriter();
		CsvWriter writer = new CsvWriter(output, settings);


		writer.processRecord(new Object[]{"SUPER", "Value 1", "Value 2"}); //writing an array

		//Here's our car
		Car car = new Car();
		car.setYear(2012);
		car.setMake("Toyota");
		car.setModel("Camry");
		car.setPrice(new BigDecimal("10000"));
		writer.processRecord(car);

		//And another car
		car.setYear(2014);
		car.setPrice(new BigDecimal("12000"));
		writer.processRecord(car);

		writer.processRecord(new Object[]{"SUPER", "Value 3"}); //no value for column "field2", an empty column will be written

		writer.close();

		print(output.toString());
		//##CODE_END

		printAndValidate();

	}

	@Test
	public void example012WriteMapWithHeaderMapping() {
		StringWriter output = new StringWriter();
		TsvWriterSettings settings = new TsvWriterSettings();

		//##CODE_START
		settings.setHeaderWritingEnabled(true);
		settings.setHeaders("Header 5", "Header 7", "Header 10");

		TsvWriter writer = new TsvWriter(output, settings);
		writer.writeHeaders();

		//Creating a map of rows to write our data.
		//Each entry contains the values of a column
		Map<Long, Object> values = new HashMap<Long, Object>();
		values.put(5L, "value @ 5");
		values.put(7L, "value @ 7");
		values.put(10L, "value @ 10");

		//we want to write the data stored in the map above, but the keys don't make any sense as column headers.
		//This can be easily solved with a map of headers:
		Map<Long, String> headerMapping = new HashMap<Long, String>();
		headerMapping.put(5L, "Header 5");
		headerMapping.put(7L, "Header 7");
		headerMapping.put(10L, "Header 10");

		writer.writeRow(headerMapping, values);

		values.put(5L, "other @ 5");
		values.put(10L, "other @ 10");
		values.put(11L, "something else entirely"); //will be ignored
		writer.writeRow(headerMapping, values);

		writer.close();

		println(output.toString());

		//##CODE_END
		printAndValidate();
	}

}
