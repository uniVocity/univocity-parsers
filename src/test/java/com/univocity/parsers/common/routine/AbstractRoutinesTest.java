/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

package com.univocity.parsers.common.routine;

import com.univocity.parsers.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import com.univocity.parsers.tsv.*;
import org.testng.annotations.*;

import java.io.*;
import java.sql.*;

import static org.testng.Assert.*;

public class AbstractRoutinesTest {

	static class ResultSetTest {
		AbstractRoutines routineImpl;
		String result;

		ResultSetTest(AbstractRoutines routineImpl) {
			this.routineImpl = routineImpl;
		}

		void run(ResultSet rs) throws Exception {
			StringWriter output = new StringWriter();
			routineImpl.write(rs, output);
			result = output.toString();
		}

	}

	@Test
	public void testWriteResultSet() throws Exception {
		CsvRoutines csvRoutine = new CsvRoutines();
		csvRoutine.setWriterSettings(new CsvWriterSettings());
		csvRoutine.getWriterSettings().getFormat().setLineSeparator("\n");
		ResultSetTest csvTest = new ResultSetTest(csvRoutine);

		TsvRoutines tsvRoutine = new TsvRoutines();
		tsvRoutine.setWriterSettings(new TsvWriterSettings());
		tsvRoutine.getWriterSettings().getFormat().setLineSeparator("\n");
		ResultSetTest tsvTest = new ResultSetTest(tsvRoutine);

		FixedWidthRoutines fixedWidthRoutine = new FixedWidthRoutines();
		fixedWidthRoutine.setWriterSettings(new FixedWidthWriterSettings());
		fixedWidthRoutine.getWriterSettings().getFormat().setLineSeparator("\n");
		fixedWidthRoutine.getWriterSettings().getFormat().setPadding('.');
		ResultSetTest fixedWidthTest = new ResultSetTest(fixedWidthRoutine);

		testWriteResultSet(csvTest, tsvTest, fixedWidthTest);

		String expectedCsv = "1234,Description 1\n2345,Description 2\n39,Description 3\n";
		String expectedTsv = "1234\tDescription 1\n2345\tDescription 2\n39\tDescription 3\n";
		String expectedFixedWidth = "1234Description 1...................\n2345Description 2...................\n39..Description 3...................\n";

		assertEquals(csvTest.result,expectedCsv);
		assertEquals(tsvTest.result,expectedTsv);
		assertEquals(fixedWidthTest.result,expectedFixedWidth);
	}


	private void testWriteResultSet(ResultSetTest... tests) throws Exception {
		String createTable = "CREATE TABLE test(" +
				"	id char(4) primary key," +
				"	desc varchar(32) not null" +
				")";

		Class.forName("org.hsqldb.jdbcDriver");
		Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:run");
		try {
			Statement statement = connection.createStatement();
			try {
				statement.execute(createTable);
				statement.executeUpdate("INSERT INTO test (id, desc) VALUES ('1234', 'Description 1')");
				statement.executeUpdate("INSERT INTO test (id, desc) VALUES ('2345', 'Description 2')");
				statement.executeUpdate("INSERT INTO test (id, desc) VALUES ('39' , 'Description 3')");

				for (ResultSetTest test : tests) {
					ResultSet rs = statement.executeQuery("SELECT id, desc FROM test ORDER BY id");
					try {
						test.run(rs);
					} finally {
						rs.close();
					}
				}

			} finally {
				statement.close();
			}
		} finally {
			connection.close();
		}
	}

	@Test
	public void testParseAndWrite() throws Exception {
		CsvRoutines csvRoutines = new CsvRoutines();
		CsvParserSettings parser = new CsvParserSettings();
		parser.getFormat().setLineSeparator("\n");
		parser.setNumberOfRowsToSkip(2);
		parser.setHeaderExtractionEnabled(true);

		parser.selectFields("Description");
		csvRoutines.setParserSettings(parser);

		CsvWriterSettings writer = new CsvWriterSettings();
		writer.getFormat().setDelimiter('|');
		writer.getFormat().setLineSeparator("\r\n");
		writer.getFormat().setQuoteEscape('$');
		writer.getFormat().setQuote('$');
		writer.setSkipEmptyLines(true);
		csvRoutines.setWriterSettings(writer);

		StringWriter output = new StringWriter();

		csvRoutines.parseAndWrite(ParserTestCase.newReader("/csv/essential.csv"), output);

		String expected = "ac, abs, moon\r\n" +
				"$MUST SELL!\r\n" +
				"air, moon roof, loaded$\r\n" +
				"ac, abs, moon\r\n" +
				"ac, abs, moon\r\n" +
				"ac, abs, moon\r\n" +
				"ac, abs, moon\r\n" +
				"\" ac, abs, moon \"\r\n" +
				"\" ac, abs, moon \"\r\n";

		assertEquals(output.toString(), expected);
	}
}