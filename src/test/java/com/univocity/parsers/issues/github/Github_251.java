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
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;


/**
 * From: https://github.com/univocity/univocity-parsers/issues/251
 *
 * @author camerondavison
 */
public class Github_251 {

	public static class Positive implements Validator<Integer> {
		@Override
		public String validate(Integer value) {
			if (value < 0) {
				return "value must be positive or zero";
			}
			return null;
		}
	}

	public static class A {
		@Parsed(index = 0)
		@Validate(nullable = true)
		public String nullNotBlank;

		@Parsed(index = 1)
		@Validate(oneOf = {"a", "b"})
		public String aOrB;

		@Parsed(index = 2)
		@Validate(oneOf = {"a"}, noneOf = "b")
		public String aNotB;

		@Parsed(index = 3)
		@Validate(nullable = true, oneOf = {"a", "b"})
		public String aOrBOrNull;

		@Parsed(index = 4)
		@Validate(allowBlanks = true, matches = "^[^\\d\\s]+$")
		//yet regex disallows whitespace
		public String noDigits;

		@Parsed(index = 5)
		@Validate(validators = Positive.class)
		public int positive;
	}

	@Test
	public void testValidationAnnotation() {
		CsvParserSettings s = new CsvParserSettings();
		BeanListProcessor<A> processor = new BeanListProcessor<A>(A.class);
		s.setProcessor(processor);
		s.setHeaderExtractionEnabled(false);

		final List<Object[]> errorDetails = new ArrayList<Object[]>();

		s.setProcessorErrorHandler(new RetryableErrorHandler<ParsingContext>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, ParsingContext context) {
//				System.out.println(error.getMessage());
				errorDetails.add(new Object[]{error.getRecordNumber(), error.getColumnIndex(), error.getValue()});
				this.keepRecord();
			}
		});

		CsvParser p = new CsvParser(s);

		p.parse(new StringReader("" +
				",a,a,\" \",3z,4\n" +
				"\" \",c,b,,a b,-5"));

		assertEquals(errorDetails.size(), 7);
		assertEquals(errorDetails.get(0), new Object[]{1L, 3, " "});
		assertEquals(errorDetails.get(1), new Object[]{1L, 4, "3z"});
		assertEquals(errorDetails.get(2), new Object[]{2L, 0, " "});
		assertEquals(errorDetails.get(3), new Object[]{2L, 1, "c"});
		assertEquals(errorDetails.get(4), new Object[]{2L, 2, "b"});
		assertEquals(errorDetails.get(5), new Object[]{2L, 4, "a b"});
		assertEquals(errorDetails.get(6), new Object[]{2L, 5, -5});

		List<A> beans = processor.getBeans();
		assertEquals(beans.size(), 2);

		assertEquals(beans.get(1).aNotB, null);
		assertEquals(beans.get(1).positive, 0);
	}
}