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

package com.univocity.parsers.common.processor;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.util.*;

import static org.testng.Assert.*;

public class MultiBeanListProcessorTest extends AnnotatedBeanProcessorTest{

	public static class AmountBean {
		@Trim
		@UpperCase
		@Parsed(index = 4)
		String commts;

		@Parsed(field = "amount")
		BigDecimal amnt;
	}

	public static class QuantityBean {

		@Parsed(defaultNullRead = "-1")
		Integer quantity;

		@Trim
		@LowerCase
		@BooleanString(falseStrings = {"no", "n", "null"}, trueStrings = {"yes", "y"})
		@Parsed
		Boolean pending;
	}

	public static class BrokenBean {
		@Parsed(index = 4)
		String commts;

		@Parsed
		int quantity;

		public int getQuantity() {
			return this.quantity;
		}

		public void setQuantity(int quantity) {
			if(quantity == 0) {
				throw new NullPointerException("throwing error on purpose");
			}
			this.quantity = quantity;
		}
	}


	@Test
	public void testMultiBeanProcessor() {
		MultiBeanListProcessor processor = new MultiBeanListProcessor(TestBean.class, AmountBean.class, QuantityBean.class, BrokenBean.class);

		processor.convertAll(Conversions.toNull("", "?"));

		CsvParserSettings settings = newCsvInputSettings();
		settings.setRowProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				assertEquals(context.currentRecord(), 2L);
			}
		});
		settings.excludeIndexes(0);

		StringReader reader = new StringReader(input);
		settings.setHeaderExtractionEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);

		CsvParser parser = new CsvParser(settings);
		parser.parse(reader);

		List<TestBean> testBeans = processor.getBeans(TestBean.class);
		List<AmountBean> amountBeans = processor.getBeans(AmountBean.class);
		List<QuantityBean> quantityBeans = processor.getBeans(QuantityBean.class);
		List<BrokenBean> brokenBeans = processor.getBeans(BrokenBean.class);
		assertEquals(testBeans.size(), 2);
		assertEquals(amountBeans.size(), 2);
		assertEquals(quantityBeans.size(), 2);
		assertEquals(brokenBeans.size(), 2);

		TestBean testBean;
		AmountBean amountBean;
		QuantityBean quantityBean;
		BrokenBean brokenBean;

		testBean = testBeans.get(0);
		amountBean = amountBeans.get(0);
		quantityBean = quantityBeans.get(0);
		brokenBean = brokenBeans.get(0);

		assertEquals(testBean.amnt, new BigDecimal("555.999"));
		assertNull(testBean.commts);
		assertEquals(testBean.quantity, (Object) 1);
		assertTrue(testBean.pending);
		assertEquals(brokenBean.quantity, 1);
		assertNull(brokenBean.commts);

		assertEquals(amountBean.amnt, new BigDecimal("555.999"));
		assertNull(amountBean.commts);
		assertEquals(quantityBean.quantity, (Object) 1);
		assertTrue(quantityBean.pending);

		testBean = testBeans.get(1);
		amountBean = amountBeans.get(1);
		quantityBean = quantityBeans.get(1);
		assertNull(brokenBeans.get(1)); //Second row generated a NullPointerException and no bean is generated here

		assertEquals(testBean.amnt, null);
		assertEquals(testBean.quantity, (Object) 0);
		assertFalse(testBean.pending);
		assertEquals(testBean.commts, "\" someth");

		assertEquals(amountBean.amnt, null);
		assertEquals(amountBean.commts, "\" SOMETHING \""); //upper cased
		assertEquals(quantityBean.quantity, Integer.valueOf(-1));
		assertFalse(quantityBean.pending);


	}
}
