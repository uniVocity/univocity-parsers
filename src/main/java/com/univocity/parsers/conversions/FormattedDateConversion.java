/*
 * Copyright (c) 2013 uniVocity Software Pty Ltd. All rights reserved.
 * This file is subject to the terms and conditions defined in file
 * 'LICENSE.txt', which is part of this source code package.
 */
package com.univocity.parsers.conversions;

import com.univocity.parsers.common.*;

import java.text.*;
import java.util.*;

/**
 * Converts objects of different date types ({@code java.util.Date} and {@code java.util.Calendar}) to a formatted
 * date {@code String}.
 *
 * <p> The reverse conversion is not supported.
 *
 * <p> The date patterns must follow the pattern rules of {@link SimpleDateFormat}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see SimpleDateFormat
 */
public class FormattedDateConversion implements Conversion<Object, String> {

	private final SimpleDateFormat dateFormat;
	private final String valueIfObjectIsNull;

	/**
	 *
	 * @param format The pattern to be used to convert an input date into a String in {@link FormattedDateConversion#execute(Object)}.
	 * @param locale the {@link Locale} that determines how the date mask should be formatted.
	 * @param valueIfObjectIsNull default String value to be returned when an input is {@code null} . Used when {@link FormattedDateConversion#execute(Object)} is invoked with a {@code null} parameter.
	 */
	public FormattedDateConversion(String format, Locale locale, String valueIfObjectIsNull) {
		this.valueIfObjectIsNull = valueIfObjectIsNull;
		locale = locale == null ? Locale.getDefault() : locale;
		this.dateFormat = new SimpleDateFormat(format, locale);
	}

	@Override
	public String execute(Object input) {
		if (input == null) {
			return valueIfObjectIsNull;
		}
		Date date = null;
		if (input instanceof Date) {
			date = ((Date) input);
		} else if (input instanceof Calendar) {
			date = ((Calendar) input).getTime();
		}

		if (date != null) {
			return dateFormat.format(date);
		}

		DataProcessingException exception = new DataProcessingException("Cannot format '{value}' to a date. Not an instance of java.util.Date or java.util.Calendar");
		exception.setValue(input);
		throw exception;
	}

	/**
	 * Unsupported operation.
	 *
	 * @param input the input be converted.
	 *
	 * @return throws a {@code UnsupportedOperationException}
	 */
	@Override
	public Object revert(String input) {
		throw new UnsupportedOperationException("Can't convert an input string into date type");
	}
}
