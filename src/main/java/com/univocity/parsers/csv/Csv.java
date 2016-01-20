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
package com.univocity.parsers.csv;

/**
 * This class provides default configurations using CSV formats commonly used for parsing/writing.
 */
public class Csv {


	/**
	 * Provides a basic CSV configuration that allows parsing CSV files produced by Microsoft Excel.
	 *
	 * @return a pre-configured {@link CsvParserSettings} object with suggested settings
	 * for parsing CSV files produced by Microsoft Excel.
	 */
	public static CsvParserSettings parseExcel() {
		CsvParserSettings settings = new CsvParserSettings();
		settings.getFormat().setLineSeparator("\r\n");
		settings.getFormat().setComment('\0');
		settings.setParseUnescapedQuotes(false);

		settings.setSkipEmptyLines(false);
		settings.trimValues(false);

		return settings;
	}

	/**
	 * Provides a basic CSV configuration for parsing CSV files in accordance with the
	 * rules established by <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
	 *
	 * @return a pre-configured {@link CsvParserSettings} object with suggested settings for parsing
	 * CSV using the <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a> rules.
	 */
	public static CsvParserSettings parseRfc4180() {
		CsvParserSettings settings = parseExcel();
		settings.setNormalizeLineEndingsWithinQuotes(false);
		return settings;
	}

	/**
	 * Provides a basic CSV configuration that allows writing CSV files that can be read by Microsoft Excel.
	 *
	 * @return a pre-configured {@link CsvWriterSettings} object with suggested settings for generating
	 * CSV files that can be read by Microsoft Excel.
	 */
	public static CsvWriterSettings writeExcel() {
		CsvWriterSettings settings = new CsvWriterSettings();

		settings.getFormat().setLineSeparator("\r\n");
		settings.getFormat().setComment('\0');
		settings.setEmptyValue(null);

		settings.setSkipEmptyLines(false);
		settings.trimValues(false);

		return settings;
	}

	/**
	 * Provides a basic CSV configuration for writing CSV files in accordance with the
	 * rules established by <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
	 *
	 * @return a pre-configured {@link CsvWriterSettings} object with the settings required to generate
	 * CSV files in accordance with the rules established by <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>
	 */
	public static CsvWriterSettings writeRfc4180() {
		CsvWriterSettings settings = writeExcel();

		settings.setNormalizeLineEndingsWithinQuotes(false);
		settings.setQuoteEscapingEnabled(true);

		return settings;
	}
}
