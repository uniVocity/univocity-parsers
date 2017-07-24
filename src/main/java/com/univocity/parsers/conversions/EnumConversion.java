/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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

import java.lang.reflect.*;
import java.util.*;

/**
 * Converts Strings to enumeration constants and vice versa.
 *
 * <p> This class supports multiple types of identification of enumeration constants. For example, you can match the literal ({@link Enum#name()} the ordinal ({@link Enum#ordinal()} or
 * the result of a method defined in your enumeration.
 *
 * <p> The reverse conversion from an enumeration to String (in {@link EnumConversion#revert(Enum)} will return a String using the first {@link EnumSelector} provided in the constructor of this class.
 *
 * @param <T> the enumeration type whose constants will be converted from/to {@code String}

 * @see ObjectConversion
 * @see EnumSelector
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public class EnumConversion<T extends Enum<T>> extends ObjectConversion<T> {

	private final Class<T> enumType;
	private final Field customEnumField;
	private final Method customEnumMethod;

	private final EnumSelector[] selectors;
	private final Map<String, T>[] conversions;

	/**
	 * Defines a conversion for an enumeration type that will attempt to match Strings against
	 * the results of the output produced by ({@link Enum#name()}, ({@link Enum#ordinal()} and ({@link Enum#toString()}
	 * of each constant of the given enumeration (@link {@link Class#getEnumConstants()}).
	 *
	 * @param enumType the enumeration type to be converted from/to {@code String}
	 */
	public EnumConversion(Class<T> enumType) {
		this(enumType, EnumSelector.NAME, EnumSelector.ORDINAL, EnumSelector.STRING);
	}

	/**
	 * Defines a conversion for an enumeration type that will attempt to match Strings the list of {@link EnumSelector}s, in the specified order.
	 * Each {@link EnumSelector} identifies which element of each constant of the enumeration class (@link {@link Class#getEnumConstants()}
	 * should be used to match equivalent {@code String}s.
	 *
	 * @param enumType the enumeration type to be converted from/to {@code String}
	 * @param selectors the selection elements of the enumeration to use for matching {@code String}s.
	 */
	public EnumConversion(Class<T> enumType, EnumSelector... selectors) {
		this(enumType, null, null, null, selectors);
	}

	/**
	 * Defines a conversion for an enumeration type that will attempt to match Strings the list of {@link EnumSelector}s, in the specified order.
	 * Each {@link EnumSelector} identifies which element of each constant of the enumeration class (@link {@link Class#getEnumConstants()}
	 * should be used to match equivalent {@code String}s.
	 *
	 * @param enumType the enumeration type to be converted from/to {@code String}
	 * @param customEnumElement name of custom element of the enumeration (attribute or method) whose values should be used to match equivalent {@code String}s.
	 * @param selectors the selection elements of the enumeration to use for matching {@code String}s.
	 */
	public EnumConversion(Class<T> enumType, String customEnumElement, EnumSelector... selectors) {
		this(enumType, null, null, customEnumElement);
	}

	/**
	 * Defines a conversion for an enumeration type that will attempt to match Strings the list of {@link EnumSelector}s, in the specified order.
	 * Each {@link EnumSelector} identifies which element of each constant of the enumeration class (@link {@link Class#getEnumConstants()}
	 * should be used to match equivalent {@code String}s.
	 *
	 * @param enumType the enumeration type to be converted from/to {@code String}
	 * @param valueIfStringIsNull the default enumeration constant to use if the input {@code String} is {@code null}
	 * @param valueIfEnumIsNull the default {@code String} value to use if the input {@code enum} constant is {@code null}
	 * @param customEnumElement name of custom element of the enumeration (attribute or method) whose values should be used to match equivalent {@code String}s.
	 * @param selectors the selection elements of the enumeration to use for matching {@code String}s.
	 */
	@SuppressWarnings("unchecked")
	public EnumConversion(Class<T> enumType, T valueIfStringIsNull, String valueIfEnumIsNull, String customEnumElement, EnumSelector... selectors) {
		super(valueIfStringIsNull, valueIfEnumIsNull);
		this.enumType = enumType;

		if (customEnumElement != null) {
			customEnumElement = customEnumElement.trim();
			if (customEnumElement.isEmpty()) {
				customEnumElement = null;
			}
		}

		LinkedHashSet<EnumSelector> selectorSet = new LinkedHashSet<EnumSelector>();
		Collections.addAll(selectorSet, selectors);

		if ((selectorSet.contains(EnumSelector.CUSTOM_FIELD) || selectorSet.contains(EnumSelector.CUSTOM_METHOD)) && customEnumElement == null) {
			throw new IllegalArgumentException("Cannot create custom enum conversion without a field name to use");
		}

		Field field = null;
		Method method = null;
		if (customEnumElement != null) {
			IllegalStateException fieldError = null;
			IllegalStateException methodError = null;

			try {
				field = enumType.getDeclaredField(customEnumElement);
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
			} catch (Throwable e) {
				fieldError = new IllegalStateException("Unable to access custom field '" + customEnumElement + "' in enumeration type " + enumType.getName(), e);
			}

			if (field == null) {
				try {
					method = enumType.getDeclaredMethod(customEnumElement);
					if (!method.isAccessible()) {
						method.setAccessible(true);
					}
				} catch (Throwable e) {
					methodError = new IllegalStateException("Unable to access custom method '" + customEnumElement + "' in enumeration type " + enumType.getName(), e);
				}
				if (method != null) {
					if (method.getReturnType() == void.class) {
						throw new IllegalArgumentException("Custom method '" + customEnumElement + "' in enumeration type " + enumType.getName() + " must return a value");
					}
					if (!selectorSet.contains(EnumSelector.CUSTOM_METHOD)) {
						selectorSet.add(EnumSelector.CUSTOM_METHOD);
					}
				}
			} else if (!selectorSet.contains(EnumSelector.CUSTOM_FIELD)) {
				selectorSet.add(EnumSelector.CUSTOM_FIELD);
			}

			if (selectorSet.contains(EnumSelector.CUSTOM_FIELD) && fieldError != null) {
				throw fieldError;
			}
			if (selectorSet.contains(EnumSelector.CUSTOM_METHOD) && methodError != null) {
				throw methodError;
			}
			if (field == null && method == null) {
				throw new IllegalStateException("No method/field named '" + customEnumElement + "' found in enumeration type " + enumType.getName());
			}
		}

		if (selectorSet.contains(EnumSelector.CUSTOM_FIELD) && selectorSet.contains(EnumSelector.CUSTOM_METHOD)) {
			throw new IllegalArgumentException("Cannot create custom enum conversion using both method and field values");
		}

		if (selectorSet.isEmpty()) {
			throw new IllegalArgumentException("Selection of enum conversion types cannot be empty.");
		}

		this.customEnumField = field;
		this.customEnumMethod = method;
		this.selectors = selectorSet.toArray(new EnumSelector[selectorSet.size()]);
		this.conversions = new Map[selectorSet.size()];
		initializeMappings(selectorSet);
	}

	private void initializeMappings(Set<EnumSelector> conversionTypes) {
		T[] constants = enumType.getEnumConstants();
		int i = 0;
		for (EnumSelector conversionType : conversionTypes) {
			Map<String, T> map = new HashMap<String, T>(constants.length);
			conversions[i++] = map;
			for (T constant : constants) {
				String key = getKey(constant, conversionType);
				if (map.containsKey(key)) {
					throw new IllegalArgumentException("Enumeration element type " + conversionType + " does not uniquely identify elements of " + enumType.getName() + ". Got duplicate value '" + key + "' from constants '" + constant
						+ "' and '" + map.get(key) + "'.");
				}
				map.put(key, constant);
			}
		}
	}

	private String getKey(T constant, EnumSelector conversionType) {
		switch (conversionType) {
			case NAME:
				return constant.name();
			case ORDINAL:
				return String.valueOf(constant.ordinal());
			case STRING:
				return constant.toString();
			case CUSTOM_FIELD:
				try {
					return String.valueOf(customEnumField.get(constant));
				} catch (Throwable e) {
					throw new IllegalStateException("Error reading custom field '" + customEnumField.getName() + "' from enumeration constant '" + constant + "' of type " + enumType.getName(), e);
				}
			case CUSTOM_METHOD:
				try {
					return String.valueOf(customEnumMethod.invoke(constant));
				} catch (Throwable e) {
					throw new IllegalStateException("Error reading custom method '" + customEnumMethod.getName() + "' from enumeration constant '" + constant + "' of type " + enumType.getName(), e);
				}
			default:
				throw new IllegalStateException("Unsupported enumeration selector type " + conversionType);
		}
	}

	@Override
	public String revert(T input) {
		if (input == null) {
			return super.revert(null);
		}

		return getKey(input, selectors[0]);
	}

	@Override
	protected T fromString(String input) {
		for (Map<String, T> conversion : conversions) {
			T value = conversion.get(input);
			if (value != null) {
				return value;
			}
		}
		DataProcessingException exception = new DataProcessingException("Cannot convert '{value}' to enumeration of type " + enumType.getName());
		exception.setValue(input);
		exception.markAsNonFatal();
		throw exception;
	}

}
