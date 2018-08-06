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
import com.univocity.parsers.common.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/univocity/univocity-parsers/issues/266
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_266 {

	public static class A {
		@Parsed
		public String a;

		@Parsed
		public String b;

		@Parsed
		@Validate(oneOf = {"0", "1"})
		public String c;
	}

	@Test
	public void testValidationAnnotation() {
		CsvParserSettings settings = new CsvParserSettings();
		final boolean[] ran = new boolean[]{false};
		settings.setProcessorErrorHandler(new ProcessorErrorHandler<Context>() {
			@Override
			public void handleError(DataProcessingException error, Object[] inputRow, Context context) {
				assertEquals(error.getColumnIndex(), 2);
				assertEquals(inputRow[2], "3");
				ran[0] = true;
			}
		});
		new CsvRoutines(settings).parseAll(A.class, new StringReader("a,b,c\n,,3\n"));
		assertTrue(ran[0]);
	}

}
