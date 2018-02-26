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
import com.univocity.parsers.common.fields.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

/**
 * A powerful and flexible CSV writer implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see CsvFormat
 * @see CsvWriterSettings
 * @see CsvParser
 * @see AbstractWriter
 */
public class CsvWriter extends AbstractWriter<CsvWriterSettings> {

	private char separator;
	private char quoteChar;
	private char escapeChar;
	private char escapeEscape;
	private boolean ignoreLeading;
	private boolean ignoreTrailing;
	private boolean quoteAllFields;
	private boolean escapeUnquoted;
	private boolean inputNotEscaped;
	private char newLine;
	private boolean dontProcessNormalizedNewLines;
	private boolean[] quotationTriggers;
	private char maxTrigger;
	private Set<Integer> quotedColumns;
	private FieldSelector quotedFieldSelector;

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 *
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(CsvWriterSettings settings) {
		this((Writer) null, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param writer   the output resource that will receive CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(Writer writer, CsvWriterSettings settings) {
		super(writer, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, CsvWriterSettings settings) {
		super(file, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive CSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, String encoding, CsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive CSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, Charset encoding, CsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, CsvWriterSettings settings) {
		super(output, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the CSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, String encoding, CsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the CSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, Charset encoding, CsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * Initializes the CSV writer with CSV-specific configuration
	 *
	 * @param settings the CSV writer configuration
	 */
	protected final void initialize(CsvWriterSettings settings) {
		CsvFormat format = settings.getFormat();
		this.separator = format.getDelimiter();
		this.quoteChar = format.getQuote();
		this.escapeChar = format.getQuoteEscape();
		this.escapeEscape = settings.getFormat().getCharToEscapeQuoteEscaping();
		this.newLine = format.getNormalizedNewline();

		this.quoteAllFields = settings.getQuoteAllFields();
		this.ignoreLeading = settings.getIgnoreLeadingWhitespaces();
		this.ignoreTrailing = settings.getIgnoreTrailingWhitespaces();
		this.escapeUnquoted = settings.isEscapeUnquotedValues();
		this.inputNotEscaped = !settings.isInputEscaped();
		this.dontProcessNormalizedNewLines = !settings.isNormalizeLineEndingsWithinQuotes();

		this.quotationTriggers = null;
		this.quotedColumns = null;
		this.maxTrigger = 0;

		quotedColumns = Collections.emptySet();
		quotedFieldSelector = settings.getQuotedFieldSelector();

		int triggerCount = settings.getQuotationTriggers().length;
		int offset = settings.isQuoteEscapingEnabled() ? 1 : 0;
		char[] tmp = Arrays.copyOf(settings.getQuotationTriggers(), triggerCount + offset);
		if (offset == 1) {
			tmp[triggerCount] = quoteChar;
		}

		for (int i = 0; i < tmp.length; i++) {
			if (maxTrigger < tmp[i]) {
				maxTrigger = tmp[i];
			}
		}
		if (maxTrigger != 0) {
			maxTrigger++;
			this.quotationTriggers = new boolean[maxTrigger];
			Arrays.fill(quotationTriggers, false);
			for (int i = 0; i < tmp.length; i++) {
				quotationTriggers[tmp[i]] = true;
			}
		}
	}

	@Override
	protected void processRow(Object[] row) {
		if (recordCount == 0L && quotedFieldSelector != null) {
			int[] quotedIndexes = quotedFieldSelector.getFieldIndexes(headers);
			if (quotedIndexes.length > 0) {
				quotedColumns = new HashSet<Integer>();
				for (int idx : quotedIndexes) {
					quotedColumns.add(idx);
				}
			}
		}
		for (int i = 0; i < row.length; i++) {
			if (i != 0) {
				appendToRow(separator);
			}

			if (dontProcessNormalizedNewLines) {
				appender.enableDenormalizedLineEndings(false);
			}

			String nextElement = getStringValue(row[i]);
			int originalLength = appender.length();
			boolean isElementQuoted = append(quoteAllFields || quotedColumns.contains(i), nextElement);

			//skipped all whitespaces and wrote nothing
			if (appender.length() == originalLength) {
				if (isElementQuoted) {
					if (nextElement == null) {
						append(false, nullValue);
					} else {
						append(true, emptyValue);
					}
				} else if (nextElement == null) {
					append(false, nullValue);
				} else {
					append(false, emptyValue);
				}
			}

			if (isElementQuoted) {
				appendToRow(quoteChar);
				appendValueToRow();
				appendToRow(quoteChar);
				if (dontProcessNormalizedNewLines) {
					appender.enableDenormalizedLineEndings(true);
				}
			} else {
				appendValueToRow();
			}
		}
	}


	private boolean quoteElement(int start, String element) {
		final int length = element.length();
		if (maxTrigger == 0) {
			for (int i = start; i < length; i++) {
				char nextChar = element.charAt(i);
				if (nextChar == separator || nextChar == newLine) {
					return true;
				}
			}
		} else {
			for (int i = start; i < length; i++) {
				char nextChar = element.charAt(i);
				if (nextChar == separator || nextChar == newLine || nextChar < maxTrigger && quotationTriggers[nextChar]) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean append(boolean isElementQuoted, String element) {
		if (element == null) {
			if (nullValue == null) {
				return isElementQuoted;
			}
			element = nullValue;
		}

		int start = 0;
		if (this.ignoreLeading) {
			start = skipLeadingWhitespace(whitespaceRangeStart, element);
		}

		final int length = element.length();
		if (start < length && element.charAt(start) == quoteChar) {
			isElementQuoted = true;
		}

		if (isElementQuoted) {
			if (usingNullOrEmptyValue && length >= 2) {
				if (element.charAt(0) == quoteChar && element.charAt(length - 1) == quoteChar) {
					appender.append(element);
					return false;
				} else {
					appendQuoted(start, element);
					return true;
				}
			} else {
				appendQuoted(start, element);
				return true;
			}
		}

		int i = start;
		char ch = '\0';
		for (; i < length; i++) {
			ch = element.charAt(i);
			if (ch == quoteChar || ch == separator || ch == newLine || ch == escapeChar || (ch < maxTrigger && quotationTriggers[ch])) {
				appender.append(element, start, i);
				start = i + 1;

				if (ch == quoteChar || ch == escapeChar) {
					if (quoteElement(i, element)) {
						appendQuoted(i, element);
						return true;
					} else if (escapeUnquoted) {
						appendQuoted(i, element);
					} else {
						appender.append(element, i, length);
						if (ignoreTrailing && element.charAt(length - 1) <= ' ' && whitespaceRangeStart < element.charAt(length - 1)) {
							appender.updateWhitespace();
						}
					}
					return isElementQuoted;
				} else if (ch == escapeChar && inputNotEscaped && escapeEscape != '\0' && escapeUnquoted) {
					appender.append(escapeEscape);
				} else if (ch == separator || ch == newLine || ch < maxTrigger && quotationTriggers[ch]) {
					appendQuoted(i, element);
					return true;
				}
				appender.append(ch);
			}
		}

		appender.append(element, start, i);
		if (ch <= ' ' && ignoreTrailing && whitespaceRangeStart < ch) {
			appender.updateWhitespace();
		}
		return isElementQuoted;
	}

	private void appendQuoted(int start, String element) {
		final int length = element.length();
		int i = start;
		char ch = '\0';
		for (; i < length; i++) {
			ch = element.charAt(i);
			if (ch == quoteChar || ch == newLine || ch == escapeChar) {
				appender.append(element, start, i);
				start = i + 1;
				if (ch == quoteChar && inputNotEscaped) {
					appender.append(escapeChar);
				} else if (ch == escapeChar && inputNotEscaped && escapeEscape != '\0') {
					appender.append(escapeEscape);
				}
				appender.append(ch);
			}
		}
		appender.append(element, start, i);
		if (ch <= ' ' && ignoreTrailing && whitespaceRangeStart < ch) {
			appender.updateWhitespace();
		}
	}
}
