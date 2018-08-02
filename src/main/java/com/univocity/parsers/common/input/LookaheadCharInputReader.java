/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import java.io.*;
import java.util.*;

/**
 * A special implementation of {@link CharInputReader} that wraps another {@link CharInputReader} and
 * collects a sequence of characters from the wrapped input, in order to analyze what the buffer contains
 * ahead of the current position.
 */
public class LookaheadCharInputReader implements CharInputReader {

	private final CharInputReader reader;
	private char[] lookahead = new char[0];
	private int length = 0;
	private int start = 0;

	private final char newLine;
	private char delimiter;
	private final int whitespaceRangeStart;

	/**
	 * Creates a lookahead input reader by wrapping a given {@link CharInputReader} implementation
	 *
	 * @param reader               the input reader whose characters will read and stored in a limited internal buffer,
	 *                             in order to allow a parser to query what the characters are available ahead of the current input position.
	 * @param newLine              the normalized character that represents a line ending. Used internally as a stop character.
	 * @param whitespaceRangeStart starting range of characters considered to be whitespace.
	 */
	public LookaheadCharInputReader(CharInputReader reader, char newLine, int whitespaceRangeStart) {
		this.reader = reader;
		this.newLine = newLine;
		this.whitespaceRangeStart = whitespaceRangeStart;
	}

	/**
	 * Matches a sequence of characters against the current lookahead buffer.
	 *
	 * @param current  the last character used by the parser, which should match the first character in the lookahead buffer
	 * @param sequence the expected sequence of characters after the current character, that are expected appear in the current lookahead buffer
	 * @param wildcard character used in the sequence as a wildcard (e.g. * or ?), meaning any character is acceptable in its place.
	 *
	 * @return {@code true} if the current character and the sequence characters that follows are present in the lookahead, otherwise {@code false}
	 */
	public boolean matches(char current, char[] sequence, char wildcard) {
		if (sequence.length > length - start) {
			return false;
		}

		if (sequence[0] != current && sequence[0] != wildcard) {
			return false;
		}

		for (int i = 1; i < sequence.length; i++) {
			char ch = sequence[i];
			if (ch != wildcard && ch != lookahead[i - 1 + start]) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Matches a sequence of characters against the current lookahead buffer.
	 *
	 * @param sequence the expected sequence of characters that are expected appear in the current lookahead buffer
	 * @param wildcard character used in the sequence as a wildcard (e.g. * or ?), meaning any character is acceptable in its place.
	 *
	 * @return {@code true} if the given sequence of characters is present in the lookahead, otherwise {@code false}
	 */
	public boolean matches(char[] sequence, char wildcard) {
		if (sequence.length > length - start) {
			return false;
		}

		for (int i = 0; i < sequence.length; i++) {
			char ch = sequence[i];
			if (ch != wildcard && sequence[i] != lookahead[i + start]) {
				return false;
			}
		}
		return true;

	}

	/**
	 * Returns the current lookahead value.
	 *
	 * @return the current lookahead value, or an empty {@code String} if the lookahead buffer is empty.
	 */
	public String getLookahead() {
		if (start >= length) {
			return "";
		}
		return new String(lookahead, start, length);
	}

	/**
	 * Returns the lookahead value prepended with the current character
	 *
	 * @param current the current character obtained by the parser
	 *
	 * @return a {@code String} formed by the given character followed by the lookahead value (if any).
	 */
	public String getLookahead(char current) {
		if (start >= length) {
			return String.valueOf(current);
		}
		return current + new String(lookahead, start, length - 1);
	}

	/**
	 * Fills the lookahead buffer with a given number of characters that will be extracted from the wrapped {@link CharInputReader}
	 *
	 * @param numberOfCharacters the number of characters to read from the wrapped {@link CharInputReader}, given in the constructor of this class.
	 */
	public void lookahead(int numberOfCharacters) {
		numberOfCharacters += length - start;

		if (lookahead.length < numberOfCharacters) {
			lookahead = Arrays.copyOf(lookahead, numberOfCharacters);
		}

		if (start >= length) {
			start = 0;
			length = 0;
		}

		try {
			numberOfCharacters -= length;
			while (numberOfCharacters-- > 0) {
				lookahead[length] = reader.nextChar();
				length++;
			}
		} catch (EOFException ex) {
			//ignore.
		}
	}

	@Override
	public void start(Reader reader) {
		this.reader.start(reader);
	}

	@Override
	public void stop() {
		this.reader.stop();
	}

	@Override
	public char nextChar() {
		if (start >= length) {
			return reader.nextChar();
		} else {
			return lookahead[start++];
		}
	}

	@Override
	public long charCount() {
		return reader.charCount();
	}

	@Override
	public long lineCount() {
		return reader.lineCount();
	}

	@Override
	public void skipLines(long lineCount) {
		reader.skipLines(lineCount);
	}

	@Override
	public void enableNormalizeLineEndings(boolean escaping) {
		reader.enableNormalizeLineEndings(escaping);
	}

	@Override
	public String readComment() {
		return reader.readComment();
	}

	@Override
	public char[] getLineSeparator() {
		return reader.getLineSeparator();
	}

	@Override
	public final char getChar() {
		if (start != 0 && start >= length) {
			return reader.getChar();
		} else {
			return lookahead[start - 1];
		}
	}

	@Override
	public char skipWhitespace(char ch, char stopChar1, char stopChar2) {
		while (start < length && ch <= ' ' && ch != stopChar1 && ch != newLine && ch != stopChar2 && whitespaceRangeStart < ch) {
			ch = lookahead[start++];
		}
		return reader.skipWhitespace(ch, stopChar1, stopChar2);
	}

	@Override
	public String currentParsedContent() {
		return reader.currentParsedContent();
	}

	@Override
	public void markRecordStart() {
		reader.markRecordStart();
	}

	@Override
	public String getString(char ch, char stop, boolean trim, String nullValue, int maxLength) {
		return reader.getString(ch, stop, trim, nullValue, maxLength);
	}

	@Override
	public String getQuotedString(char quote, char escape, char escapeEscape, int maxLength, char stop1, char stop2, boolean keepQuotes, boolean keepEscape, boolean trimLeading, boolean trimTrailing) {
		return reader.getQuotedString(quote, escape, escapeEscape, maxLength, stop1, stop2, keepQuotes, keepEscape, trimLeading, trimTrailing);
	}

	@Override
	public int currentParsedContentLength() {
		return reader.currentParsedContentLength();
	}

	@Override
	public boolean skipString(char ch, char stop) {
		return reader.skipString(ch, stop);
	}

	@Override
	public boolean skipQuotedString(char quote, char escape, char stop1, char stop2) {
		return reader.skipQuotedString(quote, escape, stop1, stop2);
	}
}
