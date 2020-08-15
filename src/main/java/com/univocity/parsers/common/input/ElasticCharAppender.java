package com.univocity.parsers.common.input;

/**
 * A character appender that restores its internal buffer size after expanding to accommodate larger contents.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public class ElasticCharAppender extends ExpandingCharAppender {

	private static final char[] EMPTY_CHAR_ARRAY = new char[0];

	private int defaultLength;

	public ElasticCharAppender(String emptyValue) {
		this(4096, emptyValue);
	}

	public ElasticCharAppender(int defaultLength, String emptyValue) {
		super(defaultLength, emptyValue, 0);
		this.defaultLength = defaultLength;
	}

	@Override
	public String getAndReset() {
		String out = super.getAndReset();

		if (chars.length > defaultLength) {
			chars = new char[defaultLength];
		}
		return out;
	}

	@Override
	public char[] getCharsAndReset() {
		char[] out = super.getCharsAndReset();

		if (chars.length > defaultLength) {
			chars = new char[defaultLength];
		}
		return out;
	}

	@Override
	public void reset() {
		if (chars.length > defaultLength) {
			chars = new char[defaultLength];
		}
		super.reset();
	}


	public String getTrimmedStringAndReset() {
		int length = index - whitespaceCount;

		int start = 0;
		while (start < length && chars[start] <= ' ') {
			start++;
		}
		if (start >= length) {
			return emptyValue;
		}

		while (chars[length - 1] <= ' ') {
			length--;
		}
		length -= start;
		if (length <= 0) {
			return emptyValue;
		}

		String out = new String(chars, start, length);
		reset();
		return out;
	}

	public char[] getTrimmedCharsAndReset() {
		int length = index - whitespaceCount;

		int start = 0;
		while (start < length && chars[start] <= ' ') {
			start++;
		}
		if (start >= length) {
			return EMPTY_CHAR_ARRAY;
		}

		while (chars[length - 1] <= ' ') {
			length--;
		}
		length -= start;
		if (length <= 0) {
			return EMPTY_CHAR_ARRAY;
		}

		char[] out = new char[length];
		System.arraycopy(chars, start, out, 0, length);
		reset();
		return out;
	}
}
