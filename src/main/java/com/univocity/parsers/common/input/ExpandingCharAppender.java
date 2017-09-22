/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * An implementation {@link CharAppender} that expands the internal buffer of characters as required.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class ExpandingCharAppender extends DefaultCharAppender {

	private static final int MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

	/**
	 * Creates an {@code ExpandingCharAppender} a the default value to return when no characters have been accumulated.
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param emptyValue default value to return when no characters have been accumulated
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public ExpandingCharAppender(String emptyValue, int whitespaceRangeStart) {
		this(8192, emptyValue, whitespaceRangeStart);
	}

	/**
	 * Creates an {@code ExpandingCharAppender} a the default value to return when no characters have been accumulated.
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param initialBufferLength the initial length of the internal buffer.
	 * @param emptyValue          default value to return when no characters have been accumulated
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 */
	public ExpandingCharAppender(int initialBufferLength, String emptyValue, int whitespaceRangeStart) {
		super(initialBufferLength, emptyValue, whitespaceRangeStart);
	}

	@Override
	public void appendIgnoringWhitespace(char ch) {
		try {
			super.appendIgnoringWhitespace(ch);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			super.appendIgnoringWhitespace(ch);
		}
	}


	@Override
	public void appendIgnoringPadding(char ch, char padding) {
		try {
			super.appendIgnoringPadding(ch, padding);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			super.appendIgnoringPadding(ch, padding);
		}
	}

	@Override
	public void appendIgnoringWhitespaceAndPadding(char ch, char padding) {
		try {
			super.appendIgnoringWhitespaceAndPadding(ch, padding);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			super.appendIgnoringWhitespaceAndPadding(ch, padding);
		}
	}


	@Override
	public void append(char ch) {
		try {
			super.append(ch);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			super.append(ch);
		}
	}

	@Override
	public final void fill(char ch, int length) {
		try {
			super.fill(ch, length);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			super.fill(ch, length);
		}
	}

	final void expandAndRetry() {
		expand();
		index--;
	}

	private void expand(int additionalLength, double factor) {
		if (chars.length == MAX_ARRAY_LENGTH) {
			throw new TextParsingException(null, "Can't expand internal appender array to over " + MAX_ARRAY_LENGTH + " characters in length.");
		}
		chars = Arrays.copyOf(chars, (int) Math.min(((index + additionalLength) * factor), MAX_ARRAY_LENGTH));
	}

	final void expand() {
		expand(0, 2.0);
	}

	final void expand(int additionalLength) {
		expand(additionalLength, 1.5);
	}

	@Override
	public final void prepend(char ch) {
		try {
			super.prepend(ch);
		} catch (ArrayIndexOutOfBoundsException e) {
			expand();
			super.prepend(ch);
		}
	}

	@Override
	public final void prepend(char ch1, char ch2) {
		try {
			super.prepend(ch1, ch2);
		} catch (ArrayIndexOutOfBoundsException e) {
			expand(2);
			super.prepend(ch1, ch2);
		}
	}

	@Override
	public final void prepend(char[] chars) {
		try {
			super.prepend(chars);
		} catch (ArrayIndexOutOfBoundsException e) {
			expand(chars.length);
			super.prepend(chars);
		}
	}

	public final void append(DefaultCharAppender appender) {
		try {
			super.append(appender);
		} catch (ArrayIndexOutOfBoundsException e) {
			expand(appender.index);
			this.append(appender);
		}
	}

	public final char appendUntil(char ch, CharInput input, char stop) {
		try {
			return super.appendUntil(ch, input, stop);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			return this.appendUntil(input.getChar(), input, stop);
		}
	}


	public final char appendUntil(char ch, CharInput input, char stop1, char stop2) {
		try {
			return super.appendUntil(ch, input, stop1, stop2);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			return this.appendUntil(input.getChar(), input, stop1, stop2);
		}
	}

	public final char appendUntil(char ch, CharInput input, char stop1, char stop2, char stop3) {
		try {
			return super.appendUntil(ch, input, stop1, stop2, stop3);
		} catch (ArrayIndexOutOfBoundsException e) {
			expandAndRetry();
			return this.appendUntil(input.getChar(), input, stop1, stop2, stop3);
		}
	}

	@Override
	public final void append(char[] ch, int from, int length) {
		if (index + length <= chars.length) {
			super.append(ch, from, length);
		} else {
			chars = Arrays.copyOf(chars, Math.min(((chars.length + length + index)), MAX_ARRAY_LENGTH));
			super.append(ch, from, length);
		}
	}

	public final void append(String string, int from, int to) {
		try {
			super.append(string, from, to);
		} catch (IndexOutOfBoundsException e) {
			expand(to - from);
			super.append(string, from, to);
		}
	}
}
