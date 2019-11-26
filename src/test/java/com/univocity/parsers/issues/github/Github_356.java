/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
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

package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/356
 */
public class Github_356 {

	public static class A {
		@Parsed
		@Format(formats = "dd/MM/yyyy")
		public Date date;

		@Parsed
		public int id;
	}

	@Test
	public void testValidationAnnotation() {
		CsvParserSettings s = new CsvParserSettings();
		s.setLineSeparatorDetectionEnabled(true);
		BeanListProcessor<A> processor = new BeanListProcessor<A>(A.class);
		s.setProcessor(processor);

		final List<Object[]> errorDetails = new ArrayList<Object[]>();

		s.setProcessorErrorHandler(new RetryableErrorHandler<ParsingContext>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
				this.keepRecord();
			}
		});

		CsvParser p = new CsvParser(s);

		p.parse(new StringReader("" +
				"id,date\n" +
				"5,\"15,08,1983\""));

		List<A> beans = processor.getBeans();
		assertEquals(beans.size(), 1);

		assertEquals(beans.get(0).id, 5);
		assertNull(beans.get(0).date);
	}
}