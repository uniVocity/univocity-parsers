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
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/196
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_196 extends Example {

	@Test
	public void testQuotedEmptyAroundWhitespaces() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setDelimiter(',');
		settings.getFormat().setQuote('\'');
		settings.getFormat().setQuoteEscape('\'');
		settings.setEmptyValue("");
		settings.setAutoConfigurationEnabled(false);
		CsvParser csvParser = new CsvParser(settings);

		String[] values = csvParser.parseLine("2, '' ");
		assertNotNull(values);
		assertEquals(values[0], "2");
		assertEquals(values[1], "");

		values = csvParser.parseLine(" '' ");
		assertNotNull(values);
		assertEquals(values[0], "");
	}
}

