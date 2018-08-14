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
import com.univocity.parsers.conversions.*;
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
			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("lalala"));
			assertTrue(e.getMessage().contains("Unable to set value '443' of type 'java.lang.String' to field method 'setComment' "));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}

	@Test
	public void testGetterDataProcessingExceptionHandling() {
		CsvParserSettings settings = new CsvParserSettings();

		try {
			new CsvRoutines(settings).writeAll(Collections.singleton(new A("345")), A.class, new StringWriter());
			fail("Expecting exception to be thrown");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("lalala"));
			assertTrue(e.getMessage().contains("Unable to get value from field"));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}

	public static class RangeLimiter extends ValidatedConversion {
		int min;
		int max;

		public RangeLimiter(String[] args) {
			super(false, false); //not null, not blank
			min = Integer.parseInt(args[0]);
			max = Integer.parseInt(args[1]);
		}

		protected void validate(Object value) {
			super.validate(value); //runs the existing validations for not null and not blank
			int v = ((Number) value).intValue();
			if (v < min || v > max) {
				throw new DataValidationException("out of range: " + min + " >= " + value + " <=" + max);
			}
		}
	}

	public static class B {
		public int c;

		public B() {

		}

		public B(int c) {
			this.c = c;
		}

		@Parsed(field = "c")
		@Convert(conversionClass = RangeLimiter.class, args = {"1", "5"})
		public void setComment(int comment) {
			this.c = comment;
		}

		@Parsed(field = "c")
		@Convert(conversionClass = RangeLimiter.class, args = {"1", "5"})
		public int getComment() {
			return c;
		}
	}


	@Test
	public void testCustomValidationHandlingOnParse() {
		CsvParserSettings settings = new CsvParserSettings();

		try {
			new CsvRoutines(settings).parseAll(B.class, new StringReader("a,b,c\n,,443\n"));
			fail("Expecting exception to be thrown");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("out of range: 1 >= 443 <=5"));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}

	@Test
	public void testCustomValidationHandlingOnWrite() {
		CsvParserSettings settings = new CsvParserSettings();

		try {
			new CsvRoutines(settings).writeAll(Collections.singleton(new B(345)), B.class, new StringWriter());
			fail("Expecting exception to be thrown");
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.getMessage().startsWith("out of range: 1 >= 345 <=5"));
			assertTrue(e.getMessage().contains("Internal state when error was thrown"));
		}
	}
}
