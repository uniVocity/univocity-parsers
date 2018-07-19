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

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/34
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */

public class Github_34 {

	@Test
	public void ensureFirstQuoteCharacterIsEscaped() {

		StringWriter out = new StringWriter();

		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		CsvWriter writer = new CsvWriter(out, writerSettings);
		writer.writeRow("Quote", "\"", "Value with quote\"");
		writer.close();

		String result = out.toString();

		assertEquals(result, "Quote,\"\"\"\",Value with quote\"\n");

		String[] line = new CsvParser(new CsvParserSettings()).parseLine(result);
		assertEquals(line[0], "Quote");
		assertEquals(line[1], "\"");
		assertEquals(line[2], "Value with quote\"");
	}
}
