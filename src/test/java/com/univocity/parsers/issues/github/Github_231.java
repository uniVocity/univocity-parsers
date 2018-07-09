/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.univocity.parsers.issues.github;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.nio.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/231
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_231 {

	@Test
	public void testStopParsingConcurrencyIssue() {
		CsvParserSettings s = new CsvParserSettings();
		s.setHeaderExtractionEnabled(true);
		final CsvParser parser = new CsvParser(s);

		parser.beginParsing(new StringReader("LALALA"){
			@Override
			public int read(char[] cbuf, int off, int len) throws IOException {
				parser.stopParsing();
				return super.read(cbuf, off, len);
			}
		});


		String[] parsedHeaders;
		try {
			parsedHeaders = parser.getContext().parsedHeaders();
		} finally {
			parser.stopParsing();
		}

	}
}
