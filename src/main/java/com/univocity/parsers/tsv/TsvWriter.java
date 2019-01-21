/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import java.io.*;
import java.nio.charset.*;

/**
 * A powerful and flexible TSV writer implementation.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see TsvFormat
 * @see TsvWriterSettings
 * @see TsvParser
 * @see AbstractWriter
 */
public class TsvWriter extends AbstractWriter<TsvWriterSettings> {

	private boolean joinLines;

	private char escapeChar;
	private char escapedTabChar;
	private char newLine;

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 *
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(TsvWriterSettings settings) {
		this((Writer) null, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param writer   the output resource that will receive TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(Writer writer, TsvWriterSettings settings) {
		super(writer, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, TsvWriterSettings settings) {
		super(file, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive TSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, String encoding, TsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive TSV records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(File file, Charset encoding, TsvWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the TSV records produced by this class.
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, TsvWriterSettings settings) {
		super(output, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the TSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, String encoding, TsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * The TsvWriter supports all settings provided by {@link TsvWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the TSV records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the TSV writer configuration
	 */
	public TsvWriter(OutputStream output, Charset encoding, TsvWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * Initializes the TSV writer with TSV-specific configuration
	 *
	 * @param settings the TSV writer configuration
	 */
	protected final void initialize(TsvWriterSettings settings) {
		this.escapeChar = settings.getFormat().getEscapeChar();
		this.escapedTabChar = settings.getFormat().getEscapedTabChar();
		this.joinLines = settings.isLineJoiningEnabled();
		this.newLine = settings.getFormat().getNormalizedNewline();
	}

	@Override
	protected void processRow(Object[] row) {
		for (int i = 0; i < row.length; i++) {
			if (i != 0) {
				appendToRow('\t');
			}

			String nextElement = getStringValue(row[i]);
			boolean allowTrim = allowTrim(i);
			int originalLength = appender.length();
			append(nextElement, allowTrim);

			//skipped all whitespaces and wrote nothing
			if (appender.length() == originalLength && nullValue != null && !nullValue.isEmpty()) {
				append(nullValue, allowTrim);
			}

			appendValueToRow();
		}
	}

	private void append(String element, boolean allowTrim) {
		if (element == null) {
			element = nullValue;
		}

		if (element == null) {
			return;
		}

		int start = 0;
		if (allowTrim && this.ignoreLeading) {
			start = skipLeadingWhitespace(whitespaceRangeStart, element);
		}

		final int length = element.length();

		int i = start;
		char ch = '\0';
		for (; i < length; i++) {
			ch = element.charAt(i);
			if (ch == '\t' || ch == '\n' || ch == '\r' || ch == '\\') {
				appender.append(element, start, i);
				start = i + 1;
				appender.append(escapeChar);
				if (ch == '\t') {
					appender.append(escapedTabChar);
				} else if (ch == '\n') {
					appender.append(joinLines ? newLine : 'n');
				} else if (ch == '\\') {
					appender.append('\\');
				} else {
					appender.append(joinLines ? newLine : 'r');
				}
			}
		}
		appender.append(element, start, i);
		if (allowTrim && ch <= ' ' && ignoreTrailing && whitespaceRangeStart < ch) {
			appender.updateWhitespace();
		}
	}
}