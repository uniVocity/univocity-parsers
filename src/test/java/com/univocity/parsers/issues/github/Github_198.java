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

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/198
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_198 extends Example {

	@Test
	public void testDelimiterDetection() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();

		CsvParser csvParser = new CsvParser(settings);

		csvParser.beginParsing(new StringReader("entity-id;code;color-code;display-name\n" +
				"12345;789;123-456;Display Name 1\n" +
				"12346;789;123-456;Display Name 2\n" +
				"12347;789;123-456;Display Name 3\n"));

		CsvFormat format = csvParser.getDetectedFormat();
		assertEquals(format.getDelimiter(), ';');

		csvParser.stopParsing();
	}
}

