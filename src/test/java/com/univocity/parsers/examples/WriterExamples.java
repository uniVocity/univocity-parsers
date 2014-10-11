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

import java.io.*;
import java.math.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import com.univocity.parsers.tsv.*;

public class WriterExamples extends Example {

	List<Object[]> rows = Arrays.asList(
			new Object[][] {
					{ "1997", "Ford", "E350", "ac, abs, moon", "3000.00" },
					{ "1999", "Chevy", "Venture \"Extended Edition\"", "", "4900.00" },
					{ "1996", "Jeep", "Grand Cherokee", "MUST SELL!\nair, moon roof, loaded", "4799.00" },
					{},
					{ "1999", "Chevy", "Venture \"Extended Edition, Very Large\"", null, "5000.00" },
					{ null, "", "Venture \"Extended Edition\"", null, "4900.00" },
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
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(15, 10, 35);
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
		processor.convertFields(Conversions.toDate(" yyyy MMM dd "), Conversions.trim()).add("date");

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
		FixedWidthFieldLengths lengths = new FixedWidthFieldLengths(10, 10, 35, 10, 40);
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
}
