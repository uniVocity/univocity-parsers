/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_5 {

	@Test
	public void testMasterDetailWontIncludeRowsAboveMasterRow() {

		String input = "A,B\n" +
			"MASTER,m1\n" +
			"child1,1\n" +
			"child2,2\n" +
			"MASTER,m2\n" +
			"child,1";

		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");

		MasterDetailListProcessor processor = new MasterDetailListProcessor(new ObjectRowListProcessor()) {
			@Override
			protected boolean isMasterRecord(String[] row, ParsingContext context) {
				return "MASTER".equals(row[0]);
			}
		};
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader(input));

		List<MasterDetailRecord> masterRecords = processor.getRecords();
		assertEquals(masterRecords.size(), 2);

		assertEquals(masterRecords.get(0).getMasterRow(), new Object[]{"MASTER", "m1"});
		assertEquals(masterRecords.get(0).getDetailRows().size(), 2);
		assertEquals(masterRecords.get(0).getDetailRows().get(0), new Object[]{"child1", "1"});
		assertEquals(masterRecords.get(0).getDetailRows().get(1), new Object[]{"child2", "2"});

		assertEquals(masterRecords.get(1).getMasterRow(), new Object[]{"MASTER", "m2"});
		assertEquals(masterRecords.get(1).getDetailRows().size(), 1);
		assertEquals(masterRecords.get(1).getDetailRows().get(0), new Object[]{"child", "1"});
	}
}
