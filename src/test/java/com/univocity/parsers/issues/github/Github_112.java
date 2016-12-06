/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.*;
import com.univocity.parsers.csv.*;
import org.testng.annotations.*;

import java.io.*;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/112
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_112 {

	@Test
	public void headersAvailableOnBeginParsing() {
		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setLineSeparator("\n");
		s.setHeaderExtractionEnabled(true);

		CsvParser p = new CsvParser(s);
		p.beginParsing(new StringReader("a,b,c\n1,2,3"));
		assertEquals(p.getContext().headers(), new String[]{"a", "b", "c"});
		assertEquals(p.getContext().parsedHeaders(), new String[]{"a", "b", "c"});
		assertEquals(p.parseNext(), new String[]{"1", "2", "3"});
	}


	@Test
	public void headersAvailableOnProcessStarted() {
		CsvParserSettings s = new CsvParserSettings();
		s.getFormat().setLineSeparator("\n");
		s.setHeaderExtractionEnabled(true);
		s.setProcessor(new RowProcessor() {
			@Override
			public void processStarted(ParsingContext context) {
				assertEquals(context.headers(), new String[]{"a", "b", "c"});
				assertEquals(context.parsedHeaders(), new String[]{"a", "b", "c"});
			}

			@Override
			public void rowProcessed(String[] row, ParsingContext context) {
				assertEquals(row, new String[]{"1", "2", "3"});
			}

			@Override
			public void processEnded(ParsingContext context) {

			}
		});

		CsvParser p = new CsvParser(s);
		p.parse(new StringReader("a,b,c\n1,2,3"));
	}

}
