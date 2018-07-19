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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

public class Github_93 {

	@Test
	public void testVaryingLengthRecordsCanBeRead() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(new StringReader("a,b\n1,2,3\n4\n5,6,7,8,9"));

		Record record = parser.parseNextRecord();
		assertEquals(record.getString(0), "a");
		assertEquals(record.getString(1), "b");

		record = parser.parseNextRecord();
		assertEquals(record.getString(0), "1");
		assertEquals(record.getString(1), "2");
		assertEquals(record.getString(2), "3");

		record = parser.parseNextRecord();
		assertEquals(record.getString(0), "4");

		record = parser.parseNextRecord();
		assertEquals(record.getString(0), "5");
		assertEquals(record.getString(1), "6");
		assertEquals(record.getString(2), "7");
		assertEquals(record.getString(3), "8");
		assertEquals(record.getString(4), "9");

		assertNull(parser.parseNextRecord());


	}
}