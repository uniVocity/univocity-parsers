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

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/378
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_378 {

	@Test
	public void testParsingWithSpaceAfterQuoteAndNonPrintableChar() {
		char delim = (char) 031;
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setDelimiter(delim);
		settings.getFormat().setQuote('\'');

		String[] line = new CsvParser(settings).parseLine("C123" + delim + "'This is quoted string followed by white spaces' " + delim + " " + delim + "0");
		assertEquals(line[0], "C123");
		assertEquals(line[1], "This is quoted string followed by white spaces");
		assertNull(line[2], null);
		assertEquals(line[3], "0");
	}

}
