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
package com.univocity.parsers.fixed;

import com.univocity.parsers.common.*;

import java.io.*;
import java.nio.charset.*;

/**
 * A fast and flexible fixed-with writer implementation.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see FixedWidthFormat
 * @see FixedWidthFields
 * @see FixedWidthWriterSettings
 * @see FixedWidthParser
 * @see AbstractWriter
 */
public class FixedWidthWriter extends AbstractWriter<FixedWidthWriterSettings> {

	private boolean ignoreLeading;
	private boolean ignoreTrailing;
	private int[] fieldLengths;
	private FieldAlignment[] fieldAlignments;
	private char[] fieldPaddings;
	private char padding;
	private char defaultPadding;
	private int length;
	private FieldAlignment alignment;

	private Lookup[] lookaheadFormats;
	private Lookup[] lookbehindFormats;
	private char[] lookupChars;
	private Lookup lookbehindFormat;
	private int[] rootLengths;
	private FieldAlignment[] rootAlignments;
	private boolean[] ignore;
	private boolean[] rootIgnore;
	private int ignoreCount;


	private char[] rootPaddings;
	private boolean defaultHeaderPadding;
	private FieldAlignment defaultHeaderAlignment;

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 *
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(FixedWidthWriterSettings settings) {
		this((Writer) null, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param writer   the output resource that will receive fixed-width records produced by this class.
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(Writer writer, FixedWidthWriterSettings settings) {
		super(writer, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive fixed-width records produced by this class.
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(File file, FixedWidthWriterSettings settings) {
		super(file, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive fixed-width records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(File file, String encoding, FixedWidthWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param file     the output file that will receive fixed-width records produced by this class.
	 * @param encoding the encoding of the file
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(File file, Charset encoding, FixedWidthWriterSettings settings) {
		super(file, encoding, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the fixed-width records produced by this class.
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(OutputStream output, FixedWidthWriterSettings settings) {
		super(output, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the fixed-width records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(OutputStream output, String encoding, FixedWidthWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param output   the output stream that will be written with the fixed-width records produced by this class.
	 * @param encoding the encoding of the stream
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(OutputStream output, Charset encoding, FixedWidthWriterSettings settings) {
		super(output, encoding, settings);
	}

	/**
	 * Initializes the Fixed-Width writer with CSV-specific configuration
	 *
	 * @param settings the Fixed-Width  writer configuration
	 */
	protected final void initialize(FixedWidthWriterSettings settings) {
		FixedWidthFormat format = settings.getFormat();
		this.padding = format.getPadding();
		this.defaultPadding = padding;

		this.ignoreLeading = settings.getIgnoreLeadingWhitespaces();
		this.ignoreTrailing = settings.getIgnoreTrailingWhitespaces();

		this.fieldLengths = settings.getAllLengths();
		this.fieldAlignments = settings.getFieldAlignments();
		this.fieldPaddings = settings.getFieldPaddings();
		this.ignore = settings.getFieldsToIgnore();
		if (ignore != null) {
			for (int i = 0; i < ignore.length; i++) {
				if (ignore[i]) {
					ignoreCount++;
				}
			}
		}

		this.lookaheadFormats = settings.getLookaheadFormats();
		this.lookbehindFormats = settings.getLookbehindFormats();

		this.defaultHeaderPadding = settings.getUseDefaultPaddingForHeaders();
		this.defaultHeaderAlignment = settings.getDefaultAlignmentForHeaders();
		super.enableNewlineAfterRecord(settings.getWriteLineSeparatorAfterRecord());

		if (lookaheadFormats != null || lookbehindFormats != null) {
			lookupChars = new char[Lookup.calculateMaxLookupLength(lookaheadFormats, lookbehindFormats)];
			rootLengths = fieldLengths;
			rootAlignments = fieldAlignments;
			rootPaddings = fieldPaddings;
			rootIgnore = ignore;
		} else {
			lookupChars = null;
			rootLengths = null;
			rootAlignments = null;
			rootPaddings = null;
			rootIgnore = null;
		}
	}

	@Override
	protected void processRow(Object[] row) {
		if (row.length > 0 && lookaheadFormats != null || lookbehindFormats != null) {
			int dstBegin = 0;
			for (int i = 0; i < row.length && dstBegin < lookupChars.length; i++) {
				String value = String.valueOf(row[i]);
				int len = value.length();

				if (dstBegin + len > lookupChars.length) {
					len = lookupChars.length - dstBegin;
				}

				value.getChars(0, len, lookupChars, dstBegin);
				dstBegin += len;
			}

			for (int i = lookupChars.length - 1; i > dstBegin; i--) {
				lookupChars[i] = '\0';
			}

			boolean matched = false;
			if (lookaheadFormats != null) {
				for (int i = 0; i < lookaheadFormats.length; i++) {
					if (lookaheadFormats[i].matches(lookupChars)) {
						fieldLengths = lookaheadFormats[i].lengths;
						fieldAlignments = lookaheadFormats[i].alignments;
						fieldPaddings = lookaheadFormats[i].paddings;
						ignore = lookaheadFormats[i].ignore;
						matched = true;
						break;
					}
				}
				if (lookbehindFormats != null && matched) {
					lookbehindFormat = null;
					for (int i = 0; i < lookbehindFormats.length; i++) {
						if (lookbehindFormats[i].matches(lookupChars)) {
							lookbehindFormat = lookbehindFormats[i];
							break;
						}
					}
				}
			} else {
				for (int i = 0; i < lookbehindFormats.length; i++) {
					if (lookbehindFormats[i].matches(lookupChars)) {
						lookbehindFormat = lookbehindFormats[i];
						matched = true;
						fieldLengths = rootLengths;
						fieldAlignments = rootAlignments;
						fieldPaddings = rootPaddings;
						ignore = rootIgnore;
						break;
					}
				}
			}

			if (!matched) {
				if (lookbehindFormat == null) {
					if (rootLengths == null) {
						throw new TextWritingException("Cannot write with the given configuration. No default field lengths defined and no lookahead/lookbehind value match '" + new String(lookupChars) + '\'', getRecordCount(), row);
					}
					fieldLengths = rootLengths;
					fieldAlignments = rootAlignments;
					fieldPaddings = rootPaddings;
					ignore = rootIgnore;
				} else {
					fieldLengths = lookbehindFormat.lengths;
					fieldAlignments = lookbehindFormat.alignments;
					fieldPaddings = lookbehindFormat.paddings;
					ignore = lookbehindFormat.ignore;
				}
			}
		}

		if (expandRows) {
			row = expand(row, fieldLengths.length - ignoreCount, null);
		}

		final int lastIndex = fieldLengths.length < row.length ? fieldLengths.length : row.length;
		int off = 0;
		for (int i = 0; i < lastIndex + off; i++) {
			length = fieldLengths[i];
			if (ignore[i]) {
				off++;
				this.appender.fill(' ', length);
			} else {
				alignment = fieldAlignments[i];
				padding = fieldPaddings[i];
				if (writingHeaders) {
					if (defaultHeaderPadding) {
						padding = defaultPadding;
					}
					if (defaultHeaderAlignment != null) {
						alignment = defaultHeaderAlignment;
					}
				}
				String nextElement = getStringValue(row[i - off]);
				processElement(nextElement);
				appendValueToRow();
			}
		}
	}

	private void append(String element) {
		int start = 0;
		if (this.ignoreLeading) {
			start = skipLeadingWhitespace(whitespaceRangeStart, element);
		}

		int padCount = alignment.calculatePadding(length, element.length() - start);
		length -= padCount;
		appender.fill(padding, padCount);

		if (this.ignoreTrailing) {
			int i = start;
			while (i < element.length() && length > 0) {
				for (; i < element.length() && length-- > 0; i++) {
					char nextChar = element.charAt(i);
					appender.appendIgnoringWhitespace(nextChar);
				}
				if (length == -1 && appender.whitespaceCount() > 0) {
					//if we got here then the value to write got truncated exactly after one or more whitespaces.
					//In this case, if the whitespaces are not at the end of the truncated value they will be put back to the output.
					for (int j = i; j < element.length(); j++) {
						if (element.charAt(j) > ' ') {
							//resets the whitespace count so the original whitespaces are printed to the output.
							appender.resetWhitespaceCount();
							break;
						}
					}
					if (appender.whitespaceCount() > 0) {
						length = 0;
					}
				}
				length += appender.whitespaceCount();
				appendValueToRow();
			}
		} else {
			for (int i = start; i < element.length() && length-- > 0; i++) {
				char nextChar = element.charAt(i);
				appender.append(nextChar);
			}
		}
	}

	private void processElement(String element) {
		if (element != null) {
			append(element);
		}
		appender.fill(padding, length);
	}
}
