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

import com.univocity.parsers.common.*;

import java.io.*;

/**
 * The definition of a character input reader used by all univocity-parsers that extend {@link AbstractParser}.
 *
 * <p> This interface declares basic functionalities to provide a common input manipulation structure for all parser classes.
 * <p> Implementations of this interface <b>MUST</b> convert the sequence of newline characters defined by {@link Format#getLineSeparator()} into the normalized newline character provided in {@link Format#getNormalizedNewline()}.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.univocity.parsers.common.Format
 */
public interface CharInputReader extends CharInput {

	/**
	 * Initializes the CharInputReader implementation with a {@link java.io.Reader} which provides access to the input.
	 *
	 * @param reader A {@link java.io.Reader} that provides access to the input.
	 */
	void start(Reader reader);

	/**
	 * Stops the CharInputReader from reading characters from the {@link java.io.Reader} provided in {@link CharInputReader#start(Reader)} and closes it.
	 */
	void stop();

	/**
	 * Returns the next character in the input provided by the active {@link java.io.Reader}.
	 * <p> If the input contains a sequence of newline characters (defined by {@link Format#getLineSeparator()}), this method will automatically converted them to the newline character specified in {@link Format#getNormalizedNewline()}.
	 * <p> A subsequent call to this method will return the character after the newline sequence.
	 *
	 * @return the next character in the input. '\0' if there are no more characters in the input or if the CharInputReader was stopped.
	 */
	char nextChar();

	/**
	 * Returns the last character returned by the {@link #nextChar()} method.
	 *
	 * @return the last character returned by the {@link #nextChar()} method.'\0' if there are no more characters in the input or if the CharInputReader was stopped.
	 */
	char getChar();

	/**
	 * Returns the number of characters returned by {@link CharInputReader#nextChar()} at any given time.
	 *
	 * @return the number of characters returned by {@link CharInputReader#nextChar()}
	 */
	long charCount();

	/**
	 * Returns the number of newlines read so far.
	 *
	 * @return the number of newlines read so far.
	 */
	long lineCount();

	/**
	 * Skips characters in the input until the given number of lines is discarded.
	 *
	 * @param lineCount the number of lines to skip from the current location in the input
	 */
	void skipLines(long lineCount);

	/**
	 * Collects the comment line found on the input.
	 *
	 * @return the text found in the comment from the current position.
	 */
	String readComment();

	/**
	 * Indicates to the input reader that the parser is running in "escape" mode and
	 * new lines should be returned as-is to prevent modifying the content of the parsed value.
	 *
	 * @param escaping flag indicating that the parser is escaping values and line separators are to be returned as-is.
	 */
	void enableNormalizeLineEndings(boolean escaping);

	/**
	 * Returns the line separator by this character input reader. This could be the line separator defined
	 * in the {@link Format#getLineSeparator()} configuration, or the line separator sequence identified automatically
	 * when {@link CommonParserSettings#isLineSeparatorDetectionEnabled()} evaluates to {@code true}.
	 *
	 * @return the line separator in use.
	 */
	char[] getLineSeparator();

	/**
	 * Skips characters from the current input position, until a non-whitespace character, or a stop character is found
	 *
	 * @param current   the current character of the input
	 * @param stopChar1 the first stop character (which can be a whitespace)
	 * @param stopChar2 the second character (which can be a whitespace)
	 *
	 * @return the first non-whitespace character (or delimiter) found in the input.
	 */
	char skipWhitespace(char current, char stopChar1, char stopChar2);

	/**
	 * Returns the length of the character sequence parsed to produce the current record.
	 * @return the length of the text content parsed for the current input record
	 */
	int currentParsedContentLength();

	/**
	 * Returns a String with the input character sequence parsed to produce the current record.
	 *
	 * @return the text content parsed for the current input record.
	 */
	String currentParsedContent();

	/**
	 * Marks the start of a new record in the input, used internally to calculate the result of {@link #currentParsedContent()}
	 */
	void markRecordStart();

	/**
	 * Attempts to collect a {@code String} from the current position until a stop character is found on the input,
	 * or a line ending is reached. If the {@code String} can be obtained, the current position of the parser will be updated to
	 * the last consumed character. If the internal buffer needs to be reloaded, this method will return {@code null}
	 * and the current position of the buffer will remain unchanged.
	 *
	 * @param ch        the current character to be considered. If equal to the stop character the {@code nullValue} will be returned
	 * @param stop      the stop character that identifies the end of the content to be collected
	 * @param trim      flag indicating whether or not trailing whitespaces should be discarded
	 * @param nullValue value to return when the length of the content to be returned is {@code 0}.
	 * @param maxLength the maximum length of the {@code String} to be returned. If the length exceeds this limit, {@code null} will be returned
	 *
	 * @return the {@code String} found on the input, or {@code null} if the buffer needs to reloaded or the maximum length has been exceeded.
	 */
	String getString(char ch, char stop, boolean trim, String nullValue, int maxLength);

	/**
	 * Attempts to skip a {@code String} from the current position until a stop character is found on the input,
	 * or a line ending is reached. If the {@code String} can be skipped, the current position of the parser will be updated to
	 * the last consumed character. If the internal buffer needs to be reloaded, this method will return {@code false}
	 * and the current position of the buffer will remain unchanged.
	 *
	 * @param ch        the current character to be considered. If equal to the stop character {@code false} will be returned
	 * @param stop      the stop character that identifies the end of the content to be collected
	 *
	 * @return {@code true} if an entire {@code String} value was found on the input and skipped, or {@code false} if the buffer needs to reloaded.
	 */
	boolean skipString(char ch, char stop);

	/**
	 * Attempts to collect a quoted {@code String} from the current position until a closing quote or stop character is found on the input,
	 * or a line ending is reached. If the {@code String} can be obtained, the current position of the parser will be updated to
	 * the last consumed character. If the internal buffer needs to be reloaded, this method will return {@code null}
	 * and the current position of the buffer will remain unchanged.
	 *
	 * @param quote the quote character
	 * @param escape the quote escape character
	 * @param escapeEscape the escape of the quote escape character
	 * @param maxLength the maximum length of the {@code String} to be returned. If the length exceeds this limit, {@code null} will be returned
	 * @param stop1 the first stop character that identifies the end of the content to be collected
	 * @param stop2 the second stop character that identifies the end of the content to be collected
	 * @param keepQuotes flag to indicate the quotes that wrap the resulting {@code String} should be kept.
	 * @param keepEscape flag to indicate that escape sequences should be kept
	 * @param trimLeading flag to indicate leading whitespaces should be trimmed
	 * @param trimTrailing flag to indicate that trailing whitespaces should be trimmed
	 * @return the {@code String} found on the input, or {@code null} if the buffer needs to reloaded or the maximum length has been exceeded.
	 */
	String getQuotedString(char quote, char escape, char escapeEscape, int maxLength, char stop1, char stop2, boolean keepQuotes, boolean keepEscape, boolean trimLeading, boolean trimTrailing);

	/**
	 * Attempts to skip a quoted {@code String} from the current position until a stop character is found on the input,
	 * or a line ending is reached. If the {@code String} can be skipped, the current position of the parser will be updated to
	 * the last consumed character. If the internal buffer needs to be reloaded, this method will return {@code false}
	 * and the current position of the buffer will remain unchanged.
	 *
	 * @param quote the quote character
	 * @param escape the quote escape character
	 * @param stop1 the first stop character that identifies the end of the content to be collected
	 * @param stop2 the second stop character that identifies the end of the content to be collected
	 *
	 * @return {@code true} if an entire {@code String} value was found on the input and skipped, or {@code false} if the buffer needs to reloaded.
	 */
	boolean skipQuotedString(char quote, char escape, char stop1, char stop2);
}