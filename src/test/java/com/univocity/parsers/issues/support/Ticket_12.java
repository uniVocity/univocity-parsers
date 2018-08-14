/*******************************************************************************
 * Copyright 2018 Univocity Software Pty Ltd
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
package com.univocity.parsers.issues.support;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

public class Ticket_12 {
	public static class A {
		public String c;

		public A() {

		}

		public A(String c) {
			this.c = c;
		}

		@Parsed(field = "c")
		public void setComment(String comment) {
			if (comment != null && comment.length() > 1) {
				throw new DataProcessingException("lalala");
			}
			this.c = comment;
		}

		@Parsed(field = "c")
		public String getComment() {
			if (c != null && c.length() > 1) {
				throw new DataProcessingException("lalala");
			}
			return c;
		}
	}

	@Test
	public void testSetterDataProcessingExceptionHandling() {
		CsvParserSettings settings = new CsvParserSettings();

		try {
			new CsvRoutines(settings).parseAll(A.class, new StringReader("a,b,c\n,,443\n"));
			fail("Expecting exception to be thrown");
		} catch (Exception e) {
//			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("lalala"));
			assertTrue(e.getMessage().contains("Unable to set value '443' of type 'java.lang.String' to field method 'setComment' "));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}

	@Test
	public void testGetterDataProcessingExceptionHandling() {
		CsvParserSettings settings = new CsvParserSettings();

		try {
			new CsvRoutines(settings).writeAll(Collections.singleton(new A("lol")), A.class, new StringWriter());
			fail("Expecting exception to be thrown");
		} catch (Exception e) {
//			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("lalala"));
			assertTrue(e.getMessage().contains("Unable to get value from field"));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}
}
