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
package com.univocity.parsers.common.input;

import java.util.*;

public class DefaultCharAppender implements CharAppender {

	final char[] emptyChars; // default value to return when no characters have been accumulated
	final char[] chars;
	final char padding;
	int index = 0;
	final String emptyValue; // default value to return when no characters have been accumulated
	int whitespaceCount = 0;

	/**
	 * Creates a DefaultCharAppender with a maximum limit of characters to append and the default value to return when no characters have been accumulated.
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param maxLength maximum limit of characters to append
	 * @param emptyValue default value to return when no characters have been accumulated
	 */
	public DefaultCharAppender(int maxLength, String emptyValue) {
		this(maxLength, emptyValue, ' ');
	}

	/**
	 * Creates a DefaultCharAppender with a maximum limit of characters to append, the default value to return when no characters have been accumulated, and the padding character to ignore when calling {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char)}.
	 *
	 * @param maxLength maximum limit of characters to append
	 * @param emptyValue default value to return when no characters have been accumulated
	 * @param padding the padding character to ignore when calling {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char)}.
	 */
	public DefaultCharAppender(int maxLength, String emptyValue, char padding) {
		this.chars = new char[maxLength];
		this.emptyValue = emptyValue;
		this.padding = padding;

		if (emptyValue == null) {
			emptyChars = null;
		} else {
			emptyChars = emptyValue.toCharArray();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendIgnoringWhitespaceAndPadding(char ch) {
		if (ch <= ' ' || ch == padding) {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
		chars[index++] = ch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendIgnoringPadding(char ch) {
		if (ch == padding) {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
		chars[index++] = ch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void appendIgnoringWhitespace(char ch) {
		if (ch <= ' ') {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
		chars[index++] = ch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void append(char ch) {
		chars[index++] = ch;
	}

	/**
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 * @return a String containing the accumulated characters without the trailing white spaces. Or the {@link DefaultCharAppender#emptyValue} defined in the constructor of this class.
	 */
	@Override
	public String getAndReset() {
		String out = emptyValue;
		if (index > whitespaceCount) {
			out = new String(chars, 0, index - whitespaceCount);
		}
		index = 0;
		whitespaceCount = 0;
		return out;
	}

	/**
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> Does not discard the accumulated value.
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 * @return a String containing the accumulated characters without the trailing white spaces. Or the {@link DefaultCharAppender#emptyValue} defined in the constructor of this class.
	 */
	@Override
	public String toString() {
		if (index <= whitespaceCount) {
			return emptyValue;
		}
		return new String(chars, 0, index - whitespaceCount);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int length() {
		return index - whitespaceCount;
	}

	/**
	 * Returns the accumulated characters, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be character sequence of the {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 * @return a character array containing the accumulated characters without the trailing white spaces. Or the characters of the {@link DefaultCharAppender#emptyValue} defined in the constructor of this class.
	 */
	@Override
	public char[] getCharsAndReset() {
		char[] out = emptyChars;
		if (index > whitespaceCount) {
			out = Arrays.copyOf(chars, index - whitespaceCount);
		}
		index = 0;
		whitespaceCount = 0;
		return out;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int whitespaceCount() {
		return whitespaceCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void reset() {
		index = 0;
		whitespaceCount = 0;
	}

	/**
	 * Appends the contents of another DefaultCharAppender, discarding any of its trailing whitespace characters
	 * @param appender The DefaultCharAppender instance got get contents from.
	 */
	public void append(DefaultCharAppender appender) {
		System.arraycopy(appender.chars, 0, this.chars, this.index, appender.index - appender.whitespaceCount);
		this.index += appender.index - appender.whitespaceCount;
		appender.reset();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resetWhitespaceCount() {
		whitespaceCount = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public char[] getChars() {
		return chars;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fill(char ch, int length) {
		for (int i = 0; i < length; i++) {
			chars[index++] = ch;
		}
	}
}
