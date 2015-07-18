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

import com.univocity.parsers.conversions.*;

import java.math.*;
import java.util.*;

class RecordImpl implements Record {

	private final String[] data;
	private final RecordMetaDataImpl metaData;

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

	@Override
	public <T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, headerName, expectedType, conversions);
	}

	@Override
	public <T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, column, expectedType, conversions);
	}

	@Override
	public <T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions) {
		return metaData.getValue(data, columnIndex, expectedType, conversions);
	}

	@Override
	public <T> T getValue(String headerName, T defaultValue) {
		return metaData.getObjectValue(data, headerName, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@Override
	public <T> T getValue(Enum<?> column, T defaultValue) {
		return metaData.getObjectValue(data, column, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@Override
	public <T> T getValue(int columnIndex, T defaultValue) {
		return metaData.getObjectValue(data, columnIndex, (Class<T>) defaultValue.getClass(), defaultValue);
	}

	@Override
	public <T> T getValue(String headerName, T defaultValue, Conversion... conversions) {
		return metaData.getValue(data, headerName, defaultValue, conversions);
	}

	@Override
	public <T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions) {
		return metaData.getValue(data, column, defaultValue, conversions);
	}

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
	public byte getByte(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, byte.class, (byte) 0, format, formatOptions);
	}

	@Override
	public byte getByte(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, byte.class, (byte) 0, format, formatOptions);
	}

	@Override
	public byte getByte(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, byte.class, (byte) 0, format, formatOptions);
	}

	@Override
	public short getShort(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, short.class, (short) 0, format, formatOptions);
	}

	@Override
	public short getShort(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, short.class, (short) 0, format, formatOptions);
	}

	@Override
	public short getShort(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, short.class, (short) 0, format, formatOptions);
	}

	@Override
	public int getInt(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, int.class, 0, format, formatOptions);
	}

	@Override
	public int getInt(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, int.class, 0, format, formatOptions);
	}

	@Override
	public int getInt(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, int.class, 0, format, formatOptions);
	}

	@Override
	public long getLong(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, long.class, 0L, format, formatOptions);
	}

	@Override
	public long getLong(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, long.class, 0L, format, formatOptions);
	}

	@Override
	public long getLong(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, long.class, 0L, format, formatOptions);
	}

	@Override
	public float getFloat(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, float.class, 0.0f, format, formatOptions);
	}

	@Override
	public float getFloat(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, float.class, 0.0f, format, formatOptions);
	}

	@Override
	public float getFloat(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, float.class, 0.0f, format, formatOptions);
	}

	@Override
	public double getDouble(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, double.class, 0.0, format, formatOptions);
	}

	@Override
	public double getDouble(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, double.class, 0.0, format, formatOptions);
	}

	@Override
	public double getDouble(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, double.class, 0.0, format, formatOptions);
	}

	@Override
	public char getChar(String headerName) {
		return metaData.getObjectValue(data, headerName, char.class, '\0');
	}

	@Override
	public char getChar(Enum<?> column) {
		return metaData.getObjectValue(data, column, char.class, '\0');
	}

	@Override
	public char getChar(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, char.class, '\0');
	}

	@Override
	public boolean getBoolean(String headerName) {
		return metaData.getObjectValue(data, headerName, boolean.class, false, null, null);
	}

	@Override
	public boolean getBoolean(Enum<?> column) {
		return metaData.getObjectValue(data, column, boolean.class, false, null, null);
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		return metaData.getObjectValue(data, columnIndex, boolean.class, false, null, null);
	}

	@Override
	public boolean getBoolean(String headerName, String trueString, String falseString) {
		return metaData.getObjectValue(data, headerName, boolean.class, false, trueString, falseString);
	}

	@Override
	public boolean getBoolean(Enum<?> column, String trueString, String falseString) {
		return metaData.getObjectValue(data, column, boolean.class, false, trueString, falseString);
	}

	@Override
	public boolean getBoolean(int columnIndex, String trueString, String falseString) {
		return metaData.getObjectValue(data, columnIndex, boolean.class, false, trueString, falseString);
	}

	@Override
	public BigInteger getBigInteger(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigInteger getBigInteger(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigInteger getBigInteger(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, BigInteger.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, BigDecimal.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, Date.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, Date.class, null, format, formatOptions);
	}

	@Override
	public Date getDate(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Date.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(String headerName, String format, String formatOptions) {
		return metaData.getObjectValue(data, headerName, Calendar.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(Enum<?> column, String format, String formatOptions) {
		return metaData.getObjectValue(data, column, Calendar.class, null, format, formatOptions);
	}

	@Override
	public Calendar getCalendar(int columnIndex, String format, String formatOptions) {
		return metaData.getObjectValue(data, columnIndex, Calendar.class, null, format, formatOptions);
	}

	@Override
	public Map<String, String> toFieldMap(String... selectedFields) {
		return fillFieldMap(new HashMap<String, String>(selectedFields.length));
	}

	@Override
	public Map<Integer, String> toIndexMap(int... selectedIndex) {
		return fillIndexMap(new HashMap<Integer, String>(selectedIndex.length));
	}

	@Override
	public <T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T selectedColumns) {
		return fillEnumMap(new EnumMap<T, String>(enumType));
	}

	@Override
	public Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields) {
		for (int i = 0; i < selectedFields.length; i++) {
			map.put(selectedFields[i], getString(selectedFields[i]));
		}
		return map;
	}

	@Override
	public Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes) {
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
		for (int i = 0; i < selectedFields.length; i++) {
			map.put(selectedFields[i], metaData.getObjectValue(data, selectedFields[i], null, null));
		}
		return map;
	}

	@Override
	public Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes) {
		for (int i = 0; i < selectedIndexes.length; i++) {
			map.put(selectedIndexes[i], metaData.getObjectValue(data, selectedIndexes[i], null, null));
		}
		return map;
	}

	@Override
	public <T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Map<T, Object> map, T... selectedColumns) {
		for (int i = 0; i < selectedColumns.length; i++) {
			map.put(selectedColumns[i], metaData.getObjectValue(data, selectedColumns[i], null, null));
		}
		return map;
	}
}
