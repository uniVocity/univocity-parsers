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
 * From: https://github.com/uniVocity/univocity-parsers/issues/116
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_116 {

	public static class AN {

		@Parsed(field="A", defaultNullRead="")
		private String a;
		@Parsed(field="B", defaultNullRead="N/A")
		private String b;
		@Parsed(field="C", defaultNullRead="etc")
		private String c;
	}

	@Test
	public void testAnnotationWithDefaultNullRead() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		BeanListProcessor<AN> beanProcessor = new BeanListProcessor<AN>(AN.class);
		parserSettings.setProcessor(beanProcessor);

		String input = "A,B,C\n1,2,3\n,,\n4,,\n";

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(input));

		List<AN> beans = beanProcessor.getBeans();
		assertEquals(beans.size(), 3);
		AN bean = beans.get(0);
		assertEquals(bean.a, "1");
		assertEquals(bean.b, "2");
		assertEquals(bean.c, "3");

		bean = beans.get(1);
		assertEquals(bean.a, "");
		assertEquals(bean.b, "N/A");
		assertEquals(bean.c, "etc");

		bean = beans.get(2);
		assertEquals(bean.a, "4");
		assertEquals(bean.b, "N/A");
		assertEquals(bean.c, "etc");
	}

	@Test
	public void testAnnotationWithDefaultNullReadAndMissingColumns() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		BeanListProcessor<AN> beanProcessor = new BeanListProcessor<AN>(AN.class);
		parserSettings.setProcessor(beanProcessor);

		String input = "A\n1\n,2";

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(input));

		List<AN> beans = beanProcessor.getBeans();
		assertEquals(beans.size(), 2);
		AN bean = beans.get(0);
		assertEquals(bean.a, "1");
		assertEquals(bean.b, "N/A");
		assertEquals(bean.c, "etc");

		bean = beans.get(1);
		assertEquals(bean.a, "");
		assertEquals(bean.b, "N/A");
		assertEquals(bean.c, "etc");
	}
}
