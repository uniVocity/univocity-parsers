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
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/50
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Github_50 {

	public static class P {
		@Parsed
		public String p;

		@Parsed
		public String q;

	}

	public static class E extends P {
		@Parsed
		private String a;

		@Parsed
		@Format(formats = "dd-MMM-yyyy", options = "locale=en")
		private Date b;

		@Parsed
		@Format(formats = "dd-MMM-yyyy", options = "locale=en")
		private Date c;
	}

	private void runTest(boolean strictValidationEnabled) {
		final BeanListProcessor<E> processor = new BeanListProcessor<E>(E.class);
		processor.setStrictHeaderValidationEnabled(strictValidationEnabled);

		final CsvParserSettings settings = new CsvParserSettings();
		settings.setHeaderExtractionEnabled(true);
		settings.getFormat().setLineSeparator("\n");
		settings.setRowProcessor(processor);

		new CsvParser(settings).parse(new StringReader("q,a,Y,b\n1,a@b.com,blah,25-DEC-2015"));

		E e = processor.getBeans().get(0);
		assertNotNull(e);
		assertNull(e.p);
		assertEquals(e.q, "1");
		assertEquals(e.a, "a@b.com");
		assertNotNull(e.b);
		assertNull(e.c);
	}

	@Test
	public void ensureBeanIsParsedWhenColumnsAreNotPresent() {
		runTest(false);
	}

	@Test
	public void ensureStringValidationStillWorks() {
		try {
			runTest(true);
			fail("Expecting error caused by missing field 'c'");
		} catch (DataProcessingException ex) {
			//success!
		}
	}

	public static class Z {
		@Format(formats = "0000")
		@Parsed(index = 0)
		int i;
	}

	@Test
	public void testFormatWithPrimitiveTypes() {
		Z z = new Z();
		z.i = 10;

		StringWriter writer = new StringWriter();
		new CsvRoutines().writeAll(Collections.singletonList(z), Z.class, writer);

		assertEquals(writer.toString().trim(), "0010");

		z = new CsvRoutines().parseAll(Z.class, new StringReader("0011")).get(0);
		assertEquals(z.i, 11);
	}
}
