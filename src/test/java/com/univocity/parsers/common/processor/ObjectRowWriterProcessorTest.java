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
import org.testng.annotations.*;

import java.math.*;
import java.text.*;
import java.util.*;

import static com.univocity.parsers.conversions.Conversions.*;
import static org.testng.Assert.*;

public class ObjectRowWriterProcessorTest {

	private final SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);

	private final String[] headers = "date,amount,quantity,pending,comments".split(",");

	private final Object[][] values;

	{
		try {
			values = new Object[][]{
					{format.parse("10-oct-2001"), new BigDecimal("555.999"), 1, true, null},
					{format.parse("11-oct-2001"), null, null, false, "  something  "}
			};
		} catch (ParseException e) {
			throw new IllegalStateException(e);
		}
	}

	private ObjectRowWriterProcessor newProcessorWithFieldNames() {
		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		Conversion<?, ?> toNull = toNull("?");

		processor.convertFields(toNull).set("quantity", "amount");

		processor.convertFields(toDate("dd-MM-yyyy")).set("date");
		processor.convertFields(toBigDecimal()).set("amount");
		processor.convertFields(toInteger()).set("quantity");
		processor.convertFields(toBoolean("y", "n")).set("pending");
		processor.convertFields(trim(), toNull).set("comments");

		return processor;
	}

	private ObjectRowWriterProcessor newProcessorWithFieldIndexes() {
		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		Conversion<?, ?> toNull = toNull("?");

		processor.convertIndexes(toNull).set(1, 2);

		processor.convertIndexes(toDate("dd-MM-yyyy")).set(0);
		processor.convertIndexes(toBigDecimal()).set(1);
		processor.convertIndexes(toInteger()).set(2);
		processor.convertIndexes(toBoolean("y", "n")).set(3);
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
	public void conversionTest(ObjectRowWriterProcessor processor) {
		Object[] row;

		row = processor.write(values[0], headers, null);
		assertEquals(row[0], "10-10-2001");
		assertEquals(row[1], "555.999");
		assertEquals(row[2], "1");
		assertEquals(row[3], "y");
		assertEquals(row[4], "?");

		row = processor.write(values[1], headers, null);
		assertEquals(row[0], "11-10-2001");
		assertEquals(row[1], "?");
		assertEquals(row[2], "?");
		assertEquals(row[3], "n");
		assertEquals(row[4], "something"); // trimmed
	}

	@Test
	public void testTypeConversion() {
		ObjectRowWriterProcessor processor = new ObjectRowWriterProcessor();
		processor.convertType(Boolean.class, Conversions.string(), Conversions.toUpperCase());
		processor.convertType(String.class, Conversions.toUpperCase(), Conversions.trim());
		processor.convertType(Date.class, Conversions.toDate("yyyy-MMM-dd"), Conversions.toUpperCase());

		Object[] row;
		row = processor.write(values[0], headers, null);
		assertEquals(row[0], "2001-OCT-10");
		assertEquals(row[1], new BigDecimal("555.999"));
		assertEquals(row[2], 1);
		assertEquals(row[3], "TRUE");
		assertEquals(row[4], null);

		row = processor.write(values[1], headers, null);
		assertEquals(row[0], "2001-OCT-11");
		assertEquals(row[1], null);
		assertEquals(row[2], null);
		assertEquals(row[3], "FALSE");
		assertEquals(row[4], "SOMETHING"); // trimmed

	}
}
