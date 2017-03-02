/*
 * Copyright 2015 uniVocity Software Pty Ltd
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.univocity.parsers.common.record;

import com.univocity.parsers.common.*;
import com.univocity.parsers.conversions.*;

import java.math.*;
import java.util.*;

class RecordImpl<C extends Context> implements Record {

	private final String[] data;
	private final RecordMetaDataImpl<C> metaData;

	RecordImpl(String[] data, RecordMetaDataImpl metaData) {
		this.data = data;
		this.metaData = metaData;
	}

	@Override
	public RecordMetaData getMetaData() {
		return metaData;
	}

	@Override
	public String[] getValues() {
		return data;
	}

	@Override
	public <T> T getValue(String headerName, Class<T> expectedType) {
		return metaData.getObjectValue(data, headerName, expectedType, null);
	}

	@Override
	public <T> T getValue(Enum<?> column, Class<T> expectedType) {
		return metaData.getObjectValue(data, column, expectedType, null);
	}

	@Override
	public <T> T getValue(int columnIndex, Class<T> expectedType) {
		return metaData.getObjectValue(data, columnIndex, expectedType, null);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, headerName, expectedType, conversions);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, column, expectedType, conversions);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, columnIndex, expectedType, conversions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(String headerName, T defaultValue) {
		return metaData.getObjectValue(data, headerName, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(Enum<?> column, T defaultValue) {
		return metaData.getObjectValue(data, column, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getValue(int columnIndex, T defaultValue) {
		return metaData.getObjectValue(data, columnIndex, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(String headerName, T defaultValue, Conversion... conversions) {
		return metaData.getValue(data, headerName, defaultValue, conversions);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions) {
		return metaData.getValue(data, column, defaultValue, conversions);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <T> T getValue(int columnIndex, T defaultValue, Conversion... conversions) {
		return metaData.getValue(data, columnIndex, defaultValue, conversions);
	}

	@Override
	public String getString(String headerName) {
		return metaData.getValue(data, headerName);
	}

	@Override
	public String getString(Enum<?> column) {
		return metaData.getValue(data, column);
	}

	@Override
	public String getString(int columnIndex) {
		return metaData.getValue(data, columnIndex);
	}

	@Override
	public String getString(String headerName, int maxLength) {
		return truncate(metaData.getValue(data, headerName), maxLength);
	}

	@Override
	public String getString(Enum<?> column, int maxLength) {
		return truncate(metaData.getValue(data, column), maxLength);
	}

	@Override
	public String getString(int columnIndex, int maxLength) {
		return truncate(metaData.getValue(data, columnIndex), maxLength);
	}

	private String truncate(String string, int maxLength) {
		if (string == null) {
			return null;
		}
		if (maxLength < 0) {
			throw new IllegalArgumentException("Maximum length can't be negative");
		}
		if (string.length() > maxLength) {
			return string.substring(0, maxLength);
		}
		return string;
	}

	@Override
	public Byte getByte(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Byte.class, null, format, formatOptions);
	}

	@Override
	public Byte getByte(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Byte.class, null, format, formatOptions);
	}

	@Override
	public Byte getByte(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Byte.class, null, format, formatOptions);
	}

	@Override
	public Short getShort(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Short.class, null, format, formatOptions);
	}

	@Override
	public Short getShort(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Short.class, null, format, formatOptions);
	}

	@Override
	public Short getShort(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Short.class, null, format, formatOptions);
	}

	@Override
	public Integer getInt(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Integer.class, null, format, formatOptions);
	}

	@Override
	public Integer getInt(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Integer.class, null, format, formatOptions);
	}

	@Override
	public Integer getInt(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Integer.class, null, format, formatOptions);
	}

	@Override
	public Long getLong(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Long.class, null, format, formatOptions);
	}

	@Override
	public Long getLong(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Long.class, null, format, formatOptions);
	}

	@Override
	public Long getLong(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Long.class, null, format, formatOptions);
	}

	@Override
	public Float getFloat(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Float.class, null, format, formatOptions);
	}

	@Override
	public Float getFloat(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Float.class, null, format, formatOptions);
	}

	@Override
	public Float getFloat(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Float.class, null, format, formatOptions);
	}

	@Override
	public Double getDouble(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Double.class, null, format, formatOptions);
	}

	@Override
	public Double getDouble(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Double.class, null, format, formatOptions);
	}

	@Override
	public Double getDouble(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Double.class, null, format, formatOptions);
	}

	@Override
	public Character getChar(String headerName) {
		return metaData.getObjectValue(data, headerName, Character.class, null);
	}

	@Override
	public Character getChar(Enum<?> column) {
		return metaData.getObjectValue(data, column, Character.class, null);
	}

	@Override
	public Character getChar(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Character.class, null);
	}

	@Override
	public Boolean getBoolean(String headerName) {
		return metaData.getObjectValue(data, headerName, Boolean.class, null);
	}

	@Override
	public Boolean getBoolean(Enum<?> column) {
		return metaData.getObjectValue(data, column, Boolean.class, null);
	}

	@Override
	public Boolean getBoolean(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Boolean.class, null);
	}

	@Override
	public Boolean getBoolean(String headerName, String trueString, String falseString) {
		return metaData.getObjectValue(data, headerName, Boolean.class, false, trueString, falseString);
	}

	@Override
	public Boolean getBoolean(Enum<?> column, String trueString, String falseString) {
		return metaData.getObjectValue(data, column, Boolean.class, false, trueString, falseString);
	}

	@Override
	public Boolean getBoolean(int columnIndex, String trueString, String falseString) {
		return metaData.getObjectValue(data, columnIndex, Boolean.class, false, trueString, falseString);
	}

	@Override
	public BigInteger getBigInteger(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigInteger getBigInteger(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigInteger getBigInteger(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Date.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Date.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Date.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(String headerName, String format, String... formatOptions) {
		return metaData.getObjectValue(data, headerName, Calendar.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(Enum<?> column, String format, String... formatOptions) {
		return metaData.getObjectValue(data, column, Calendar.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(int columnIndex, String format, String... formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Calendar.class, null, format, formatOptions);
	}


	private String[] buildSelection(String[] selectedFields) {
		if (selectedFields.length == 0) {
			selectedFields = metaData.headers();
		}
		return selectedFields;
	}

	private int[] buildSelection(int[] selectedIndexes) {
		if (selectedIndexes.length == 0) {
			selectedIndexes = new int[data.length];
			for (int i = 0; i < data.length; i++) {
				selectedIndexes[i] = i;
			}
		}
		return selectedIndexes;
	}

	public <T extends Enum<T>> T[] buildSelection(Class<T> enumType, T... selectedColumns) {
		if (selectedColumns.length == 0) {
			selectedColumns = enumType.getEnumConstants();
		}
		return selectedColumns;
	}

	@Override
	public Map<Integer, String> toIndexMap(int... selectedIndexes) {
		return fillIndexMap(new HashMap<Integer, String>(selectedIndexes.length), selectedIndexes);
	}


	@Override
	public Map<String, String> toFieldMap(String... selectedFields) {
		return fillFieldMap(new HashMap<String, String>(selectedFields.length), selectedFields);
	}

	@Override
	public <T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T... selectedColumns) {
		return fillEnumMap(new EnumMap<T, String>(enumType), selectedColumns);
	}

	@Override
	public Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields) {
		selectedFields = buildSelection(selectedFields);
		for (int i = 0; i < selectedFields.length; i++) {
			map.put(selectedFields[i], getString(selectedFields[i]));
		}
		return map;
	}

	@Override
	public Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes) {
		selectedIndexes = buildSelection(selectedIndexes);
		for (int i = 0; i < selectedIndexes.length; i++) {
			map.put(selectedIndexes[i], getString(selectedIndexes[i]));
		}
		return map;
	}

	@Override
	public <T extends Enum<T>> Map<T, String> fillEnumMap(Map<T, String> map, T... selectedColumns) {
		for (int i = 0; i < selectedColumns.length; i++) {
			map.put(selectedColumns[i], getString(selectedColumns[i]));
		}
		return map;
	}

	@Override
	public Map<String, Object> toFieldObjectMap(String... selectedFields) {
		return fillFieldObjectMap(new HashMap<String, Object>(selectedFields.length), selectedFields);
	}

	@Override
	public Map<Integer, Object> toIndexObjectMap(int... selectedIndex) {
		return fillIndexObjectMap(new HashMap<Integer, Object>(selectedIndex.length), selectedIndex);
	}

	@Override
	public <T extends Enum<T>> Map<T, Object> toEnumObjectMap(Class<T> enumType, T... selectedColumns) {
		return fillEnumObjectMap(new EnumMap<T, Object>(enumType), selectedColumns);
	}

	@Override
	public Map<String, Object> fillFieldObjectMap(Map<String, Object> map, String... selectedFields) {
		selectedFields = buildSelection(selectedFields);
		for (int i = 0; i < selectedFields.length; i++) {
			map.put(selectedFields[i], metaData.getObjectValue(data, selectedFields[i], null, null));
		}
		return map;
	}

	@Override
	public Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes) {
		selectedIndexes = buildSelection(selectedIndexes);
		for (int i = 0; i < selectedIndexes.length; i++) {
			map.put(selectedIndexes[i], metaData.getObjectValue(data, selectedIndexes[i], null, null));
		}
		return map;
	}

	@Override
	public <T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Map<T, Object> map, T... selectedColumns) {
		selectedColumns = buildSelection((Class<T>) selectedColumns.getClass().getComponentType(), selectedColumns);
		for (int i = 0; i < selectedColumns.length; i++) {
			map.put(selectedColumns[i], metaData.getObjectValue(data, selectedColumns[i], null, null));
		}
		return map;
	}

	@Override
	public BigInteger getBigInteger(String headerName) {
		return metaData.getObjectValue(data, headerName, BigInteger.class, null);
	}

	@Override
	public BigInteger getBigInteger(Enum<?> column) {
		return metaData.getObjectValue(data, column, BigInteger.class, null);
	}

	@Override
	public BigInteger getBigInteger(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, BigInteger.class, null);
	}

	@Override
	public BigDecimal getBigDecimal(String headerName) {
		return metaData.getObjectValue(data, headerName, BigDecimal.class, null);
	}

	@Override
	public BigDecimal getBigDecimal(Enum<?> column) {
		return metaData.getObjectValue(data, column, BigDecimal.class, null);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, BigDecimal.class, null);
	}

	@Override
	public Byte getByte(String headerName) {
		return metaData.getObjectValue(data, headerName, Byte.class, null);
	}

	@Override
	public Byte getByte(Enum<?> column) {
		return metaData.getObjectValue(data, column, Byte.class, null);
	}

	@Override
	public Byte getByte(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Byte.class, null);
	}

	@Override
	public Short getShort(String headerName) {
		return metaData.getObjectValue(data, headerName, Short.class, null);
	}

	@Override
	public Short getShort(Enum<?> column) {
		return metaData.getObjectValue(data, column, Short.class, null);
	}

	@Override
	public Short getShort(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Short.class, null);
	}

	@Override
	public Integer getInt(String headerName) {
		return metaData.getObjectValue(data, headerName, Integer.class, null);
	}

	@Override
	public Integer getInt(Enum<?> column) {
		return metaData.getObjectValue(data, column, Integer.class, null);
	}

	@Override
	public Integer getInt(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Integer.class, null);
	}

	@Override
	public Long getLong(String headerName) {
		return metaData.getObjectValue(data, headerName, Long.class, null);
	}

	@Override
	public Long getLong(Enum<?> column) {
		return metaData.getObjectValue(data, column, Long.class, null);
	}

	@Override
	public Long getLong(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Long.class, null);
	}

	@Override
	public Float getFloat(String headerName) {
		return metaData.getObjectValue(data, headerName, Float.class, null);
	}

	@Override
	public Float getFloat(Enum<?> column) {
		return metaData.getObjectValue(data, column, Float.class, null);
	}

	@Override
	public Float getFloat(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Float.class, null);
	}

	@Override
	public Double getDouble(String headerName) {
		return metaData.getObjectValue(data, headerName, Double.class, null);
	}

	@Override
	public Double getDouble(Enum<?> column) {
		return metaData.getObjectValue(data, column, Double.class, null);
	}

	@Override
	public Double getDouble(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Double.class, null);
	}

	@Override
	public Date getDate(String headerName) {
		return metaData.getObjectValue(data, headerName, Date.class, null);
	}

	@Override
	public Date getDate(Enum<?> column) {
		return metaData.getObjectValue(data, column, Date.class, null);
	}

	@Override
	public Date getDate(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Date.class, null);
	}

	@Override
	public Calendar getCalendar(String headerName) {
		return metaData.getObjectValue(data, headerName, Calendar.class, null);
	}

	@Override
	public Calendar getCalendar(Enum<?> column) {
		return metaData.getObjectValue(data, column, Calendar.class, null);
	}

	@Override
	public Calendar getCalendar(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, Calendar.class, null);
	}

	public String toString() {
		if (data == null) {
			return "null";
		}
		if (data.length == 0) {
			return "[]";
		}
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			if (out.length() != 0) {
				out.append(',').append(' ');
			}
			out.append(data[i]);
		}

		return out.toString();
	}

	@Override
	public boolean equals(Object o) {
		return o == this;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(data);
	}
}
