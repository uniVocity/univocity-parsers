/*******************************************************************************
 * Copyright 2016 Univocity Software Pty Ltd
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
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_111 {

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

		public boolean getB(){
			throw new IllegalStateException("I'm explosive!");
		}
	}

	@Test
	public void handleExceptionsAndContinueParsing() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(beanProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		final List<String> errors = new ArrayList<String>();

		parserSettings.setProcessorErrorHandler(new RetryableErrorHandler<ParsingContext>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				assertNotNull(context);
				errors.add(error.getColumnName() + "(" + error.getColumnIndex() + "):" + inputRow[error.getColumnIndex()]);

				if(error.getColumnIndex() == 0){
					setDefaultValue(50);
				}

				keepRecord();
			}
		});

		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AA,BB\nA,B\nC,D,\n1,true"));
		} catch (DataProcessingException e) {
			fail("Did not expect exception to be thrown here");
		}

		assertEquals(beanProcessor.getBeans().size(), 3);
		assertEquals(beanProcessor.getBeans().get(0).a, 50);
		assertEquals(beanProcessor.getBeans().get(0).b, false);
		assertEquals(beanProcessor.getBeans().get(1).a, 50);
		assertEquals(beanProcessor.getBeans().get(1).b, false);
		assertEquals(beanProcessor.getBeans().get(2).a, 1);
		assertEquals(beanProcessor.getBeans().get(2).b, true);

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

		writerSettings.setProcessorErrorHandler(new RetryableErrorHandler<ParsingContext>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				assertNull(context);
				errors.add(Arrays.toString(inputRow));

				if(error.getColumnIndex() == 1){
					setDefaultValue(false);
					keepRecord();
				}
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

		assertEquals(errors.size(), 2);
		assertEquals(errors.get(0), "[I'm not a bean]");
		assertEquals(errors.get(1), "[1, null]");
		assertEquals(output.toString(), "1,false\n");
	}

	@Test(expectedExceptions = DataProcessingException.class)
	public void dontHandleExceptionsStopParsing() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setProcessor(beanProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("AA,BB\nA,B\nC,D,\n1,true"));
	}


	@Test(expectedExceptions = DataProcessingException.class)
	public void dontHandleExceptionsAndStopWriting() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		BeanWriterProcessor<AB> beanProcessor = new BeanWriterProcessor<AB>(AB.class);
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setHeaders("AA", "BB");
		writerSettings.setRowWriterProcessor(beanProcessor);

		CsvWriter writer = new CsvWriter(new StringWriter(), writerSettings);
		writer.processRecordsAndClose(new Object[]{"I'm not a bean", null, new AB(1, true)});

	}
}
