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


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/328
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_328 {

	@Test
	public void testLowerThom(){
		CsvParserSettings settings = new CsvParserSettings();
		char d = 231;

		settings.detectFormatAutomatically(';', ',', d);
		CsvParser parser = new CsvParser(settings);


		String s = "" +
				"1" + d + "2001-01-01" + d + "First row" + d + "1.1\n" +
				"2" + d + "2002-02-02" + d + "Second row" + d + "2.2\n" +
				"3" + d + "2003-03-03" + d + "Third row" + d + "3.3\n" +
				"4" + d + "2004-04-04" + d + "Fourth row" + d + "4.4";

		List<String[]> rows = parser.parseAll(new StringReader(s));

		CsvFormat format = parser.getDetectedFormat();
		assertEquals(format.getDelimiter(), d);
		assertEquals(rows.size(), 4);
	}
}
