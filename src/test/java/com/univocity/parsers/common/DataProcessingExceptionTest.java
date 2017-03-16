/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common;


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class DataProcessingExceptionTest {

	@Test
	public void testRestrictionOfValuesDisplayedInErrorMessage() {
		String[] headers = new String[]{"aaaa", "bbbb", "cccc"};

		DataProcessingException ex = new DataProcessingException("{x}boom: '{value}' is broken. Headers: {headers}");
		assertEquals(ex.getMessage(), "" +
				"{x}boom: '{value}' is broken. Headers: {headers}");

		ex.setValue("Mary had a little lamb");
		assertEquals(ex.getMessage(), "" +
				"{x}boom: 'Mary had a little lamb' is broken. Headers: {headers}\n" +
				"Internal state when error was thrown: value=Mary had a little lamb");

		ex.setErrorContentLength(14);
		assertEquals(ex.getMessage(), "" +
				"{x}boom: '... a little lamb' is broken. Headers: {headers}\n" +
				"Internal state when error was thrown: value=... a little lamb");

		ex.setValue("headers", headers);
		assertEquals(ex.getMessage(), "" +
				"{x}boom: '... a little lamb}' is broken. Headers: ...a, bbbb, cccc]\n" +
				"Internal state when error was thrown: value=... a little lamb");

		ex.setErrorContentLength(0);
		assertEquals(ex.getMessage(), "" +
				"{x}boom: '{value}' is broken. Headers: {headers}");
	}


	@Test
	public void testRestrictionOfValuesDisplayedInErrorMessageWhileParsing() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setErrorContentLength(0);
		BeanListProcessor<A> beanProcessor = new BeanListProcessor<A>(A.class);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AA,BB\nA,B\nC,D,\n1,true"));
		} catch (DataProcessingException e) {
			assertFalse(e.getMessage().contains("Unable to set value 'A'"));
		}
	}

	@Test
	public void testRestrictionOfValuesDisplayedInErrorMessageWhileWriting() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.setErrorContentLength(0);

		BeanWriterProcessor<A> beanProcessor = new BeanWriterProcessor<A>(A.class);
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setHeaders("AA", "BB");
		writerSettings.setRowWriterProcessor(beanProcessor);

		CsvWriter writer = new CsvWriter(new StringWriter(), writerSettings);
		try {
			writer.processRecordsAndClose(new Object[]{"I'm not a bean", null, new A(new Date())});
		} catch (DataProcessingException e) {
			assertFalse(e.getMessage().contains("I'm not a bean"));
		}
	}

	public static class A {
		@Parsed(field = "AA")
		private Date a;

		public A(){

		}

		public A(Date a) {
			this.a = a;

		}
	}
}