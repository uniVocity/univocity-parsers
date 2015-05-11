/*******************************************************************************
 * Copyright (c) 2014 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 ******************************************************************************/
package com.univocity.parsers.issues.support;

import static org.testng.Assert.*;

import java.io.*;
import java.math.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;

/**
 * Description: class cast exception caused when writing formatted value from annotated bean.
 * Root cause:
 * 			2 conversions were being executed instead of one: One for the formatter and the other for the default conversion.
 * 			The former produced a formatted String value as expected.
 * 			The latter tried to read a non-string value to convert to the default String representation, which was incorrect.
 * Resolution:
 * 			Skipping the use of the default conversion when a formatter is already in place.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Ticket_1 {

	public static class SimpleBean {
		@Format(formats = "#0.00")
		@Parsed(field = "number")
		private BigDecimal number;

		public BigDecimal getNumber() {
			return number;
		}

		public void setNumber(BigDecimal number) {
			this.number = number;
		}
	}

	private FixedWidthFieldLengths lengths;

	@BeforeClass
	private void initialize() {
		LinkedHashMap<String, Integer> fieldLengthMap = new LinkedHashMap<String, Integer>();
		fieldLengthMap.put("number", Integer.valueOf(30));
		lengths = new FixedWidthFieldLengths(fieldLengthMap);
	}

	@Test
	public void testBeanToFixedWidth() {
		SimpleBean simpleBean = new SimpleBean();
		simpleBean.setNumber(BigDecimal.ZERO);

		FixedWidthWriterSettings writeSettings = new FixedWidthWriterSettings(lengths);
		writeSettings.getFormat().setLineSeparator("\r\n");

		BeanWriterProcessor<SimpleBean> beanWriterProcessor = new BeanWriterProcessor<SimpleBean>(SimpleBean.class);
		writeSettings.setRowWriterProcessor(beanWriterProcessor);

		Writer w = new StringWriter();
		FixedWidthWriter writer = new FixedWidthWriter(w, writeSettings);
		writer.processRecord(simpleBean);
		writer.close();

		assertEquals(w.toString(), "0.00                          \r\n");
	}

	@Test
	public void testFixedWidthToBean() {
		FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(lengths);
		BeanListProcessor<SimpleBean> processor = new BeanListProcessor<SimpleBean>(SimpleBean.class);
		parserSettings.setRowProcessor(processor);
		parserSettings.setHeaderExtractionEnabled(false);
		FixedWidthParser parser = new FixedWidthParser(parserSettings);
		parser.parse(new StringReader("0.00"));

		assertEquals(processor.getBeans().size(), 1);
		assertEquals(processor.getBeans().get(0).getNumber(), new BigDecimal("0.00"));
	}
}
