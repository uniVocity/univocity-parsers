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
 * <p> Calls to {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} and {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)} should accumulate the
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
	public void appendIgnoringWhitespace(char ch);

	/**
	 * Appends the given character and marks it as ignored if it is a padding character (the definition of a padding character is implementation dependent.)
	 * @param ch character to append
	 */
	public void appendIgnoringPadding(char ch);

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '}) or a padding character (the definition of a padding character is implementation dependent.)
	 * @param ch character to append
	 */
	public void appendIgnoringWhitespaceAndPadding(char ch);

	/**
	 * Appends the given character.
	 * @param ch the character to append
	 */
	public void append(char ch);

	/**
	 * Returns the current accumulated value length (the sum of all appended characters - whitespaceCount).
	 * @return the current accumulated value length (the sum of all appended characters - whitespaceCount).
	 */
	public int length();

	/**
	 * Returns the current number of whitespaces accumulated after the last non-whitespace character.
	 * <p> This is the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * @return the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 */
	public int whitespaceCount();

	/**
	 * Resets the number of whitespaces accumulated after the last non-whitespace character.
	 * <p> This is the number of whitespaces accumulated using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> A subsequent call to {@link CharAppender#whitespaceCount()} should return 0.
	 */
	public void resetWhitespaceCount();

	/**
	 * Returns the accumulated value as a String, discarding any trailing whitespace characters identified when using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link CharAppender#reset()})
	 * @return a String containing the accumulated characters without the trailing whitespaces.
	 */
	public String getAndReset();

	/**
	 * Clears the accumulated value and the whitespace count.
	 */
	public void reset();

	/**
	 * Returns the accumulated characters, discarding any trailing whitespace characters identified when using {@link CharAppender#appendIgnoringWhitespace(char)}, {@link CharAppender#appendIgnoringPadding(char)} or {@link CharAppender#appendIgnoringWhitespaceAndPadding(char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link CharAppender#reset()})
	 * @return a character array containing the accumulated characters without the trailing whitespaces.
	 */
	public char[] getCharsAndReset();

	/**
	 * Returns the internal character array.
	 * @return the internal character array.
	 */
	public char[] getChars();

	/**
	 * Adds a sequence of repeated characters to the input.
	 * @param ch the character to append
	 * @param length the number of times the given character should be appended.
	 */
	public void fill(char ch, int length);

}
