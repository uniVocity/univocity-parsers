/*******************************************************************************
 * Copyright 2015 uniVocity Software Pty Ltd
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
 ******************************************************************************/

package com.univocity.parsers.common.record;

import com.univocity.parsers.conversions.*;

import java.math.*;
import java.util.*;

public interface Record {

	public RecordMetaData getMetaData();

	public String[] getValues();

	public <T> T getValue(String headerName, Class<T> expectedType);

	public <T> T getValue(Enum<?> column, Class<T> expectedType);

	public <T> T getValue(int columnIndex, Class<T> expectedType);

	public <T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions);

	public <T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions);

	public <T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions);

	public <T> T getValue(String headerName, T defaultValue);

	public <T> T getValue(Enum<?> column, T defaultValue);

	public <T> T getValue(int columnIndex, T defaultValue);

	public <T> T getValue(String headerName, T defaultValue, Conversion... conversions);

	public <T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions);

	public <T> T getValue(int columnIndex, T defaultValue, Conversion... conversions);

	public String getString(String headerName);

	public String getString(Enum<?> column);

	public String getString(int columnIndex);

	public byte getByte(String headerName, String format, String formatOptions);

	public byte getByte(Enum<?> column, String format, String formatOptions);

	public byte getByte(int columnIndex, String format, String formatOptions);

	public short getShort(String headerName, String format, String formatOptions);

	public short getShort(Enum<?> column, String format, String formatOptions);

	public short getShort(int columnIndex, String format, String formatOptions);

	public int getInt(String headerName, String format, String formatOptions);

	public int getInt(Enum<?> column, String format, String formatOptions);

	public int getInt(int columnIndex, String format, String formatOptions);

	public long getLong(String headerName, String format, String formatOptions);

	public long getLong(Enum<?> column, String format, String formatOptions);

	public long getLong(int columnIndex, String format, String formatOptions);

	public float getFloat(String headerName, String format, String formatOptions);

	public float getFloat(Enum<?> column, String format, String formatOptions);

	public float getFloat(int columnIndex, String format, String formatOptions);

	public double getDouble(String headerName, String format, String formatOptions);

	public double getDouble(Enum<?> column, String format, String formatOptions);

	public double getDouble(int columnIndex, String format, String formatOptions);

	public char getChar(String headerName);

	public char getChar(Enum<?> column);

	public char getChar(int columnIndex);

	public boolean getBoolean(String headerName);

	public boolean getBoolean(Enum<?> column);

	public boolean getBoolean(int columnIndex);

	public boolean getBoolean(String headerName, String trueString, String falseString);

	public boolean getBoolean(Enum<?> column, String trueString, String falseString);

	public boolean getBoolean(int columnIndex, String trueString, String falseString);

	public BigInteger getBigInteger(String headerName, String format, String formatOptions);

	public BigInteger getBigInteger(Enum<?> column, String format, String formatOptions);

	public BigInteger getBigInteger(int columnIndex, String format, String formatOptions);

	public BigDecimal getBigDecimal(String headerName, String format, String formatOptions);

	public BigDecimal getBigDecimal(Enum<?> column, String format, String formatOptions);

	public BigDecimal getBigDecimal(int columnIndex, String format, String formatOptions);

	public Date getDate(String headerName, String format, String formatOptions);

	public Date getDate(Enum<?> column, String format, String formatOptions);

	public Date getDate(int columnIndex, String format, String formatOptions);

	public Calendar getCalendar(String headerName, String format, String formatOptions);

	public Calendar getCalendar(Enum<?> column, String format, String formatOptions);

	public Calendar getCalendar(int columnIndex, String format, String formatOptions);

	public Map<String, String> toFieldMap(String... selectedFields);

	public Map<Integer, String> toIndexMap(int... selectedIndex);

	public <T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T selectedColumns);

	public Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields);

	public Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes);

	public <T extends Enum<T>> Map<T, String> fillEnumMap(Map<T, String> map, T... selectedColumns);

	public Map<String, Object> toFieldObjectMap(String... selectedFields);

	public Map<Integer, Object> toIndexObjectMap(int... selectedIndex);

	public <T extends Enum<T>> Map<T, Object> toEnumObjectMap(Class<T> enumType, T... selectedColumns);

	public Map<String, Object> fillFieldObjectMap(Map<String, Object> map, String... selectedFields);

	public Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes);

	public <T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Map<T, Object> map, T... selectedColumns);
}
