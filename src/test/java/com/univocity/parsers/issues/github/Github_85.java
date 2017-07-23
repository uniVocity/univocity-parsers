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

import com.univocity.parsers.common.record.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;
import java.io.*;
import java.util.*;

public class Github_85 {

	public enum E{
		A,
		B,
		C
	}

	static final String input = "" +
			"A, B, X, C\n" +
			"value1, value2, useless value, value3\n" +
			"value4, value5, useless value, value6";

	@Test
	public void testRecordsWithFieldSelectionAndEnum() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setHeaderExtractionEnabled(true);
		settings.selectFields((Enum[]) E.values());
		List<Record> records = new CsvParser(settings).parseAllRecords(new StringReader(input));

		Record r1 = records.get(0);
		assertEquals(r1.getString(E.A), "value1");
		assertEquals(r1.getString(E.B), "value2");
		assertEquals(r1.getString(E.C), "value3");

		Record r2 = records.get(1);
		assertEquals(r2.getString(E.A), "value4");
		assertEquals(r2.getString(E.B), "value5");
		assertEquals(r2.getString(E.C), "value6");

	}
}
