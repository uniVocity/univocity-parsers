/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 *
 * From: https://github.com/univocity/univocity-parsers/issues/26
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class Github_26 {
	public static class AB {

		@Parsed(field = "AA")
		private String a;

		@Parsed(field = "BB")
		private boolean b;

		public AB() {

		}
	}

	@Test
	public void ensureExceptionContainsColumnInformation() {
		CsvParserSettings parserSettings = new CsvParserSettings();
		BeanListProcessor<AB> beanProcessor = new BeanListProcessor<AB>(AB.class);
		parserSettings.getFormat().setLineSeparator("\n");
		parserSettings.setRowProcessor(beanProcessor);
		parserSettings.setHeaderExtractionEnabled(true);

		CsvParser parser = new CsvParser(parserSettings);
		try {
			parser.parse(new StringReader("AAAA,BB\nA,B\nC,D"));
			fail("Expected exception to be thrown here");
		} catch (DataProcessingException e) {
			assertEquals(e.getLineIndex(), 2);
			assertEquals(e.getColumnIndex(), 1);
			assertEquals(e.getColumnName(), "BB");
			assertEquals(e.getHeaders(), new String[]{"AAAA", "BB"});
			assertEquals(e.getValue(), "B");
			assertEquals(e.getRow(), new Object[]{"A", "B"});
		}
	}
}
