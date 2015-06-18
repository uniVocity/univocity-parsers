/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

public class LookaheadCharInputReader implements CharInputReader {

	private final CharInputReader reader;
	private char[] lookahead = new char[0];
	private int length = 0;
	private int start = 0;

	public LookaheadCharInputReader(CharInputReader reader) {
		this.reader = reader;
	}

	public boolean matches(char current, char[] sequence) {
		if (sequence.length > length - start) {
			return false;
		}

		if (sequence[0] != current) {
			return false;
		}

		for (int i = 1; i < sequence.length; i++) {
			if (sequence[i] != lookahead[i - 1 + start]) {
				return false;
			}
		}
		return true;

	}

	public boolean matches(char[] sequence) {
		if (sequence.length > length - start) {
			return false;
		}

		for (int i = 0; i < sequence.length; i++) {
			if (sequence[i] != lookahead[i + start]) {
				return false;
			}
		}
		return true;

	}

	public String getLookahead() {
		if (start >= length) {
			return "";
		}
		return new String(lookahead, start, length);
	}

	public String getLookahead(char current) {
		if (start >= length) {
			return String.valueOf(current);
		}
		return current + new String(lookahead, start, length - 1);
	}

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
	public void skipLines(int lineCount) {
		reader.skipLines(lineCount);
	}

}
