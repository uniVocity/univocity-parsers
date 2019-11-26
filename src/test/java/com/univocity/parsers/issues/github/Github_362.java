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

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/362
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_362 {

	@Test
	public void testCommentCharWriting() {
		StringWriter sw = new StringWriter();
		{
			CsvWriterSettings writerSettings = new CsvWriterSettings();
			CsvWriter writer = new CsvWriter(sw, writerSettings);
			writer.writeRow(new String[]{"#field1", "field2", "field3"});
			writer.close();
		}
		StringReader sr = new StringReader(sw.toString());
		{
			CsvParserSettings parserSettings = new CsvParserSettings();
			CsvParser parser = new CsvParser(parserSettings);
			List<String[]> rows = parser.parseAll(sr);
			String[] row = rows.get(0);
			assertEquals(row[0], "#field1");
		}
	}
}
