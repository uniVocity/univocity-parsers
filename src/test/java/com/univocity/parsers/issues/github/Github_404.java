/*******************************************************************************
 * Copyright 2020 Univocity Software Pty Ltd
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
public class Github_404 {

	@Test
	public void testMultiDelimiter() {
		CsvFormat format = new CsvFormat();
		format.setDelimiter("||");
		format.setQuote('\'');
		CsvParserSettings settings = new CsvParserSettings();
		settings.setFormat(format);
		CsvParser parser = new CsvParser(settings);
		String[] line = parser.parseLine("foo||bar||'||'||'foo||bar'");
		assertEquals(line[0], "foo");
		assertEquals(line[1], "bar");
		assertEquals(line[2], "||");
		assertEquals(line[3], "foo||bar");
		assertEquals(line.length, 4);
	}

}
