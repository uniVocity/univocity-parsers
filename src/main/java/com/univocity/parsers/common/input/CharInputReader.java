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
* The base class for implementing different strategies of character input reading.
* 
* <p> This interface declares basic functionalities to provide a common input manipulation structure for all parser classes.
* <p> It provides the essential conversion of sequences of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
* <p> It also provides a default implementation for most of the methods specified by the {@link CharInputReader} interface.
* <p> Extending classes must essentially read characters from a given {@link java.io.Reader} and assign it to the public {@link CharInputReader#buffer} when requested (in the {@link CharInputReader#reloadBuffer()} method).
* <p> This class converts the sequence of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
* 
* @see com.univocity.parsers.common.Format
* @see com.univocity.parsers.common.input.DefaultCharInputReader
* @see com.univocity.parsers.common.input.concurrent.ConcurrentCharInputReader
*
* @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
*
*/
public abstract class CharInputReader {

	private final char lineSeparator1;
	private final char lineSeparator2;
	private final char normalizedLineSeparator;

	private char next = '\0';

	private int lineCount;
	private int charCount;
	private int i;

	public char[] buffer;
	public int length = -1;
	
	private char ch = '\0';
	public boolean whitespace;
	
	public boolean end = false;
	public boolean live = true;
	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 * @param lineSeparator the sequence of characters that represent a newline, as defined in {@link Format#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link Format#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 */
	public CharInputReader(char[] lineSeparator, char normalizedLineSeparator) {
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
	 * Passes the {@link java.io.Reader} provided in the {@link CharInputReader#start(Reader)} method to the extending class so it can begin loading characters from it.
	 * @param reader the {@link java.io.Reader} provided in {@link CharInputReader#start(Reader)}
	 */
	protected abstract void setReader(Reader reader);

	/**
	 * Informs the extending class that the buffer has been read entirely and requests for another batch of characters.
	 * Implementors must assign the new character buffer to the public {@link CharInputReader#buffer} attribute, as well as the number of characters available to the public {@link CharInputReader#length} attribute.
	 * To notify the input does not have any more characters, {@link CharInputReader#length} must receive the <b>-1</b> value
	 */
	protected abstract void reloadBuffer();

	/**
	 * Stops the CharInputReader from reading characters from the {@link java.io.Reader} provided in {@link CharInputReader#start(Reader)} and closes it.
	 */
	public abstract void stop();

	
	/**
	 * Initializes the CharInputReader implementation with a {@link java.io.Reader} which provides access to the input.
	 * @param reader A {@link java.io.Reader} that provides access to the input.
	 */
	public final void start(Reader reader) {
		end = false;
		live = true;
		ch = '\0';
		
		stop();
		setReader(reader);
		lineCount = 0;

		updateBuffer();
		if (length > 0) {
			next = buffer[i++];
			whitespace = next <= ' ';
		}
	}

	/**
	 * Requests the next batch of characters from the implementing class and updates
	 * the character count.
	 *
	 * <p> If there are no more characters in the input, the reading will stop by invoking the {@link CharInputReader#stop()} method.
	 */
	private final void updateBuffer() {
		reloadBuffer();

		charCount += i;
		i = 0;

		if (length == -1) {
			stop();
		}
	}

	/**
	 * Updates the next character in the input provided by the active {@link java.io.Reader}.
	 * <p> If the input contains a sequence of newline characters (defined by {@link Format#getLineSeparator()}), this method will automatically converted them to the newline character specified in {@link Format#getNormalizedNewline()}.
	 * <p> A subsequent call to this method will return the character after the newline sequence.
	 * @return the next character in the input.
	 */
	public final char nextChar() {
		ch = next;

		if (i >= length) {
			if (length != -1) {
				updateBuffer();
			} else {
				end = true;
				live = false;
				ch = '\0';
				return ch;
			}
		}

		next = buffer[i++];

		if (lineSeparator1 == ch && (lineSeparator2 == '\0' || lineSeparator2 == next)) {
			lineCount++;
			if (lineSeparator2 != '\0') {
				ch = normalizedLineSeparator;

				if (i >= length) {
					if (length != -1) {
						updateBuffer();
					} else {
						end = true;
						live = false;
						ch = '\0';
						return ch;
					}
				}

				if (i < length) {
					next = buffer[i++];
				}
			}
		}

		whitespace = ch <= ' ';
		return ch;
	}


	/**
	 * Returns the number of newlines read so far.
	 * @return the number of newlines read so far.
	 */
	public final int lineCount() {
		return lineCount;
	}

	/**
	 * Skips characters in the input until the given number of lines is discarded.
	 * @param lines the number of lines to skip from the current location in the input
	 */
	public final void skipLines(int lines) {
		if (lines < 1) {
			return;
		}
		int expectedLineCount = this.lineCount + lines;

		do {
			nextChar();
		} while (lineCount < expectedLineCount && !end);
		if (end && lineCount < lines) {
			throw new IllegalArgumentException("Unable to skip " + lines + " lines from line " + (expectedLineCount - lines) + ". End of input reached");
		}
	}

	
	/**
	 * Returns the number of characters returned by {@link CharInputReader#nextChar()} at any given time.
	 * @return the number of characters returned by {@link CharInputReader#nextChar()}
	 */
	public final int charCount() {
		return charCount + i;
	}
}
