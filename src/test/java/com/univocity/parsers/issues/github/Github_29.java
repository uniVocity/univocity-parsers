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
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/29
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_29 {
	public static class AB {

		@Parsed(field = "AA")
		private int a;

		@Parsed(field = "BB")
		private boolean b;

		public AB() {

		}

		public AB(int a, boolean b) {
			this.a = a;
			this.b = b;
		}
	}

	@Test
	public void handleExceptionsAndContinueParsing() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		final List<String> errors = new ArrayList<String>();

		parserSettings.setRowProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				assertNotNull(context);
				errors.add(error.getColumnName() + "(" + error.getColumnIndex() + "):" + inputRow[error.getColumnIndex()]);
			}
		});

		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AA,BB\nA,B\nC,D,\n1,true"));
		} catch (DataProcessingException e) {
			fail("Did not expect exception to be thrown here");
		}

		assertEquals(beanProcessor.getBeans().size(), 1);
		assertEquals(beanProcessor.getBeans().get(0).a, 1);
		assertEquals(beanProcessor.getBeans().get(0).b, true);

		assertEquals(errors.size(), 4);
		assertEquals(errors.get(0), "AA(0):A");
		assertEquals(errors.get(1), "BB(1):B");
		assertEquals(errors.get(2), "AA(0):C");
		assertEquals(errors.get(3), "BB(1):D");
	}

	@Test
	public void handleExceptionsAndContinueWriting() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		BeanWriterProcessor<AB> beanProcessor = new BeanWriterProcessor<AB>(AB.class);
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setHeaders("AA", "BB");
		writerSettings.setRowWriterProcessor(beanProcessor);

		final List<String> errors = new ArrayList<String>();

		writerSettings.setRowProcessorErrorHandler(new RowProcessorErrorHandler() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				assertNull(context);
				errors.add(Arrays.toString(inputRow));
			}
		});

		StringWriter output = new StringWriter();

		CsvWriter writer = new CsvWriter(output, writerSettings);
		try {
			writer.processRecordsAndClose(new Object[]{"I'm not a bean", null, new AB(1, true)});
		} catch (DataProcessingException e) {
			e.printStackTrace();
			fail("Did not expect exception to be thrown here");
		}

		assertEquals(errors.size(), 1);
		assertEquals(errors.get(0), "[I'm not a bean]");
		assertEquals(output.toString(), "1,true\n");
	}
}
