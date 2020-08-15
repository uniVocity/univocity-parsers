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


import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/405
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_409 {

	@Test
	public void testPaddingOnFixedWidth() {
		String rawData = "A\tB\tC.\t\"G\n" +
				"I\n" +
				"\"\t\"J\n" +
				"M\"\n" +
				"\n";

		final List<String[]> rows = new ArrayList<String[]>();
		final CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically('\t');
		settings.setIgnoreLeadingWhitespaces(false);
		settings.setIgnoreTrailingWhitespaces(false);
		settings.setSkipEmptyLines(false);
		
		settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.RAISE_ERROR);

		//Ansonsten sind leere Zeilen null-values und führen zu Fehlern.
		settings.setNullValue("");

		settings.setProcessor(new AbstractRowProcessor() {
			@Override
			public void rowProcessed(final String[] row, final ParsingContext __) {
				if (row != null) {
					rows.add(row);
				}
			}
		});

		final CsvParser parser = new CsvParser(settings);

		parser.beginParsing(new StringReader(rawData));
		assertEquals(parser.getDetectedFormat().getQuoteEscape(), '\"');
		parser.stopParsing();

		parser.parse(new StringReader(rawData));

		String[] row = rows.get(0);
		assertEquals(row[0], "A");
		assertEquals(row[1], "B");
		assertEquals(row[2], "C.");
		assertEquals(row[3], "G\nI\n");
		assertEquals(row[4], "J\nM");
	}

}
