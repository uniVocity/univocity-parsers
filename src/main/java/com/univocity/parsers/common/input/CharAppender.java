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
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public interface CharAppender extends CharSequence {

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
	 * Returns first the position of a given character
	 * @param ch the character to look for
	 * @param from the starting index from where the search will begin.
	 * @return the position of the given character in the appended content, {@code -1} if not found
	 */
	int indexOf(char ch, int from);

	/**
	 * Returns the first position of any given character
	 * @param chars the characters to look for
	 * @param from the starting index from where the search will begin.
	 * @return the position any one of the given characters in the appended content, {@code -1} if none found
	 */
	int indexOfAny(char[] chars, int from);

	/**
	 * Returns a section of the appended content
	 * @param from the starting position in the buffer
	 * @param length the number of characters to accumulate from the given start position
	 * @return a {@code String} with the section of characters accumulated by this appender.
	 */
	String substring(int from, int length);

	/**
	 * Removes a section from the appended content
	 * @param from the starting position in the buffer (inclusive)
	 * @param length the number of characters to accumulate from the given start position
	 */
	void remove(int from, int length);

	/**
	 * Appends the given codepoint.
	 * @param ch the codepoint to append
	 */
	void append(int ch);

	/**
	 * Appends the {@code String} representation of a given object.
	 * @param obj the object whose {@code String} representation will be appended.
	 */
	void append(Object obj);

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
	 * Prepends the current accumulated value with a couple of characters
	 * @param ch1 the first character to prepend in front of the current accumulated value.
	 * @param ch2 the second character to prepend in front of the current accumulated value.
	 */
	void prepend(char ch1, char ch2);


	/**
	 * Prepends the current accumulated value a sequence of characters
	 * @param chars the character sequence to prepend in front of the current accumulated value.
	 */
	void prepend(char[] chars);

	/**
	 * Updates the internal whitespace count of this appender to trim trailing whitespaces.
	 */
	void updateWhitespace();

	/**
	 * Appends characters from the input, until a stop character is found
	 * @param ch the first character of the input to be appended.
	 * @param input the input whose the following characters will be appended
	 * @param stop the stop character
	 * @return the stop character found on the input.
	 */
	char appendUntil(char ch, CharInput input, char stop);

	/**
	 * Appends characters from the input, until a stop character is found
	 * @param ch the first character of the input to be appended.
	 * @param input the input whose the following characters will be appended
	 * @param stop1 the first stop character
	 * @param stop2 the second stop character
	 * @return one of the stop characters found on the input.
	 */
	char appendUntil(char ch, CharInput input, char stop1, char stop2);

	/**
	 * Appends characters from the input, until a stop character is found
	 * @param ch the first character of the input to be appended.
	 * @param input the input whose the following characters will be appended
	 * @param stop1 the first stop character
	 * @param stop2 the second stop character
	 * @param stop3 the third stop character
	 * @return one of the stop characters found on the input.
	 */
	char appendUntil(char ch, CharInput input, char stop1, char stop2, char stop3);


	/**
	 * Appends characters from an input array
	 * @param ch the character array
	 * @param from the position of the first character in the array to be appended
	 * @param length the number of characters to be appended from the given posiion.
	 */
	void append(char[] ch, int from, int length);

	/**
	 * Appends characters from an input array
	 * @param ch the character array
	 */
	void append(char[] ch);

	/**
	 * Appends codepoints from an input array
	 * @param ch the codepoint array
	 */
	void append(int[] ch);


	/**
	 * Appends characters from an input {@code String}
	 * @param string the input String
	 */
	void append(String string);

	/**
	 * Appends the contents of a String to this appender
	 *
	 * @param string the string whose characters will be appended.
	 * @param from   the index of the first character to append
	 * @param to     the index of the last character to append
	 */
	void append(String string, int from, int to);

	/**
	 * Ignores the given number of characters at the end of the appended content,
	 * effectively marking these as whitespace. Invoking {@link #resetWhitespaceCount()}
	 * or {@link #updateWhitespace()} will undo this effect.
	 *
	 * @param count the number of characters to ignore
	 */
	void ignore(int count);

	/**
	 * Deletes a given number of characters from the end of the appended content.
	 * Will reset the internal whitespace count if any. Invoke {@link #updateWhitespace()}
	 * to recalculate the number of trailing whitespaces in the appended content.
	 * @param count the number of characters to delete.
	 */
	void delete(int count);
}
