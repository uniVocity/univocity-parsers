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
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/139
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_139 {

	public static class Person1 {
		@Parsed(field = "person")
		String name;

		@Nested
		PhoneList phoneList;

	}

	public static class PhoneList {
		@Parsed
		String phone1;

		@Parsed
		String phone2;

		@Nested
		Mobile mobile;
	}

	public static class Mobile {
		@Parsed
		String mobile;
	}

	@Test
	public void parseAndWriteNestedObjects() throws Exception {
		String input = "person,phone1,phone2,mobile\n" +
				"Mary Jane,041233122,081133352,0101013333\n" +
				"John Citizen,1234333122,12248750,\n";

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.getFormat().setLineSeparator("\n");

		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.setHeaderWritingEnabled(true);
		writerSettings.getFormat().setLineSeparator("\n");

		CsvRoutines routines = new CsvRoutines(parserSettings, writerSettings);

		List<Person1> person1List = routines.parseAll(Person1.class, new StringReader(input));

		assertEquals(person1List.size(), 2);
		assertEquals(person1List.get(0).name, "Mary Jane");
		assertEquals(person1List.get(0).phoneList.phone1, "041233122");
		assertEquals(person1List.get(0).phoneList.phone2, "081133352");
		assertEquals(person1List.get(0).phoneList.mobile.mobile, "0101013333");

		assertEquals(person1List.get(1).name, "John Citizen");
		assertEquals(person1List.get(1).phoneList.phone1, "1234333122");
		assertEquals(person1List.get(1).phoneList.phone2, "12248750");
		assertNull(person1List.get(1).phoneList.mobile.mobile);

		StringWriter output = new StringWriter();

		routines.writeAll(person1List, Person1.class, output);
		assertEquals(output.toString(), input);
	}


}
