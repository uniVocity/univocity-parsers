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
package com.univocity.parsers.conversions;

import com.univocity.parsers.common.*;

/**
 * Converts Strings to Characters and vice versa
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CharacterConversion extends ObjectConversion<Character> {
	/**
	 * Creates a Conversion from String to Character with default values to return when the input is null.
	 * This default constructor assumes the output of a conversion should be null when input is null
	 */
	public CharacterConversion() {
		super();
	}

	/**
	 * Creates a Conversion from String to Character with default values to return when the input is null.
	 * @param valueIfStringIsNull default Character value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Character input is null. Used when {@code revert(Character)} is invoked.
	 */
	public CharacterConversion(Character valueIfStringIsNull, String valueIfObjectIsNull) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
	}

	/**
	 * Converts a String to a Character.
	 * @throws IllegalArgumentException if the input String length is not equal to 1, then an IllegalArgumentException is thrown.
	 */
	@Override
	protected Character fromString(String input) {
		if (input.length() != 1) {
			DataProcessingException exception = new DataProcessingException("'{value}' is not a character");
			exception.setValue(input);
			throw exception;
		}
		return input.charAt(0);
	}

}
