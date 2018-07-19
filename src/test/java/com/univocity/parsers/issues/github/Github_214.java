/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/214
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_214 {

	@Test
	public void detectCsvFormat() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();

		CsvParser parser = new CsvParser(settings);

		String input = "" +
				"1_car_name|2_truck_name|3_wheels\n" +
				"audi,q5,4\n" +
				"audi,q3,4";

		parser.beginParsing(new StringReader(input));

		CsvFormat format = parser.getDetectedFormat();
		parser.stopParsing();

		assertEquals(format.getDelimiter(), ',');
	}

	@Test
	public void detectCsvFormatWithPriorityList() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically( '-', '.');
		CsvParser parser = new CsvParser(settings);

		String s = "" +
				"1;2001-01-01;First row;1.1\n" +
				"2;2002-02-02;Second row;2.2\n" +
				"3;2003-03-03;Third row;3.3\n" +
				"4;2004-04-04;Fourth row;4.4";

		parser.beginParsing(new StringReader(s));

		CsvFormat format = parser.getDetectedFormat();

		assertEquals(format.getDelimiter(), '-');

		format.setDelimiter(';');

		parser.updateFormat(format);

		List<String[]> rows = parser.parseAll();
		assertEquals(rows.size(), 4);

		assertEquals(Arrays.toString(rows.get(0)),"[1, 2001-01-01, First row, 1.1]");
		assertEquals(Arrays.toString(rows.get(1)),"[2, 2002-02-02, Second row, 2.2]");
		assertEquals(Arrays.toString(rows.get(2)),"[3, 2003-03-03, Third row, 3.3]");
		assertEquals(Arrays.toString(rows.get(3)),"[4, 2004-04-04, Fourth row, 4.4]");

	}
}
