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
 * From: https://github.com/univocity/univocity-parsers/issues/185
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_185 extends Example {

	@Test
	public void testCurrentParsedContentWithInputStream() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		CsvParser parser = new CsvParser(settings);

		parser.beginParsing(new ByteArrayInputStream("a".getBytes("UTF-8")));
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), "a");
	}

	@Test
	public void testCurrentParsedContentWithReader() throws Exception {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		CsvParser parser = new CsvParser(settings);

		parser.beginParsing(new StringReader("a"));
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), "a");
	}
}
