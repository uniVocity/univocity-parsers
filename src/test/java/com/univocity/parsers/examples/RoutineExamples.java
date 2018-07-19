/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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

import com.univocity.parsers.csv.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;
import java.sql.*;
import java.util.*;

public class RoutineExamples extends Example {

	@Test
	public void example001IterateOverBeans() {

		//##CODE_START

		// Let's configure our input format using the parser settings, as usual
		// This configuration will be used as the base configuration for our routine.
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");

		// Here we create an instance of our routines object.
		CsvRoutines routines = new CsvRoutines(parserSettings); // Can also use TSV and Fixed-width routines

		// the iterate() method receives our annotated class and an input to parse, and return
		// an Iterator for objects of this class.

		// internally, it will create a special instance of BeanRowProcessor
		// to handle the conversion of each record to a TestBean
		for (TestBean bean : routines.iterate(TestBean.class, getReader("/examples/bean_test.csv"))) {
			println(bean); //let's print it out.
		}

		//##CODE_END

		printAndValidate();
	}


	@Test
	public void example002GetAllBeansAndWrite() {

		//##CODE_START

		// This time we're going to parse a list of beans at once and write them to an output.
		// First we configure the input format
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");

		// Then the output format
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\r\n");
		writerSettings.getFormat().setDelimiter(';');
		writerSettings.setQuoteAllFields(true);

		// Let's create a new routines object with the parser and writer configuration.
		CsvRoutines routines = new CsvRoutines(parserSettings, writerSettings); // Can also use TSV and Fixed-width routines

		// The parseAll routine allows us to get all beans using a single line of code.
		List<TestBean> allBeans = routines.parseAll(TestBean.class, getReader("/examples/bean_test.csv"));

		// For convenience, we will write to a String:
		StringWriter output = new StringWriter();

		// Now, let's write all beans to the output using the writeAll routine:
		// Note that it takes an Iterable as the input. You could use routines.iterate(),
		// as shown in the previous example, to avoid loading all objects in memory.
		routines.writeAll(allBeans, TestBean.class, output);

		// And here's the result
		println(output.toString());

		//##CODE_END

		printAndValidate();
	}

	@Test
	public void example003ParseAndWrite() {
		//##CODE_START
		// The Csv class contains a few static methods that provide pre-defined configurations for CSV parsers/writers
		// Here we will read a csv and write its data so it is compatible with the RFC-4180 standard.
		CsvRoutines routines = new CsvRoutines(new CsvParserSettings(), Csv.writeRfc4180());

		// let's parse only the model and year columns (at positions 2 and 0 respectively)
		routines.getParserSettings().selectIndexes(2, 0);
		routines.getParserSettings().getFormat().setLineSeparator("\n");

		Reader input = getReader("/examples/example.csv");
		Writer output = new StringWriter();

		// using the parseAndWrite method, all rows from the input are streamed to the output efficiently.
		routines.parseAndWrite(input, output);

		// here's the result
		print(output);
		//##CODE_END

		printAndValidate();
	}

	protected Statement connectToDatabase() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:run");
		return connection.createStatement();
	}

	protected Statement populateDatabase() throws Exception {
		//##CODE_START

		String createTable = "CREATE TABLE users(" +
				"	id INTEGER IDENTITY PRIMARY KEY," +
				"	name VARCHAR(50) not null," +
				"	email VARCHAR(50) not null" +
				")";

		Class.forName("org.hsqldb.jdbcDriver");
		Statement statement = connectToDatabase();

		statement.execute(createTable);
		statement.executeUpdate("INSERT INTO users (name, email) VALUES ('Tomonobu Itagaki', 'dead@live.com')");
		statement.executeUpdate("INSERT INTO users (name, email) VALUES ('Caine Hill', 'chill@company.com')");
		statement.executeUpdate("INSERT INTO users (name, email) VALUES ('You Sir', 'user@email.com')");

		//##CODE_END
		return statement;
	}

	@Test
	public void example004DumpResultSet() throws Exception {
		// For convenience, we will write to a String:
		StringWriter output = new StringWriter();

		// Let's create a database in memory, insert data and then run a select statement to create a ResultSet.
		Statement statement = populateDatabase();
		try {
			//##CODE_START
			ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

			// To dump the data of our ResultSet, we configure the output format:
			TsvWriterSettings writerSettings = new TsvWriterSettings();
			writerSettings.getFormat().setLineSeparator("\n");
			writerSettings.setHeaderWritingEnabled(true); // we want the column names to be printed out as well.

			// Then create a routines object:
			TsvRoutines routines = new TsvRoutines(writerSettings);

			// The write() method takes care of everything. Both resultSet and output are closed by the routine.
			routines.write(resultSet, output);

			//##CODE_END

			print(output.toString());
		} finally {
			statement.getConnection().close();
		}


		printAndValidate();
	}


	@Test(dependsOnMethods = "example004DumpResultSet")
	public void example005DumpResultSetWithCustomHeaders() throws Exception {
		StringWriter output = new StringWriter();

		Statement statement = connectToDatabase();
		try {
			ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

			CsvWriterSettings csvWriterSettings = new CsvWriterSettings();
			String headers[] = {"Custom", "Headers", "Should", "Work", "Just", "Fine"};
			csvWriterSettings.setHeaders(headers);
			csvWriterSettings.setHeaderWritingEnabled(true);

			CsvRoutines csvRoutines = new CsvRoutines(csvWriterSettings);
			csvRoutines.write(resultSet, output);
			print(output.toString());
		} finally {
			statement.getConnection().close();
		}

		printAndValidate();
	}

	@Test(dependsOnMethods = "example004DumpResultSet")
	public void example006DumpResultSetWithSelection() throws Exception {
		StringWriter output = new StringWriter();

		Statement statement = connectToDatabase();
		try {
			ResultSet resultSet = statement.executeQuery("SELECT * FROM users");

			CsvWriterSettings csvWriterSettings = new CsvWriterSettings();
			csvWriterSettings.selectFields("name", "id");
			csvWriterSettings.setHeaderWritingEnabled(true);
			csvWriterSettings.setColumnReorderingEnabled(true);

			CsvRoutines csvRoutines = new CsvRoutines(csvWriterSettings);
			csvRoutines.write(resultSet, output);
			print(output.toString());
		} finally {
			statement.getConnection().close();
		}
		printAndValidate();
	}
}
