/*******************************************************************************
 * Copyright 2019 Univocity Software Pty Ltd
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
 * From: https://github.com/univocity/univocity-parsers/issues/303
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_303 {

	@Test
	public void testAutoClosingDisabledSingleThread() {
		testAutoClosingDisabled(false);
	}

	@Test
	public void testAutoClosingDisabledMultiThread() {
		testAutoClosingDisabled(true);
	}


	public void testAutoClosingDisabled(boolean separateThread) {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setAutoClosingEnabled(false);
		settings.setReadInputOnSeparateThread(separateThread);

		final boolean[] closed = new boolean[1];

		StringReader reader = new StringReader("a\nb\nc"){
			@Override
			public void close() {
				closed[0] = true;
				super.close();
			}
		};

		CsvParser parser = new CsvParser(settings);
		parser.parseAll(reader);
		assertFalse(closed[0]);

		reader.close();
		assertTrue(closed[0]);
	}

}
