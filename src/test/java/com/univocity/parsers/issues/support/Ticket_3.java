/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
package com.univocity.parsers.issues.support;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_3 {

	@Test
	public void testEscapeError() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.getFormat().setQuoteEscape('\\');
		settings.getFormat().setCharToEscapeQuoteEscaping('\0');
		settings.setHeaderExtractionEnabled(false);
		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(new InputStreamReader(Ticket_3.class.getResourceAsStream("/issues/ticket_3/input.csv"), "UTF-8"));

		/*
		"0\",\"0"     ==> [0","0]
		"1\\",\"1"    ==> [1\","1]
		"2\\\",\"2"   ==> [2\\",2]
		 */
		String[] expected0 = new String[]{"0\",\"0"};
		String[] expected1 = new String[]{"1\\\",\"1"};
		String[] expected2 = new String[]{"2\\\\\",\"2"};

		assertEquals(allRows.get(0), expected0);
		assertEquals(allRows.get(1), expected1);
		assertEquals(allRows.get(2), expected2);
	}

	@Test
	public void testEscapeEscape() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setLineSeparatorDetectionEnabled(true);
		settings.getFormat().setQuoteEscape('\\');
		settings.getFormat().setCharToEscapeQuoteEscaping('\\');
		settings.setParseUnescapedQuotes(false);
		settings.setHeaderExtractionEnabled(false);
		CsvParser parser = new CsvParser(settings);

		List<String[]> allRows = parser.parseAll(new InputStreamReader(Ticket_3.class.getResourceAsStream("/issues/ticket_3/input.csv"), "UTF-8"));

		/*
		"0\",\"0"     ==> [0","0]
		"1\\",\"1"    ==> [1\],[\"1"]
		"2\\\",\"2"   ==> [2\","2]
		 */
		String[] expected0 = new String[]{"0\",\"0"};
		String[] expected1 = new String[]{"1\\", "\\\"1\""};
		String[] expected2 = new String[]{"2\\\",\"2"};

		assertEquals(allRows.get(0), expected0);
		assertEquals(allRows.get(1), expected1);
		assertEquals(allRows.get(2), expected2);
	}

}
