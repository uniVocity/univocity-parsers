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

import java.io.*;

import static com.univocity.parsers.csv.UnescapedQuoteHandling.*;

/**
 * A very fast CSV parser implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see CsvFormat
 * @see CsvParserSettings
 * @see CsvWriter
 * @see AbstractParser
 */
public class CsvParser extends AbstractParser<CsvParserSettings> {

	private final boolean ignoreTrailingWhitespace;
	private final boolean ignoreLeadingWhitespace;
	private boolean parseUnescapedQuotes;
	private boolean parseUnescapedQuotesUntilDelimiter;
	private final boolean doNotEscapeUnquotedValues;
	private final boolean keepEscape;

	private char delimiter;
	private char quote;
	private char quoteEscape;
	private final char escapeEscape;
	private final char newLine;
	private final DefaultCharAppender whitespaceAppender;
	private final boolean normalizeLineEndingsInQuotes;
	private UnescapedQuoteHandling quoteHandling;


	/**
	 * The CsvParser supports all settings provided by {@link CsvParserSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param settings the parser configuration
	 */
	public CsvParser(CsvParserSettings settings) {
		super(settings);
		ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
		ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();
		parseUnescapedQuotes = settings.isParseUnescapedQuotes();
		parseUnescapedQuotesUntilDelimiter = settings.isParseUnescapedQuotesUntilDelimiter();
		doNotEscapeUnquotedValues = !settings.isEscapeUnquotedValues();
		keepEscape = settings.isKeepEscapeSequences();
		normalizeLineEndingsInQuotes = settings.isNormalizeLineEndingsWithinQuotes();


		CsvFormat format = settings.getFormat();
		delimiter = format.getDelimiter();
		quote = format.getQuote();
		quoteEscape = format.getQuoteEscape();
		escapeEscape = format.getCharToEscapeQuoteEscaping();
		newLine = format.getNormalizedNewline();

		whitespaceAppender = new DefaultCharAppender(settings.getMaxCharsPerColumn(), "");

		this.quoteHandling = settings.getUnescapedQuoteHandling();
		if (quoteHandling == null) {
			if (parseUnescapedQuotes) {
				if (parseUnescapedQuotesUntilDelimiter) {
					quoteHandling = STOP_AT_DELIMITER;
				} else {
					quoteHandling = STOP_AT_CLOSING_QUOTE;
				}
			} else {
				quoteHandling = RAISE_ERROR;
			}
		} else {
			parseUnescapedQuotesUntilDelimiter = quoteHandling == STOP_AT_DELIMITER || quoteHandling == SKIP_VALUE;
			parseUnescapedQuotes = quoteHandling != RAISE_ERROR;
		}
	}

	protected void initialize() {
		input.setDelimiter(delimiter);
		input.setEscape(quote);
		input.setQuoteEscape(quoteEscape);
		input.setEscapeEscape(escapeEscape);
	}

	@Override
	protected void parseRecord() {
		if (ch <= ' ' && ignoreLeadingWhitespace) {
			ch = input.skipWhitespace(ch);
		}

		while (ch != newLine) {
			if (ch <= ' ' && ignoreLeadingWhitespace) {
				ch = input.skipWhitespace(ch);
			}

			if (ch == delimiter) {
				output.emptyParsed();
			} else {
				if (ch == quote) {
					if (normalizeLineEndingsInQuotes) {
						parseQuotedValue('\0');
					} else {
						input.enableNormalizeLineEndings(false);
						parseQuotedValue('\0');
						input.enableNormalizeLineEndings(true);
					}
				} else if (doNotEscapeUnquotedValues) {
					if (ignoreTrailingWhitespace) {
						ch = input.appendIWUntilDelimiter(ch, output.appender);
					} else {
						ch = input.appendUntilDelimiter(ch, output.appender);
					}
				} else {
					parseValueProcessingEscape('\0');
				}
				output.valueParsed();
			}
			if (ch != newLine) {
				ch = input.nextChar();
				if (ch == newLine) {
					output.emptyParsed();
				}
			}
		}
	}

	private void skipValue() {
		output.appender.reset();
		ch = input.appendUntilDelimiter(ch, NoopCharAppender.getInstance());
	}

	private void handleValueSkipping(boolean quoted) {
		switch (quoteHandling) {
			case SKIP_VALUE:
				skipValue();
				break;
			case RAISE_ERROR:
				throw new TextParsingException(context, "Unescaped quote character '" + quote
						+ "' inside " + (quoted ? "quoted" : "") + " value of CSV field. To allow unescaped quotes, set 'parseUnescapedQuotes' to 'true' in the CSV parser settings. Cannot parse CSV input.");
		}
	}

	private void handleUnescapedQuoteInValue() {
		switch (quoteHandling) {
			case STOP_AT_CLOSING_QUOTE:
			case STOP_AT_DELIMITER:
				output.appender.append(quote);
				parseValueProcessingEscape(ch);
				break;
			default:
				handleValueSkipping(false);
				break;
		}
	}

	private boolean handleUnescapedQuote() {
		switch (quoteHandling) {
			case STOP_AT_CLOSING_QUOTE:
			case STOP_AT_DELIMITER:
				output.appender.append(quote);
				output.appender.append(ch);
				parseQuotedValue(ch);
				return true; //continue;
			default:
				handleValueSkipping(true);
				return false;
		}
	}

	private void parseValueProcessingEscape(char prev) {
		if (ignoreTrailingWhitespace) {
			while (ch != delimiter && ch != newLine) {
				if (ch != quote && ch != quoteEscape) {
					if (prev == quote) { //unescaped quote detected
						handleUnescapedQuoteInValue();
						return;
					}
					output.appender.appendIgnoringWhitespace(ch);
				} else if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\0') {
					if (keepEscape) {
						output.appender.appendIgnoringWhitespace(escapeEscape);
					}
					output.appender.appendIgnoringWhitespace(quoteEscape);
					ch = '\0';
				} else if (prev == quoteEscape) {
					if (ch == quote) {
						if (keepEscape) {
							output.appender.appendIgnoringWhitespace(quoteEscape);
						}
						output.appender.appendIgnoringWhitespace(quote);
						ch = '\0';
					} else {
						output.appender.appendIgnoringWhitespace(prev);
					}
				} else if (ch == quote && prev == quote) {
					output.appender.appendIgnoringWhitespace(quote);
				}
				prev = ch;
				ch = input.nextChar();
			}
		} else {
			while (ch != delimiter && ch != newLine) {
				if (ch != quote && ch != quoteEscape) {
					if (prev == quote) { //unescaped quote detected
						handleUnescapedQuoteInValue();
						break;
					}
					output.appender.append(ch);
				} else if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\0') {
					if (keepEscape) {
						output.appender.append(escapeEscape);
					}
					output.appender.append(quoteEscape);
					ch = '\0';
				} else if (prev == quoteEscape) {
					if (ch == quote) {
						if (keepEscape) {
							output.appender.append(quoteEscape);
						}
						output.appender.append(quote);
						ch = '\0';
					} else {
						output.appender.append(prev);
					}
				} else if (ch == quote && prev == quote) {
					output.appender.appendIgnoringWhitespace(quote);
				}
				prev = ch;
				ch = input.nextChar();
			}
		}
	}

	private void parseQuotedValue(char prev) {
		if (prev != '\0' && parseUnescapedQuotesUntilDelimiter) {
			if (quoteHandling == SKIP_VALUE) {
				skipValue();
				return;
			}
			output.appender.prepend(quote);
			ch = input.nextChar();
			if (ignoreTrailingWhitespace) {
				ch = input.appendIWUntilDelimiter(ch, output.appender);
			} else {
				ch = input.appendUntilDelimiter(ch, output.appender);
			}
			return;
		} else {

			while (true) {
				ch = input.nextChar();

				if (prev == quote && (ch <= ' ' || ch == delimiter || ch == newLine)) {
					break;
				}

				if (ch != quote && ch != quoteEscape) {
					if (prev == quote) { //unescaped quote detected
						if (handleUnescapedQuote()) {
							break;
						} else {
							return;
						}
					}
					ch = input.appendUtilAnyEscape(ch, output.appender);
				} else if (ch == quoteEscape && prev == escapeEscape && escapeEscape != '\0') {
					if (keepEscape) {
						output.appender.append(escapeEscape);
					}
					output.appender.append(quoteEscape);
					ch = '\0';
				} else if (prev == quoteEscape) {
					if (ch == quote) {
						if (keepEscape) {
							output.appender.append(quoteEscape);
						}
						output.appender.append(quote);
						ch = '\0';
					} else {
						output.appender.append(prev);
					}
				} else if (ch == quote && prev == quote) {
					output.appender.append(quote);
				}
				prev = ch;
			}

			// handles whitespaces after quoted value: whitespaces are ignored. Content after whitespaces may be parsed if 'parseUnescapedQuotes' is enabled.
			if (ch != delimiter && ch != newLine && ch <= ' ') {
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
				if (ch != delimiter && parseUnescapedQuotes) {
					if (output.appender instanceof DefaultCharAppender) {
						//puts the quote before whitespaces back, then restores the whitespaces
						output.appender.append(quote);
						((DefaultCharAppender) output.appender).append(whitespaceAppender);
					}
					//the next character is not the escape character, put it there
					if (ch != quote && ch != quoteEscape) {
						output.appender.append(ch);
					}

					//sets this character as the previous character (may be escaping)
					//calls recursively to keep parsing potentially quoted content
					parseQuotedValue(ch);
				}
			}

			if (ch != delimiter && ch != newLine) {
				throw new TextParsingException(context, "Unexpected character '" + ch + "' following quoted value of CSV field. Expecting '" + delimiter + "'. Cannot parse CSV input.");
			}
		}
	}

	@Override
	protected InputAnalysisProcess getInputAnalysisProcess() {
		if (settings.isDelimiterDetectionEnabled() || settings.isQuoteDetectionEnabled()) {
			return new CsvFormatDetector(20, settings) {
				@Override
				void apply(char delimiter, char quote, char quoteEscape) {
					if (settings.isDelimiterDetectionEnabled()) {
						CsvParser.this.delimiter = delimiter;

					}
					if (settings.isQuoteDetectionEnabled()) {
						CsvParser.this.quote = quote;
						CsvParser.this.quoteEscape = quoteEscape;
					}
				}
			};
		}
		return null;
	}

	/**
	 * Returns the CSV format detected when one of the following settings is enabled:
	 * <ul>
	 * <li>{@link CommonParserSettings#isLineSeparatorDetectionEnabled()}</li>
	 * <li>{@link CsvParserSettings#isDelimiterDetectionEnabled()}</li>
	 * <li>{@link CsvParserSettings#isQuoteDetectionEnabled()}</li>
	 * </ul>
	 *
	 * The detected format will be available once the parsing process is initialized (i.e. when {@link AbstractParser#beginParsing(Reader) runs}.
	 *
	 * @return the detected CSV format, or {@code null} if no detection has been enabled or if the parsing process has not been started yet.
	 */
	public final CsvFormat getDetectedFormat() {
		CsvFormat out = null;
		if (settings.isDelimiterDetectionEnabled()) {
			out = settings.getFormat().clone();
			out.setDelimiter(this.delimiter);
		}
		if (settings.isQuoteDetectionEnabled()) {
			out = out == null ? settings.getFormat().clone() : out;
			out.setQuote(quote);
			out.setQuoteEscape(quoteEscape);
		}
		if (settings.isLineSeparatorDetectionEnabled()) {
			out = out == null ? settings.getFormat().clone() : out;
			out.setLineSeparator(input.getLineSeparator());
		}
		return out;
	}
}
