/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.csv;

import com.univocity.parsers.common.*;

import java.io.*;
import java.nio.charset.*;

/**
 * A powerful and flexible CSV writer implementation.
 *
 * @see CsvFormat
 * @see CsvWriterSettings
 * @see CsvParser
 * @see AbstractWriter
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
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

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(CsvWriterSettings settings) {
		this((Writer) null, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param writer the output resource that will receive CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(Writer writer, CsvWriterSettings settings) {
		super(writer, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, CsvWriterSettings settings) {
		super(file, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive CSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, String encoding, CsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive CSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(File file, Charset encoding, CsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param output the output stream that will be written with the CSV records produced by this class.
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, CsvWriterSettings settings) {
		super(output, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 *@param output the output stream that will be written with the CSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, String encoding, CsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * The CsvWriter supports all settings provided by {@link CsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param output the output stream that will be written with the CSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the CSV writer configuration
	 */
	public CsvWriter(OutputStream output, Charset encoding, CsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * Initializes the CSV writer with CSV-specific configuration
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processRow(Object[] row) {
		for (int i = 0; i < row.length; i++) {
			if (i != 0) {
				appendToRow(separator);
			}

			String nextElement = getStringValue(row[i]);
			boolean isElementQuoted = quoteElement(nextElement);

			if (isElementQuoted) {
				appender.append(quoteChar);
			}

			int originalLength = appender.length();
			append(isElementQuoted, nextElement);

			//skipped all whitespaces and wrote nothing
			if (appender.length() == originalLength) {
				if (isElementQuoted) {
					if (nextElement == null) {
						append(false, nullValue);
					} else {
						append(true, emptyValue);
					}
				} else {
					append(false, nullValue);
				}
			}

			if (isElementQuoted) {
				appendValueToRow();
				appendToRow(quoteChar);
			} else {
				appendValueToRow();
			}
		}
	}

	private boolean quoteElement(String nextElement) {
		if (quoteAllFields) {
			return true;
		}
		if (nextElement == null) {
			return false;
		}

		int start = 0;
		if (ignoreLeading) {
			start = skipLeadingWhitespace(nextElement);
		}

		if (start < nextElement.length() && nextElement.charAt(start) == quoteChar) {
			return true;
		}

		for (int j = start; j < nextElement.length(); j++) {
			char nextChar = nextElement.charAt(j);
			if (nextChar == separator || nextChar == newLine) {
				return true;
			}
		}

		return false;
	}

	private void append(boolean isElementQuoted, String element) {
		if (element == null) {
			element = nullValue;
		}
		if (element == null) {
			return;
		}

		int start = 0;
		if (this.ignoreLeading) {
			start = skipLeadingWhitespace(element);
		}

		if (this.ignoreTrailing) {
			for (int i = start; i < element.length(); i++) {
				char nextChar = element.charAt(i);
				if (nextChar == quoteChar && (isElementQuoted || escapeUnquoted) && inputNotEscaped) {
					appender.appendIgnoringWhitespace(escapeChar);
				} else if (nextChar == escapeChar && inputNotEscaped && escapeEscape != '\0' && (isElementQuoted || escapeUnquoted)) {
					appender.appendIgnoringWhitespace(escapeEscape);
				}
				appender.appendIgnoringWhitespace(nextChar);
			}
		} else {
			for (int i = start; i < element.length(); i++) {
				char nextChar = element.charAt(i);
				if (nextChar == quoteChar && (isElementQuoted || escapeUnquoted) && inputNotEscaped) {
					appender.append(escapeChar);
				} else if (nextChar == escapeChar && inputNotEscaped && escapeEscape != '\0' && (isElementQuoted || escapeUnquoted)) {
					appender.appendIgnoringWhitespace(escapeEscape);
				}
				appender.append(nextChar);
			}
		}
	}
}
