/*******************************************************************************
 * Copyright 2016 uniVocity Software Pty Ltd
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
 * Default implementation for conversions from input Objects of type <b>I</b> to output Objects of type <b>O</b>
 *
 * <p>Extending classes must implement a proper String to <b>T</b> conversion in {@link ObjectConversion#fromString(String)}
 * <p>This abstract class provides default results for conversions when the input is null.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <I> The object type resulting from conversions of values of type <b>O</b>.
 * @param <O> The object type resulting from conversions of values of type <b>I</b>.
 */

public abstract class NullConversion<I, O> implements Conversion<I, O> {

	private O valueOnNullInput;
	private I valueOnNullOutput;

	/**
	 * Creates a Conversion from an object to another object of a different type, with default values to return when the input is null.
	 * The default constructor assumes the output of a conversion should be null when input is null
	 */
	public NullConversion() {
		this(null, null);
	}

	/**
	 * Creates a Conversion from an object to another object of a different type, with default values to return when the input is null.
	 *
	 * @param valueOnNullInput default value of type <b>O</b> to be returned when the input object <b>I</b> is null. Used when {@link NullConversion#execute(Object)} is invoked.
	 * @param valueOnNullOutput default value of type <b>I</b> to be returned when an input of type <b>I</b> is null. Used when {@link NullConversion#revert(Object)} is invoked.
	 */
	public NullConversion(O valueOnNullInput, I valueOnNullOutput) {
		this.valueOnNullInput = valueOnNullInput;
		this.valueOnNullOutput = valueOnNullOutput;
	}

	/**
	 * Converts the given instance of type <b>I</b> to an instance of <b>O</b>
	 *
	 * @param input the input value of type <b>I</b> to be converted to an object of type <b>O</b>
	 *
	 * @return the conversion result, or the value of {@link NullConversion#valueOnNullInput} if the input object is null.
	 */
	@Override
	public O execute(I input) {
		if (input == null) {
			return valueOnNullInput;
		}
		return fromInput(input);
	}

	/**
	 * Creates an instance of <b>O</b> from a <b>I</b> object
	 *
	 * @param input The object of type <b>I</b> to be converted to <b>O</b>
	 *
	 * @return an instance of <b>O</b>, converted from the <b>I</b> input.
	 */
	protected abstract O fromInput(I input);

	/**
	 * Converts a value of type <b>O</b> back to a value of type <b>I</b>
	 *
	 * @param input the input of type <b>O</b> to be converted to an output <b>I</b>
	 *
	 * @return the conversion result, or the value of {@link NullConversion#valueOnNullOutput} if the input object is null.
	 */
	@Override
	public I revert(O input) {
		if (input == null) {
			return valueOnNullOutput;
		}
		return undo(input);
	}

	/**
	 * Converts a value of type <b>O</b> back to <b>I</b>.
	 * @param input the input object to be converted to <b>I</b>
	 * @return the conversion result
	 */
	protected abstract I undo(O input);

	/**
	 * returns a default value of type <b>O</b> to be returned when the input of type <b>I</b> is null. Used when {@link NullConversion#execute(Object)} is invoked.
	 *
	 * @return the default value of type <b>O</b> used when converting from a null <b>I</b>
	 */
	public O getValueOnNullInput() {
		return valueOnNullInput;
	}

	/**
	 * returns default instance of <b>I</b> to be returned when an input of type <b>O</b> is null. Used when {@link NullConversion#revert(Object)} is invoked.
	 *
	 * @return the default <b>I</b> instance used when converting from a null <b>O</b>
	 */
	public I getValueOnNullOutput() {
		return valueOnNullOutput;
	}

	/**
	 * defines the default value of type <b>O</b> which should be returned when {@link NullConversion#execute(Object)} is invoked with a null <b>I</b>..
	 *
	 * @param valueOnNullInput the default value of type <b>T</b> when converting from a null input
	 */
	public void setValueOnNullInput(O valueOnNullInput) {
		this.valueOnNullInput = valueOnNullInput;
	}

	/**
	 * defines the default value of type <b>I</b> which should be returned when {@link NullConversion#revert(Object)} is invoked with a null <b>O</b>.
	 *
	 * @param valueOnNullOutput a default value of type <b>I</b> when converting from a null input
	 */
	public void setValueOnNullOutput(I valueOnNullOutput) {
		this.valueOnNullOutput = valueOnNullOutput;
	}
}
