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

import java.io.*;

import com.univocity.parsers.common.*;

/**
 * A fast and flexible fixed-with writer implementation.
 *
 * @see FixedWidthFormat
 * @see FixedWidthFieldLengths
 * @see FixedWidthWriterSettings
 * @see FixedWidthParser
 * @see AbstractWriter
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class FixedWidthWriter extends AbstractWriter<FixedWidthWriterSettings> {

	private final boolean ignoreLeading;
	private final boolean ignoreTrailing;
	private final int[] fieldLengths;
	private final char padding;
	private int length;

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 * <p><strong>Important: </strong> by not providing an instance of {@link java.io.Writer} to this constructor, only the operations that write to Strings are available.</p>
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(FixedWidthWriterSettings settings) {
		this(null, settings);
	}

	/**
	 * The FixedWidthWriter supports all settings provided by {@link FixedWidthWriterSettings}, and requires this configuration to be properly initialized.
	 * @param writer the output resource that will receive fixed-width records produced by this class.
	 * @param settings the fixed-width writer configuration
	 */
	public FixedWidthWriter(Writer writer, FixedWidthWriterSettings settings) {
		super(writer, settings);

		FixedWidthFormat format = settings.getFormat();
		this.padding = format.getPadding();

		this.ignoreLeading = settings.getIgnoreLeadingWhitespaces();
		this.ignoreTrailing = settings.getIgnoreTrailingWhitespaces();

		this.fieldLengths = settings.getFieldLengths();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void processRow(Object[] row) {
		int lastIndex = fieldLengths.length < row.length ? fieldLengths.length : row.length;

		for (int i = 0; i < lastIndex; i++) {
			length = fieldLengths[i];
			String nextElement = getStringValue(row[i]);
			processElement(nextElement);
			appendValueToRow();
		}
	}

	private void append(String element) {
		int start = 0;
		if (this.ignoreLeading) {
			start = skipLeadingWhitespace(element);
		}

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
		while (length-- > 0) {
			appender.append(padding);
		}
	}
}
