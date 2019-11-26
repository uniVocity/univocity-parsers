/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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


import com.univocity.parsers.common.*;
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/349
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_349 {

	@Test
	public void testNoDuplicates() {
		ResultIterator<Record, ParsingContext> iterator = getParsedIteratorForResource("notduplicate_1,notduplicate_2,notduplicate_3\r\nvalueForFirst,valueForSecond,valueForThird");
		Record row = iterator.next();
		Map<String, String> parsedRow = row.fillFieldMap(new HashMap<String, String>());
		assertEquals(parsedRow.size(), 3);
		assertEquals(parsedRow.get("notduplicate_1"), "valueForFirst");
		assertEquals(parsedRow.get("notduplicate_2"), "valueForSecond");
		assertEquals(parsedRow.get("notduplicate_3"), "valueForThird");
	}

	@Test
	public void duplicatesNotAtEnd() {
		ResultIterator<Record, ParsingContext> iterator = getParsedIteratorForResource("duplicate,duplicate,notduplicate\r\nvalueForFirst,valueForSecond,valueForThird");
		Record row = iterator.next();
		Map<String, String> parsedRow = row.fillFieldMap(new HashMap<String, String>());
		assertEquals(parsedRow.size(), 2);
		assertTrue(parsedRow.get("duplicate").equals("valueForFirst") || parsedRow.get("duplicate").equals("valueForSecond"));
		assertEquals(parsedRow.get("notduplicate"), "valueForThird");
	}

	@Test
	public void duplicatesAtEnd() {
		ResultIterator<Record, ParsingContext> iterator = getParsedIteratorForResource("duplicate,notduplicate,duplicate\r\nvalueForFirst,valueForSecond,valueForThird");
		Record row = iterator.next();
		Map<String, String> parsedRow = row.fillFieldMap(new HashMap<String, String>());

		assertEquals(parsedRow.size(), 2);
		assertTrue(parsedRow.get("duplicate").equals("valueForFirst") || parsedRow.get("duplicate").equals("valueForThird"));
		assertEquals(parsedRow.get("notduplicate"), "valueForSecond");
//		assertEquals(parsedRow.get("duplicate\r"), "valueForThird"); // carriage return kept
	}

	private ResultIterator<Record, ParsingContext> getParsedIteratorForResource(String input) {
		try {
			InputStream resource = new ByteArrayInputStream(input.getBytes("UTF-8"));
			CsvParser parser = new CsvParser(buildSettings());
			return parser.iterateRecords(resource, "UTF-8").iterator();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private CsvParserSettings buildSettings() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		settings.getFormat().setLineSeparator("\r\n");
		settings.setHeaderExtractionEnabled(true);
		return settings;
	}
}
