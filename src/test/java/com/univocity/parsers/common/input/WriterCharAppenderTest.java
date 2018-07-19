/*
 * Copyright 2016 Univocity Software Pty Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.common.input;

import com.univocity.parsers.csv.CsvWriter;
import com.univocity.parsers.csv.CsvWriterSettings;
import java.io.StringWriter;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

public class WriterCharAppenderTest {

	@Test
	public void testAppendAndExpandWhenAppendingChar() {
		CsvWriterSettings settings = new CsvWriterSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.getFormat().setDelimiter('\t');
		settings.setMaxCharsPerColumn(16); // note default max length before expanding
		settings.getFormat().setQuote('"');
		settings.getFormat().setQuoteEscape('"');
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setQuoteAllFields(true);
		settings.setEmptyValue("");

		StringWriter stringWriter = new StringWriter();
		CsvWriter writer = new CsvWriter(stringWriter, settings);

		// test data's first column length is specific to repro bug occuring due to
		// appending quote character occuring at writer buffer boundary
		String[] testCase = {"abcdefghijklmno", "pqrst", "uvwxyz"};
		String expectedString = "\"abcdefghijklmno\"\t\"pqrst\"\t\"uvwxyz\"\n";

		writer.writeRow(testCase);
		writer.close();

		assertEquals(stringWriter.toString(), expectedString);
	}
}
