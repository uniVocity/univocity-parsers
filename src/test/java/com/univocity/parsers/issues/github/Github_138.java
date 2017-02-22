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

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/138
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_138 {

	public static class Person {
		@Parsed(index = 1)
		String name;
		@Parsed(index = 2)
		String age;
		Address address;
	}

	public static class Address {
		@Parsed(index = 3)
		String street;
		String city;
	}

	@Test
	public void parseWithInvalidIndex() throws Exception {

		Reader input = new StringReader("a,b,c\nd,e,f");

		BeanListProcessor rowProcessor = new BeanListProcessor(Person.class);

		CsvParserSettings s = new CsvParserSettings();
		s.setProcessor(rowProcessor);

		CsvParser p = new CsvParser(s);
		p.parse(input);

		List<Person> beans = rowProcessor.getBeans();
		assertEquals(beans.get(0).name, "b");
		assertEquals(beans.get(1).name, "e");

		assertEquals(beans.get(0).age, "c");
		assertEquals(beans.get(1).age, "f");

		assertNull(beans.get(0).address, "c");
		assertNull(beans.get(1).address, "f");
	}


}
