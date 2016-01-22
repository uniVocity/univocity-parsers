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

package com.univocity.parsers.common.record;

import com.univocity.parsers.conversions.*;

import java.math.*;
import java.util.*;

public interface Record {

	RecordMetaData getMetaData();

	String[] getValues();

	<T> T getValue(String headerName, Class<T> expectedType);

	<T> T getValue(Enum<?> column, Class<T> expectedType);

	<T> T getValue(int columnIndex, Class<T> expectedType);

	@SuppressWarnings("rawtypes")
	<T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions);

	@SuppressWarnings("rawtypes")
	<T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions);

	@SuppressWarnings("rawtypes")
	<T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions);

	<T> T getValue(String headerName, T defaultValue);

	<T> T getValue(Enum<?> column, T defaultValue);

	<T> T getValue(int columnIndex, T defaultValue);

	@SuppressWarnings("rawtypes")
	<T> T getValue(String headerName, T defaultValue, Conversion... conversions);

	@SuppressWarnings("rawtypes")
	<T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions);

	@SuppressWarnings("rawtypes")
	<T> T getValue(int columnIndex, T defaultValue, Conversion... conversions);

	String getString(String headerName);

	String getString(Enum<?> column);

	String getString(int columnIndex);

	Byte getByte(String headerName, String format, String... formatOptions);

	Byte getByte(Enum<?> column, String format, String... formatOptions);

	Byte getByte(int columnIndex, String format, String... formatOptions);

	Short getShort(String headerName, String format, String... formatOptions);

	Short getShort(Enum<?> column, String format, String... formatOptions);

	Short getShort(int columnIndex, String format, String... formatOptions);

	Integer getInt(String headerName, String format, String... formatOptions);

	Integer getInt(Enum<?> column, String format, String... formatOptions);

	Integer getInt(int columnIndex, String format, String... formatOptions);

	Long getLong(String headerName, String format, String... formatOptions);

	Long getLong(Enum<?> column, String format, String... formatOptions);

	Long getLong(int columnIndex, String format, String... formatOptions);

	Float getFloat(String headerName, String format, String... formatOptions);

	Float getFloat(Enum<?> column, String format, String... formatOptions);

	Float getFloat(int columnIndex, String format, String... formatOptions);

	Double getDouble(String headerName, String format, String... formatOptions);

	Double getDouble(Enum<?> column, String format, String... formatOptions);

	Double getDouble(int columnIndex, String format, String... formatOptions);

	Byte getByte(String headerName);

	Byte getByte(Enum<?> column);

	Byte getByte(int columnIndex);

	Short getShort(String headerName);

	Short getShort(Enum<?> column);

	Short getShort(int columnIndex);

	Integer getInt(String headerName);

	Integer getInt(Enum<?> column);

	Integer getInt(int columnIndex);

	Long getLong(String headerName);

	Long getLong(Enum<?> column);

	Long getLong(int columnIndex);

	Float getFloat(String headerName);

	Float getFloat(Enum<?> column);

	Float getFloat(int columnIndex);

	Double getDouble(String headerName);

	Double getDouble(Enum<?> column);

	Double getDouble(int columnIndex);
	
	Character getChar(String headerName);

	Character getChar(Enum<?> column);

	Character getChar(int columnIndex);

	Boolean getBoolean(String headerName);

	Boolean getBoolean(Enum<?> column);

	Boolean getBoolean(int columnIndex);

	Boolean getBoolean(String headerName, String trueString, String falseString);

	Boolean getBoolean(Enum<?> column, String trueString, String falseString);

	Boolean getBoolean(int columnIndex, String trueString, String falseString);

	BigInteger getBigInteger(String headerName, String format, String... formatOptions);

	BigInteger getBigInteger(Enum<?> column, String format, String... formatOptions);

	BigInteger getBigInteger(int columnIndex, String format, String... formatOptions);

	BigDecimal getBigDecimal(String headerName, String format, String... formatOptions);

	BigDecimal getBigDecimal(Enum<?> column, String format, String... formatOptions);

	BigDecimal getBigDecimal(int columnIndex, String format, String... formatOptions);
	
	BigInteger getBigInteger(String headerName);

	BigInteger getBigInteger(Enum<?> column);

	BigInteger getBigInteger(int columnIndex);

	BigDecimal getBigDecimal(String headerName);

	BigDecimal getBigDecimal(Enum<?> column);

	BigDecimal getBigDecimal(int columnIndex);

	Date getDate(String headerName, String format, String... formatOptions);

	Date getDate(Enum<?> column, String format, String... formatOptions);

	Date getDate(int columnIndex, String format, String... formatOptions);

	Calendar getCalendar(String headerName, String format, String... formatOptions);

	Calendar getCalendar(Enum<?> column, String format, String... formatOptions);

	Calendar getCalendar(int columnIndex, String format, String... formatOptions);
	
	Date getDate(String headerName);

	Date getDate(Enum<?> column);

	Date getDate(int columnIndex);

	Calendar getCalendar(String headerName);

	Calendar getCalendar(Enum<?> column);

	Calendar getCalendar(int columnIndex);

	Map<String, String> toFieldMap(String... selectedFields);

	Map<Integer, String> toIndexMap(int... selectedIndex);

	<T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T... selectedColumns);

	Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields);

	Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes);

	<T extends Enum<T>> Map<T, String> fillEnumMap(Map<T, String> map, T... selectedColumns);

	Map<String, Object> toFieldObjectMap(String... selectedFields);

	Map<Integer, Object> toIndexObjectMap(int... selectedIndex);

	<T extends Enum<T>> Map<T, Object> toEnumObjectMap(Class<T> enumType, T... selectedColumns);

	Map<String, Object> fillFieldObjectMap(Map<String, Object> map, String... selectedFields);

	Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes);

	<T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Map<T, Object> map, T... selectedColumns);
}
