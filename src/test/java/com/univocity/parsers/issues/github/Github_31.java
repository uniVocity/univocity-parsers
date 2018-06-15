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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

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
		FEMALE('F'),
		UNSPECIFIED('U');

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
			} else if (code == 'F') {
				return "FEM";
			} else {
				return "UNS";
			}
		}

		public static Gender fromCode(String code) {
			if (code == null) {
				return UNSPECIFIED;
			}
			if (code.toLowerCase().startsWith("m")) {
				return MALE;
			}
			if (code.toLowerCase().startsWith("f")) {
				return FEMALE;
			}
			return UNSPECIFIED;
		}
	}

	public static class AB {

		@Parsed(index = 0)
		private Gender a;

		@Parsed(index = 1)
		private Gender b;

		@EnumOptions(customElement = "code")
		@Parsed(index = 2, defaultNullRead = "UNSPECIFIED", defaultNullWrite = "U")
		private Gender c;

		@EnumOptions(customElement = "getCode")
		@Parsed(index = 3)
		private Gender d;

		@EnumOptions(customElement = "fromCode")
		@Parsed(index = 4)
		private Gender e;

		public AB() {

		}
	}

	@Test
	public void testConversionToEnumAndBack() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.setRowProcessor(beanProcessor);

		String input = "0,MALE,,MAL,m\n1,FEMALE,M,FEM,Foo";

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(input));

		List<AB> beans = beanProcessor.getBeans();
		assertEquals(beans.get(0).a, Gender.MALE);
		assertEquals(beans.get(0).b, Gender.MALE);
		assertEquals(beans.get(0).c, Gender.UNSPECIFIED);
		assertEquals(beans.get(0).d, Gender.MALE);
		assertEquals(beans.get(0).e, Gender.MALE);
		assertEquals(beans.get(1).a, Gender.FEMALE);
		assertEquals(beans.get(1).b, Gender.FEMALE);
		assertEquals(beans.get(1).c, Gender.MALE);
		assertEquals(beans.get(1).d, Gender.FEMALE);
		assertEquals(beans.get(1).e, Gender.FEMALE);

		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setRowWriterProcessor(new BeanWriterProcessor<AB>(AB.class));

		beans.get(0).c = null;

		StringWriter out = new StringWriter();
		CsvWriter writer = new CsvWriter(out, writerSettings);
		writer.processRecordsAndClose(beans);

		String result = out.toString();
		assertEquals(result, "MALE,MALE,U,MALE,MALE\nFEMALE,FEMALE,MALE,FEMALE,FEMALE\n");
	}
}
