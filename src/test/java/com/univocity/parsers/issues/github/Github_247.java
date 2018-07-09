/*
 * Copyright (c) 2018. uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.processor.RowListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.Test;

import static com.univocity.parsers.ParserTestCase.newReader;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/247
 *
 * @author camerondavison
 */
public class Github_247 {

	@Test(expectedExceptions = TextParsingException.class)
	public void test100Cols() throws Exception {
		final CsvParserSettings s = new CsvParserSettings();
		s.setHeaderExtractionEnabled(true);
		s.setMaxColumns(99);
		s.setLineSeparatorDetectionEnabled(true);

		final RowListProcessor rowListProcessor = new RowListProcessor();
		s.setProcessor(rowListProcessor);

		final CsvParser parser = new CsvParser(s);
		parser.parse(newReader("/issues/github_247/input-100.txt"));
		fail("should have exception");
	}
}
