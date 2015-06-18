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

import java.beans.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.*;
import java.text.*;
import java.util.*;

import com.univocity.parsers.annotations.*;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.common.*;
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
		if ("null".equals(defaultValue)) {
			return null;
		}
		if ("'null'".equals(defaultValue)) {
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
			} else if (annType == EnumOptions.class) {
				if (!fieldType.isEnum()) {
					throw new IllegalStateException("Invalid " + EnumOptions.class.getName() + " annotation on attribute " + field.getName() + " of type " + field.getType().getName() + ". Attribute must be an enum type.");
				}
				EnumOptions enumOptions = ((EnumOptions) annotation);
				String element = enumOptions.customElement().trim();
				if (element.isEmpty()) {
					element = null;
				}

				Enum nullReadValue = nullRead == null ? null : Enum.valueOf(fieldType, nullRead);

				return new EnumConversion(fieldType, nullReadValue, nullWrite, element, enumOptions.selectors());
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
					throw new DataProcessingException("Invalid annotation: Field " + field.getName() + " has type " + fieldType.getName() + " instead of boolean.");
				}
				BooleanString boolString = ((BooleanString) annotation);
				String[] falseStrings = boolString.falseStrings();
				String[] trueStrings = boolString.trueStrings();
				Boolean valueForNull = nullRead == null ? null : Boolean.valueOf(nullRead);

				if (valueForNull == null && fieldType == boolean.class) {
					valueForNull = Boolean.FALSE;
				}

				return Conversions.toBoolean(valueForNull, nullWrite, trueStrings, falseStrings);
			} else if (annType == Format.class) {
				Format format = ((Format) annotation);
				String[] formats = format.formats();

				Conversion conversion = null;

				if (fieldType == BigDecimal.class) {
					BigDecimal defaultForNull = nullRead == null ? null : new BigDecimal(nullRead);
					conversion = Conversions.formatToBigDecimal(defaultForNull, nullWrite, formats);
				} else if (Number.class.isAssignableFrom(fieldType)) {
					conversion = Conversions.formatToNumber(formats);
				} else {
					Date dateIfNull = null;
					if (nullRead != null) {
						if ("now".equalsIgnoreCase(nullRead)) {
							dateIfNull = new Date();
						} else {
							if (formats.length == 0) {
								throw new DataProcessingException("No format defined");
							}
							SimpleDateFormat sdf = new SimpleDateFormat(formats[0]);
							dateIfNull = sdf.parse(nullRead);
						}
					}

					if (Date.class == fieldType) {
						conversion = Conversions.toDate(dateIfNull, nullWrite, formats);
					} else if (Calendar.class == fieldType) {
						Calendar calendarIfNull = null;
						if (dateIfNull != null) {
							calendarIfNull = Calendar.getInstance();
							calendarIfNull.setTime(dateIfNull);
						}
						conversion = Conversions.toCalendar(calendarIfNull, nullWrite, formats);
					}
				}

				if (conversion != null) {
					String[] options = format.options();
					if (options.length > 0) {
						//noinspection ConstantConditions
						if (conversion instanceof FormattedConversion) {
							Object[] formatters = ((FormattedConversion) conversion).getFormatterObjects();
							for (Object formatter : formatters) {
								applyFormatSettings(formatter, options);
							}
						} else {
							throw new DataProcessingException("Options '" + Arrays.toString(options) + "' not supported by conversion of type '" + conversion.getClass() + "'. It must implement " + FormattedConversion.class);
						}
					}
					return conversion;
				}

			} else if (annType == Convert.class) {
				Convert convert = ((Convert) annotation);
				String[] args = convert.args();
				Class conversionClass = convert.conversionClass();
				if (!Conversion.class.isAssignableFrom(conversionClass)) {
					throw new DataProcessingException("Not a valid conversion class: '" + conversionClass.getSimpleName() + "' (" + conversionClass.getName() + ')');
				}
				try {
					Constructor constructor = conversionClass.getConstructor(String[].class);
					return (Conversion) constructor.newInstance((Object) args);
				} catch (NoSuchMethodException e) {
					throw new DataProcessingException("Could not find a public constructor with a String[] parameter in custom conversion class '" + conversionClass.getSimpleName() + "' (" + conversionClass.getName() + ')', e);
				} catch (Exception e) {
					throw new DataProcessingException("Unexpected error instantiating custom conversion class '" + conversionClass.getSimpleName() + "' (" + conversionClass.getName() + ')', e);
				}
			}
			return null;
		} catch (DataProcessingException ex) {
			throw ex;
		} catch (Throwable ex) {
			throw new DataProcessingException("Unexpected error identifying conversions to apply over field " + field.getName() + " of class " + field.getDeclaringClass().getName(), ex);
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
				throw new DataProcessingException("Invalid default value for character '" + nullRead + "'. It should contain one character only.");
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
		} else if (fieldType == Long.class || fieldType == long.class) {
			conversion = Conversions.toLong();
			valueIfStringIsNull = nullRead == null ? null : Long.valueOf(nullRead);
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
		} else if (Enum.class.isAssignableFrom(fieldType)) {
			conversion = Conversions.toEnum(fieldType);
		}

		if (conversion != null) {
			conversion.setValueIfStringIsNull(valueIfStringIsNull);
			conversion.setValueIfObjectIsNull(getNullWriteValue(parsed));
		}

		return conversion;
	}

	public static void applyFormatSettings(Object formatter, String[] propertiesAndValues) {
		if (propertiesAndValues.length == 0) {
			return;
		}

		Map<String, String> values = new HashMap<String, String>();
		for (String setting : propertiesAndValues) {
			if (setting == null) {
				throw new DataProcessingException("Illegal format among: " + Arrays.toString(propertiesAndValues));
			}
			String[] pair = setting.split("=");
			if (pair.length != 2) {
				throw new DataProcessingException("Illegal format setting '" + setting + "' among: " + Arrays.toString(propertiesAndValues));
			}

			values.put(pair[0], pair[1]);
		}

		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(formatter.getClass(), Object.class);
			for (PropertyDescriptor property : beanInfo.getPropertyDescriptors()) {
				String name = property.getName();
				String value = values.remove(name);
				if (value != null) {
					invokeSetter(formatter, property, value);
				}

				if ("decimalFormatSymbols".equals(property.getName())) {
					DecimalFormatSymbols modifiedDecimalSymbols = new DecimalFormatSymbols();
					boolean modified = false;
					try {
						BeanInfo decimalBeanInfo = Introspector.getBeanInfo(modifiedDecimalSymbols.getClass(), Object.class);
						for (PropertyDescriptor prop : decimalBeanInfo.getPropertyDescriptors()) {
							value = values.remove(prop.getName());
							if (value != null) {
								invokeSetter(modifiedDecimalSymbols, prop, value);
								modified = true;
							}
						}

						if (modified) {
							property.getWriteMethod().invoke(formatter, modifiedDecimalSymbols);
						}
					} catch (Throwable ex) {
						throw new DataProcessingException("Error trying to configure decimal symbols  of formatter '" + formatter.getClass() + '.', ex);
					}
				}
			}
		} catch (IntrospectionException e) {
			//ignore and proceed
		}

		if (!values.isEmpty()) {
			throw new DataProcessingException("Cannot find properties in formatter of type '" + formatter.getClass() + "': " + values);
		}
	}

	private static void invokeSetter(Object formatter, PropertyDescriptor property, String value) {
		Method writeMethod = property.getWriteMethod();
		if (writeMethod == null) {
			throw new DataProcessingException("Cannot set property '" + property.getName() + "' of formatter '" + formatter.getClass() + "' to " + value + ". No setter defined");
		}
		Class<?> parameterType = writeMethod.getParameterTypes()[0];
		Object parameterValue = null;
		if (parameterType == String.class) {
			parameterValue = value;
		} else if (parameterType == Integer.class || parameterType == int.class) {
			parameterValue = Integer.parseInt(value);
		} else if (parameterType == Character.class || parameterType == char.class) {
			parameterValue = value.charAt(0);
		} else if (parameterType == Currency.class) {
			parameterValue = Currency.getInstance(value);
		} else if (parameterType == Boolean.class) {
			parameterValue = Boolean.valueOf(value);
		} else if (parameterType == TimeZone.class) {
			parameterValue = TimeZone.getTimeZone(value);
		} else if (parameterType == DateFormatSymbols.class) {
			parameterValue = DateFormatSymbols.getInstance(new Locale(value));
		}
		if (parameterValue == null) {
			throw new DataProcessingException("Cannot set property '" + property.getName() + "' of formatter '" + formatter.getClass() + ". Cannot convert '" + value + "' to instance of " + parameterType);
		}

		try {
			writeMethod.invoke(formatter, parameterValue);
		} catch (Throwable e) {
			throw new DataProcessingException("Error setting property '" + property.getName() + "' of formatter '" + formatter.getClass() + ", with '" + parameterValue + "' (converted from '" + value + "')", e);
		}
	}

	private static boolean allFieldsIndexOrNameBased(boolean searchName, Class<?> beanClass) {
		boolean hasAnnotation = false;
		for (Field field : beanClass.getDeclaredFields()) {
			Parsed annotation = field.getAnnotation(Parsed.class);
			if (annotation != null) {
				hasAnnotation = true;
				if ((annotation.index() != -1 && searchName) || (annotation.index() == -1 && !searchName)) {
					return false;
				}
			}
		}
		return hasAnnotation;
	}

	public static boolean allFieldsIndexBased(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(false, beanClass);
	}

	public static boolean hasNestedElements(Class<?> beanClass) {
		for (Field field : beanClass.getDeclaredFields()) {
			if (field.getAnnotation(Nested.class) != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean allFieldsNameBased(Class<?> beanClass) {
		return allFieldsIndexOrNameBased(true, beanClass);
	}

	public static Integer[] getSelectedIndexes(Class<?> beanClass) {
		List<Integer> indexes = new ArrayList<Integer>();
		for (Field field : beanClass.getDeclaredFields()) {
			Parsed annotation = field.getAnnotation(Parsed.class);
			if (annotation != null) {
				if (annotation.index() != -1) {
					if (indexes.contains(annotation.index())) {
						throw new IllegalArgumentException("Duplicate field index '" + annotation.index() + "' found in attribute '" + field.getName() + "' of class " + beanClass.getName());
					}
					indexes.add(annotation.index());
				}
			}
		}

		return indexes.toArray(new Integer[indexes.size()]);
	}

	public static String[] deriveHeaderNamesFromFields(Class<?> beanClass) {
		ArrayList<String> out = new ArrayList<String>();
		ArrayList<Integer> indexes = new ArrayList<Integer>();
		Field[] declared = beanClass.getDeclaredFields();

		for (Field field : declared) {
			Parsed annotation = field.getAnnotation(Parsed.class);
			String name = null;
			if (annotation != null) {
				if (annotation.field().isEmpty()) {
					name = field.getName();
				} else {
					name = annotation.field();
				}
				if (annotation.index() != -1 && indexes.contains(annotation.index())) {
					throw new IllegalArgumentException("Duplicate field index found in attribute '" + field.getName() + "' of class " + beanClass.getName());
				}
				indexes.add(annotation.index());
			}
			if (name != null) {
				out.add(name);
			}
		}

		int col = -1;
		for (int i : indexes) {
			col++;
			if (i == -1) {
				continue;
			}
			if (i != col) {
				if (i >= out.size()) {
					return ArgumentUtils.EMPTY_STRING_ARRAY;  // index goes beyond list of header names, can't derive.
				}
				Collections.swap(out, i, col);
			}
		}

		return out.toArray(new String[out.size()]);
	}

	public static Headers findHeadersAnnotation(Class<?> beanClass) {
		Headers headers;

		Class<?> parent = beanClass;
		do {
			headers = parent.getAnnotation(Headers.class);

			if (headers != null) {
				return headers;
			} else {
				for (Class<?> iface : parent.getInterfaces()) {
					headers = findHeadersAnnotation(iface);
					if (headers != null) {
						return headers;
					}
				}
			}

			parent = parent.getSuperclass();
		} while (parent != null);

		return null;
	}
}
