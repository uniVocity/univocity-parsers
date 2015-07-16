/*
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
 */

package com.univocity.parsers.common.record;

import com.univocity.parsers.annotations.helpers.*;
import com.univocity.parsers.common.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

import java.util.*;

class RecordMetaDataImpl implements RecordMetaData {

	private final ParsingContext context;

	private Map<Class, Conversion> conversionByType = new HashMap<Class, Conversion>();
	private Map<String, Integer> columnMap;
	private int[] enumMap;
	private MetaData[] indexMap;

	private FieldConversionMapping conversions = null;

	private static final class MetaData {
		MetaData(int index) {
			this.index = index;
		}

		public final int index;
		public Class<?> type = String.class;
		public Object defaultValue = null;
		public Conversion[] conversions = null;
	}

	RecordMetaDataImpl(ParsingContext context) {
		this.context = context;
	}

	private MetaData getMetaData(String name) {
		if (columnMap == null) {
			String[] headers = getValidatedHeaders();
			columnMap = new HashMap<String, Integer>(headers.length);

			int[] extractedIndexes = context.extractedFieldIndexes();
			boolean columnsReordered = context.columnsReordered();

			if (columnsReordered && extractedIndexes != null) {
				for (int i = 0; i < extractedIndexes.length; i++) {
					int originalIndex = extractedIndexes[i];
					String header = headers[originalIndex];
					columnMap.put(header, i);
				}
			} else {
				for (int i = 0; i < extractedIndexes.length; i++) {
					columnMap.put(headers[i], i);
				}
			}
		}

		return getMetaData(columnMap.get(name).intValue());
	}

	private String[] getValidatedHeaders() {
		String[] headers = context.headers();
		if (headers == null || headers.length == 0) {
			throw new IllegalStateException("No headers parsed from input nor provided in the user settings. Only index-based operations are available.");
		}
		return headers;
	}

	private MetaData getMetaData(Enum<?> column) {
		if (enumMap == null) {
			String[] headers = getValidatedHeaders();
			Enum<?>[] constants = column.getClass().getEnumConstants();
			int lastOrdinal = Integer.MIN_VALUE;
			for (int i = 0; i < constants.length; i++) {
				if (lastOrdinal < constants[i].ordinal()) {
					lastOrdinal = constants[i].ordinal();
				}
			}

			enumMap = new int[lastOrdinal];
			for (int i = 0; i < constants.length; i++) {
				Enum<?> constant = constants[i];
				String name = constant.toString();
				int index = ArgumentUtils.indexOf(headers, name);
				enumMap[constant.ordinal()] = index;
			}
		}
		return getMetaData(enumMap[column.ordinal()]);
	}

	public MetaData getMetaData(int index) {
		if (indexMap == null || indexMap.length < index) {
			int startFrom = 0;
			int lastIndex = index;

			if (indexMap != null) {
				startFrom = indexMap.length;
				indexMap = Arrays.copyOf(indexMap, index);
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

				indexMap = new MetaData[lastIndex];
			}

			for (int i = startFrom; i < lastIndex; i++) {
				indexMap[i] = new MetaData(i);
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
	public void setTypeOf(Enum<?> column, Class<?> type) {
		getMetaData(column).type = type;
	}

	@Override
	public void setTypeOf(String headerName, Class<?> type) {
		getMetaData(headerName).type = type;
	}

	@Override
	public void setTypeOf(int columnIndex, Class<?> type) {
		getMetaData(columnIndex).type = type;
	}

	@Override
	public <T> void setDefaultValueOf(Enum<?> column, T defaultValue) {
		getMetaData(column).defaultValue = defaultValue;
	}

	@Override
	public <T> void setDefaultValueOf(String headerName, T defaultValue) {
		getMetaData(headerName).defaultValue = defaultValue;
	}

	@Override
	public <T> void setDefaultValueOf(int columnIndex, T defaultValue) {
		getMetaData(columnIndex).defaultValue = defaultValue;
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

	@Override
	public <T extends Enum<T>> FieldSet<T> convertFields(Class<T> enumType, Conversion... conversions) {
		return (FieldSet<T>) getConversions().applyConversionsOnFieldEnums(conversions);
	}

	@Override
	public FieldSet<String> convertFields(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldNames(conversions);
	}

	@Override
	public FieldSet<Integer> convertIndexes(Conversion... conversions) {
		return getConversions().applyConversionsOnFieldIndexes(conversions);
	}

	@Override
	public String[] headers() {
		return context.headers();
	}

	String getValue(String[] data, String headerName) {
		MetaData md = metadataOf(headerName);
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

	private <T> T convert(MetaData md, String[] data, Class<T> type, T defaultValue) {
		Object out = data[md.index];

		if (type != null) {
			Conversion conversion = null;
			if (type != String.class) {
				conversion = conversionByType.get(type);
				if (conversion == null) {
					conversion = AnnotationHelper.getDefaultConversion(type, null);
					conversionByType.put(type, conversion);
				}
			}
			conversion.execute(data[md.index]);
		} else if (md.conversions == null) {
			if (conversions != null) {
				String[] headers = headers();
				if (headers == null) {
					headers = data;
				}
				conversions.prepareExecution(headers);
			}
			md.conversions = conversions.getConversions(md.index, md.type);

			for (int i = 0; i < md.conversions.length; i++) {
				out = md.conversions[i].execute(out);
			}
		}
		if (out == null) {
			return defaultValue;
		}
		return type.cast(out);
	}

	<T> T getObjectValue(String[] data, String headerName, Class<T> type, T defaultValue) {
		return convert(metadataOf(headerName), data, type, defaultValue);
	}

	<T> T getObjectValue(String[] data, int columnIndex, Class<T> type, T defaultValue) {
		return convert(metadataOf(columnIndex), data, type, defaultValue);
	}

	<T> T getObjectValue(String[] data, Enum<?> column, Class<T> type, T defaultValue) {
		return convert(metadataOf(column), data, type, defaultValue);
	}

}
