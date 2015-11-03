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
 * Converts Strings to instances of {@link java.util.Calendar} and vice versa.
 *
 * <p> This class supports multiple date formats. For example, you can define conversions from dates represented by different Strings such as "2001/05/02 and Dec/2013".
 *
 * <p> The reverse conversion from a Calendar to String (in {@link CalendarConversion#revert(Calendar)} will return a formatted String using the date pattern provided in this class constructor
 * <p> The date patterns must follows the pattern rules of {@link java.text.SimpleDateFormat}
 *
 * @see java.text.SimpleDateFormat
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class CalendarConversion extends ObjectConversion<Calendar> implements FormattedConversion<SimpleDateFormat> {

	private final DateConversion dateConversion;

	/**
	 * Defines a conversion from String to {@link java.util.Calendar} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param valueIfStringIsNull default Calendar value to be returned when the input String is null. Used when {@link ObjectConversion#execute(String)} is invoked.
	 * @param valueIfObjectIsNull default String value to be returned when a Calendar input is null. Used when {@link CalendarConversion#revert(Calendar)} is invoked.
	 * @param dateFormats list of acceptable date patterns The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 */
	public CalendarConversion(Calendar valueIfStringIsNull, String valueIfObjectIsNull, String... dateFormats) {
		super(valueIfStringIsNull, valueIfObjectIsNull);
		ArgumentUtils.noNulls("Date formats", dateFormats);
		dateConversion = new DateConversion(dateFormats);
	}

	/**
	 * Defines a conversion from String to {@link java.util.Calendar} using a sequence of acceptable date patterns.
	 * This constructor assumes the output of a conversion should be null when input is null
	 * @param dateFormats list of acceptable date patterns The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 */
	public CalendarConversion(String... dateFormats) {
		this(null, null, dateFormats);
	}

	/**
	 * Converts Calendar to a formatted date String.
	 * <p>The pattern used to generate the formatted date is the first date pattern provided in the constructor of this class
	 * @param input the Calendar to be converted to a String
	 * @return a formatted date String representing the date provided by the given Calendar, or the value of {@link CalendarConversion#getValueIfObjectIsNull()} if the Calendar parameter is null.
	 */
	@Override
	public String revert(Calendar input) {
		if (input == null) {
			return super.revert(null);
		}
		return dateConversion.revert(input.getTime());
	}

	/**
	 * Converts a formatted date String to an instance of Calendar.
	 * <p>The pattern in the formatted date must match one of the date patterns provided in the constructor of this class.
	 * @param input the String containing a formatted date which must be converted to a Calendar
	 * @return the Calendar instance containing the date information represented by the given String, or the value of {@link CalendarConversion#getValueIfStringIsNull()} if the String input is null.
	 */
	@Override
	protected Calendar fromString(String input) {
		Date date = dateConversion.execute(input);
		Calendar out = Calendar.getInstance();
		out.setTime(date);
		return out;
	}

	@Override
	public SimpleDateFormat[] getFormatterObjects() {
		return dateConversion.getFormatterObjects();
	}

}
