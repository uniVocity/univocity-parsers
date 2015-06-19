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
package com.univocity.parsers.tsv;

import com.univocity.parsers.common.*;

import java.io.*;
import java.nio.charset.*;

/**
 * A powerful and flexible TSV writer implementation.
 *
 * @see TsvFormat
 * @see TsvWriterSettings
 * @see TsvParser
 * @see AbstractWriter
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class TsvWriter extends AbstractWriter<TsvWriterSettings> {

	private boolean ignoreLeading;
	private boolean ignoreTrailing;

	private char escapeChar;

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(TsvWriterSettings settings) {
		this((Writer) null, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param writer the output resource that will receive TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(Writer writer, TsvWriterSettings settings) {
		super(writer, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, TsvWriterSettings settings) {
		super(file, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive TSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, String encoding, TsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param file the output file that will receive TSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, Charset encoding, TsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param output the output stream that will be written with the TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, TsvWriterSettings settings) {
		super(output, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *@param output the output stream that will be written with the TSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, String encoding, TsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * @param output the output stream that will be written with the TSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, Charset encoding, TsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * Initializes the TSV writer with TSV-specific configuration
	 * @param settings the TSV writer configuration
	 */
	protected final void initialize(TsvWriterSettings settings) {
		this.escapeChar = settings.getFormat().getEscapeChar();
		this.ignoreLeading = settings.getIgnoreLeadingWhitespaces();
		this.ignoreTrailing = settings.getIgnoreTrailingWhitespaces();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processRow(Object[] row) {
		for (int i = 0; i < row.length; i++) {
			if (i != 0) {
				appendToRow('\t');
			}

			String nextElement = getStringValue(row[i]);

			int originalLength = appender.length();
			append(nextElement);

			//skipped all whitespaces and wrote nothing
			if (appender.length() == originalLength && nullValue != null && !nullValue.isEmpty()) {
				append(nullValue);
			}

			appendValueToRow();
		}
	}

	private void append(String element) {
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
				char ch = element.charAt(i);
				if (ch == '\t') {
					appender.append(escapeChar);
					appender.append('t');
				} else if (ch == '\n') {
					appender.append(escapeChar);
					appender.append('n');
				} else if (ch == '\\') {
					appender.append(escapeChar);
					appender.append('\\');
				} else if (ch == '\r') {
					appender.append(escapeChar);
					appender.append('r');
				} else {
					appender.appendIgnoringWhitespace(ch);
				}
			}
		} else {
			for (int i = start; i < element.length(); i++) {
				char ch = element.charAt(i);
				if (ch == '\t') {
					appender.append(escapeChar);
					appender.append('t');
				} else if (ch == '\n') {
					appender.append(escapeChar);
					appender.append('n');
				} else if (ch == '\\') {
					appender.append(escapeChar);
					appender.append('\\');
				} else if (ch == '\r') {
					appender.append(escapeChar);
					appender.append('r');
				} else {
					appender.append(ch);
				}
			}
		}
	}
}
