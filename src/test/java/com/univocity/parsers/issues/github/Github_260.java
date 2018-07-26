/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.issues.github;


import com.univocity.parsers.annotations.*;
import com.univocity.parsers.fixed.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/260
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_260 {

	public static class PaddedNumber {
		private Integer number;

		public PaddedNumber() {

		}

		public PaddedNumber(Integer number) {
			this.number = number;
		}

		@Parsed(defaultNullWrite = "   ")
		@FixedWidth(value = 3, padding = '0', alignment = FieldAlignment.RIGHT)
		public Integer getNumber() {
			return number;
		}

		@Parsed
		@FixedWidth(value = 3, alignment = FieldAlignment.RIGHT)
		public void setNumber(Integer number) {
			this.number = number;
		}
	}

	@Test
	public void testParseAnnotatedIntegerWithPadding() {
		final FixedWidthParserSettings settings = new FixedWidthParserSettings(FixedWidthFields.forParsing(PaddedNumber.class));
		settings.getFormat().setLineSeparator("\n");

		List<PaddedNumber> result = new FixedWidthRoutines(settings).parseAll(PaddedNumber.class, new StringReader("001\n   \n000"));

		assertEquals(result.get(0).number, Integer.valueOf(1));
		assertEquals(result.get(1).number, null);
		assertEquals(result.get(2).number, Integer.valueOf(0));
	}

	@Test
	public void testWriteAnnotatedIntegerWithPadding() {
		final FixedWidthWriterSettings settings = new FixedWidthWriterSettings(FixedWidthFields.forWriting(PaddedNumber.class));
		settings.getFormat().setLineSeparator("\n");
		settings.trimValues(false);

		List<PaddedNumber> objects = new ArrayList<PaddedNumber>();
		objects.add(new PaddedNumber(1));
		objects.add(new PaddedNumber(null));
		objects.add(new PaddedNumber(0));

		StringWriter out = new StringWriter();

		new FixedWidthRoutines(settings).writeAll(objects, PaddedNumber.class, out);

		assertEquals(out.toString(), "001\n   \n000\n");
	}

}
