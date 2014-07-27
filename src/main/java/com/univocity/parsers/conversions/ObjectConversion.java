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
 * Default implementation for conversions from an input String to Objects of a given type <b>T</b>
 *
 * <p>Extending classes must implement a proper String to <b>T</b> conversion in {@link ObjectConversion#fromString(String)}
 * <p>This abstract class provides default results for conversions when the input is null.
 * <p>It also provides a default implementation of {@link ObjectConversion#revert(Object)} that returns the result of <i>input.toString()</i>
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> The object type resulting from conversions of String values.
 */
public abstract class ObjectConversion<T> implements Conversion<String, T> {

	private T valueIfStringIsNull;
	private String valueIfObjectIsNull;

	/**
	 * Creates a Conversion from String to an Object with default values to return when the input is null.
	 * The default constructor assumes the output of a conversion should be null when input is null
	 */
	public ObjectConversion() {
		this(null, null);
	}

	/**
	 * Creates a Conversion from String to an Object with default values to return when the input is null.
	 * @param valueIfStringIsNull default value of type <b>T</b> to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when an input of type <b>T</b> is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 */
	public ObjectConversion(T valueIfStringIsNull, String valueIfObjectIsNull) {
		this.valueIfStringIsNull = valueIfStringIsNull;
		this.valueIfObjectIsNull = valueIfObjectIsNull;
	}

	/**
	 * Converts the given String to an instance of <b>T</b>
	 * @param input the input String to be converted to an object of type <b>T</b>
	 * @return the conversion result, or the value of {@link ObjectConversion#valueIfStringIsNull} if the input String is null.
	 */
	@Override
	public T execute(String input) {
		if (input == null) {
			return valueIfStringIsNull;
		}
		return fromString(input);
	}

	/**
	 * Creates an instance of <b>T</b> from a String representation.
	 * @param input The String to be converted to <b>T</b>
	 * @return an instance of <b>T</b>, converted from the String input.
	 */
	protected abstract T fromString(String input);

	/**
	 * Converts a value of type <b>T</b> back to a String
	 * <p> This is a general implementation that simply returns the result of <i>input.toString()</i>
	 * @param input the input of type <b>T</b> to be converted to a String
	 * @return the conversion result, or the value of {@link ObjectConversion#valueIfObjectIsNull} if the input object is null.
	 */
	@Override
	public String revert(T input) {
		if (input == null) {
			return valueIfObjectIsNull;
		}
		return String.valueOf(input);
	}

	/**
	 *returns a default value of type <b>T</b> to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @return the default value of type <b>T</b> used when converting from a null input
	 */
	public T getValueIfStringIsNull() {
		return valueIfStringIsNull;
	}

	/**
	 * returns default String value to be returned when an input of type <b>T</b> is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 * @return the default String value used when converting from a null input
	 */
	public String getValueIfObjectIsNull() {
		return valueIfObjectIsNull;
	}

	/**
	 * defines a default value of type <b>T</b> which should be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfStringIsNull the default value of type <b>T</b> when converting from a null input
	 */
	public void setValueIfStringIsNull(T valueIfStringIsNull) {
		this.valueIfStringIsNull = valueIfStringIsNull;
	}

	/**
	 * returns default value of type <b>T</b> which should be returned when the input String is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 * @param valueIfObjectIsNull a default value of type <b>T</b> when converting from a null input
	 */
	public void setValueIfObjectIsNull(String valueIfObjectIsNull) {
		this.valueIfObjectIsNull = valueIfObjectIsNull;
	}

}
