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

import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;

public interface RecordMetaData {

	int indexOf(Enum<?> column);

	int indexOf(String headerName);

	Class<?> typeOf(Enum<?> column);

	Class<?> typeOf(String headerName);

	Class<?> typeOf(int columnIndex);

	@SuppressWarnings("rawtypes")
	void setTypeOfColumns(Class<?> type, Enum... columns);

	void setTypeOfColumns(Class<?> type, String... headerNames);

	void setTypeOfColumns(Class<?> type, int... columnIndexes);

	<T> void setDefaultValueOf(Enum<?> column, T defaultValue);

	<T> void setDefaultValueOf(String headerName, T defaultValue);

	<T> void setDefaultValueOf(int columnIndex, T defaultValue);

	Object defaultValueOf(Enum<?> column);

	Object defaultValueOf(String headerName);

	Object defaultValueOf(int columnIndex);

	@SuppressWarnings("rawtypes")
	<T extends Enum<T>> FieldSet<T> convertFields(Class<T> enumType, Conversion... conversions);

	@SuppressWarnings("rawtypes")
	FieldSet<String> convertFields(Conversion... conversions);

	@SuppressWarnings("rawtypes")
	FieldSet<Integer> convertIndexes(Conversion... conversions);

	String[] headers();

	boolean containsColumn(String headerName);

}
