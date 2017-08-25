/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import com.univocity.parsers.examples.*;
import org.testng.annotations.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/184
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_184 extends Example {

	@Test
	public void testContextWithRoutineIterateBeans() {

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");

		CsvRoutines routines = new CsvRoutines(parserSettings);

		IterableResult<TestBean, ParsingContext> iterable = routines.iterate(TestBean.class, getReader("/examples/bean_test.csv"));
		for (TestBean bean : iterable) {
			println(iterable.getContext().currentParsedContent());
			println(bean);
		}

		printAndValidate();
	}

	@Test
	public void testContextWithRoutineIterator() {

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");

		CsvRoutines routines = new CsvRoutines(parserSettings);

		ResultIterator<TestBean, ParsingContext> iterator = routines.iterate(TestBean.class, getReader("/examples/bean_test.csv")).iterator();

		assertTrue(iterator.hasNext());
		String content = iterator.getContext().currentParsedContent();

		assertTrue(iterator.hasNext());
		assertEquals(iterator.getContext().currentParsedContent(), content);

		assertNotNull(iterator.next());
		assertNotNull(iterator.next());

		assertFalse(iterator.hasNext());
		assertNull(iterator.next());
	}

}
