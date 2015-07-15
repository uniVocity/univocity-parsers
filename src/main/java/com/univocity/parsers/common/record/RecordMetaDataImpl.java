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

import com.univocity.parsers.common.*;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

import java.util.*;

class RecordMetaDataImpl implements RecordMetaData {

	private final ParsingContext context;

	private Map<String, Metadata> columnMap;
	private Metadata[] enumMap;
	private Metadata[] indexMap;

	private FieldConversionMapping conversions = null;

	private static final class Metadata {
		Metadata(int index) {
			this.index = index;
		}

		public final int index;
		public Class<?> type = String.class;
		public Object defaultValue = null;
		public Conversion[] conversionSequence; //TODO initialize on first run
	}

	RecordMetaDataImpl(ParsingContext context) {
		this.context = context;
	}

	private Metadata getMetaData(String name) {
		if (columnMap == null) {
			String[] headers = getValidatedHeaders();
			columnMap = new HashMap<String, Metadata>(headers.length);

			int[] extractedIndexes = context.extractedFieldIndexes();
			boolean columnsReordered = context.columnsReordered();

			if (columnsReordered && extractedIndexes != null) {
				for (int i = 0; i < extractedIndexes.length; i++) {
					int originalIndex = extractedIndexes[i];
					String header = headers[originalIndex];
					columnMap.put(header, new Metadata(i));
				}
			} else {
				for (int i = 0; i < extractedIndexes.length; i++) {
					columnMap.put(headers[i], new Metadata(i));
				}
			}
		}

		return columnMap.get(name);
	}

	private String[] getValidatedHeaders() {
		String[] headers = context.headers();
		if (headers == null || headers.length == 0) {
			throw new IllegalStateException("No headers parsed from input nor provided in the user settings. Only index-based operations are available.");
		}
		return headers;
	}

	private Metadata getMetaData(Enum<?> column) {
		if (enumMap == null) {
			String[] headers = getValidatedHeaders();
			Enum<?>[] constants = column.getClass().getEnumConstants();
			int lastOrdinal = Integer.MIN_VALUE;
			for (int i = 0; i < constants.length; i++) {
				if (lastOrdinal < constants[i].ordinal()) {
					lastOrdinal = constants[i].ordinal();
				}
			}

			enumMap = new Metadata[lastOrdinal];
			for (int i = 0; i < constants.length; i++) {
				Enum<?> constant = constants[i];
				String name = constant.toString();
				int index = ArgumentUtils.indexOf(headers, name);
				enumMap[constant.ordinal()] = new Metadata(index);
			}
		}
		return enumMap[column.ordinal()];
	}

	public Metadata getMetaData(int index) {
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

				indexMap = new Metadata[lastIndex];
			}

			for (int i = startFrom; i < lastIndex; i++) {
				indexMap[i] = new Metadata(i);
			}
		}
		return indexMap[index];
	}

	@Override
	public int indexOf(Enum<?> column) {
		return getMetaData(column).index;
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
}
