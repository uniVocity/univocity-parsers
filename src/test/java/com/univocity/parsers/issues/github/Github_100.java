/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_100 {

	@Test
	public void testTabAsQuoteInCsv() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setQuote('\t');
		CsvParser parser = new CsvParser(settings);

		String[] fields = parser.parseLine("\tvalue1\t,,\tv1,',\", v3\t, value2 ");

		assertEquals(fields[0], "value1");
		assertEquals(fields[1], null);
		assertEquals(fields[2], "v1,',\", v3");
		assertEquals(fields[3], "value2");
		assertEquals(fields.length, 4);
	}

}
