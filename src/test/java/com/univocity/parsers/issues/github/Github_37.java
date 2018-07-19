/*
 * Copyright (c) 2015. Univocity Software Pty Ltd
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

import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Github_37 {

	@Test
	public void testMapWritingWithRowProcessor() {
		CsvWriterSettings settings = new CsvWriterSettings();

		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		processor.convertType(Integer.class, Conversions.formatToNumber("$##0.00"));
		processor.convertType(Boolean.class, Conversions.toBoolean("Y", "N"));

		settings.setRowWriterProcessor(processor);

		settings.getFormat().setLineSeparator("\n");

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, settings);

		Map<String, Object[]> rows = new TreeMap<String, Object[]>();
		rows.put("A", new Object[]{1, false});
		rows.put("B", new Object[]{true, (short)2, null});
		rows.put("C", new Object[]{null, true, true, 0});

		// We are writing column by column, i.e. rows should be printed as:
		// 1, true, null
		// false, 2, true
		// null, null, true
		// null, null, 0

		writer.processObjectRecordsAndClose(rows);

		assertEquals("$1.00,Y,\n" +
				"N,2,Y\n" +
				",,Y\n" +
				",,$0.00\n", out.toString());
	}
}

