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


import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/383
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_383 {


	@Test
	public void testFieldNameExclusionError() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.excludeFields("x", "y");

		CsvParser parser = new CsvParser(settings);

		String[] row = parser.parseAll(new StringReader("a,b,x\n1,2,3")).get(0);
		assertEquals(Arrays.toString(row), "[1, 2]");

	}

	@Test
	public void testIndexExclusionError() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.excludeIndexes(2, 3);

		CsvParser parser = new CsvParser(settings);

		String[] row = parser.parseAll(new StringReader("1,2,3")).get(0);
		assertEquals(Arrays.toString(row), "[1, 2]");

	}

}
