/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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
import com.univocity.parsers.conversions.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/23
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_23 {

	public static class AB {

		@Parsed(index = 0)
		private boolean a;

		@Parsed(index = 1)
		private boolean b;

		public AB() {

		}
	}

	@Test
	public void testCaseInsensitiveBooleanConversion() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parseLine("false,true");
		parser.parseLine("TRUE,FALSE");
		parser.parseLine("faLse,True");
		parser.parseLine("tRUE,FAlsE");

		List<AB> beans = beanProcessor.getBeans();

		assertFalse(beans.isEmpty());
		assertEquals(beans.size(), 4);

		assertFalse(beans.get(0).a);
		assertTrue(beans.get(0).b);

		assertTrue(beans.get(1).a);
		assertFalse(beans.get(1).b);

		assertFalse(beans.get(2).a);
		assertTrue(beans.get(2).b);

		assertTrue(beans.get(3).a);
		assertFalse(beans.get(3).b);
	}

	public static class MyNegativeBooleanConversion extends ObjectConversion<Boolean> {
		@Override
		protected Boolean fromString(String input) {
			return !Boolean.valueOf(input.toLowerCase());
		}
	}

	public static class AB1 {
		@Convert(conversionClass = MyNegativeBooleanConversion.class)
		@Parsed(index = 0)
		private boolean a;

		@Convert(conversionClass = MyNegativeBooleanConversion.class)
		@Parsed(index = 1)
		private boolean b;

		public AB1() {

		}
	}

	@Test
	public void testCustomBooleanConversion() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB1> beanProcessor = new BeanListProcessor<AB1>(AB1.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parseLine("false,true");
		parser.parseLine("TRUE,FALSE");
		parser.parseLine("faLse,True");
		parser.parseLine("tRUE,FAlsE");

		List<AB1> beans = beanProcessor.getBeans();

		assertFalse(beans.isEmpty());
		assertEquals(beans.size(), 4);

		assertTrue(beans.get(0).a);
		assertFalse(beans.get(0).b);

		assertFalse(beans.get(1).a);
		assertTrue(beans.get(1).b);

		assertTrue(beans.get(2).a);
		assertFalse(beans.get(2).b);

		assertFalse(beans.get(3).a);
		assertTrue(beans.get(3).b);
	}
}
