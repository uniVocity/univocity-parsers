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

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.*;
import java.util.*;

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
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see java.text.DecimalFormat
 */
public abstract class NumericConversion<T extends Number> extends ObjectConversion<T> implements FormattedConversion<DecimalFormat> {

	private DecimalFormat[] formatters = new DecimalFormat[0];
	private String[] formats = new String[0];
	private final ParsePosition position = new ParsePosition(0);
	private Class<? extends Number> numberType = Number.class;

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param valueIfStringIsNull default Number to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Number input is null. Used when {@link NumericConversion#revert(Number)} is invoked.
	 * @param numericFormats      list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
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
	 *
	 * @param valueIfStringIsNull default Number to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Number input is null. Used when {@link NumericConversion#revert(Number)} is invoked.
	 * @param numericFormatters   list formatters of acceptable numeric patterns. The first formatter in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 */
	public NumericConversion(T valueIfStringIsNull, String valueIfObjectIsNull, DecimalFormat... numericFormatters) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
		ArgumentUtils.noNulls("Numeric formatters", numericFormatters);
		this.formatters = numericFormatters.clone();
		this.formats = new String[numericFormatters.length];
		for (int i = 0; i < numericFormatters.length; i++) {
			formats[i] = numericFormatters[i].toPattern();
		}
	}

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns. The patterns
	 * must be added to this conversion class through the {@link #addFormat(String, String...)} method.
	 *
	 * @param valueIfStringIsNull default Number to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Number input is null. Used when {@link NumericConversion#revert(Number)} is invoked.
	 */
	public NumericConversion(T valueIfStringIsNull, String valueIfObjectIsNull) {
		super(valueIfStringIsNull, valueIfObjectIsNull);

	}

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param numericFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 */
	public NumericConversion(String... numericFormats) {
		this(null, null, numericFormats);
	}

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param numericFormatters list formatters of acceptable numeric patterns. The first formatter in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 */
	public NumericConversion(DecimalFormat... numericFormatters) {
		this(null, null, numericFormatters);
	}

	/**
	 * Defines a conversion from String to {@link java.lang.Number} using a sequence of acceptable numeric patterns. The patterns
	 * must be added to this conversion class through the {@link #addFormat(String, String...)} method.
	 *
	 * This constructor assumes the output of a conversion should be null when input is null
	 */
	public NumericConversion() {
		super();
	}

	/**
	 * Returns the implementation of {@code Number} that should be used when converting numeric data.
	 *
	 * @return the implementation of {@code Number} that should be used when converting numeric data.
	 */
	public Class<? extends Number> getNumberType() {
		return this.numberType;
	}

	/**
	 * Defines a specific implementation of {@code Number} that should be used when converting numeric data.
	 *
	 * @param numberType the implementation of {@code Number} that should be used when converting numeric data.
	 */
	public void setNumberType(Class<? extends Number> numberType) {
		this.numberType = numberType;
	}

	@Override
	public DecimalFormat[] getFormatterObjects() {
		return formatters;
	}

	/**
	 * Method called by the constructor of this class to apply custom configurations to each formatter instantiated with the numeric formats provided.
	 *
	 * @param formatter a DecimalFormat instance initialized with one of the patterns provided in the constructor of this class.
	 */
	protected abstract void configureFormatter(DecimalFormat formatter);

	/**
	 * Converts a formatted numeric String to an instance of Number.
	 * <p>The pattern in the formatted input must match one of the numeric patterns provided in the constructor of this class.
	 *
	 * @param input the String containing a formatted number which must be converted to a number
	 *
	 * @return the Number instance containing the value represented by the given String, or the value of {@link ObjectConversion#getValueIfStringIsNull()} if the String input is null.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected T fromString(String input) {
		for (int i = 0; i < formatters.length; i++) {
			position.setIndex(0);
			T out = (T) formatters[i].parse(input, position);
			if (formatters.length == 1 || position.getIndex() == input.length()) {
				if (out == null || numberType == Number.class) {
					return out;
				} else if (numberType == Double.class) {
					return (T) Double.valueOf(out.doubleValue());
				} else if (numberType == Float.class) {
					return (T) Float.valueOf(out.floatValue());
				} else if (numberType == BigDecimal.class) {
					if (out instanceof BigDecimal) {
						return out;
					}
					return (T) new BigDecimal(String.valueOf(out));
				} else if (numberType == BigInteger.class) {
					if (out instanceof BigInteger) {
						return out;
					}
					return (T) BigInteger.valueOf(out.longValue());
				} else if (numberType == Long.class) {
					return (T) Long.valueOf(out.longValue());
				} else if (numberType == Integer.class) {
					return (T) Integer.valueOf(out.intValue());
				} else if (numberType == Short.class) {
					return (T) Short.valueOf(out.shortValue());
				} else if (numberType == Byte.class) {
					return (T) Byte.valueOf(out.byteValue());
				}
				return out;
			}
		}
		DataProcessingException exception = new DataProcessingException("Cannot parse '{value}' as a valid number. Supported formats are: " + Arrays.toString(formats));
		exception.setValue(input);
		throw exception;
	}

	/**
	 * Converts Number to a formatted numeric String.
	 * <p>The pattern used to generate the formatted number is the first numeric pattern provided in the constructor of this class
	 *
	 * @param input the Number to be converted to a String
	 *
	 * @return a formatted numeric String representing the value provided by the given Number, or the value of {@link ObjectConversion#getValueIfObjectIsNull()} if the Number parameter is null.
	 */
	@Override
	public String revert(T input) {
		if (input == null) {
			return super.revert(null);
		}
		for (DecimalFormat formatter : formatters) {
			try {
				return formatter.format(input);
			} catch (Throwable ex) {
				//ignore and continue
			}
		}
		DataProcessingException exception = new DataProcessingException("Cannot format '{value}'. No valid formatters were defined.");
		exception.setValue(input);
		throw exception;
	}

	/**
	 * Adds a new numeric pattern to be used to parse input Strings and convert them to numbers.
	 *
	 * @param format        a numeric pattern. The first pattern added to this class will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 * @param formatOptions a sequence of properties and their values, used to configure the underlying formatter. Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 */
	public void addFormat(String format, String... formatOptions) {
		DecimalFormat formatter = new DecimalFormat(format);
		configureFormatter(formatter);
		AnnotationHelper.applyFormatSettings(formatter, formatOptions);

		this.formats = Arrays.copyOf(formats, formats.length + 1);
		this.formatters = Arrays.copyOf(formatters, formatters.length + 1);

		formats[formats.length - 1] = format;
		formatters[formatters.length - 1] = formatter;
	}
}
