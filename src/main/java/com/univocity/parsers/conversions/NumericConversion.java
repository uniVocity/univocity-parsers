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

import java.text.*;
import java.util.*;

import com.univocity.parsers.common.*;

/**
 * Converts Strings to instances of {@link java.lang.Number} and vice versa.
 * 
 * <p> This class supports multiple Number formats. For example, you can define conversions from Numbers represented by different Strings such as "1,000,000.00 and $5.00".
 * <p> Extending classes must implement the {@link NumericConversion#configureFormatter(DecimalFormat)} method to provide specific configuration to the DecimalFormat instance.
 * <p> The reverse conversion from a Number to String (in {@link NumericConversion#revert(Number)} will return a formatted String using the pattern provided in this class constructor
 * <p> The numeric patterns must follows the pattern rules of {@link java.text.DecimalFormat}
 * 
 * @param <T> The type of numbers supported by this conversion class.
 * 
 * @see java.text.DecimalFormat
 * 
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class NumericConversion<T extends Number> extends ObjectConversion<T> {

	private final DecimalFormat[] formatters;
	private final String[] formats;

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param valueIfStringIsNull default Number to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Number input is null. Used when {@link NumericConversion#revert(Number)} is invoked.
	 * @param numericFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}. 
	 */
	public NumericConversion(T valueIfStringIsNull, String valueIfObjectIsNull, String... numericFormats) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
		ArgumentUtils.noNulls("Numeric formats", numericFormats);
		this.formats = numericFormats.clone();
		this.formatters = new DecimalFormat[numericFormats.length];
		for (int i = 0; i < numericFormats.length; i++) {
			String numericFormat = numericFormats[i];
			formatters[i] = new DecimalFormat(numericFormat);
			configureFormatter(formatters[i]);
		}
	}

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param numericFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}. 
	 */
	public NumericConversion(String... numericFormats) {
		this(null, null, numericFormats);
	}

	/**
	 * Method called by the constructor of this class to apply custom configurations to each formatter instantiated with the numeric formats provided. 
	 * @param formatter a DecimalFormat instance initialized with one of the patterns provided in the constructor of this class.
	 */
	protected abstract void configureFormatter(DecimalFormat formatter);

	/**
	 * Converts a formatted numeric String to an instance of Number.
	 * <p>The pattern in the formatted input must match one of the numeric patterns provided in the constructor of this class. 
	 * @param input the String containing a formatted number which must be converted to a number
	 * @return the Number instance containing the value represented by the given String, or the value of {@link ObjectConversion#getValueIfStringIsNull()} if the String input is null.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T fromString(String input) {
		for (DecimalFormat formatter : formatters) {
			try {
				return (T) formatter.parse(input);
			} catch (ParseException ex) {
				continue;
			}
		}
		throw new IllegalArgumentException("Cannot parse '" + input + "' as a valid number. Supported formats are: " + Arrays.toString(formats));
	}

	/**
	 * Converts Number to a formatted numeric String.
	 * <p>The pattern used to generate the formatted number is the first numeric pattern provided in the constructor of this class 
	 * @param input the Number to be converted to a String
	 * @return a formatted numeric String representing the value provided by the given Number, or the value of {@link ObjectConversion#getValueIfObjectIsNull()} if the Number parameter is null.
	 */
	@Override
	public String revert(T input) {
		if (input == null) {
			return super.revert(input);
		}
		for (DecimalFormat formatter : formatters) {
			try {
				return formatter.format(input);
			} catch (Exception ex) {
				continue;
			}
		}
		throw new IllegalStateException("Cannot format '" + input + "'. No valid formatters were defined.");
	}

}
