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

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/140
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_140 {

	@Test(expectedExceptions = TextParsingException.class)
	public void parseExceptionInConcurrentInput() throws Exception {

		Reader explodingInput = new Reader() {

			String firstPart = "a,b,c\n1,2,3\n4,5";

			@Override
			public int read(char[] chars, int off, int len) throws IOException {
				if (firstPart != null) {
					for (int i = 0; i < firstPart.length() && len-- > 0; i++) {
						chars[off++] = firstPart.charAt(i);
					}
					int out = firstPart.length();
					firstPart = null;
					return out;
				}
				throw new IllegalStateException("Boom!");
			}

			@Override
			public void close() throws IOException {

			}
		};

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(settings);
		parser.beginParsing(explodingInput);

		assertEquals(Arrays.toString(parser.parseNext()), "[a, b, c]");
		assertEquals(Arrays.toString(parser.parseNext()), "[1, 2, 3]");

		//should fail
		System.out.println(Arrays.toString(parser.parseNext()));
		fail("Expected exception here.");
	}


}
