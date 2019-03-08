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


import com.univocity.parsers.common.*;
import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static java.lang.Boolean.*;
import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/314
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_314 {
	@Test
	public void testContextOnIterable() {

		CsvParserSettings settings = new CsvParserSettings();
		settings.detectFormatAutomatically();
		settings.setHeaderExtractionEnabled(true);
		CsvParser parser = new CsvParser(settings);
		IterableResult<Record, ParsingContext> iter = parser.iterateRecords(new StringReader("a,b,c\n1,2,3\n4,5,6\n"));
		iter.getContext().parsedHeaders(); // this throws a NullPointerException

		Iterator<Record> it = iter.iterator();

		Record r = it.next();
		assertTrue(r.getMetaData().containsColumn("C"));

		r = it.next();
		assertTrue(r.getMetaData().containsColumn("c"));

		assertNull(it.next());
	}
}
