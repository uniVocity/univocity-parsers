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
package com.univocity.parsers.issues.github;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;
import java.util.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/224
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_224 {

	public static class A {
		@Parsed
		int a;
	}

	public static class B {
		@Parsed
		int b;
	}

	@Test
	public void testRoutinesReuse() {
		CsvWriterSettings writerSettings = new CsvWriterSettings();
		writerSettings.getFormat().setLineSeparator("\n");
		writerSettings.setHeaderWritingEnabled(true);
		CsvRoutines csvRoutines = new CsvRoutines(writerSettings);

		StringWriter writer = new StringWriter();
		csvRoutines.writeAll(Collections.singleton(new A()), A.class, writer);
		assertEquals(writer.toString(), "a\n0\n");

		writer = new StringWriter();
		csvRoutines.writeAll(Collections.singleton(new B()), B.class, writer);
		assertEquals(writer.toString(), "b\n0\n");
	}
}
