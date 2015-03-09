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

import java.io.*;

import com.univocity.parsers.common.*;

/**
 *
 * The base class for implementing different flavours of {@link CharInputReader}.
 *
 * <p> It provides the essential conversion of sequences of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
 * <p> It also provides a default implementation for most of the methods specified by the {@link CharInputReader} interface.
 * <p> Extending classes must essentially read characters from a given {@link java.io.Reader} and assign it to the public {@link AbstractCharInputReader#buffer} when requested (in the {@link AbstractCharInputReader#reloadBuffer()} method).
 *
 * @see com.univocity.parsers.common.Format
 * @see com.univocity.parsers.common.input.DefaultCharInputReader
 * @see com.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */

public abstract class AbstractCharInputReader implements CharInputReader {

	private boolean lineSeparatorDefined;
	private final boolean detectLineSeparator;
	private char lineSeparator1;
	private char lineSeparator2;
	private final char normalizedLineSeparator;

	private long lineCount;
	private long charCount;

	public int i;
	public char[] buffer;
	public int length = -1;

	/**
	 * Creates a new instance that attempts to detect the newlines used in the input automatically.
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 */
	public AbstractCharInputReader(char normalizedLineSeparator) {
		this.detectLineSeparator = true;
		this.lineSeparator1 = '\0';
		this.lineSeparator2 = '\0';
		this.normalizedLineSeparator = normalizedLineSeparator;
	}

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 * @param lineSeparator the sequence of characters that represent a newline, as defined in {@link Format#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 */
	public AbstractCharInputReader(char[] lineSeparator, char normalizedLineSeparator) {
		this.detectLineSeparator = false;
		if (lineSeparator == null || lineSeparator.length == 0) {
			throw new IllegalArgumentException("Invalid line separator. Expected 1 to 2 characters");
		}
		if (lineSeparator.length > 2) {
			throw new IllegalArgumentException("Invalid line separator. Up to 2 characters are expected. Got " + lineSeparator.length + " characters.");
		}
		this.lineSeparator1 = lineSeparator[0];
		this.lineSeparator2 = lineSeparator.length == 2 ? lineSeparator[1] : '\0';
		this.normalizedLineSeparator = normalizedLineSeparator;
	}

	/**
	 * Passes the {@link java.io.Reader} provided in the {@link AbstractCharInputReader#start(Reader)} method to the extending class so it can begin loading characters from it.
	 * @param reader the {@link java.io.Reader} provided in {@link AbstractCharInputReader#start(Reader)}
	 */
	protected abstract void setReader(Reader reader);

	/**
	 * Informs the extending class that the buffer has been read entirely and requests for another batch of characters.
	 * Implementors must assign the new character buffer to the public {@link AbstractCharInputReader#buffer} attribute, as well as the number of characters available to the public {@link AbstractCharInputReader#length} attribute.
	 * To notify the input does not have any more characters, {@link AbstractCharInputReader#length} must receive the <b>-1</b> value
	 */
	protected abstract void reloadBuffer();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void start(Reader reader) {
		stop();
		setReader(reader);
		lineSeparatorDefined = false;
		lineCount = 0;

		updateBuffer();
		if (length > 0) {
			i++;
		}
	}

	/**
	 * Requests the next batch of characters from the implementing class and updates
	 * the character count.
	 *
	 * <p> If there are no more characters in the input, the reading will stop by invoking the {@link AbstractCharInputReader#stop()} method.
	 */
	private final void updateBuffer() {
		reloadBuffer();

		charCount += i;
		i = 0;

		if (length == -1) {
			stop();
		}

		if (detectLineSeparator && !lineSeparatorDefined) {
			detectLineSeparator();
		}
	}

	/**
	 * Detects the line separator used in the input automatically by traversing the character buffer
	 */
	private final void detectLineSeparator() {
		char separator1 = '\0';
		char separator2 = '\0';
		for (int c = 0; c < length; c++) {
			char ch = buffer[c];
			if (ch == '\n' || ch == '\r') {
				if (separator1 == '\0') {
					separator1 = ch;
				} else {
					separator2 = ch;
					break;
				}
			} else if (separator1 != '\0') {
				break;
			}
		}

		if (separator1 != '\0') {
			if (separator1 == '\n') {
				this.lineSeparator1 = '\n';
				this.lineSeparator2 = '\0';
			} else if (separator1 == '\r') {
				this.lineSeparator1 = '\r';
				if (separator2 == '\n') {
					this.lineSeparator2 = '\n';
				} else {
					this.lineSeparator2 = '\0';
				}
			}
			this.lineSeparatorDefined = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final char nextChar() {
		if (length == -1) {
			throw new EOFException();
		}

		char ch = buffer[i - 1];

		if (i >= length) {
			if (length != -1) {
				updateBuffer();
			} else {
				throw new EOFException();
			}
		}

		i++;

		if (lineSeparator1 == ch && (lineSeparator2 == '\0' || lineSeparator2 == buffer[i - 1])) {
			lineCount++;
			if (lineSeparator2 != '\0') {
				ch = normalizedLineSeparator;

				if (i >= length) {
					if (length != -1) {
						updateBuffer();
					} else {
						throw new EOFException();
					}
				}

				if (i < length) {
					i++;
				}
			} else {
				return normalizedLineSeparator;
			}
		}

		return ch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long lineCount() {
		return lineCount;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void skipLines(int lines) {
		if (lines < 1) {
			return;
		}
		long expectedLineCount = this.lineCount + lines;

		try {
			do {
				nextChar();
			} while (lineCount < expectedLineCount);
			if (lineCount < lines) {
				throw new IllegalArgumentException("Unable to skip " + lines + " lines from line " + (expectedLineCount - lines) + ". End of input reached");
			}
		} catch (EOFException ex) {
			throw new IllegalArgumentException("Unable to skip " + lines + " lines from line " + (expectedLineCount - lines) + ". End of input reached");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final long charCount() {
		return charCount + i;
	}
}
