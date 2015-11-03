/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.processor;

import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class MasterDetailProcessorTest {

	private String totalsOnTop = "type,amount\n"
		+ "T,100\n"
		+ "50\n"
		+ "40\n"
		+ "10\n"
		+ "T,200\n"
		+ "170\n"
		+ "30";

	private String totalsAtBottom = "type,amount\n"
		+ "50\n"
		+ "40\n"
		+ "10\n"
		+ "T,100\n"
		+ "170\n"
		+ "30\n"
		+ "T,200";

	@DataProvider(name = "inputsAndProcessors")
	private Object[][] getInputsAndProcessors() {
		return new Object[][]{
			{totalsOnTop, getProcessor(true)},
			{totalsAtBottom, getProcessor(false)},
		};
	}

	@Test(dataProvider = "inputsAndProcessors")
	public void testMasterDetail(String input, MasterDetailListProcessor processor) {
		StringReader reader = new StringReader(input);

		CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<MasterDetailRecord> records = processor.getRecords();
		assertEquals(records.size(), 2);

		MasterDetailRecord record;
		record = records.get(0);

		assertEquals(sumItems(record), getTotal(record));

		record = records.get(1);
		assertEquals(sumItems(record), getTotal(record));
	}

	private Integer getTotal(MasterDetailRecord record) {
		Object[] masterRow = record.getMasterRow();
		return ((BigInteger) masterRow[1]).intValue();
	}

	private Integer sumItems(MasterDetailRecord record) {
		List<Object[]> rows = record.getDetailRows();

		Integer out = 0;

		for (Object[] row : rows) {
			out += (Integer) row[0];
		}
		return out;
	}

	private MasterDetailListProcessor getProcessor(boolean totalsOnTop) {
		final ObjectRowListProcessor items = new ObjectRowListProcessor();
		MasterDetailListProcessor totals = new MasterDetailListProcessor(totalsOnTop ? RowPlacement.TOP : RowPlacement.BOTTOM, items) {
			@Override
			protected boolean isMasterRecord(String[] row, ParsingContext context) {
				return "T".equals(row[0]);
			}
		};

		totals.convertIndexes(Conversions.toBigInteger()).set(1);
		items.convertIndexes(Conversions.toInteger()).set(0);

		return totals;
	}

}
