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
package com.univocity.parsers.conversions;

/**
 * Removes leading and trailing white spaces from an input String
 *
 * The {@link TrimConversion#revert(String)} implements the same behavior of {@link TrimConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class TrimConversion implements Conversion<String, String> {

	private final int length;

	/**
	 * Creates a trim conversion that removes leading and trailing whitespaces of any input String.
	 */
	public TrimConversion() {
		this.length = -1;
	}

	/**
	 * Creates a trim-to-length conversion that limits the length of any resulting String. Input Strings are trimmed, and
	 * if the resulting String has more characters than the given limit, any characters over the given limit will be discarded.
	 *
	 * @param length the maximum number of characters of any String returned by this conversion.
	 */
	public TrimConversion(int length) {
		if (length < 0) {
			throw new IllegalArgumentException("Maximum trim length must be positive");
		}
		this.length = length;
	}

	/**
	 * Removes leading and trailing white spaces from the input and returns the result.
	 * Equivalent to {@link TrimConversion#revert(String)}
	 *
	 * @param input the String to be trimmed
	 *
	 * @return the input String without leading and trailing white spaces, or null if the input is null.
	 */
	@Override
	public String execute(String input) {
		if (input == null) {
			return null;
		}
		if (input.length() == 0) {
			return input;
		}
		if (length != -1) {
			int begin = 0;
			while (begin < input.length() && input.charAt(begin) <= ' ') {
				begin++;
			}
			if (begin == input.length()) {
				return "";
			}

			int end = begin + (length < input.length() ? length : input.length()) - 1;
			if (end >= input.length()) {
				end = input.length() - 1;
			}

			while (input.charAt(end) <= ' ') {
				end--;
			}

			return input.substring(begin, end + 1);
		}
		return input.trim();
	}

	/**
	 * Removes leading and trailing white spaces from the input and returns the result.
	 * Equivalent to {@link TrimConversion#execute(String)}
	 *
	 * @param input the String to be trimmed
	 *
	 * @return the input String without leading and trailing white spaces, or null if the input is null.
	 */
	@Override
	public String revert(String input) {
		return execute(input);
	}

}
