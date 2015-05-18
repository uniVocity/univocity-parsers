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

import static org.testng.Assert.*;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;

/**
 *
 * From: https://github.com/uniVocity/univocity-parsers/issues/31
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_31 {

	public static enum Gender {
		MALE('M'),
		FEMALE('F');

		private final char code;

		Gender(char code) {
			this.code = code;
		}

		public char code() {
			return code;
		}

		public String getCode() {
			if (code == 'M') {
				return "MAL";
			} else {
				return "FEM";
			}
		}
	}

	public static class AB {

		@Parsed(index = 0)
		private Gender a;

		@Parsed(index = 1)
		private Gender b;

		@EnumOptions(customElement = "code")
		@Parsed(index = 2)
		private Gender c;

		@EnumOptions(customElement = "getCode")
		@Parsed(index = 3)
		private Gender d;

		public AB() {

		}
	}

	@Test
	public void testConversionToEnumByOrdinal() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("0,MALE,F,MAL\n1,FEMALE,M,FEM"));

		List<AB> beans = beanProcessor.getBeans();
		assertEquals(beans.get(0).a, Gender.MALE);
		assertEquals(beans.get(0).b, Gender.MALE);
		assertEquals(beans.get(0).c, Gender.FEMALE);
		assertEquals(beans.get(0).d, Gender.MALE);
		assertEquals(beans.get(1).a, Gender.FEMALE);
		assertEquals(beans.get(1).b, Gender.FEMALE);
		assertEquals(beans.get(1).c, Gender.MALE);
		assertEquals(beans.get(1).d, Gender.FEMALE);

	}
}
