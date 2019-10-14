/*
 * Copyright (c) 2018. Univocity Software Pty Ltd
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.common.record;

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

import java.lang.annotation.*;
import java.util.*;

class RecordMetaDataImpl<C extends Context> implements RecordMetaData {

	final C context;

	@SuppressWarnings("rawtypes")
	private Map<Class, Conversion> conversionByType = new HashMap<Class, Conversion>();

	@SuppressWarnings("rawtypes")
	private Map<Class, Map<Annotation, Conversion>> conversionsByAnnotation = new HashMap<Class, Map<Annotation, Conversion>>();

	private Map<Integer, Annotation> annotationHashes = new HashMap<Integer, Annotation>();
	private MetaData[] indexMap;

	private FieldConversionMapping conversions = null;

	RecordMetaDataImpl(C context) {
		this.context = context;
	}

	private MetaData getMetaData(String name) {
		int index = context.indexOf(name);
		if (index == -1) {
			getValidatedHeaders();
			throw new IllegalArgumentException("Header name '" + name + "' not found. Available columns are: " + Arrays.asList(selectedHeaders()));
		}
		return getMetaData(index);
	}

	private NormalizedString[] getValidatedHeaders() {
		NormalizedString[] headers = NormalizedString.toIdentifierGroupArray(context.headers());
		if (headers == null || headers.length == 0) {
			throw new IllegalStateException("No headers parsed from input nor provided in the user settings. Only index-based operations are available.");
		}
		return headers;
	}

	private MetaData getMetaData(Enum<?> column) {
		NormalizedString[] headers = NormalizedString.toIdentifierGroupArray(context.headers());
		if (headers == null || headers.length == 0) {
			throw new IllegalStateException("No headers parsed from input nor provided in the user settings. Only index-based operations are available.");
		}
		return getMetaData(context.indexOf(column));
	}

	public MetaData getMetaData(int index) {
		if (indexMap == null || indexMap.length < index + 1 || indexMap[index] == null) {
			synchronized (this) {
				if (indexMap == null || indexMap.length < index + 1 || indexMap[index] == null) {
					int startFrom = 0;
					int lastIndex = index;

					if (indexMap != null) {
						startFrom = indexMap.length;
						indexMap = Arrays.copyOf(indexMap, index + 1);
					} else {
						String[] headers = context.headers();
						if (headers != null && lastIndex < headers.length) {
							lastIndex = headers.length;
						}

						int[] indexes = context.extractedFieldIndexes();
						if (indexes != null) {
							for (int i = 0; i < indexes.length; i++) {
								if (lastIndex < indexes[i]) {
									lastIndex = indexes[i];
								}
							}
						}

						indexMap = new MetaData[lastIndex + 1];
					}

					for (int i = startFrom; i < lastIndex + 1; i++) {
						indexMap[i] = new MetaData(i);
					}
				}
			}
		}
		return indexMap[index];
	}

	@Override
	public int indexOf(Enum<?> column) {
		return getMetaData(column).index;
	}

	MetaData metadataOf(String headerName) {
		return getMetaData(headerName);
	}

	MetaData metadataOf(Enum<?> column) {
		return getMetaData(column);
	}

	MetaData metadataOf(int columnIndex) {
		return getMetaData(columnIndex);
	}

	@Override
	public int indexOf(String headerName) {
		return getMetaData(headerName).index;
	}

	@Override
	public Class<?> typeOf(Enum<?> column) {
		return getMetaData(column).type;
	}

	@Override
	public Class<?> typeOf(String headerName) {
		return getMetaData(headerName).type;
	}

	@Override
	public Class<?> typeOf(int columnIndex) {
		return getMetaData(columnIndex).type;
	}

	@Override
	public <T> void setDefaultValueOfColumns(T defaultValue, Enum<?>... columns) {
		for (Enum<?> column : columns) {
			getMetaData(column).defaultValue = defaultValue;
		}
	}

	@Override
	public <T> void setDefaultValueOfColumns(T defaultValue, String... headerNames) {
		for (String headerName : headerNames) {
			getMetaData(headerName).defaultValue = defaultValue;
		}
	}

	@Override
	public <T> void setDefaultValueOfColumns(T defaultValue, int... columnIndexes) {
		for (int columnIndex : columnIndexes) {
			getMetaData(columnIndex).defaultValue = defaultValue;
		}
	}

	@Override
	public Object defaultValueOf(Enum<?> column) {
		return getMetaData(column).defaultValue;
	}

	@Override
	public Object defaultValueOf(String headerName) {
		return getMetaData(headerName).defaultValue;
	}

	@Override
	public Object defaultValueOf(int columnIndex) {
		return getMetaData(columnIndex).defaultValue;
	}

	private FieldConversionMapping getConversions() {
		if (conversions == null) {
			conversions = new FieldConversionMapping();
		}
		return conversions;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public <T extends Enum<T>> FieldSet<T> convertFields(Class<T> enumType, Conversion... conversions) {
		return (FieldSet) getConversions().applyConversionsOnFieldEnums(conversions);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public FieldSet<String> convertFields(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldNames(conversions);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public FieldSet<Integer> convertIndexes(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldIndexes(conversions);
	}

	@Override
	public String[] headers() {
		return context.headers();
	}

	@Override
	public String[] selectedHeaders() {
		return context.selectedHeaders();
	}

	String getValue(String[] data, String headerName) {
		MetaData md = metadataOf(headerName);
		if (md.index >= data.length) {
			return null;
		}
		return data[md.index];
	}

	String getValue(String[] data, int columnIndex) {
		MetaData md = metadataOf(columnIndex);
		return data[md.index];
	}

	String getValue(String[] data, Enum<?> column) {
		MetaData md = metadataOf(column);
		return data[md.index];
	}

	@SuppressWarnings("rawtypes")
	private <T> T convert(MetaData md, String[] data, Class<T> expectedType, Conversion[] conversions) {
		return expectedType.cast(convert(md, data, conversions));
	}

	@SuppressWarnings("rawtypes")
	private Object convert(MetaData md, String[] data, Object defaultValue, Conversion[] conversions) {
		Object out = convert(md, data, conversions);
		return out == null ? defaultValue : out;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static Object convert(MetaData md, String[] data, Conversion[] conversions) {
		Object out = data[md.index];
		for (int i = 0; i < conversions.length; i++) {
			out = conversions[i].execute(out);
		}
		return out;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	<T> T getValue(String[] data, String headerName, T defaultValue, Conversion[] conversions) {
		return (T) convert(metadataOf(headerName), data, defaultValue, conversions);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	<T> T getValue(String[] data, int columnIndex, T defaultValue, Conversion[] conversions) {
		return (T) convert(metadataOf(columnIndex), data, defaultValue, conversions);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	<T> T getValue(String[] data, Enum<?> column, T defaultValue, Conversion[] conversions) {
		return (T) convert(metadataOf(column), data, defaultValue, conversions);
	}

	@SuppressWarnings("rawtypes")
	<T> T getValue(String[] data, String headerName, Class<T> expectedType, Conversion[] conversions) {
		return convert(metadataOf(headerName), data, expectedType, conversions);
	}

	@SuppressWarnings("rawtypes")
	<T> T getValue(String[] data, int columnIndex, Class<T> expectedType, Conversion[] conversions) {
		return convert(metadataOf(columnIndex), data, expectedType, conversions);
	}

	@SuppressWarnings("rawtypes")
	<T> T getValue(String[] data, Enum<?> column, Class<T> expectedType, Conversion[] conversions) {
		return convert(metadataOf(column), data, expectedType, conversions);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private <T> T convert(MetaData md, String[] data, Class<T> type, T defaultValue, Annotation annotation) {
		Object out = md.index < data.length ? data[md.index] : null;

		if (out == null) {
			out = defaultValue == null ? md.defaultValue : defaultValue;
		}

		if (annotation == null) {
			initializeMetadataConversions(data, md);
			out = md.convert(out);

			if (out == null) {
				out = defaultValue == null ? md.defaultValue : defaultValue;
			}
		}

		if (type != null) {
			if (out != null && type.isAssignableFrom(out.getClass())) {
				return (T) out;
			}
			Conversion conversion;
			if (annotation == null) {
				conversion = conversionByType.get(type);
				if (conversion == null) {
					conversion = AnnotationHelper.getDefaultConversion(type, null, null);
					conversionByType.put(type, conversion);
				}
			} else {
				Map<Annotation, Conversion> m = conversionsByAnnotation.get(type);
				if (m == null) {
					m = new HashMap<Annotation, Conversion>();
					conversionsByAnnotation.put(type, m);
				}
				conversion = m.get(annotation);
				if (conversion == null) {
					conversion = AnnotationHelper.getConversion(type, annotation);
					m.put(annotation, conversion);
				}
			}

			if (conversion == null) {
				if(type == String.class){
					if(out == null){
						return null;
					}
					return (T) (md.index < data.length ? data[md.index] : null);
				}
				String message = "";
				if (type == Date.class || type == Calendar.class) {
					message = ". Need to specify format for date";
				}
				DataProcessingException exception = new DataProcessingException("Cannot convert '{value}' to " + type.getName() + message);
				exception.setValue(out);
				exception.setErrorContentLength(context.errorContentLength());
				throw exception;


			}
			out = conversion.execute(out);

		}
		if (type == null) {
			return (T) out;
		}
		try {
			return type.cast(out);
		} catch (ClassCastException e) {
			DataProcessingException exception = new DataProcessingException("Cannot cast value '{value}' of type " + out.getClass().toString() + " to " + type.getName());
			exception.setValue(out);
			exception.setErrorContentLength(context.errorContentLength());
			throw exception;
		}
	}

	private void initializeMetadataConversions(String[] data, MetaData md) {
		if (conversions != null) {
			synchronized (this) {

				String[] headers = headers();
				if (headers == null) {
					headers = data;
				}
				conversions.prepareExecution(false, headers);
				md.setDefaultConversions(conversions.getConversions(md.index, md.type));
			}
		}
	}

	<T> T getObjectValue(String[] data, String headerName, Class<T> type, T defaultValue) {
		return convert(metadataOf(headerName), data, type, defaultValue, null);
	}

	<T> T getObjectValue(String[] data, int columnIndex, Class<T> type, T defaultValue) {
		return convert(metadataOf(columnIndex), data, type, defaultValue, null);
	}

	<T> T getObjectValue(String[] data, Enum<?> column, Class<T> type, T defaultValue) {
		return convert(metadataOf(column), data, type, defaultValue, null);
	}

	<T> T getObjectValue(String[] data, String headerName, Class<T> type, T defaultValue, String format, String... formatOptions) {
		if (format == null) {
			return getObjectValue(data, headerName, type, defaultValue);
		}
		return convert(metadataOf(headerName), data, type, defaultValue, buildAnnotation(type, format, formatOptions));
	}

	<T> T getObjectValue(String[] data, int columnIndex, Class<T> type, T defaultValue, String format, String... formatOptions) {
		if (format == null) {
			return getObjectValue(data, columnIndex, type, defaultValue);
		}
		return convert(metadataOf(columnIndex), data, type, defaultValue, buildAnnotation(type, format, formatOptions));
	}

	<T> T getObjectValue(String[] data, Enum<?> column, Class<T> type, T defaultValue, String format, String... formatOptions) {
		if (format == null) {
			return getObjectValue(data, column, type, defaultValue);
		}
		return convert(metadataOf(column), data, type, defaultValue, buildAnnotation(type, format, formatOptions));
	}

	static Annotation buildBooleanStringAnnotation(final String[] trueStrings, final String[] falseStrings) {
		return new com.univocity.parsers.annotations.BooleanString() {
			@Override
			public String[] trueStrings() {
				return trueStrings == null ? ArgumentUtils.EMPTY_STRING_ARRAY : trueStrings;
			}

			@Override
			public String[] falseStrings() {
				return falseStrings == null ? ArgumentUtils.EMPTY_STRING_ARRAY : falseStrings;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return com.univocity.parsers.annotations.BooleanString.class;
			}
		};
	}

	private static Annotation newFormatAnnotation(final String format, final String... formatOptions) {
		return new com.univocity.parsers.annotations.Format() {
			@Override
			public String[] formats() {
				return new String[]{format};
			}

			@Override
			public String[] options() {
				return formatOptions;
			}

			@Override
			public Class<? extends Annotation> annotationType() {
				return com.univocity.parsers.annotations.Format.class;
			}
		};
	}

	<T> Annotation buildAnnotation(Class<T> type, final String args1, final String... args2) {
		Integer hash = (type.hashCode() * 31) + String.valueOf(args1).hashCode() + (31 * Arrays.toString(args2).hashCode());
		Annotation out = annotationHashes.get(hash);
		if (out == null) {
			if (type == Boolean.class || type == boolean.class) {
				out = buildBooleanStringAnnotation(args1 == null ? null : new String[]{args1}, args2);
			} else {
				out = newFormatAnnotation(args1, args2);
			}
			annotationHashes.put(hash, out);
		}
		return out;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setTypeOfColumns(Class<?> type, Enum... columns) {
		for (int i = 0; i < columns.length; i++) {
			getMetaData(columns[i]).type = type;
		}
	}

	@Override
	public void setTypeOfColumns(Class<?> type, String... headerNames) {
		for (int i = 0; i < headerNames.length; i++) {
			getMetaData(headerNames[i]).type = type;
		}
	}

	@Override
	public void setTypeOfColumns(Class<?> type, int... columnIndexes) {
		for (int i = 0; i < columnIndexes.length; i++) {
			getMetaData(columnIndexes[i]).type = type;
		}
	}

	@Override
	public boolean containsColumn(String headerName) {
		if (headerName == null) {
			return false;
		}
		return context.indexOf(headerName) != -1;
	}
}
