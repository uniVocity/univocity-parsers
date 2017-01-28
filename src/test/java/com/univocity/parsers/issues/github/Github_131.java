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

import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/131
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_131 {

	@Test
	public void testColumnReorderingOnLine() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.selectIndexes(0, 3);
		settings.setColumnReorderingEnabled(false);
		CsvParser parse = new CsvParser(settings);

		String[] strings = parse.parseLine("1,2,3,4,5");
		assertEquals(Arrays.toString(strings), "[1, null, null, 4, null]");
	}
}
