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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

public class Github_92 {

	@Test
	public void emptyValueWorksOnEOF() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setEmptyValue("?");
		settings.getFormat().setQuote('\'');

		CsvParser parser = new CsvParser(settings);

		String[] row;
		row = parser.parseLine("\'\'");
		assertNotNull(row);
		assertEquals(row[0], "?");
		assertEquals(row.length, 1);

		row = parser.parseLine("\'\',''");
		assertEquals(row[0], "?");
		assertEquals(row[1], "?");
		assertEquals(row.length, 2);

		row = parser.parseLine("\'\'\'");
		assertNotNull(row);
		assertEquals(row[0], "'");
		assertEquals(row.length, 1);

		row = parser.parseLine("\'\','a'");
		assertEquals(row[0], "?");
		assertEquals(row[1], "a");
		assertEquals(row.length, 2);

		row = parser.parseLine("");
		assertNull(row);

		row = parser.parseLine("'");
		assertEquals(row[0], "'");
		assertEquals(row.length, 1);

		row = parser.parseLine("a'");
		assertEquals(row[0], "a'");
		assertEquals(row.length, 1);
	}
}
