/*******************************************************************************
 * Copyright 2017 uniVocity Software Pty Ltd
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

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/132
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_132 {

	public static class SomeBean {
		@Parsed(index = 1)
		private int orderNr;

		@Parsed(index = 3)
		private String pkNr;

		@Parsed(index = 6)
		private int status;
	}

	@Test
	public void testAnnotationWithIndex() {
		String input = "0948;960567;0621;06215005852549;20160115;;1;0.000000;;;18;072;03212248/8486748;0;;";

		BeanListProcessor<SomeBean> rowProcessor = new BeanListProcessor<SomeBean>(SomeBean.class);

		CsvParserSettings parserSettings = new CsvParserSettings();
		parserSettings.setProcessor(rowProcessor);
		parserSettings.setHeaderExtractionEnabled(false);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.getFormat().setComment('#');
		parserSettings.getFormat().setDelimiter(';');
		CsvParser parser = new CsvParser(parserSettings);
		parser.parse(new StringReader(input));

	}
}
