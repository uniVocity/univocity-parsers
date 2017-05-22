/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import java.io.*;
import java.sql.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/157
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_157 extends RoutineExamples {

	@Test
	public void testCustomHeadersWhileDumpingResultSet() throws Exception {
		StringWriter output = new StringWriter();

		Statement statement = populateDatabase();
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

}
