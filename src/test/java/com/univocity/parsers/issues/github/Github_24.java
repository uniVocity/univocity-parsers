/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
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
 * From: https://github.com/univocity/univocity-parsers/issues/24
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_24 {

	public static class AB {

		@Parsed(field = "AA")
		private String a;

		@Parsed(field = "BB")
		private String b;

		public AB() {

		}
	}

	/**
	 * As of https://github.com/univocity/univocity-parsers/issues/48, this test is no longer valid by default
	 *
	 * Originally the parser would force an exact match of headers declared in the bean and in the input.
	 * With the changes introduced in Github_48, the parsing process became less strict to allow
	 * beans to be loaded using inputs with varying number of columns. When a column is not found in the input
	 * the values attributed to the bean will be considered null. If conversions are defined for that field,
	 * they will be applied over the null value.
	 *
	 * To keep getting errors when headers are not fully matched, enable the strict header validation using
	 * {@link BeanProcessor#setStrictHeaderValidationEnabled(boolean)}
	 */
	@Test
	public void ensureExceptionsAreThrown() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		beanProcessor.setStrictHeaderValidationEnabled(true);
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AAAA,BB\nA,B\nC,D"));
			fail("Expected exception to be thrown here");
		} catch (TextParsingException e) {
			//success!!
		}
	}

	@Test
	public void ensureExceptionsAreNotThrownWhenAColumnDoesntMatch() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.getFormat().setLineSeparator("\n");
		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AAAA,BB\nA,B\nC,D"));
		} catch (TextParsingException e) {
			fail("Not expecting exceptions to be thrown here");
		}

		List<AB> beans = beanProcessor.getBeans();
		assertFalse(beans.isEmpty());
		assertNull(beans.get(0).a);
		assertEquals(beans.get(0).b, "B");

		assertNull(beans.get(1).a);
		assertEquals(beans.get(1).b, "D");
	}
}
