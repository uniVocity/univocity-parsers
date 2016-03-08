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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class AnnotatedBeanProcessorTest {

	String input = "date,amount,quantity,pending,comments\n"
		+ "10-oct-2001,555.999,1,yEs,?\n"
		+ "2001-10-10,,?,N,\"  \"\" something \"\"  \"";

	static class TestBean {

		@Parsed(defaultNullRead = "0")
		Integer quantity;

		@Trim(length = 8)
		@LowerCase
		@Parsed(index = 4)
		String commts;

		@Parsed(field = "amount")
		BigDecimal amnt;

		@Trim
		@LowerCase
		@BooleanString(falseStrings = {"no", "n", "null"}, trueStrings = {"yes", "y"})
		@Parsed
		Boolean pending;
	}

	protected CsvParserSettings newCsvInputSettings() {
		return new CsvParserSettings();
	}

	@Test
	public void testAnnotatedBeanProcessor() {
		BeanListProcessor<TestBean> processor = new BeanListProcessor<TestBean>(TestBean.class);

		processor.convertAll(Conversions.toNull("", "?"));
		CsvParserSettings settings = newCsvInputSettings();
		settings.excludeIndexes(0);

		StringReader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<TestBean> beans = processor.getBeans();
		assertEquals(beans.size(), 2);

		TestBean bean;
		bean = beans.get(0);
		assertEquals(bean.amnt, new BigDecimal("555.999"));
		assertEquals(bean.quantity, (Object) 1);
		assertTrue(bean.pending);
		assertNull(bean.commts);

		bean = beans.get(1);
		assertEquals(bean.amnt, null);
		assertEquals(bean.quantity, (Object) 0);
		assertFalse(bean.pending);
		assertEquals(bean.commts, "\" someth"); // trimmed to 8 characters
	}

	@Test
	public void testAnnotatedBeanProcessorWithOneFieldOnly() {
		BeanListProcessor<TestBean> processor = new BeanListProcessor<TestBean>(TestBean.class);

		processor.convertAll(Conversions.toNull("", "?"));
		CsvParserSettings settings = newCsvInputSettings();
		settings.setColumnReorderingEnabled(true);
		settings.selectIndexes(1);

		StringReader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<TestBean> beans = processor.getBeans();
		assertEquals(beans.size(), 2);

		TestBean bean;
		bean = beans.get(0);
		assertEquals(bean.amnt, new BigDecimal("555.999"));
		assertNull(bean.quantity);
		assertNull(bean.pending);
		assertNull(bean.commts);

		bean = beans.get(1);
		assertEquals(bean.amnt, null);
		assertNull(bean.quantity);
		assertNull(bean.pending);
		assertNull(bean.commts);
	}

}
