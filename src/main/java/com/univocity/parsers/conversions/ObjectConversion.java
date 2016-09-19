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
 * @param <T> The object type resulting from conversions of String values.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class ObjectConversion<T> extends NullConversion<String, T> {

	/**
	 * Creates a Conversion from String to an Object with default values to return when the input is null.
	 * The default constructor assumes the output of a conversion should be null when input is null
	 */
	public ObjectConversion() {
		super(null, null);
	}

	/**
	 * Creates a Conversion from String to an Object with default values to return when the input is null.
	 *
	 * @param valueIfStringIsNull default value of type <b>T</b> to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when an input of type <b>T</b> is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 */
	public ObjectConversion(T valueIfStringIsNull, String valueIfObjectIsNull) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
	}

	/**
	 * Converts the given String to an instance of <b>T</b>
	 *
	 * @param input the input String to be converted to an object of type <b>T</b>
	 *
	 * @return the conversion result, or the value of {@link ObjectConversion#getValueIfStringIsNull()} if the input String is null.
	 */
	@Override
	public T execute(String input) {
		return super.execute(input);
	}

	/**
	 * Creates an instance of <b>T</b> from a String representation.
	 *
	 * @param input The String to be converted to <b>T</b>
	 *
	 * @return an instance of <b>T</b>, converted from the String input.
	 */
	protected final T fromInput(String input) {
		return fromString(input);
	}

	/**
	 * Creates an instance of <b>T</b> from a String representation.
	 *
	 * @param input The String to be converted to <b>T</b>
	 *
	 * @return an instance of <b>T</b>, converted from the String input.
	 */
	protected abstract T fromString(String input);

	/**
	 * Converts a value of type <b>T</b> back to a String
	 * <p> This is a general implementation that simply returns the result of <i>input.toString()</i>
	 *
	 * @param input the input of type <b>T</b> to be converted to a String
	 *
	 * @return the conversion result, or the value of {@link ObjectConversion#getValueIfObjectIsNull()} if the input object is null.
	 */
	@Override
	public String revert(T input) {
		return super.revert(input);
	}

	@Override
	protected final String undo(T input) {
		return String.valueOf(input);
	}

	/**
	 * returns a default value of type <b>T</b> to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 *
	 * @return the default value of type <b>T</b> used when converting from a null input
	 */
	public T getValueIfStringIsNull() {
		return getValueOnNullInput();
	}

	/**
	 * returns default String value to be returned when an input of type <b>T</b> is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 *
	 * @return the default String value used when converting from a null input
	 */
	public String getValueIfObjectIsNull() {
		return getValueOnNullOutput();
	}

	/**
	 * defines a default value of type <b>T</b> which should be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 *
	 * @param valueIfStringIsNull the default value of type <b>T</b> when converting from a null input
	 */
	public void setValueIfStringIsNull(T valueIfStringIsNull) {
		setValueOnNullInput(valueIfStringIsNull);
	}

	/**
	 * returns default value of type <b>T</b> which should be returned when the input String is null. Used when {@link ObjectConversion#revert(Object)} is invoked.
	 *
	 * @param valueIfObjectIsNull a default value of type <b>T</b> when converting from a null input
	 */
	public void setValueIfObjectIsNull(String valueIfObjectIsNull) {
		setValueOnNullOutput(valueIfObjectIsNull);
	}

}
