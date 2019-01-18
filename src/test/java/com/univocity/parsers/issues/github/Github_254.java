/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
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
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/254
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_254 {

	@Test
	public void testParse() {
		CsvParserSettings s = new CsvParserSettings();
		s.setLineSeparatorDetectionEnabled(true);
		s.setColumnReorderingEnabled(false);
		s.selectFields("r1", "r2");

		List<String[]> rows = new CsvParser(s).parseAll(new StringReader("r1,r2\nref1\nref1,\nref1"));
		assertEquals(rows.size(), 4);

		for (String[] row : rows) {
			assertEquals(row.length, 2);
		}
	}

}
