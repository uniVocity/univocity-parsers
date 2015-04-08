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
package com.univocity.parsers.common;

import java.io.*;

/**
 * A simple Reader implementation to enable parsers to process lines on demand, via {@link AbstractParser#parseLine(String)}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
class LineReader extends Reader {

	private String line;
	private int length;
	private int next = 0;

	public LineReader() {

	}

	public void setLine(String line) {
		this.line = line;
		this.length = line.length();
		this.next = 0;
	}

	@Override
	public int read(char cbuf[], int off, int len) {
		if (len == 0) {
			return 0;
		}
		if (next >= length) {
			return -1;
		}
		int read = Math.min(length - next, len);
		line.getChars(next, next + read, cbuf, off);
		next += read;
		return read;
	}

	@Override
	public long skip(long ns) {
		return 0;
	}

	@Override
	public boolean ready() {
		return line != null;
	}

	@Override
	public boolean markSupported() {
		return false;
	}

	@Override
	public void close() {
		line = null;
	}
}
