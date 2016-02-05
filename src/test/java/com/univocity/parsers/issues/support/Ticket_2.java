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
package com.univocity.parsers.issues.support;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.math.*;
import java.text.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_2 {

	public static class SimpleBean {
		@Parsed(applyDefaultConversion = false)
		private BigDecimal number;

		public BigDecimal getNumber() {
			return number;
		}

		public void setNumber(BigDecimal number) {
			this.number = number;
		}
	}

	public static class SimplerBean {
		@Convert(conversionClass = MyNumericConversion.class, args = "#0.00")
		@Parsed
		private BigDecimal number;

		public BigDecimal getNumber() {
			return number;
		}

		public void setNumber(BigDecimal number) {
			this.number = number;
		}
	}

	public static class EvenSimplerBean {
		@Format(formats = "#0.00", options = "decimalSeparator=,")
		@Parsed
		private BigDecimal number;

		public BigDecimal getNumber() {
			return number;
		}

		public void setNumber(BigDecimal number) {
			this.number = number;
		}
	}

	public static class MyNumericConversion extends NumericConversion<BigDecimal> {

		public MyNumericConversion(String[] args) {
			super(args[0]);
		}

		private DecimalFormat formatter;

		@Override
		protected void configureFormatter(DecimalFormat formatter) {
			DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
			decimalFormatSymbols.setDecimalSeparator(',');
			formatter.setDecimalFormatSymbols(decimalFormatSymbols);
			this.formatter = formatter;
		}

		@Override
		public String revert(BigDecimal input) {
			return formatter.format(input);
		}

		@Override
		protected BigDecimal fromString(String input) {
			return new BigDecimal(input);
		}
	}

	private <T> FixedWidthWriterSettings getSettings(Class<T> beanClass) {
		LinkedHashMap<String, Integer> fieldLengthMap = new LinkedHashMap<String, Integer>();
		fieldLengthMap.put("number", Integer.valueOf(30));
		FixedWidthFields lengths = new FixedWidthFields(fieldLengthMap);
		FixedWidthWriterSettings writeSettings = new FixedWidthWriterSettings(lengths);
		writeSettings.getFormat().setLineSeparator("\r\n");
		BeanWriterProcessor<T> beanWriterProcessor = new BeanWriterProcessor<T>(beanClass);

		writeSettings.setRowWriterProcessor(beanWriterProcessor);
		return writeSettings;
	}

	private void writeAndValidate(FixedWidthWriterSettings settings, Object bean, String expectedOutput) {
		StringWriter w = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(w, settings);
		writer.processRecord(bean);
		writer.close();

		assertEquals(w.toString(), expectedOutput);
	}

	@Test
	public void testCustomConversionAppliedManually() throws Exception {
		SimpleBean simpleBean = new SimpleBean();
		simpleBean.setNumber(BigDecimal.ZERO);

		FixedWidthWriterSettings writeSettings = getSettings(SimpleBean.class);
		((BeanWriterProcessor<?>) writeSettings.getRowWriterProcessor()).convertFields(new MyNumericConversion(new String[]{"#0.00"})).add("number");

		writeAndValidate(writeSettings, simpleBean, "0,00                          \r\n");
	}

	@Test
	public void testCustomConversionAsAnnotation() throws Exception {
		SimplerBean simpleBean = new SimplerBean();
		simpleBean.setNumber(BigDecimal.ZERO);

		FixedWidthWriterSettings writeSettings = getSettings(SimplerBean.class);
		writeAndValidate(writeSettings, simpleBean, "0,00                          \r\n");
	}

	@Test
	public void testConversionWithOptions() throws Exception {
		EvenSimplerBean simpleBean = new EvenSimplerBean();
		simpleBean.setNumber(BigDecimal.ZERO);

		FixedWidthWriterSettings writeSettings = getSettings(EvenSimplerBean.class);
		writeAndValidate(writeSettings, simpleBean, "0,00                          \r\n");
	}
}
