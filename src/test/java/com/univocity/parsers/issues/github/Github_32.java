/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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
 * From: https://github.com/univocity/univocity-parsers/issues/32
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_32 {

	@Test
	public void testHeadersAreExtractedByReusedParserInstance() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.detectFormatAutomatically();

		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);

		List<String[]> rows;
		rows = parser.parseAll(new StringReader("Amount,Tax,Total\n1.99,10.0,2.189\n5,20.0,6"));
		assertEquals(rows.size(), 2);

		rows = parser.parseAll(new StringReader("Amount;Tax;Total\n1,99;10,0;2,189\n5;20,0;6"));
		assertEquals(rows.size(), 2);
	}
}
