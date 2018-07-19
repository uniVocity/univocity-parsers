/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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
package com.univocity.parsers.issues.support;

import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;
import java.util.Map.*;

import static org.testng.Assert.*;

public class Ticket_6 {

	@Test
	public void testWriteStringValuesToRowOnDemand() {

		Map<List<String>, Integer> input = new LinkedHashMap<List<String>, Integer>();
		input.put(Arrays.asList("A", "B"), 1);
		input.put(Arrays.asList("C", "F"), 2);
		input.put(Arrays.asList("D", "M"), 3);

		StringWriter output = new StringWriter();
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		CsvWriter writer = new CsvWriter(output, writerSettings);

		for (Entry<List<String>, Integer> entry : input.entrySet()) {
			writer.addStringValues(entry.getKey());
			writer.addValue(entry.getValue());
			writer.writeValuesToRow();
		}
		writer.close();

		String result = output.toString();

		assertEquals(result, "A,B,1\nC,F,2\nD,M,3\n");
	}
}
