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
package com.univocity.parsers.common.input;

/**
 * Default implementation of the {@link CharAppender} interface
 */
public class DefaultCharAppender implements CharAppender {

	final int whitespaceRangeStart;
	final char[] emptyChars; // default value to return when no characters have been accumulated
	char[] chars;
	int index;
	final String emptyValue; // default value to return when no characters have been accumulated
	int whitespaceCount;

	/**
	 * Creates a DefaultCharAppender with a maximum limit of characters to append and the default value to return when no characters have been accumulated.
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param maxLength  maximum limit of characters to append
	 * @param emptyValue default value to return when no characters have been accumulated
	 * @param whitespaceRangeStart starting range of characters considered to be whitespace.
	 */
	public DefaultCharAppender(int maxLength, String emptyValue, int whitespaceRangeStart) {
		this.whitespaceRangeStart = whitespaceRangeStart;
		this.chars = new char[maxLength];
		this.emptyValue = emptyValue;

		if (emptyValue == null) {
			emptyChars = null;
		} else {
			emptyChars = emptyValue.toCharArray();
		}
	}

	@Override
	public void appendIgnoringPadding(char ch, char padding) {
		chars[index++] = ch;
		if (ch == padding) {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
	}

	@Override
	public void appendIgnoringWhitespaceAndPadding(char ch, char padding) {
		chars[index++] = ch;
		if (ch == padding || (ch <= ' ' && whitespaceRangeStart < ch)) {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
	}

	@Override
	public void appendIgnoringWhitespace(char ch) {
		chars[index++] = ch;
		if (ch <= ' ' && whitespaceRangeStart < ch) {
			whitespaceCount++;
		} else {
			whitespaceCount = 0;
		}
	}

	@Override
	public int indexOf(char ch, int from) {
		int len = index - whitespaceCount;
		for (int i = from; i < len; i++) {
			if (chars[i] == ch) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int indexOfAny(char[] chars, int from) {
		int len = index - whitespaceCount;
		for (int i = from; i < len; i++) {
			for(int j = 0; j < chars.length; j++){
				if (this.chars[i] == chars[j]) {
					return i;
				}
			}
		}
		return -1;
	}

	@Override
	public String substring(int from, int length) {
		return new String(chars, from, length);
	}

	@Override
	public void remove(int from, int length) {
		if (length > 0) {
			int srcPos = from + length;
			int len = index - length;
			if(srcPos + len > index){
				len = len - from;
			}

			System.arraycopy(chars, srcPos, chars, from, len);
			index -= length;
		}
	}

	@Override
	public void append(char ch) {
		chars[index++] = ch;
	}

	@Override
	public final void append(Object o) {
		append(String.valueOf(o));
	}

	@Override
	public final void append(int ch) {
		if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
			append((char) ch);
		} else {
			int off = ch - Character.MIN_SUPPLEMENTARY_CODE_POINT;
			append((char) ((off >>> 10) + Character.MIN_HIGH_SURROGATE));
			append((char) ((off & 0x3ff) + Character.MIN_LOW_SURROGATE));
		}
	}

	@Override
	public final void append(int[] ch) {
		for (int i = 0; i < ch.length; i++) {
			append(ch[i]);
		}
	}

	/**
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char, char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 *
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
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char, char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> Does not discard the accumulated value.
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 *
	 * @return a String containing the accumulated characters without the trailing white spaces. Or the {@link DefaultCharAppender#emptyValue} defined in the constructor of this class.
	 */
	@Override
	public final String toString() {
		if (index <= whitespaceCount) {
			return emptyValue;
		}
		return new String(chars, 0, index - whitespaceCount);
	}

	@Override
	public final int length() {
		return index - whitespaceCount;
	}

	/**
	 * Returns the accumulated characters, discarding any trailing whitespace characters identified when using {@link DefaultCharAppender#appendIgnoringWhitespace(char)}, {@link DefaultCharAppender#appendIgnoringPadding(char, char)} or {@link DefaultCharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the return value will be character sequence of the {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 *
	 * @return a character array containing the accumulated characters without the trailing white spaces. Or the characters of the {@link DefaultCharAppender#emptyValue} defined in the constructor of this class.
	 */
	@Override
	public char[] getCharsAndReset() {
		char[] out = emptyChars;
		if (index > whitespaceCount) {
			int length = index - whitespaceCount;
			out = new char[length];
			System.arraycopy(chars, 0, out, 0, length);
		}
		index = 0;
		whitespaceCount = 0;
		return out;
	}

	@Override
	public final int whitespaceCount() {
		return whitespaceCount;
	}

	@Override
	public void reset() {
		index = 0;
		whitespaceCount = 0;
	}

	/**
	 * Appends the contents of another DefaultCharAppender, discarding any of its trailing whitespace characters
	 *
	 * @param appender The DefaultCharAppender instance got get contents from.
	 */
	public void append(DefaultCharAppender appender) {
		System.arraycopy(appender.chars, 0, this.chars, this.index, appender.index - appender.whitespaceCount);
		this.index += appender.index - appender.whitespaceCount;
		appender.reset();
	}

	@Override
	public final void resetWhitespaceCount() {
		whitespaceCount = 0;
	}

	@Override
	public final char[] getChars() {
		return chars;
	}

	@Override
	public void fill(char ch, int length) {
		for (int i = 0; i < length; i++) {
			chars[index++] = ch;
		}
	}

	/**
	 * Prepends the current accumulated value with a character
	 *
	 * @param ch the character to prepend in front of the current accumulated value.
	 */
	@Override
	public void prepend(char ch) {
		System.arraycopy(chars, 0, this.chars, 1, index);
		chars[0] = ch;
		index++;
	}

	@Override
	public void prepend(char ch1, char ch2) {
		System.arraycopy(chars, 0, this.chars, 2, index);
		chars[0] = ch1;
		chars[1] = ch2;
		index += 2;
	}

	@Override
	public void prepend(char[] chars) {
		System.arraycopy(this.chars, 0, this.chars, chars.length, index);
		System.arraycopy(chars, 0, this.chars, 0, chars.length);
		index += chars.length;
	}

	/**
	 * Updates the internal whitespace count of this appender to trim trailing whitespaces.
	 */
	public final void updateWhitespace() {
		whitespaceCount = 0;
		for (int i = index - 1; i >= 0 && chars[i] <= ' ' && whitespaceRangeStart < chars[i]; i--, whitespaceCount++)
			;
	}

	public char appendUntil(char ch, CharInput input, char stop) {
		for (; ch != stop; ch = input.nextChar()) {
			chars[index++] = ch;
		}
		return ch;
	}

	public char appendUntil(char ch, CharInput input, char stop1, char stop2) {
		for (; ch != stop1 && ch != stop2; ch = input.nextChar()) {
			chars[index++] = ch;
		}
		return ch;
	}

	public char appendUntil(char ch, CharInput input, char stop1, char stop2, char stop3) {
		for (; ch != stop1 && ch != stop2 && ch != stop3; ch = input.nextChar()) {
			chars[index++] = ch;
		}
		return ch;
	}

	@Override
	public void append(char[] ch, int from, int length) {
		System.arraycopy(ch, from, chars, index, length);
		index += length;
	}

	@Override
	public final void append(char[] ch) {
		append(ch, 0, ch.length);
	}

	public void append(String string, int from, int to) {
		string.getChars(from, to, chars, index);
		index += to - from;
	}

	@Override
	public final void append(String string) {
		append(string, 0, string.length());
	}

	@Override
	public final char charAt(int i) {
		return chars[i];
	}

	@Override
	public final String subSequence(int from, int to) {
		return new String(chars, from, to - from);
	}

	@Override
	public final void ignore(int count) {
		whitespaceCount += count;
	}

	@Override
	public void delete(int count){
		index -= count;
		if(index < 0){
			index = 0;
		}
		whitespaceCount = 0;
	}
}
