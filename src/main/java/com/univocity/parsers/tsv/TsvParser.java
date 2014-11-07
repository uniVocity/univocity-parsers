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
package com.univocity.parsers.tsv;

import com.univocity.parsers.common.*;

/**
 * A very fast TSV parser implementation.
 *
 * @see TsvFormat
 * @see TsvParserSettings
 * @see TsvWriter
 * @see AbstractParser
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TsvParser extends AbstractParser<TsvParserSettings> {

	private final boolean ignoreTrailingWhitespace;
	private final boolean ignoreLeadingWhitespace;

	private final char newLine;
	private final char escapeChar;

	/**
	 * The TsvParser supports all settings provided by {@link TsvParserSettings}, and requires this configuration to be properly initialized.
	 * @param settings the parser configuration
	 */
	public TsvParser(TsvParserSettings settings) {
		super(settings);
		ignoreTrailingWhitespace = settings.getIgnoreTrailingWhitespaces();
		ignoreLeadingWhitespace = settings.getIgnoreLeadingWhitespaces();

		TsvFormat format = settings.getFormat();
		newLine = format.getNormalizedNewline();
		escapeChar = settings.getFormat().getEscapeChar();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseRecord() {
		if (ignoreLeadingWhitespace && ch != '\t' && ch <= ' ') {
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

	private void parseField() {
		if (ignoreLeadingWhitespace && ch != '\t' && ch <= ' ') {
			skipWhitespace();
		}

		if (ch == '\t') {
			output.emptyParsed();
		} else {
			if (ignoreTrailingWhitespace) {
				while (ch != '\t' && ch != newLine) {
					if (ch == escapeChar) {
						ch = input.nextChar();
						if (ch == 't') {
							output.appender.appendIgnoringWhitespace('\t');
						} else if (ch == 'n') {
							output.appender.appendIgnoringWhitespace('\n');
						} else if (ch == '\\') {
							output.appender.appendIgnoringWhitespace('\\');
						} else if (ch == 'r') {
							output.appender.appendIgnoringWhitespace('\r');
						} else {
							output.appender.append(escapeChar);
							if (ch == newLine || ch == '\t') {
								break;
							}
							output.appender.appendIgnoringWhitespace(ch);
						}
						ch = input.nextChar();
					} else {
						output.appender.appendIgnoringWhitespace(ch);
						ch = input.nextChar();
					}
				}
			} else {
				while (ch != '\t' && ch != newLine) {
					if (ch == escapeChar) {
						ch = input.nextChar();
						if (ch == 't') {
							output.appender.append('\t');
						} else if (ch == 'n') {
							output.appender.append('\n');
						} else if (ch == '\\') {
							output.appender.append('\\');
						} else if (ch == 'r') {
							output.appender.append('\r');
						} else {
							output.appender.append(escapeChar);
							if (ch == newLine || ch == '\t') {
								break;
							}
							output.appender.append(ch);
						}
						ch = input.nextChar();
					} else {
						output.appender.append(ch);
						ch = input.nextChar();
					}
				}
			}
			output.valueParsed();
		}
	}

	private void skipWhitespace() {
		while (ch != '\t' && ch <= ' ' && ch != newLine) {
			ch = input.nextChar();
		}
	}

}
