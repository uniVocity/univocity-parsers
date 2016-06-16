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

/**
 *
 * The general interface for classes responsible for appending characters efficiently while handling whitespaces and padding characters.
 *
 * <p> Calls to {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} and {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)} should accumulate the
 * given character and only discard whitespaces/padding if no non-whitespace is appended:
 *
 * <p> For example:
 *
 * 	<hr><blockquote><pre>
 * append('a');                   // accumulated value is now "a";        whitespaceCount = 0;
 * appendIgnoringWhitespace('b'); // accumulated value is now "ab";       whitespaceCount = 0;
 * appendIgnoringWhitespace(' '); // accumulated value remains "ab";      whitespaceCount = 1;
 * appendIgnoringWhitespace(' '); // accumulated value remains "ab";      whitespaceCount = 2;
 * appendIgnoringWhitespace('c'); // accumulated value is now "ab  c";    whitespaceCount = 0;
 * appendIgnoringWhitespace(' '); // accumulated value remains "ab  c";   whitespaceCount = 1;
 * appendIgnoringWhitespace('d'); // accumulated value is now "ab  c d";  whitespaceCount = 0;
 * append(' ');					  // accumulated value is now "ab  c d "; whitespaceCount = 0;
 *  </pre></blockquote><hr>
 *
 * <p> <b>Implementation note:</b> White spaces should be identified as any character {@code <= ' '}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface CharAppender {

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '})
	 * @param ch character to append
	 */
	void appendIgnoringWhitespace(char ch);

	/**
	 * Appends the given character and marks it as ignored if it is a padding character (the definition of a padding character is implementation dependent.)
	 * @param ch character to append
	 * @param padding the padding character to ignore
	 */
	void appendIgnoringPadding(char ch, char padding);

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '}) or a padding character (the definition of a padding character is implementation dependent.)
	 * @param ch character to append
	 * @param padding the padding character to ignore
	 */
	void appendIgnoringWhitespaceAndPadding(char ch, char padding);

	/**
	 * Appends the given character.
	 * @param ch the character to append
	 */
	void append(char ch);

	/**
	 * Returns the current accumulated value length (the sum of all appended characters - whitespaceCount).
	 * @return the current accumulated value length (the sum of all appended characters - whitespaceCount).
	 */
	int length();

	/**
	 * Returns the current number of whitespaces accumulated after the last non-whitespace character.
	 * <p> This is the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * @return the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 */
	int whitespaceCount();

	/**
	 * Resets the number of whitespaces accumulated after the last non-whitespace character.
	 * <p> This is the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> A subsequent call to {@link CharAppender#whitespaceCount()} should return 0.
	 */
	void resetWhitespaceCount();

	/**
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link CharAppender#reset()})
	 * @return a String containing the accumulated characters without the trailing whitespaces.
	 */
	String getAndReset();

	/**
	 * Clears the accumulated value and the whitespace count.
	 */
	void reset();

	/**
	 * Returns the accumulated characters, discarding any trailing whitespace characters identified when using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char, char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link CharAppender#reset()})
	 * @return a character array containing the accumulated characters without the trailing whitespaces.
	 */
	char[] getCharsAndReset();

	/**
	 * Returns the internal character array.
	 * @return the internal character array.
	 */
	char[] getChars();

	/**
	 * Adds a sequence of repeated characters to the input.
	 * @param ch the character to append
	 * @param length the number of times the given character should be appended.
	 */
	void fill(char ch, int length);

	/**
	 * Prepends the current accumulated value with a character
	 * @param ch the character to prepend in front of the current accumulated value.
	 */
	void prepend(char ch);

	/**
	 * Updates the internal whitespace count of this appender to trim trailing whitespaces.
	 */
	void updateWhitespace();

	/**
	 * Appends characters from the input, until a stop character is found
	 * @param ch the first character of the input to be appended.
	 * @param input the input whose the following characters will be appended
	 * @param stop1 the first stop character
	 * @param stop2 the second stop character
	 * @return one of the stop characters found on the input.
	 */
	char appendUntil(char ch, CharInputReader input, char stop1, char stop2);

	/**
	 * Appends characters from the input, until a stop character is found
	 * @param ch the first character of the input to be appended.
	 * @param input the input whose the following characters will be appended
	 * @param stop1 the first stop character
	 * @param stop2 the second stop character
	 * @param stop3 the third stop character
	 * @return one of the stop characters found on the input.
	 */
	char appendUntil(char ch, CharInputReader input, char stop1, char stop2, char stop3);
}
