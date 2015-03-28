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
import com.univocity.parsers.common.input.*;

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
	private final boolean parseUnescapedQuotes;

	private final char delimiter;
	private final char quote;
	private final char quoteEscape;
	private final char escapeEscape;
	private final char newLine;
	private final DefaultCharAppender whitespaceAppender;
	private char[] chars;
	private int i;
	private int w;

	/**
	 * The CsvParser supports all settings provided by {@link CsvParserSettings}, and requires this configuration to be properly initialized.
	 * @param settings the parser configuration
	 */
	public CsvParser(CsvParserSettings settings) {
		super(settings);
		ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
		ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();
		parseUnescapedQuotes = settings.isParseUnescapedQuotes();

		CsvFormat format = settings.getFormat();
		delimiter = format.getDelimiter();
		quote = format.getQuote();
		quoteEscape = format.getQuoteEscape();
		escapeEscape = format.getCharToEscapeQuoteEscaping();
		newLine = format.getNormalizedNewline();

		whitespaceAppender = new DefaultCharAppender(settings.getMaxCharsPerColumn(), "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseRecord() {
		if (ch <= ' ' && ignoreLeadingWhitespace) {
			skipWhitespace();
		}

		try {
			while (ch != newLine) {

				parseField();

				if (ch != newLine) {
					ch = input.nextChar();
					if (ch == newLine) {
						output.emptyParsed();
					}
				}
			}
		} catch (EOFException ex) {
			output.appender.setLength(i, w);
			throw ex;
		}
	}

	private void parseValue() {
		if (chars != null) {
			if (ignoreTrailingWhitespace) {
				while (ch != delimiter && ch != newLine) {
					if (ch <= ' ') {
						w++;
					} else {
						w = 0;
					}
					chars[i++] = ch;
					ch = input.nextChar();
				}
			} else {
				while (ch != delimiter && ch != newLine) {
					chars[i++] = ch;
					ch = input.nextChar();
				}
			}
		} else {
			while (ch != delimiter && ch != newLine) {
				ch = input.nextChar();
			}
		}
	}

	private void parseQuotedValue(char prev) {
		ch = input.nextChar();

		if (chars != null) {
			while (!(prev == quote && (ch <= ' ' || ch == delimiter || ch == newLine))) {
				if (ch != quote && ch != quoteEscape) {
					if (prev == quote) { //unescaped quote detected
						if (parseUnescapedQuotes) {
							chars[i++] = quote;
							chars[i++] = ch;
							parseQuotedValue(ch);
							break;
						} else {
							throw new TextParsingException(context, "Unescaped quote character '" + quote
									+ "' inside quoted value of CSV field. To allow unescaped quotes, set 'parseUnescapedQuotes' to 'true' in the CSV parser settings. Cannot parse CSV input.");
						}
					}
					chars[i++] = ch;
					prev = ch;
				} else if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\0') {
					chars[i++] = ch;
					prev = '\0';
				} else if (prev == quoteEscape) {
					if (ch == quote) {
						chars[i++] = quote;
						prev = '\0';
					} else {
						chars[i++] = prev;
					}
				} else {
					prev = ch;
				}
				ch = input.nextChar();
			}
		} else {
			while (!(prev == quote && (ch <= ' ' || ch == delimiter || ch == newLine))) {
				if (ch != quote && ch != quoteEscape) {
					if (prev == quote) { //unescaped quote detected
						if (parseUnescapedQuotes) {
							parseQuotedValue(ch);
							break;
						} else {
							throw new TextParsingException(context, "Unescaped quote character '" + quote
									+ "' inside quoted value of CSV field. To allow unescaped quotes, set 'parseUnescapedQuotes' to 'true' in the CSV parser settings. Cannot parse CSV input.");
						}
					}
					prev = ch;
				} else if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\0') {
					prev = '\0';
				} else if (prev == quoteEscape) {
					if (ch == quote) {
						prev = '\0';
					}
				} else {
					prev = ch;
				}
				ch = input.nextChar();
			}
		}

		// handles whitespaces after quoted value: whitespaces are ignored. Content after whitespaces may be parsed if 'parseUnescapedQuotes' is enabled.
		if (ch != newLine && ch <= ' ') {
			whitespaceAppender.reset();
			do {
				//saves whitespaces after value
				whitespaceAppender.append(ch);
				ch = input.nextChar();
				//found a new line, go to next record.
				if (ch == newLine) {
					return;
				}
			} while (ch <= ' ');

			//there's more stuff after the quoted value, not only empty spaces.
			if (!(ch == delimiter || ch == newLine) && parseUnescapedQuotes) {
				if (output.appender instanceof DefaultCharAppender) {
					//puts the quote before whitespaces back, then restores the whitespaces
					chars[i++] = quote;
					System.arraycopy(whitespaceAppender.getChars(), 0, chars, i, whitespaceAppender.length());
					i += whitespaceAppender.length();
					whitespaceAppender.reset();
				}
				//the next character is not the escape character, put it there
				if (ch != quoteEscape) {
					chars[i++] = ch;
				}
				//sets this caracter as the previous character (may be escaping)
				//calls recursively to keep parsing potentially quoted content
				parseQuotedValue(ch);
			}
		}

		if (!(ch == delimiter || ch == newLine)) {
			throw new TextParsingException(context, "Unexpected character '" + ch + "' following quoted value of CSV field. Expecting '" + delimiter + "'. Cannot parse CSV input.");
		}
	}

	private void parseField() {
		if (ch <= ' ' && ignoreLeadingWhitespace) {
			skipWhitespace();
		}

		if (ch == delimiter) {
			output.emptyParsed();
		} else {
			chars = output.appender.getChars();
			i = 0;
			w = 0;

			if (ch == quote) {
				parseQuotedValue('\0');
			} else {
				parseValue();
			}
			output.appender.setLength(i, w);
			output.valueParsed();
		}
	}

	private void skipWhitespace() {
		while (ch <= ' ' && ch != delimiter && ch != newLine) {
			ch = input.nextChar();
		}
	}

}
