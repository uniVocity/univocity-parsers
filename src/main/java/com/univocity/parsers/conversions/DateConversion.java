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

import com.univocity.parsers.common.*;

import java.text.*;
import java.util.*;

/**
 * Converts Strings to instances of {@link java.util.Date} and vice versa.
 *
 * <p> This class supports multiple date formats. For example, you can define conversions from dates represented by different Strings such as "2001/05/02 and Dec/2013".
 *
 * <p> The reverse conversion from a Date to String (in {@link DateConversion#revert(Date)} will return a formatted String using the date pattern provided in this class constructor
 * <p> The date patterns must follows the pattern rules of {@link java.text.SimpleDateFormat}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see java.text.SimpleDateFormat
 */
public class DateConversion extends ObjectConversion<Date> implements FormattedConversion<SimpleDateFormat> {

	private final Locale locale;
	private final SimpleDateFormat[] parsers;
	private final String[] formats;

	/**
	 * Defines a conversion from String to {@link java.util.Date} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param locale              the {@link Locale} that determines how the date mask should be formatted.
	 * @param valueIfStringIsNull default Date value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Date input is null. Used when {@link DateConversion#revert(Date)} is invoked.
	 * @param dateFormats         list of acceptable date patterns The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 */
	public DateConversion(Locale locale, Date valueIfStringIsNull, String valueIfObjectIsNull, String... dateFormats) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
		ArgumentUtils.noNulls("Date formats", dateFormats);
		this.locale = locale == null ? Locale.getDefault() : locale;
		this.formats = dateFormats.clone();
		this.parsers = new SimpleDateFormat[dateFormats.length];
		for (int i = 0; i < dateFormats.length; i++) {
			String dateFormat = dateFormats[i];
			parsers[i] = new SimpleDateFormat(dateFormat, this.locale);
		}
	}

	/**
	 * Defines a conversion from String to {@link java.util.Date} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param valueIfStringIsNull default Date value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Date input is null. Used when {@link DateConversion#revert(Date)} is invoked.
	 * @param dateFormats         list of acceptable date patterns The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 */
	public DateConversion(Date valueIfStringIsNull, String valueIfObjectIsNull, String... dateFormats) {
		this(Locale.getDefault(), valueIfStringIsNull, valueIfObjectIsNull, dateFormats);
	}

	/**
	 * Defines a conversion from String to {@link java.util.Date} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param locale      the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateFormats list of acceptable date patterns The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 */
	public DateConversion(Locale locale, String... dateFormats) {
		this(locale, null, null, dateFormats);
	}

	/**
	 * Defines a conversion from String to {@link java.util.Date} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 *
	 * @param dateFormats list of acceptable date patterns The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 */
	public DateConversion(String... dateFormats) {
		this(Locale.getDefault(), null, null, dateFormats);
	}


	/**
	 * Converts Date to a formatted date String.
	 * <p>The pattern used to generate the formatted date is the first date pattern provided in the constructor of this class
	 *
	 * @param input the Date to be converted to a String
	 *
	 * @return a formatted date String representing the date provided by the given Date, or the value of {@code valueIfObjectIsNull} if the Date parameter is null.
	 */
	@Override
	public String revert(Date input) {
		if (input == null) {
			return super.revert(null);
		}
		return parsers[0].format(input);
	}

	/**
	 * Converts a formatted date String to an instance of Date.
	 * <p>The pattern in the formatted date must match one of the date patterns provided in the constructor of this class.
	 *
	 * @param input the String containing a formatted date which must be converted to a Date
	 *
	 * @return the Date instance containing the date information represented by the given String, or the value of {@code valueIfObjectIsNull} if the String input is null.
	 */
	@Override
	protected Date fromString(String input) {
		for (SimpleDateFormat formatter : parsers) {
			try {
				synchronized (formatter) {
					return formatter.parse(input);
				}
			} catch (ParseException ex) {
				//ignore and continue
			}
		}
		DataProcessingException exception = new DataProcessingException("Cannot parse '{value}' as a valid date of locale '" + locale + "'. Supported formats are: " + Arrays.toString(formats));
		exception.setValue(input);
		throw exception;
	}

	@Override
	public SimpleDateFormat[] getFormatterObjects() {
		return parsers;
	}

}
