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

import com.univocity.parsers.*;
import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/183
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_183 extends ParserTestCase {

	@DataProvider
	Object[][] inputProvider() {
		return new Object[][]{
				{false},
				{true}
		};
	}

	@Test(dataProvider = "inputProvider")
	public void testColumnReader(boolean exclude) {
		String input = "" +
				"R0C0,R0C1,R0C2,R0C3,R0C4,R0C5\n" +
				"R1C0,R1C1,R1C2,R1C3,R1C4,R1C5\n" +
				"R2C0,R2C1,R2C2,R2C3,R2C4,R2C5\n";

		CsvParserSettings csvParserSettings = new CsvParserSettings();
		ColumnProcessor rowProcessor = new ColumnProcessor();
		csvParserSettings.setProcessor(rowProcessor);
		if (exclude) {
			csvParserSettings.excludeIndexes(0, 3, 4, 5);
		} else {
			csvParserSettings.selectIndexes(1, 2);
			csvParserSettings.setHeaders("");
		}

		csvParserSettings.setColumnReorderingEnabled(false);
		csvParserSettings.getFormat().setLineSeparator("\n");
		csvParserSettings.setHeaderExtractionEnabled(false);

		CsvParser csvparser = new CsvParser(csvParserSettings);
		for (String[] row : csvparser.parseAll(new StringReader(input))) {
			assertEquals(row.length, 6);
			assertNull(row[0]);
			assertNotNull(row[1]);
			assertNotNull(row[2]);
			assertNull(row[3]);
			assertNull(row[4]);
			assertNull(row[5]);
		}

		assertEquals(rowProcessor.getColumnValuesAsMapOfIndexes().toString(), "{0=[null, null, null], 1=[R0C1, R1C1, R2C1], 2=[R0C2, R1C2, R2C2], 3=[null, null, null], 4=[null, null, null], 5=[null, null, null]}");
	}
}
