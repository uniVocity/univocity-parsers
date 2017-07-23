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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/27
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_27 {

	@Headers(sequence = {"b", "x", "a"}, write = true)
	public static class AB {

		@Parsed
		public String a;

		@Parsed
		public boolean b;

		public AB() {

		}

		public AB(String a, boolean b) {
			this.a = a;
			this.b = b;
		}
	}

	@Test
	public void testWritingWithHeaderAnnotation() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		BeanWriterProcessor<AB> beanProcessor = new BeanWriterProcessor<AB>(AB.class);
		writerSettings.setRowWriterProcessor(beanProcessor);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, writerSettings);

		List<AB> rowsToWrite = new ArrayList<AB>();
		rowsToWrite.add(new AB("Line1", true));
		rowsToWrite.add(new AB("Line2", false));

		writer.processRecordsAndClose(rowsToWrite);

		assertEquals(out.toString(), "b,x,a\ntrue,,Line1\nfalse,,Line2\n");
	}

	@Test
	public void testParsingWithHeaderAnnotation() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("true,,Line1\nfalse,,Line2\n"));

		List<AB> beans = beanProcessor.getBeans();
		assertEquals(beans.size(), 2);

		assertEquals(beans.get(0).a, "Line1");
		assertEquals(beans.get(0).b, true);

		assertEquals(beans.get(1).a, "Line2");
		assertEquals(beans.get(1).b, false);
	}

	@Headers(sequence = {"a", "b"}, write = false)
	public static class AB2 extends AB {
		public AB2() {

		}

		public AB2(String a, boolean b) {
			super(a, b);
		}
	}

	@Test
	public void testWritingWithHeaderAnnotationInSubclass() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		BeanWriterProcessor<AB2> beanProcessor = new BeanWriterProcessor<AB2>(AB2.class);
		writerSettings.setRowWriterProcessor(beanProcessor);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, writerSettings);

		List<AB2> rowsToWrite = new ArrayList<AB2>();
		rowsToWrite.add(new AB2("Line1", true));
		rowsToWrite.add(new AB2("Line2", false));

		writer.processRecordsAndClose(rowsToWrite);

		assertEquals(out.toString(), "Line1,true\nLine2,false\n");
	}

	@Test
	public void testParsingWithHeaderAnnotationInSubclass() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB2> beanProcessor = new BeanListProcessor<AB2>(AB2.class);
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("Line1,true\nLine2,false\n"));

		List<AB2> beans = beanProcessor.getBeans();
		assertEquals(beans.size(), 2);

		assertEquals(beans.get(0).a, "Line1");
		assertEquals(beans.get(0).b, true);

		assertEquals(beans.get(1).a, "Line2");
		assertEquals(beans.get(1).b, false);
	}

	@Headers(sequence = {"x", "a", "b"}, write = false, extract = true)
	interface Header {
	}

	public static class AB3 implements Header {
		@Parsed
		public String a;

		@Parsed
		public boolean b;

		public AB3() {

		}

		public AB3(String a, boolean b) {
			this.a = a;
			this.b = b;
		}
	}

	@Test
	public void testWritingWithHeaderAnnotationInInterface() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		BeanWriterProcessor<AB3> beanProcessor = new BeanWriterProcessor<AB3>(AB3.class);
		writerSettings.setRowWriterProcessor(beanProcessor);

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, writerSettings);

		List<AB3> rowsToWrite = new ArrayList<AB3>();
		rowsToWrite.add(new AB3("Line1", true));
		rowsToWrite.add(new AB3("Line2", false));

		writer.processRecordsAndClose(rowsToWrite);

		assertEquals(out.toString(), ",Line1,true\n,Line2,false\n");
	}

	@Test
	public void testParsingWithHeaderAnnotationInInterface() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB3> beanProcessor = new BeanListProcessor<AB3>(AB3.class);
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.getFormat().setLineSeparator("\n");

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("x,a,b\n,Line1,true\n,Line2,false\n"));

		List<AB3> beans = beanProcessor.getBeans();
		assertEquals(beans.size(), 2);

		assertEquals(beans.get(0).a, "Line1");
		assertEquals(beans.get(0).b, true);

		assertEquals(beans.get(1).a, "Line2");
		assertEquals(beans.get(1).b, false);
	}
}
