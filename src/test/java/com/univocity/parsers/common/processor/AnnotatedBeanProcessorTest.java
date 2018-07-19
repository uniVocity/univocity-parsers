/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
import java.lang.annotation.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class AnnotatedBeanProcessorTest {

	String input = "date,amount,quantity,pending,comments,active\n"
			+ "10-oct-2001,555.999,1,yEs,?,n\n"
			+ "2001-10-10,,?,N,\"  \"\" something \"\"  \",true";

	@Trim
	@UpperCase
	@BooleanString(falseStrings = "N", trueStrings = {"TRUE", "Y"})
	@Parsed
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	@Target(value = {ElementType.FIELD})
	public @interface MetaBoolean {

	}

	public static class TestBean {

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

		@MetaBoolean
		Boolean active;

		@BooleanString(falseStrings = "0", trueStrings = "1")
		@Parsed
		Boolean other;
	}

	protected CsvParserSettings newCsvInputSettings() {
		return new CsvParserSettings();
	}

	@Test
	public void testAnnotatedBeanProcessor() {
		BeanListProcessor<TestBean> processor = new BeanListProcessor<TestBean>(TestBean.class);

		processor.convertAll(Conversions.toNull("", "?"));
		CsvParserSettings settings = newCsvInputSettings();
		settings.getFormat().setLineSeparator("\n");
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
	public void testAnnotatedBeanWithLessColumns() {
		BeanListProcessor<TestBean> processor = new BeanListProcessor<TestBean>(TestBean.class);
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\n");
		settings.setProcessor(processor);

		StringReader reader = new StringReader("active,other\n,1\n,,\ny,0");


		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<TestBean> beans = processor.getBeans();
		assertTrue(beans.get(0).other);
		assertNull(beans.get(1).other);
		assertFalse(beans.get(2).other);
	}

	@Test
	public void testAnnotatedBeanProcessorWithOneFieldOnly() {
		BeanListProcessor<TestBean> processor = new BeanListProcessor<TestBean>(TestBean.class);

		processor.convertAll(Conversions.toNull("", "?"));
		CsvParserSettings settings = newCsvInputSettings();
		settings.getFormat().setLineSeparator("\n");
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
		assertEquals(bean.quantity, Integer.valueOf(0));
		assertNull(bean.pending);
		assertNull(bean.commts);

		bean = beans.get(1);
		assertEquals(bean.amnt, null);
		assertEquals(bean.quantity, Integer.valueOf(0));
		assertNull(bean.pending);
		assertNull(bean.commts);
	}

	public static class Data {
		@Parsed(index = 0)
		public String value;

		@Parsed(index = 1)
		public String foo;

		@Nested
		MetaData metaData;

	}

	public static class MetaData {
		@Parsed(index = 1)
		public String title;
	}

	@Test
	public void testRepeatedIndexInAnnotation() {
		BeanListProcessor<Data> rowProcessor = new BeanListProcessor<Data>(Data.class);
		CsvParserSettings settings = new CsvParserSettings();
		settings.setProcessor(rowProcessor);
		settings.setLineSeparatorDetectionEnabled(true);

		CsvParser parser = new CsvParser(settings);
		parser.parseAll(new StringReader("a1,b1,c1\na2,b2,c2\na3,b3,c3"));

		List<Data> beans = rowProcessor.getBeans();
		for (int i = 1; i <= beans.size(); i++) {
			Data bean = beans.get(i - 1);
			assertEquals(bean.value, "a" + i);
			assertEquals(bean.foo, "b" + i);
			assertEquals(bean.metaData.title, "b" + i);
		}
	}
}
