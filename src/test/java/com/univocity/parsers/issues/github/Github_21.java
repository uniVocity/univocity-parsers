/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
 * From: https://github.com/uniVocity/univocity-parsers/issues/21
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_21 {

	@DataProvider
	public Object[][] settingsAndExpectedResults() {
		return new Object[][]{
			// value to write: A|"

			{true, true, true, true, "\"A|||\"\""},  // trim, escape '|', escape unquoted, always quote:                    [ " , A , ||,  |", " ]
			{true, true, true, false, "A|||\""},     // trim, escape '|', escape unquoted, quote when required:             [   , A , ||,  |",   ]
			{true, true, false, true, "\"A|||\"\""}, // trim, escape '|', don't escape unquoted, always quote:              [ " , A , ||,  |", " ]
			{true, true, false, false, "A|\""},      // trim, escape '|', don't escape unquoted, quote when required:       [   , A , | ,  " ,   ]
			{true, false, true, true, "\"A||\"\""},  // trim, no escape, escape unquoted, always quote:                     [ " , A , | ,  |", " ]
			{true, false, true, false, "A||\""},     // trim,  no escape, escape unquoted, quote when required:             [   , A , | ,  |",   ]
			{true, false, false, true, "\"A||\"\""}, // trim, no escape, don't escape unquoted, always quote:               [ " , A , | ,  |", " ]
			{true, false, false, false, "A|\""},     // trim, no escape, don't escape unquoted, quote when required:        [   , A , | ,  " ,   ]
			{false, true, true, true, "\" A|||\" \""}, // no trim, escape '|', escape unquoted, always quote:               [ " , A , ||,  |", " ]
			{false, true, true, false, " A|||\" "},    // no trim, escape '|', escape unquoted, quote when required:        [   , A , ||,  |",   ]
			{false, true, false, true, "\" A|||\" \""},// no trim, escape '|', don't escape unquoted, always quote:         [ " , A , ||,  |", " ]
			{false, true, false, false, " A|\" "},     // no trim, escape '|', don't escape unquoted, quote when required:  [   , A , | ,  " ,   ]
			{false, false, true, true, "\" A||\" \""}, // no trim, no escape, escape unquoted, always quote:                [ " , A , | ,  |", " ]
			{false, false, true, false, " A||\" "},    // no trim, no escape, escape unquoted, quote when required:         [   , A , | ,  |",   ]
			{false, false, false, true, "\" A||\" \""},// no trim,no escape, don't escape unquoted, always quote:           [ " , A , | ,  |", " ]
			{false, false, false, false, " A|\" "},    // no trim, no escape, don't escape unquoted, quote when required:   [   , A , | ,  " ,   ]
		};
	}

	@Test(dataProvider = "settingsAndExpectedResults")
	public void testWriteQuoteEscape(boolean trim, boolean escapeQuoteEscape, boolean escapeUnquotedValues, boolean quoteAlways, String expected) {
		StringWriter output = new StringWriter();

		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.getFormat().setQuoteEscape('|');
		if (!escapeQuoteEscape) {
			writerSettings.getFormat().setCharToEscapeQuoteEscaping('\0');
		}
		writerSettings.setIgnoreLeadingWhitespaces(trim);
		writerSettings.setIgnoreTrailingWhitespaces(trim);

		writerSettings.setQuoteAllFields(quoteAlways);
		writerSettings.setEscapeUnquotedValues(escapeUnquotedValues);

		CsvWriter csvWriter = new CsvWriter(output, writerSettings);
		csvWriter.writeRow(new Object[]{" A|\" "});
		csvWriter.close();

		String result = output.toString();
		assertEquals(result, expected + '\n');

		//now, let's parse to ensure we are not "losing" characters when writing. Should always get value: A|"
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setFormat(writerSettings.getFormat());
		parserSettings.setEscapeUnquotedValues(escapeUnquotedValues);
		parserSettings.setIgnoreLeadingWhitespaces(trim);
		parserSettings.setIgnoreTrailingWhitespaces(trim);

		CsvParser parser = new CsvParser(parserSettings);
		String parsedValue = parser.parseLine(result)[0];

		if (trim) {
			assertEquals(parsedValue, "A|\"");
		} else {
			assertEquals(parsedValue, " A|\" ");
		}
	}

}
