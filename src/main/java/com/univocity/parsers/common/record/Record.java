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

	public RecordMetaData getMetaData();

	public String[] getValues();

	public <T> T getValue(String headerName, Class<T> expectedType);

	public <T> T getValue(Enum<?> column, Class<T> expectedType);

	public <T> T getValue(int columnIndex, Class<T> expectedType);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions);

	public <T> T getValue(String headerName, T defaultValue);

	public <T> T getValue(Enum<?> column, T defaultValue);

	public <T> T getValue(int columnIndex, T defaultValue);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(String headerName, T defaultValue, Conversion... conversions);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions);

	@SuppressWarnings("rawtypes") 
	public <T> T getValue(int columnIndex, T defaultValue, Conversion... conversions);

	public String getString(String headerName);

	public String getString(Enum<?> column);

	public String getString(int columnIndex);

	public Byte getByte(String headerName, String format, String ...formatOptions);

	public Byte getByte(Enum<?> column, String format, String... formatOptions);

	public Byte getByte(int columnIndex, String format, String... formatOptions);

	public Short getShort(String headerName, String format, String ...formatOptions);

	public Short getShort(Enum<?> column, String format, String ...formatOptions);

	public Short getShort(int columnIndex, String format, String... formatOptions);

	public Integer getInt(String headerName, String format, String... formatOptions);

	public Integer getInt(Enum<?> column, String format, String... formatOptions);

	public Integer getInt(int columnIndex, String format, String... formatOptions);

	public Long getLong(String headerName, String format, String ...formatOptions);

	public Long getLong(Enum<?> column, String format, String... formatOptions);

	public Long getLong(int columnIndex, String format, String... formatOptions);

	public Float getFloat(String headerName, String format, String ...formatOptions);

	public Float getFloat(Enum<?> column, String format, String ...formatOptions);

	public Float getFloat(int columnIndex, String format, String... formatOptions);

	public Double getDouble(String headerName, String format, String... formatOptions);

	public Double getDouble(Enum<?> column, String format, String... formatOptions);

	public Double getDouble(int columnIndex, String format, String... formatOptions);

	public Byte getByte(String headerName);

	public Byte getByte(Enum<?> column);

	public Byte getByte(int columnIndex);

	public Short getShort(String headerName);

	public Short getShort(Enum<?> column);

	public Short getShort(int columnIndex);

	public Integer getInt(String headerName);

	public Integer getInt(Enum<?> column);

	public Integer getInt(int columnIndex);

	public Long getLong(String headerName);

	public Long getLong(Enum<?> column);

	public Long getLong(int columnIndex);

	public Float getFloat(String headerName);

	public Float getFloat(Enum<?> column);

	public Float getFloat(int columnIndex);

	public Double getDouble(String headerName);

	public Double getDouble(Enum<?> column);

	public Double getDouble(int columnIndex);
	
	public Character getChar(String headerName);

	public Character getChar(Enum<?> column);

	public Character getChar(int columnIndex);

	public Boolean getBoolean(String headerName);

	public Boolean getBoolean(Enum<?> column);

	public Boolean getBoolean(int columnIndex);

	public Boolean getBoolean(String headerName, String trueString, String falseString);

	public Boolean getBoolean(Enum<?> column, String trueString, String falseString);

	public Boolean getBoolean(int columnIndex, String trueString, String falseString);

	public BigInteger getBigInteger(String headerName, String format, String ... formatOptions);

	public BigInteger getBigInteger(Enum<?> column, String format, String ... formatOptions);

	public BigInteger getBigInteger(int columnIndex, String format, String... formatOptions);

	public BigDecimal getBigDecimal(String headerName, String format, String... formatOptions);

	public BigDecimal getBigDecimal(Enum<?> column, String format, String... formatOptions);

	public BigDecimal getBigDecimal(int columnIndex, String format, String... formatOptions);
	
	public BigInteger getBigInteger(String headerName);

	public BigInteger getBigInteger(Enum<?> column);

	public BigInteger getBigInteger(int columnIndex);

	public BigDecimal getBigDecimal(String headerName);

	public BigDecimal getBigDecimal(Enum<?> column);

	public BigDecimal getBigDecimal(int columnIndex);

	public Date getDate(String headerName, String format, String... formatOptions);

	public Date getDate(Enum<?> column, String format, String... formatOptions);

	public Date getDate(int columnIndex, String format, String... formatOptions);

	public Calendar getCalendar(String headerName, String format, String... formatOptions);

	public Calendar getCalendar(Enum<?> column, String format, String... formatOptions);

	public Calendar getCalendar(int columnIndex, String format, String... formatOptions);
	
	public Date getDate(String headerName);

	public Date getDate(Enum<?> column);

	public Date getDate(int columnIndex);

	public Calendar getCalendar(String headerName);

	public Calendar getCalendar(Enum<?> column);

	public Calendar getCalendar(int columnIndex);

	public Map<String, String> toFieldMap(String... selectedFields);

	public Map<Integer, String> toIndexMap(int... selectedIndex);

	public <T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T ... selectedColumns);

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
