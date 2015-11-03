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
 * From: https://github.com/uniVocity/univocity-parsers/issues/19
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_19 {

	public static class NewStudentForm {
		@Parsed(index = 0)
		private String userName;

		@Parsed(index = 2)
		private String displayName;

		@Parsed(index = 1)
		private String password;

		@Parsed(index = 3)
		private String deviceName;

		public NewStudentForm() {

		}

	}

	@Test
	public void testBeanParsingWithOutOfBoundsIndex() {
		BeanListProcessor<NewStudentForm> rowProcessor = new BeanListProcessor<NewStudentForm>(NewStudentForm.class);
		CsvParserSettings parserSettings = new CsvParserSettings();

		parserSettings.setRowProcessor(rowProcessor);

		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader("psp,pass,PSP\nbsp,pass,bsp, Dev1"));

		List<NewStudentForm> beans = rowProcessor.getBeans();
		assertEquals(beans.size(), 2);

		NewStudentForm b1 = beans.get(0);
		assertEquals(b1.userName, "psp");
		assertEquals(b1.password, "pass");
		assertEquals(b1.displayName, "PSP");
		assertNull(b1.deviceName);

		NewStudentForm b2 = beans.get(1);
		assertEquals(b2.userName, "bsp");
		assertEquals(b2.password, "pass");
		assertEquals(b2.displayName, "bsp");
		assertEquals(b2.deviceName, "Dev1");
	}
}
