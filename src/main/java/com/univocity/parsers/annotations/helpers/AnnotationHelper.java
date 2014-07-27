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
package com.univocity.parsers.annotations.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.util.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.conversions.*;

/**
 * Helper class to process fields annotated with {@link Parsed}
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class AnnotationHelper {

	private AnnotationHelper() {

	}

	/**
	 * Converts the special "null" strings that might be provided by {@link Parsed#defaultNullRead() and  Parsed#defaultNullWrite()}
	 * @param defaultValue The string returned by {@link Parsed#defaultNullRead() and  Parsed#defaultNullWrite()}
	 * @return the default value if it is not the String literal "null" or "'null'".
	 * <p> If "null" was provided, then null will be returned.
	 * <p> If "'null'" was provided, then "null" will be returned.
	 */
	private static String getNullValue(String defaultValue) {
		if (defaultValue.equals("null")) {
			return null;
		}
		if (defaultValue.equals("'null'")) {
			return "null";
		}

		return defaultValue;
	}

	private static String getNullWriteValue(Parsed parsed) {
		return getNullValue(parsed.defaultNullWrite());
	}

	private static String getNullReadValue(Parsed parsed) {
		return getNullValue(parsed.defaultNullRead());
	}

	/**
	 * Iterates over all strings in the array and replaces special "null" Strings following this rule:
	 * <p>if the String not the String literal "null" or "'null'", the original String will be kept.
	 * <p> If "null" was provided, then null  will be used.
	 * <p> If "'null'" was provided, then "null" will be used.
	 *
	 * @param strings A sequence of strings
	 * @return the modified sequence of Strings.
	 */
	private static String[] addNull(String[] strings) {
		String[] out = new String[strings.length];

		for (int i = 0; i < strings.length; i++) {
			if ("null".equals(strings[i])) {
				out[i] = null;
			}
			if ("'null'".equals(strings[i])) {
				out[i] = "null";
			}
		}
		return strings;
	}

	/**
	 * Identifies the proper conversion for a given Field and an annotation from the package {@link com.univocity.parsers.annotations}
	 *
	 * @param field The field to have conversions applied to
	 * @param annotation the annotation from {@link com.univocity.parsers.annotations} that identifies a {@link Conversion} instance.
	 * @return The {@link Conversion} that should be applied to the field
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Conversion getConversion(Field field, Annotation annotation) {
		try {
			Parsed parsed = field.getAnnotation(Parsed.class);
			Class annType = annotation.annotationType();

			String nullRead = getNullReadValue(parsed);
			String nullWrite = getNullWriteValue(parsed);

			Class fieldType = field.getType();

			if (annType == NullString.class) {
				String[] nulls = ((NullString) annotation).nulls();
				return Conversions.toNull(nulls);
			} else if (annType == Trim.class) {
				return Conversions.trim();
			} else if (annType == LowerCase.class) {
				return Conversions.toLowerCase();
			} else if (annType == UpperCase.class) {
				return Conversions.toUpperCase();
			} else if (annType == Replace.class) {
				Replace replace = ((Replace) annotation);
				return Conversions.replace(replace.expression(), replace.replacement());
			} else if (annType == BooleanString.class) {
				if (fieldType != boolean.class && fieldType != Boolean.class) {
					throw new IllegalArgumentException("Invalid annotation: Field " + field.getName() + " has type " + fieldType.getName() + " instead of boolean.");
				}
				BooleanString boolString = ((BooleanString) annotation);
				String[] falseStrings = addNull(boolString.falseStrings());
				String[] trueStrings = addNull(boolString.trueStrings());
				Boolean valueForNull = nullRead == null ? null : Boolean.valueOf(nullRead);

				if (valueForNull == null && fieldType == boolean.class) {
					valueForNull = Boolean.FALSE;
				}

				return Conversions.toBoolean(valueForNull, nullWrite, trueStrings, falseStrings);
			} else if (annType == Format.class) {
				Format format = ((Format) annotation);
				String[] formats = format.formats();

				if (fieldType == BigDecimal.class) {
					BigDecimal defaultForNull = nullRead == null ? null : new BigDecimal(nullRead);
					return Conversions.formatToBigDecimal(defaultForNull, nullWrite, formats);
				} else if (Number.class.isAssignableFrom(fieldType)) {
					return Conversions.formatToNumber(formats);
				} else {
					Date dateIfNull = null;
					if (nullRead != null) {
						if (nullRead.equalsIgnoreCase("now")) {
							dateIfNull = new Date();
						} else {
							if (formats.length == 0) {
								throw new IllegalArgumentException("No format defined");
							}
							SimpleDateFormat sdf = new SimpleDateFormat(formats[0]);
							dateIfNull = sdf.parse(nullRead);
						}
					}

					if (Date.class == fieldType) {
						return Conversions.toDate(dateIfNull, nullWrite, formats);
					} else if (Calendar.class == fieldType) {
						Calendar calendarIfNull = null;
						if (dateIfNull != null) {
							calendarIfNull = Calendar.getInstance();
							calendarIfNull.setTime(dateIfNull);
						}
						return Conversions.toCalendar(calendarIfNull, nullWrite, formats);
					}
				}
			} else if (annType == Convert.class) {
				Convert convert = ((Convert) annotation);
				String[] args = convert.args();
				Class conversionClass = convert.conversionClass();
				if (!Conversion.class.isAssignableFrom(conversionClass)) {
					throw new IllegalArgumentException("Not a valid conversion class: '" + conversionClass.getName() + "'");
				}
				Constructor constructor = conversionClass.getConstructor(String[].class);
				return (Conversion) constructor.newInstance((Object[]) args);

			}
			return null;
		} catch (RuntimeException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
	}

	/**
	 * Returns the default {@link Conversion} that should be applied to the field based on its type.
	 * @param field The field whose values must be converted from a given parsed String.
	 * @return The default {@link Conversion} applied to the given field.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Conversion getDefaultConversion(Field field) {
		Parsed parsed = field.getAnnotation(Parsed.class);

		Class fieldType = field.getType();
		String nullRead = getNullReadValue(parsed);
		Object valueIfStringIsNull = null;

		ObjectConversion conversion = null;
		if (fieldType == Boolean.class || fieldType == boolean.class) {
			conversion = Conversions.toBoolean();
			valueIfStringIsNull = nullRead == null ? null : Boolean.valueOf(nullRead);
		} else if (fieldType == Character.class || fieldType == char.class) {
			conversion = Conversions.toChar();
			if (nullRead != null && nullRead.length() > 1) {
				throw new IllegalArgumentException("Invalid default value for character '" + nullRead + "'. It should contain one character only.");
			}
			valueIfStringIsNull = nullRead == null ? null : nullRead.charAt(0);
		} else if (fieldType == Byte.class || fieldType == byte.class) {
			conversion = Conversions.toByte();
			valueIfStringIsNull = nullRead == null ? null : Byte.valueOf(nullRead);
		} else if (fieldType == Short.class || fieldType == short.class) {
			conversion = Conversions.toShort();
			valueIfStringIsNull = nullRead == null ? null : Short.valueOf(nullRead);
		} else if (fieldType == Integer.class || fieldType == int.class) {
			conversion = Conversions.toInteger();
			valueIfStringIsNull = nullRead == null ? null : Integer.valueOf(nullRead);
		} else if (fieldType == Float.class || fieldType == float.class) {
			conversion = Conversions.toFloat();
			valueIfStringIsNull = nullRead == null ? null : Float.valueOf(nullRead);
		} else if (fieldType == Double.class || fieldType == double.class) {
			conversion = Conversions.toDouble();
			valueIfStringIsNull = nullRead == null ? null : Double.valueOf(nullRead);
		} else if (fieldType == BigInteger.class) {
			conversion = Conversions.toBigInteger();
			valueIfStringIsNull = nullRead == null ? null : new BigInteger(nullRead);
		} else if (fieldType == BigDecimal.class) {
			conversion = Conversions.toBigDecimal();
			valueIfStringIsNull = nullRead == null ? null : new BigDecimal(nullRead);
		}

		if (conversion != null) {
			conversion.setValueIfStringIsNull(valueIfStringIsNull);
			conversion.setValueIfObjectIsNull(getNullWriteValue(parsed));
		}

		return conversion;
	}
}
