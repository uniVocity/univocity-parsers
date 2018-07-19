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
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/30
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_30 {

	public static class AB {

		@Parsed(index = 0)
		private Long a;

		@Parsed(index = 1)
		private long b;

		public AB() {

		}
	}

	@Test
	public void testConversionToLong() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("1,2\n3,4"));

		List<AB> beans = beanProcessor.getBeans();
		assertEquals(beans.get(0).a, new Long(1));
		assertEquals(beans.get(0).b, 2L);
		assertEquals(beans.get(1).a, new Long(3));
		assertEquals(beans.get(1).b, 4L);

	}
}
