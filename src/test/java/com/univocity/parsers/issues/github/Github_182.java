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

import com.univocity.parsers.*;
import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/182
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_182 extends ParserTestCase {

	@Test
	public void testHeaderExtraction() {
		final String testValue = new StringBuilder()
				.append("Name      Surname        Age\n")
				.append("John      Smith          25 \n")
				.append("Richard   Corrington     45 \n")
				.append("Steve     Mathews        30 \n")
				.toString();

		final BeanListProcessor<TestDTO> rowProcessor = new BeanListProcessor<TestDTO>(TestDTO.class);
		final FixedWidthFields fields = new FixedWidthFields()
				.addField("name", 10)
				.addField("surname", 15)
				.addField("age", 3);

		final FixedWidthParserSettings parserSettings = new FixedWidthParserSettings(fields);
		parserSettings.getFormat().setPadding(' ');
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		new FixedWidthParser(parserSettings).parse(new StringReader(testValue));

		final List<TestDTO> records = rowProcessor.getBeans();
		assertEquals(records.size(), 3);
	}

	public static class TestDTO {
		@Parsed(index = 0)
		private String name;
		@Parsed(index = 1)
		private String surname;
		@Parsed(index = 2)
		private Integer age;

		public TestDTO() {
		}

		@Override
		public String toString() {
			return name + " " + surname + " - " + age;
		}

	}
}
