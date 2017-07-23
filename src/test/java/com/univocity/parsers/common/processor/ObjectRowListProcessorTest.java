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

import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static com.univocity.parsers.conversions.Conversions.*;
import static org.testng.Assert.*;

public class ObjectRowListProcessorTest {

	protected CsvParserSettings newCsvInputSettings() {
		CsvParserSettings out = new CsvParserSettings();
		out.getFormat().setLineSeparator("\n");
		return out;
	}

	private String[] valuesForTrue = new String[]{"yes", "y"};
	private String[] valuesForFalse = new String[]{"no", "n", null};

	private String input = "date,amount,quantity,pending,comments\n"
		+ "10-oct-2001,555.999,1,yEs,?\n"
		+ "2001-10-10,,?,N,\"  \"\" something \"\"  \"";

	private List<Object[]> process(String input, ObjectRowListProcessor processor, CsvParserSettings settings) {
		StringReader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<Object[]> rows = processor.getRows();
		return rows;
	}

	private ObjectRowListProcessor newProcessorWithFieldNames() {
		ObjectRowListProcessor processor = new ObjectRowListProcessor();
		Conversion<?, ?> toNull = toNull("", "?");

		processor.convertFields(toNull).set("quantity", "amount");

		processor.convertFields(toCalendar("dd-MMM-yyyy", "yyyy-MM-dd")).set("date");
		processor.convertFields(toBigDecimal()).set("amount");
		processor.convertFields(toInteger()).set("quantity");
		processor.convertFields(toLowerCase(), toBoolean(valuesForTrue, valuesForFalse)).set("pending");
		processor.convertFields(trim(), toNull).set("comments");

		return processor;
	}

	private ObjectRowListProcessor newProcessorWithFieldIndexes() {
		ObjectRowListProcessor processor = new ObjectRowListProcessor();
		Conversion<?, ?> toNull = toNull("", "?");

		processor.convertIndexes(toNull).set(1, 2);

		processor.convertIndexes(toCalendar("dd-MMM-yyyy", "yyyy-MM-dd")).set(0);
		processor.convertIndexes(toBigDecimal()).set(1);
		processor.convertIndexes(toInteger()).set(2);
		processor.convertIndexes(toLowerCase(), toBoolean(valuesForTrue, valuesForFalse)).set(3);
		processor.convertIndexes(trim(), toNull).set(4);

		return processor;
	}

	@DataProvider(name = "processors")
	Object[][] getProcessors() {
		return new Object[][]{
			{newProcessorWithFieldNames()},
			{newProcessorWithFieldIndexes()}
		};
	}

	@Test(dataProvider = "processors")
	public void conversionTest(ObjectRowListProcessor processor) {
		process(input, processor, newCsvInputSettings());
		List<Object[]> rows = processor.getRows();
		assertEquals(rows.size(), 2);

		Calendar date = new GregorianCalendar(2001, Calendar.OCTOBER, 10);

		Object[] row = rows.get(0);
		assertEquals(row[0], date);
		assertEquals(row[1], new BigDecimal("555.999"));
		assertEquals(row[2], 1);
		assertEquals(row[3], true);
		assertNull(row[4]);

		row = rows.get(1);
		assertEquals(row[0], date);
		assertEquals(row[1], null);
		assertEquals(row[2], null);
		assertEquals(row[3], false);
		assertEquals(row[4], "\" something \""); // trimmed
	}

	@Test(dataProvider = "processors")
	public void conversionTestOnSelectedColumnsWithReordering(ObjectRowListProcessor processor) {
		CsvParserSettings settings = newCsvInputSettings();
		settings.setColumnReorderingEnabled(true);
		settings.selectIndexes(1, 0, 3);
		settings.getFormat().setLineSeparator("\n");

		process(input, processor, settings);
		List<Object[]> rows = processor.getRows();
		assertEquals(rows.size(), 2);

		Calendar date = new GregorianCalendar(2001, Calendar.OCTOBER, 10);

		Object[] row = rows.get(0);
		assertEquals(row[0], new BigDecimal("555.999"));
		assertEquals(row[1], date);
		assertEquals(row[2], true);

		row = rows.get(1);
		assertEquals(row[0], null);
		assertEquals(row[1], date);
		assertEquals(row[2], false);
	}

	@Test(dataProvider = "processors")
	public void conversionTestOnSelectedColumnsWithoutColumnReordering(ObjectRowListProcessor processor) {

		CsvParserSettings settings = newCsvInputSettings();
		settings.selectIndexes(1, 0, 3);
		settings.setColumnReorderingEnabled(false);

		process(input, processor, settings);
		List<Object[]> rows = processor.getRows();
		assertEquals(rows.size(), 2);

		Calendar date = new GregorianCalendar(2001, Calendar.OCTOBER, 10);

		Object[] row = rows.get(0);
		assertEquals(row[0], date);
		assertEquals(row[1], new BigDecimal("555.999"));
		assertNull(row[2]);
		assertEquals(row[3], true);
		assertNull(row[4]);

		row = rows.get(1);
		assertEquals(row[0], date);
		assertEquals(row[1], null);
		assertNull(row[2]);
		assertEquals(row[3], false);
		assertNull(row[2]);
	}
}
