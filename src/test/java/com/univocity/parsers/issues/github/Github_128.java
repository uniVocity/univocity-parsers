/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/128
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_128 {

	@Test
	public void skipUntilNewLineDoesntGenerateNullOnEOF() {
		FixedWidthParserSettings settings = new FixedWidthParserSettings(new FixedWidthFields(1, 1, 1));
		settings.getFormat().setPadding('_');
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderExtractionEnabled(false);
		settings.setMaxColumns(3);

		settings.setSkipTrailingCharsUntilNewline(true);


		FixedWidthParser parser = new FixedWidthParser(settings);
		List<String[]> rows = parser.parseAll(new StringReader("123"));
		assertEquals(rows.size(), 1);
		String[] row = rows.get(0);
		assertEquals(row.length, 3);
		assertEquals(row[0], "1");
		assertEquals(row[1], "2");
		assertEquals(row[2], "3");
	}

}
