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

import java.math.*;
import java.text.*;
import java.util.*;

/**
 * This class provides default instances of common implementations if {@code com.univocity.parsers.conversions.Conversion}, as well as useful methods for obtaining new instances of these.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class Conversions {
	private Conversions() {
	}

	private static final UpperCaseConversion upperCase = new UpperCaseConversion();
	private static final LowerCaseConversion lowerCase = new LowerCaseConversion();
	private static final TrimConversion trim = new TrimConversion();
	private static final ToStringConversion toString = new ToStringConversion();

	/**
	 * Returns a singleton instance of {@link ToStringConversion}
	 *
	 * @return a singleton instance of {@link ToStringConversion}
	 */
	public static ToStringConversion string() {
		return toString;
	}

	/**
	 * Returns a singleton instance of {@link UpperCaseConversion}
	 *
	 * @return a singleton instance of {@link UpperCaseConversion}
	 */
	public static UpperCaseConversion toUpperCase() {
		return upperCase;
	}

	/**
	 * Returns a singleton instance of {@link LowerCaseConversion}
	 *
	 * @return a singleton instance of {@link LowerCaseConversion}
	 */
	public static LowerCaseConversion toLowerCase() {
		return lowerCase;
	}

	/**
	 * Returns a singleton instance of {@link TrimConversion}
	 *
	 * @return a singleton instance of {@link TrimConversion}
	 */
	public static TrimConversion trim() {
		return trim;
	}

	/**
	 * Returns a {@link TrimConversion} that limits the output to a fixed length
	 *
	 * @param length the maximum length a value can contain. Characters after this limit will
	 *               be discarded.
	 *
	 * @return a trim-to-length conversion
	 */
	public static TrimConversion trim(int length) {
		return new TrimConversion(length);
	}


	/**
	 * Returns a new instance of {@link RegexConversion}
	 *
	 * @param replaceRegex the regular expression used to match contents of a given input String
	 * @param replacement  the replacement content to replace any contents matched by the given regular expression
	 *
	 * @return the new instance of {@link RegexConversion} created with the given parameters.
	 */
	public static RegexConversion replace(String replaceRegex, String replacement) {
		return new RegexConversion(replaceRegex, replacement);
	}

	/**
	 * Returns a new instance of {@link NullStringConversion}
	 *
	 * @param nullRepresentations the sequence of Strings that represent a null value.
	 *
	 * @return the new instance of {@link NullStringConversion} created with the given parameters.
	 */
	public static NullStringConversion toNull(String... nullRepresentations) {
		return new NullStringConversion(nullRepresentations);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param locale      the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(Locale locale, String... dateFormats) {
		return new DateConversion(locale, dateFormats);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(String... dateFormats) {
		return new DateConversion(Locale.getDefault(), dateFormats);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param locale      the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateIfNull  default Date value to be returned when the input String is null. Used when {@link DateConversion#execute(String)} is invoked.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(Locale locale, Date dateIfNull, String... dateFormats) {
		return new DateConversion(locale, dateIfNull, null, dateFormats);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param dateIfNull  default Date value to be returned when the input String is null. Used when {@link DateConversion#execute(String)} is invoked.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(Date dateIfNull, String... dateFormats) {
		return new DateConversion(Locale.getDefault(), dateIfNull, null, dateFormats);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param locale       the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateIfNull   default Date value to be returned when the input String is null. Used when {@link DateConversion#execute(String)} is invoked.
	 * @param stringIfNull default String value to be returned when a Date input is null. Used when {@link DateConversion#revert(Date)} is invoked.
	 * @param dateFormats  list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(Locale locale, Date dateIfNull, String stringIfNull, String... dateFormats) {
		return new DateConversion(locale, dateIfNull, stringIfNull, dateFormats);
	}

	/**
	 * Returns a new instance of {@link DateConversion}
	 *
	 * @param dateIfNull   default Date value to be returned when the input String is null. Used when {@link DateConversion#execute(String)} is invoked.
	 * @param stringIfNull default String value to be returned when a Date input is null. Used when {@link DateConversion#revert(Date)} is invoked.
	 * @param dateFormats  list of acceptable date patterns. The first pattern in this sequence will be used to convert a Date into a String in {@link DateConversion#revert(Date)}.
	 *
	 * @return the new instance of {@link DateConversion} created with the given parameters.
	 */
	public static DateConversion toDate(Date dateIfNull, String stringIfNull, String... dateFormats) {
		return new DateConversion(Locale.getDefault(), dateIfNull, stringIfNull, dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param locale      the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(Locale locale, String... dateFormats) {
		return new CalendarConversion(locale, dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(String... dateFormats) {
		return new CalendarConversion(Locale.getDefault(), dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param locale      the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateIfNull  default Calendar value to be returned when the input String is null. Used when {@link CalendarConversion#execute(String)} is invoked.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(Locale locale, Calendar dateIfNull, String... dateFormats) {
		return new CalendarConversion(locale, dateIfNull, null, dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param dateIfNull  default Calendar value to be returned when the input String is null. Used when {@link CalendarConversion#execute(String)} is invoked.
	 * @param dateFormats list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(Calendar dateIfNull, String... dateFormats) {
		return new CalendarConversion(Locale.getDefault(), dateIfNull, null, dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param locale       the {@link Locale} that determines how the date mask should be formatted.
	 * @param dateIfNull   default Calendar value to be returned when the input String is null. Used when {@link CalendarConversion#execute(String)} is invoked.
	 * @param stringIfNull default String value to be returned when a Date input is null. Used when {@link CalendarConversion#revert(Calendar)} is invoked.
	 * @param dateFormats  list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(Locale locale, Calendar dateIfNull, String stringIfNull, String... dateFormats) {
		return new CalendarConversion(locale, dateIfNull, stringIfNull, dateFormats);
	}

	/**
	 * Returns a new instance of {@link CalendarConversion}
	 *
	 * @param dateIfNull   default Calendar value to be returned when the input String is null. Used when {@link CalendarConversion#execute(String)} is invoked.
	 * @param stringIfNull default String value to be returned when a Date input is null. Used when {@link CalendarConversion#revert(Calendar)} is invoked.
	 * @param dateFormats  list of acceptable date patterns. The first pattern in this sequence will be used to convert a Calendar into a String in {@link CalendarConversion#revert(Calendar)}.
	 *
	 * @return the new instance of {@link CalendarConversion} created with the given parameters.
	 */
	public static CalendarConversion toCalendar(Calendar dateIfNull, String stringIfNull, String... dateFormats) {
		return new CalendarConversion(Locale.getDefault(), dateIfNull, stringIfNull, dateFormats);
	}

	/**
	 * Returns a new instance of {@link ByteConversion}
	 *
	 * @return a new instance of {@link ByteConversion}
	 */
	public static ByteConversion toByte() {
		return new ByteConversion();
	}

	/**
	 * Returns a new instance of {@link ShortConversion}
	 *
	 * @return a new instance of {@link ShortConversion}
	 */
	public static ShortConversion toShort() {
		return new ShortConversion();
	}

	/**
	 * Returns a new instance of {@link IntegerConversion}
	 *
	 * @return a new instance of {@link IntegerConversion}
	 */
	public static IntegerConversion toInteger() {
		return new IntegerConversion();
	}

	/**
	 * Returns a new instance of {@link LongConversion}
	 *
	 * @return a new instance of {@link LongConversion}
	 */
	public static LongConversion toLong() {
		return new LongConversion();
	}

	/**
	 * Returns a new instance of {@link BigIntegerConversion}
	 *
	 * @return a new instance of {@link BigIntegerConversion}
	 */
	public static BigIntegerConversion toBigInteger() {
		return new BigIntegerConversion();
	}

	/**
	 * Returns a new instance of {@link FloatConversion}
	 *
	 * @return a new instance of {@link FloatConversion}
	 */
	public static FloatConversion toFloat() {
		return new FloatConversion();
	}

	/**
	 * Returns a new instance of {@link DoubleConversion}
	 *
	 * @return a new instance of {@link DoubleConversion}
	 */
	public static DoubleConversion toDouble() {
		return new DoubleConversion();
	}

	/**
	 * Returns a new instance of {@link BigDecimalConversion}
	 *
	 * @return a new instance of {@link BigDecimalConversion}
	 */
	public static BigDecimalConversion toBigDecimal() {
		return new BigDecimalConversion();
	}

	/**
	 * Returns a new instance of {@link NumericConversion}
	 *
	 * @param numberFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 *
	 * @return a new instance of {@link NumericConversion} that supports the given number formats
	 */
	public static NumericConversion<Number> formatToNumber(String... numberFormats) {
		return new NumericConversion<Number>(numberFormats) {
			@Override
			protected void configureFormatter(DecimalFormat formatter) {
			}
		};
	}

	/**
	 * Returns a new instance of {@link NumericConversion}
	 *
	 * @param numberType    type of number to be returned. The resulting instance of {@code Number} will be cast to the expected type.
	 * @param numberFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a Number into a String in {@link NumericConversion#revert(Number)}.
	 * @param <T>           type of number
	 *
	 * @return a new instance of {@link NumericConversion} that supports the given number formats
	 */
	public static <T extends Number> NumericConversion<T> formatToNumber(Class<T> numberType, String... numberFormats) {
		return new NumericConversion<T>(numberFormats) {
			@Override
			protected void configureFormatter(DecimalFormat formatter) {
			}
		};
	}

	/**
	 * Returns a new instance of {@link FormattedBigDecimalConversion}
	 *
	 * @param numberFormats list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a BigDecimal into a String in {@link NumericConversion#revert(Number)}.
	 *
	 * @return a new instance of {@link FormattedBigDecimalConversion} that supports the given number formats
	 */
	public static FormattedBigDecimalConversion formatToBigDecimal(String... numberFormats) {
		return new FormattedBigDecimalConversion(numberFormats);
	}

	/**
	 * Returns a new instance of {@link FormattedBigDecimalConversion}
	 *
	 * @param defaultValueForNullString default BigDecimal to be returned when the input String is null. Used when {@link FormattedBigDecimalConversion#execute(String)} is invoked.
	 * @param numberFormats             list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a BigDecimal into a String in {@link NumericConversion#revert(Number)}.
	 *
	 * @return a new instance of {@link FormattedBigDecimalConversion} that supports the given number formats
	 */
	public static FormattedBigDecimalConversion formatToBigDecimal(BigDecimal defaultValueForNullString, String... numberFormats) {
		return new FormattedBigDecimalConversion(defaultValueForNullString, null, numberFormats);
	}

	/**
	 * Returns a new instance of {@link FormattedBigDecimalConversion}
	 *
	 * @param defaultValueForNullString default BigDecimal to be returned when the input String is null. Used when {@link FormattedBigDecimalConversion#execute(String)} is invoked.
	 * @param stringIfNull              default String value to be returned when a BigDecimal input is null. Used when {@code FormattedBigDecimalConversion#revert(BigDecimal)} is invoked.
	 * @param numberFormats             list of acceptable numeric patterns. The first pattern in this sequence will be used to convert a BigDecimal into a String in {@link NumericConversion#revert(Number)}.
	 *
	 * @return a new instance of {@link FormattedBigDecimalConversion} that supports the given number formats
	 */
	public static FormattedBigDecimalConversion formatToBigDecimal(BigDecimal defaultValueForNullString, String stringIfNull, String... numberFormats) {
		return new FormattedBigDecimalConversion(defaultValueForNullString, stringIfNull, numberFormats);
	}

	/**
	 * Returns a new instance of {@link BooleanConversion}
	 *
	 * @param defaultValueForNullString  default Boolean value to be returned when the input String is null. Used when {@link BooleanConversion#execute(String)} is invoked.
	 * @param defaultValueForNullBoolean default String value to be returned when a Boolean input is null. Used when {@link BooleanConversion#revert(Boolean)} is invoked.
	 * @param valuesForTrue              Strings that identify the boolean value <i>true</i>. The first element will be returned when executing {@code BooleanConversion.revert(true)}
	 * @param valuesForFalse             Strings that identify the boolean value <i>false</i>. The first element will be returned when executing {@code BooleanConversion.revert(false)}
	 *
	 * @return a new instance of {@link BooleanConversion} with support for multiple representations of true and false
	 */
	public static BooleanConversion toBoolean(Boolean defaultValueForNullString, String defaultValueForNullBoolean, String[] valuesForTrue, String[] valuesForFalse) {
		return new BooleanConversion(defaultValueForNullString, defaultValueForNullBoolean, valuesForTrue, valuesForFalse);
	}

	/**
	 * Returns a new instance of {@link BooleanConversion}
	 *
	 * @param defaultValueForNullString  default Boolean value to be returned when the input String is null. Used when {@link BooleanConversion#execute(String)} is invoked.
	 * @param defaultValueForNullBoolean default String value to be returned when a Boolean input is null. Used when {@link BooleanConversion#revert(Boolean)} is invoked.
	 * @param valueForTrue               String that identify the boolean value <i>true</i>.
	 * @param valueForFalse              String that identify the boolean value <i>false</i>.
	 *
	 * @return a new instance of {@link BooleanConversion} with support for multiple representations of true and false
	 */
	public static BooleanConversion toBoolean(Boolean defaultValueForNullString, String defaultValueForNullBoolean, String valueForTrue, String valueForFalse) {
		return new BooleanConversion(defaultValueForNullString, defaultValueForNullBoolean, new String[]{valueForTrue}, new String[]{valueForFalse});
	}

	/**
	 * Returns a new instance of {@link BooleanConversion}
	 *
	 * @param valuesForTrue  Strings that identify the boolean value <i>true</i>. The first element will be returned when executing {@code BooleanConversion.revert(true)}
	 * @param valuesForFalse Strings that identify the boolean value <i>false</i>. The first element will be returned when executing {@code BooleanConversion.revert(false)}
	 *
	 * @return a new instance of {@link BooleanConversion} with support for multiple representations of true and false
	 */
	public static BooleanConversion toBoolean(String[] valuesForTrue, String[] valuesForFalse) {
		return new BooleanConversion(valuesForTrue, valuesForFalse);
	}

	/**
	 * Returns a new instance of {@link BooleanConversion} that converts the string "true" to true, and the String "false" to false.
	 *
	 * @return a new instance of {@link BooleanConversion} with support for multiple representations of true and false
	 */
	public static BooleanConversion toBoolean() {
		return toBoolean("true", "false");
	}

	/**
	 * Returns a new instance of {@link BooleanConversion}
	 *
	 * @param valueForTrue  String that identifies the boolean value <i>true</i>.
	 * @param valueForFalse String that identifies the boolean value <i>false</i>.
	 *
	 * @return a new instance of {@link BooleanConversion} with support for multiple representations of true and false
	 */
	public static BooleanConversion toBoolean(String valueForTrue, String valueForFalse) {
		return new BooleanConversion(new String[]{valueForTrue}, new String[]{valueForFalse});
	}

	/**
	 * Returns a new instance of {@link CharacterConversion}
	 *
	 * @return a new instance of {@link CharacterConversion}
	 */
	public static CharacterConversion toChar() {
		return new CharacterConversion();
	}

	/**
	 * Returns a new instance of  {@link CharacterConversion}
	 *
	 * @param defaultValueForNullString default Character value to be returned when the input String is null. Used when {@link CharacterConversion#execute(String)} is invoked.
	 * @param defaultValueForNullChar   default String value to be returned when a Character input is null. Used when {@code CharacterConversion#revert(Character)} is invoked.
	 *
	 * @return a new instance of {@link CharacterConversion}
	 */
	public static CharacterConversion toChar(Character defaultValueForNullString, String defaultValueForNullChar) {
		return new CharacterConversion(defaultValueForNullString, defaultValueForNullChar);
	}

	/**
	 * Returns a new instance of  {@link CharacterConversion}
	 *
	 * @param defaultValueForNullString default Character value to be returned when the input String is null. Used when {@link CharacterConversion#execute(String)} is invoked.
	 *
	 * @return a new instance of {@link CharacterConversion}
	 */
	public static CharacterConversion toChar(Character defaultValueForNullString) {
		return new CharacterConversion(defaultValueForNullString, null);
	}

	/**
	 * Returns a new instance of {@link EnumConversion}
	 *
	 * @param <T>      the {@code enum} type
	 * @param enumType the enumeration type to be converted from/to {@code String}
	 *
	 * @return new instance of {@link EnumConversion}
	 */
	public static <T extends Enum<T>> EnumConversion<T> toEnum(Class<T> enumType) {
		return new EnumConversion<T>(enumType);
	}

	/**
	 * Returns a new instance of {@link EnumConversion}
	 *
	 * @param <T>       the {@code enum} type
	 * @param enumType  the enumeration type to be converted from/to {@code String}
	 * @param selectors the selection elements of the enumeration to use for matching {@code String}s.
	 *
	 * @return new instance of {@link EnumConversion}
	 */
	public static <T extends Enum<T>> EnumConversion<T> toEnum(Class<T> enumType, EnumSelector... selectors) {
		return toEnum(enumType, null, null, null, selectors);
	}

	/**
	 * Returns a new instance of {@link EnumConversion}
	 *
	 * @param <T>               the {@code enum} type
	 * @param enumType          the enumeration type to be converted from/to {@code String}
	 * @param customEnumElement name of custom element of the enumeration (attribute or method) whose values should be used to match equivalent {@code String}s.
	 * @param selectors         the selection elements of the enumeration to use for matching {@code String}s.
	 *
	 * @return new instance of {@link EnumConversion}
	 */
	public static <T extends Enum<T>> EnumConversion<T> toEnum(Class<T> enumType, String customEnumElement, EnumSelector... selectors) {
		return toEnum(enumType, null, null, customEnumElement);
	}

	/**
	 * Returns a new instance of {@link EnumConversion}
	 *
	 * @param <T>                 the {@code enum} type
	 * @param enumType            the enumeration type to be converted from/to {@code String}
	 * @param valueIfStringIsNull the default enumeration constant to use if the input {@code String} is {@code null}
	 * @param valueIfEnumIsNull   the default {@code String} value to use if the input {@code enum} constant is {@code null}
	 * @param customEnumElement   name of custom element of the enumeration (attribute or method) whose values should be used to match equivalent {@code String}s.
	 * @param selectors           the selection elements of the enumeration to use for matching {@code String}s.
	 *
	 * @return new instance of {@link EnumConversion}
	 */
	public static <T extends Enum<T>> EnumConversion<T> toEnum(Class<T> enumType, T valueIfStringIsNull, String valueIfEnumIsNull, String customEnumElement, EnumSelector... selectors) {
		return new EnumConversion<T>(enumType, valueIfStringIsNull, valueIfEnumIsNull, customEnumElement, selectors);
	}
}
