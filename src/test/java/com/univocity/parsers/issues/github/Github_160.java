/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
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
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/160
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_160 {

	public static class A {
		String a;

		@FixedWidth(3)
		@Parsed(field = "a", index = 0)
		public String getA() {
			return a;
		}

		@FixedWidth(3)
		@Parsed(field = "a", index = 0)
		public void setA(String a) {
			this.a = a;
		}
	}

	public static class B {
		A a;
		String b;


		@Nested
		public void setA(A a) {
			this.a = a;
		}

		@Nested
		public A getA() {
			return a;
		}

		@FixedWidth(2)
		@Parsed(field = "b", index = 1)
		public String getB() {
			return b;
		}

		@FixedWidth(2)
		@Parsed(field = "b", index = 1)
		public void setB(String b) {
			this.b = b;
		}
	}

	@Test
	public void testMethodAnnotations() {
		CsvParserSettings s = new CsvParserSettings();
		s.detectFormatAutomatically();
		s.setHeaderExtractionEnabled(true);
		s.getFormat().setLineSeparator("\n");

		List<B> result = new CsvRoutines(s).parseAll(B.class, new StringReader("a,b\n1,2"));
		assertEquals(result.get(0).b, "2");
		assertEquals(result.get(0).a.a, "1");

		FixedWidthFields fields = FixedWidthFields.forWriting(B.class);
		System.out.println(Arrays.toString(fields.getFieldNames()));
		FixedWidthWriterSettings fs = new FixedWidthWriterSettings(fields);
		fs.setHeaderWritingEnabled(true);
		fs.getFormat().setLineSeparator("\n");
		StringWriter out = new StringWriter();

		new FixedWidthRoutines(fs).writeAll(result, B.class, out);

		assertEquals(out.toString(), "a  b \n1  2 \n");
	}
}
