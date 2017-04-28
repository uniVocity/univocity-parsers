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

import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.processor.BeanListProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;

import static org.testng.Assert.*;

/**
 * From: https://github.com/uniVocity/univocity-parsers/issues/154
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class Github_154 {

	public static class User {
		@Parsed(field = "Email")
		private String email;
	}

	@BeforeTest
	public void init() {

	}

	@Test
	public void readUtf8WithBom() throws Exception {
		// arrange
		final CsvParserSettings parserSettings = new CsvParserSettings();
		final BeanListProcessor<User> rowProcessor = new BeanListProcessor<User>(User.class);

		parserSettings.setProcessor(rowProcessor);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setSkipEmptyLines(false);

		final CsvParser parser = new CsvParser(parserSettings);

		final String encoding = "UTF-8";
		final InputStream file = getClass().getResourceAsStream("/issues/github_154/utf8-with-bom.csv");

		// act
		parser.parse(file, encoding);
		final List<User> actual = rowProcessor.getBeans();

		// assert
		assertEquals(actual.get(0).email, "dev@univocity.com");
	}

	@Test
	public void readUtf8WithoutBom() throws Exception {
		// arrange
		final CsvParserSettings parserSettings = new CsvParserSettings();
		final BeanListProcessor<User> rowProcessor = new BeanListProcessor<User>(User.class);

		parserSettings.setProcessor(rowProcessor);
		parserSettings.setLineSeparatorDetectionEnabled(true);
		parserSettings.setHeaderExtractionEnabled(true);
		parserSettings.setSkipEmptyLines(false);

		final CsvParser parser = new CsvParser(parserSettings);

		final String encoding = "UTF-8";
		final InputStream file = getClass().getResourceAsStream("/issues/github_154/utf8-without-bom.csv");

		// act
		parser.parse(file, encoding);
		final List<User> actual = rowProcessor.getBeans();

		// assert
		assertEquals(actual.get(0).email, "dev@univocity.com");
	}

}
