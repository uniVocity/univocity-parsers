/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.univocity.parsers.common.input;

import com.univocity.parsers.common.*;

import java.io.*;
import java.util.*;

/**
 * Extension of the {@link DefaultCharAppender} class to include facilities for writing to an output. Used by writers extending  {@link AbstractWriter}.
 * <p/>
 * <p> This class introduces the handling of the normalized newline character defined in {@link Format#getNormalizedNewline()} and converts it to the newline sequence in {@link Format#getLineSeparator()}
 * <p> It also introduces methods to write to an instance of  {@link java.io.Writer} directly to avoid unnecessary String instantiations.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.Format
 * @see com.univocity.parsers.common.AbstractWriter
 */
public class WriterCharAppender extends DefaultCharAppender {

	private final char lineSeparator1;
	private final char lineSeparator2;
	private final char newLine;
	private boolean denormalizeLineEndings = true;

	/**
	 * Creates a WriterCharAppender with:
	 * <ul>
	 * <li>a maximum limit of characters to append</li>
	 * <li>the default value to return when no characters have been accumulated.</li>
	 * <li>the basic {@link Format} specification for handling newlines</li>
	 * </ul>
	 * <p/>
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param maxLength  maximum limit of characters to append
	 * @param emptyValue default value to return when no characters have been accumulated
	 * @param format     output format specification used for newline handling
	 */
	public WriterCharAppender(int maxLength, String emptyValue, Format format) {
		this(maxLength, emptyValue, ' ', format);
	}

	/**
	 * Creates a WriterCharAppender with:
	 * <ul>
	 * <li>a maximum limit of characters to append</li>
	 * <li>the default value to return when no characters have been accumulated.</li>
	 * <li>the basic {@link Format} specification for handling newlines</li>
	 * </ul>
	 * <p/>
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param maxLength  maximum limit of characters to append
	 * @param emptyValue default value to return when no characters have been accumulated
	 * @param padding    the padding character to ignore when calling {@link WriterCharAppender#appendIgnoringWhitespaceAndPadding(char)}.
	 * @param format     the output format specification used for newline handling
	 */
	public WriterCharAppender(int maxLength, String emptyValue, char padding, Format format) {
		super(maxLength, emptyValue, padding);

		char[] lineSeparator = format.getLineSeparator();

		this.lineSeparator1 = lineSeparator[0];
		this.lineSeparator2 = lineSeparator.length > 1 ? lineSeparator[1] : '\0';

		newLine = format.getNormalizedNewline();
	}

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '})
	 * <p/>
	 * <p>If the given character is equal to {@link Format#getNormalizedNewline()}, then the character sequence returned by {@link Format#getLineSeparator()} is going to be appended.
	 *
	 * @param ch character to append
	 */
	@Override
	public void appendIgnoringWhitespace(char ch) {
		try {
			if (ch == newLine && denormalizeLineEndings) {
				super.appendIgnoringWhitespace(lineSeparator1);
				if (lineSeparator2 != '\0') {
					super.appendIgnoringWhitespace(lineSeparator2);
				}
			} else {
				super.appendIgnoringWhitespace(ch);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			appendIgnoringPadding(ch);
		}
	}

	/**
	 * Appends the given character and marks it as ignored if it is a padding character (depends on the character given as the value for the {@link WriterCharAppender#padding} attribute in the constructor)
	 * <p/>
	 * <p>If the given character is equal to {@link Format#getNormalizedNewline()}, then the character sequence returned by {@link Format#getLineSeparator()} is going to be appended.
	 *
	 * @param ch character to append
	 */
	@Override
	public void appendIgnoringPadding(char ch) {
		try {
			if (ch == newLine && denormalizeLineEndings) {
				super.appendIgnoringPadding(lineSeparator1);
				if (lineSeparator2 != '\0') {
					super.appendIgnoringPadding(lineSeparator2);
				}
			} else {
				super.appendIgnoringPadding(ch);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			appendIgnoringPadding(ch);
		}
	}

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '}) or a padding character (depends on the character given as the value for the {@link DefaultCharAppender#padding} attribute in the constructor)
	 * <p/>
	 * <p>If the given character is equal to {@link Format#getNormalizedNewline()}, then the character sequence returned by {@link Format#getLineSeparator()} is going to be appended.
	 *
	 * @param ch character to append
	 */
	@Override
	public void appendIgnoringWhitespaceAndPadding(char ch) {
		try {
			if (ch == newLine && denormalizeLineEndings) {
				super.appendIgnoringWhitespaceAndPadding(lineSeparator1);
				if (lineSeparator2 != '\0') {
					super.appendIgnoringWhitespaceAndPadding(lineSeparator2);
				}
			} else {
				super.appendIgnoringWhitespaceAndPadding(ch);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			appendIgnoringPadding(ch);
		}
	}

	/**
	 * Appends the given character.
	 * <p/>
	 * <p>If the given character is equal to {@link Format#getNormalizedNewline()}, then the character sequence returned by {@link Format#getLineSeparator()} is going to be appended.
	 *
	 * @param ch the character to append
	 */
	@Override
	public void append(char ch) {
		if (ch == newLine && denormalizeLineEndings) {
			appendNewLine();
		} else {
			appendAndExpand(ch);
		}
	}

	/**
	 * Writes the accumulated value to the {@link java.io.Writer}, discarding any trailing whitespace characters identified when using {@link WriterCharAppender#appendIgnoringWhitespace(char)}, {@link WriterCharAppender#appendIgnoringPadding(char)} or {@link WriterCharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the written value will be the {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 *
	 * @param writer the output writer
	 *
	 * @throws IOException if an error occurs while writing to the output.
	 */
	public void writeCharsAndReset(Writer writer) throws IOException {
		if (index - whitespaceCount > 0) {
			writer.write(chars, 0, index - whitespaceCount);
		} else if (emptyChars != null) {
			writer.write(emptyChars, 0, emptyChars.length);
		}
		index = 0;
		whitespaceCount = 0;
	}

	/**
	 * Appends the newline character sequence specified in {@link Format#getLineSeparator()}
	 */
	public void appendNewLine() {
		if (index + 2 >= chars.length) {
			expand();
		}
		chars[index++] = lineSeparator1;
		if (lineSeparator2 != '\0') {
			chars[index++] = lineSeparator2;
		}
	}

	@Override
	public void fill(char ch, int length) {
		while (index + length > chars.length) {
			expand(length);
		}
		for (int i = 0; i < length; i++) {
			chars[index++] = ch;
		}
	}

	private void appendAndExpand(char ch) {
		try {
			chars[index++] = ch;
		} catch (ArrayIndexOutOfBoundsException e) {
			expand();
			chars[index] = ch;
		}
	}

	private void expandAndRetry() {
		expand();
		index--;
	}

	private void expand() {
		chars = Arrays.copyOf(chars, (int) ((double) chars.length * 1.5));
	}

	private void expand(int additionalLength) {
		chars = Arrays.copyOf(chars, (int) ((double) (index + additionalLength) * 1.5));
	}

	public void append(DefaultCharAppender appender) {
		try {
			super.append(appender);
		} catch (ArrayIndexOutOfBoundsException e) {
			expand(appender.index);
			super.append(appender);
		}
	}

	/**
	 * Configures the appender to allow line ending sequences to be appended as-is, without replacing them by the
	 * normalized line separator character.
	 *
	 * @param enableDenormalizedLineEndings flag indicating whether denormalized line endings are allowed. The writer
	 *                                      won't convert line separators automatically.
	 */
	public void enableDenormalizedLineEndings(boolean enableDenormalizedLineEndings) {
		this.denormalizeLineEndings = enableDenormalizedLineEndings;
	}
}
