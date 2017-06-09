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

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/165
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_165 {

	@DataProvider
	public Object[][] inputBufferSizeProvider(){
		return new Object[][]{
				{1},
				{2},
				{3},
				{4},
				{5},
				{6},
				{7},
				{8},
		};
	}

	@Test(dataProvider = "inputBufferSizeProvider")
	public void testCurrentParsedContentWithShortString(int inputBufferSize) throws IOException {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setReadInputOnSeparateThread(false);
		settings.setInputBufferSize(inputBufferSize);
		settings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(settings);

		parser.beginParsing(new StringReader("a"));
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), "a");
		parser.stopParsing();

		parser.parse(new StringReader("a"));
		assertEquals(parser.getContext().currentParsedContent(), "a");

		parser.parse(new StringReader("ab"));
		assertEquals(parser.getContext().currentParsedContent(), "ab");

		parser.parse(new StringReader("ab\nc"));
		assertEquals(parser.getContext().currentParsedContent(), "c");

		parser.parse(new StringReader("ab\nc\n"));
		assertEquals(parser.getContext().currentParsedContent(), null);

		parser.beginParsing(new StringReader("ab\nc\n"));
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), "ab\n");
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), "c\n");
		parser.parseNext();
		assertEquals(parser.getContext().currentParsedContent(), null);
		parser.stopParsing();

		parser.parse(new StringReader("ab\nc\n\n"));
		assertEquals(parser.getContext().currentParsedContent(), null);
	}
}
