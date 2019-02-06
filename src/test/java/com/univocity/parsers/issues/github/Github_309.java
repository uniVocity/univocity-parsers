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
import java.util.*;

import static java.lang.Boolean.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/309
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_309 {
	@Test
	public void parserFilesTest() {

		CsvParserSettings csvParserSettings = new CsvParserSettings();
		csvParserSettings.setHeaderExtractionEnabled(TRUE);
		csvParserSettings.setLineSeparatorDetectionEnabled(TRUE);

		CsvParser parser = new CsvParser(csvParserSettings);
		parser.parse(new StringReader("A,B,C\n1,2,3"));

		String[] headers1 = parser.getContext().headers();
		assertEquals(Arrays.toString(headers1), "[A, B, C]");

		parser.parse(new StringReader("X,Y\n8,9"));
		String[] headers2 = parser.getContext().headers();

		assertNotEquals(headers1, headers2);
		assertEquals(Arrays.toString(headers2), "[X, Y]");
	}
}
