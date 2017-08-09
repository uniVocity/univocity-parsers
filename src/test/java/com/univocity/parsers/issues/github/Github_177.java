/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

import com.univocity.parsers.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/177
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_177 extends ParserTestCase {


	@Test
	public void testNewlineAfterEscapedQuote() throws Exception{
		CsvParserSettings settings = new CsvParserSettings();

		settings.getFormat().setComment('\0');
		settings.setMaxCharsPerColumn(1000000);
		settings.setMaxColumns(10000);

		settings.setQuoteDetectionEnabled(true);
		settings.setLineSeparatorDetectionEnabled(true);

		settings.setSkipEmptyLines(false);
		settings.setReadInputOnSeparateThread(true);

		settings.setUnescapedQuoteHandling(UnescapedQuoteHandling.RAISE_ERROR);

		CsvParser parser = new CsvParser(settings);

		for(String[] row : parser.parseAll(newReader("/issues/github_177/input.csv"))){
			assertEquals(row.length, 32);
		}

	}
}
