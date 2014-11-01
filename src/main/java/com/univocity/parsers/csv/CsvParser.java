/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;

/**
 * A very fast CSV parser implementation.
 *
 * @see CsvFormat
 * @see CsvParserSettings
 * @see CsvWriter
 * @see AbstractParser
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CsvParser extends AbstractParser<CsvParserSettings> {

	private final boolean ignoreTrailingWhitespace;
	private final boolean ignoreLeadingWhitespace;

	private final char delimiter;
	private final char quote;
	private final char quoteEscape;
	private final char newLine;

	/**
	 * The CsvParser supports all settings provided by {@link CsvParserSettings}, and requires this configuration to be properly initialized.
	 * @param settings the parser configuration
	 */
	public CsvParser(CsvParserSettings settings) {
		super(settings);
		ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
		ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();

		CsvFormat format = settings.getFormat();
		delimiter = format.getDelimiter();
		quote = format.getQuote();
		quoteEscape = format.getQuoteEscape();
		newLine = format.getNormalizedNewline();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseRecord() {
		if (ch <= ' ' && ignoreLeadingWhitespace) {
			skipWhitespace();
		}

		while (ch != newLine) {
			parseField();
			if (ch != newLine) {
				ch = input.nextChar();
				if (ch == newLine) {
					output.emptyParsed();
				}
			}
		}
	}

	private void parseValue() {
		if (ignoreTrailingWhitespace) {
			while (ch != delimiter && ch != newLine) {
				output.appender.appendIgnoringWhitespace(ch);
				ch = input.nextChar();
			}
		} else {
			while (ch != delimiter && ch != newLine) {
				output.appender.append(ch);
				ch = input.nextChar();
			}
		}
	}

	private void parseQuotedValue() {
		char prev = '\0';
		ch = input.nextChar();

		while (!(prev == quote && (ch == delimiter || ch <= ' '))) {
			if (ch != quote) {
				output.appender.append(ch);
				prev = ch;
			} else if (prev == quoteEscape) {
				output.appender.append(quote);
				prev = '\0';
			} else {
				prev = ch;
			}
			ch = input.nextChar();
		}

		// irrespective of any setting, if we have a quoted value followed by whitespaces, they have to be discarded
		if (ch <= ' ') {
			skipWhitespace();
		}
	}

	private void parseField() {
		if (ch <= ' ' && ignoreLeadingWhitespace) {
			skipWhitespace();
		}

		if (ch == delimiter) {
			output.emptyParsed();
		} else {
			if (ch == quote) {
				parseQuotedValue();
			} else {
				parseValue();
			}
			output.valueParsed();
		}
	}

	private void skipWhitespace() {
		while (ch <= ' ' && ch != delimiter && ch != newLine) {
			ch = input.nextChar();
		}
	}

}
