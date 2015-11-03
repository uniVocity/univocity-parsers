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
 * Converts an input String to its lower case representation
 *
 * The {@link LowerCaseConversion#revert(String)} implements the same behavior of {@link LowerCaseConversion#execute(String)}. Null inputs produce null outputs.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class LowerCaseConversion implements Conversion<String, String> {

	/**
	 * Applies the toLowerCase operation in the input and returns the result.
	 * Equivalent to {@link LowerCaseConversion#revert(String)}
	 * @param input the String to be converted to lower case
	 * @return the lower case representation of the given input, or null if the input is null.
	 */
	@Override
	public String execute(String input) {
		if (input == null) {
			return null;
		}
		return input.toLowerCase();
	}

	/**
	 * Applies the toLowerCase operation in the input and returns the result.
	 * Equivalent to {@link LowerCaseConversion#execute(String)}
	 * @param input the String to be converted to lower case
	 * @return the lower case representation of the given input, or null if the input is null.
	 */
	@Override
	public String revert(String input) {
		return execute(input);
	}

}
