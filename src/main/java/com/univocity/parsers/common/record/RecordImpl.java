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
	private final RecordMetaData metaData;

	RecordImpl(String[] data, RecordMetaData metaData) {
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
		return null;
	}

	@Override
	public <T> T getValue(Enum column, Class<T> expectedType) {
		return null;
	}

	@Override
	public <T> T getValue(int columnIndex, Class<T> expectedType) {
		return null;
	}

	@Override
	public <T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions) {
		return null;
	}

	@Override
	public <T> T getValue(Enum column, Class<T> expectedType, Conversion... conversions) {
		return null;
	}

	@Override
	public <T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions) {
		return null;
	}

	@Override
	public <T> T getValue(String headerName, T defaultValue) {
		return null;
	}

	@Override
	public <T> T getValue(Enum column, T defaultValue) {
		return null;
	}

	@Override
	public <T> T getValue(int columnIndex, T defaultValue) {
		return null;
	}

	@Override
	public <T> T getValue(String headerName, T defaultValue, Conversion... conversions) {
		return null;
	}

	@Override
	public <T> T getValue(Enum column, T defaultValue, Conversion... conversions) {
		return null;
	}

	@Override
	public <T> T getValue(int columnIndex, T defaultValue, Conversion... conversions) {
		return null;
	}

	@Override
	public String getString(String headerName) {
		return null;
	}

	@Override
	public String getString(Enum column) {
		return null;
	}

	@Override
	public String getString(int columnIndex) {
		return null;
	}

	@Override
	public byte getByte(String headerName, String... formats) {
		return 0;
	}

	@Override
	public byte getByte(Enum column, String... formats) {
		return 0;
	}

	@Override
	public byte getByte(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public short getShort(String headerName, String... formats) {
		return 0;
	}

	@Override
	public short getShort(Enum column, String... formats) {
		return 0;
	}

	@Override
	public short getShort(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public int getInt(String headerName, String... formats) {
		return 0;
	}

	@Override
	public int getInt(Enum column, String... formats) {
		return 0;
	}

	@Override
	public int getInt(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public long getLong(String headerName, String... formats) {
		return 0;
	}

	@Override
	public long getLong(Enum column, String... formats) {
		return 0;
	}

	@Override
	public long getLong(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public float getFloat(String headerName, String... formats) {
		return 0;
	}

	@Override
	public float getFloat(Enum column, String... formats) {
		return 0;
	}

	@Override
	public float getFloat(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public double getDouble(String headerName, String... formats) {
		return 0;
	}

	@Override
	public double getDouble(Enum column, String... formats) {
		return 0;
	}

	@Override
	public double getDouble(int columnIndex, String... formats) {
		return 0;
	}

	@Override
	public char getChar(String headerName) {
		return 0;
	}

	@Override
	public char getChar(Enum column) {
		return 0;
	}

	@Override
	public char getChar(int columnIndex) {
		return 0;
	}

	@Override
	public boolean getBoolean(String headerName) {
		return false;
	}

	@Override
	public boolean getBoolean(Enum column) {
		return false;
	}

	@Override
	public boolean getBoolean(int columnIndex) {
		return false;
	}

	@Override
	public boolean getBoolean(String headerName, String trueString, String falseString) {
		return false;
	}

	@Override
	public boolean getBoolean(Enum column, String trueString, String falseString) {
		return false;
	}

	@Override
	public boolean getBoolean(int columnIndex, String trueString, String falseString) {
		return false;
	}

	@Override
	public BigInteger getBigInteger(String headerName, String... formats) {
		return null;
	}

	@Override
	public BigInteger getBigInteger(Enum column, String... formats) {
		return null;
	}

	@Override
	public BigInteger getBigInteger(int columnIndex, String... formats) {
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(String headerName, String... formats) {
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(Enum column, String... formats) {
		return null;
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, String... formats) {
		return null;
	}

	@Override
	public Date getDate(String headerName, String... formats) {
		return null;
	}

	@Override
	public Date getDate(Enum column, String... formats) {
		return null;
	}

	@Override
	public Date getDate(int columnIndex, String... formats) {
		return null;
	}

	@Override
	public Calendar getCalendar(String headerName, String... formats) {
		return null;
	}

	@Override
	public Calendar getCalendar(Enum column, String... formats) {
		return null;
	}

	@Override
	public Calendar getCalendar(int columnIndex, String... formats) {
		return null;
	}

	@Override
	public Map<String, String> toFieldMap(String... selectedFields) {
		return null;
	}

	@Override
	public Map<Integer, String> toIndexMap(int... selectedIndex) {
		return null;
	}

	@Override
	public <T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T selectedColumns) {
		return null;
	}

	@Override
	public Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields) {
		return null;
	}

	@Override
	public Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes) {
		return null;
	}

	@Override
	public <T extends Enum<T>> Map<T, String> fillEnumMap(Class<T> enumType, Map<T, String> map, T... selectedColumns) {
		return null;
	}

	@Override
	public Map<String, Object> toFieldObjectMap(String... selectedFields) {
		return null;
	}

	@Override
	public Map<Integer, Object> toIndexObjectMap(int... selectedIndex) {
		return null;
	}

	@Override
	public <T extends Enum<T>> Map<T, Object> toEnumObjectMap(Class<T> enumType, T selectedColumns) {
		return null;
	}

	@Override
	public Map<String, Object> fillFieldObjectMap(Map<String, Object> map, String... selectedFields) {
		return null;
	}

	@Override
	public Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes) {
		return null;
	}

	@Override
	public <T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Class<T> enumType, Map<T, Object> map, T... selectedColumns) {
		return null;
	}
}
