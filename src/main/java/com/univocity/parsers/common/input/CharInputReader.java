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
 * The definition of a character input reader used by all uniVocity-parsers that extend {@link AbstractParser}.
 *
 * <p> This interface declares basic functionalities to provide a common input manipulation structure for all parser classes.
 * <p> Implementations of this interface <b>MUST</b> convert the sequence of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
 *
 * @see com.univocity.parsers.common.Format
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface CharInputReader {

	/**
	 * Initializes the CharInputReader implementation with a {@link java.io.Reader} which provides access to the input.
	 * @param reader A {@link java.io.Reader} that provides access to the input.
	 */
	public void start(Reader reader);

	/**
	 * Stops the CharInputReader from reading characters from the {@link java.io.Reader} provided in {@link CharInputReader#start(Reader)} and closes it.
	 */
	public void stop();

	/**
	 * Returns the next character in the input provided by the active {@link java.io.Reader}.
	 * <p> If the input contains a sequence of newline characters (defined by {@link Format#getLineSeparator()}), this method will automatically converted them to the newline character specified in {@link Format#getNormalizedNewline()}.
	 * <p> A subsequent call to this method will return the character after the newline sequence.
	 * @return the next character in the input. '\0' if there are no more characters in the input or if the CharInputReader was stopped.
	 */
	public char nextChar();

	/**
	 * Returns the number of characters returned by {@link CharInputReader#nextChar()} at any given time.
	 * @return the number of characters returned by {@link CharInputReader#nextChar()}
	 */
	public long charCount();

	/**
	 * Returns the number of newlines read so far.
	 * @return the number of newlines read so far.
	 */
	public long lineCount();

	/**
	 * Skips characters in the input until the given number of lines is discarded.
	 * @param lineCount the number of lines to skip from the current location in the input
	 */
	public void skipLines(int lineCount);
}
