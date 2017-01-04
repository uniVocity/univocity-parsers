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

import com.univocity.parsers.common.CommonParserSettings;
import com.univocity.parsers.common.CommonSettings;
import com.univocity.parsers.common.fields.*;
import com.univocity.parsers.conversions.*;


/**
 * Metadata with information about {@link Record}s parsed from the input.
 */
public interface RecordMetaData {

	/**
	 * Returns the index of a given column
	 *
	 * @param column the column whose index will be returned
	 *
	 * @return index of the given column
	 */
	int indexOf(Enum<?> column);

	/**
	 * Returns the index of a given column
	 *
	 * @param headerName name of the column whose index will be returned
	 *
	 * @return index of the given column
	 */
	int indexOf(String headerName);

	/**
	 * Returns the type associated with a given column, defined with the method {@code setTypeOfColumns(type, columns)}
	 *
	 * @param column the column whose type will be returned
	 *
	 * @return the type of the given column
	 */
	Class<?> typeOf(Enum<?> column);

	/**
	 * Returns the type associated with a given column name, defined with the method {@code setTypeOfColumns(type, columns)}
	 *
	 * @param headerName name of the column whose type will be returned
	 *
	 * @return the type of the given column
	 */
	Class<?> typeOf(String headerName);

	/**
	 * Returns the type associated with a given column, defined with the method {@code setTypeOfColumns(type, columns)}
	 *
	 * @param columnIndex the position of the column whose type will be returned
	 *
	 * @return the type of the given column
	 */
	Class<?> typeOf(int columnIndex);

	/**
	 * Associates a type with one or more column. This allows the parsed data to be converted automatically
	 * to the given type when reading data from a {@link Record}, e.g. {@link Record#toFieldObjectMap(String...)} will
	 * convert the selected field values to their respective types, and then set the result as the values in the map.
	 *
	 * @param type    the type to associate with a list of column
	 * @param columns the columns that will be associated with the given type.
	 */
	@SuppressWarnings("rawtypes")
	void setTypeOfColumns(Class<?> type, Enum... columns);

	/**
	 * Associates a type with one or more column. This allows the parsed data to be converted automatically
	 * to the given type when reading data from a {@link Record}, e.g. {@link Record#toFieldObjectMap(String...)} will
	 * convert the selected field values to their respective types, and then set the result as the values in the map.
	 *
	 * @param type        the type to associate with a list of column
	 * @param headerNames the columns that will be associated with the given type.
	 */
	void setTypeOfColumns(Class<?> type, String... headerNames);

	/**
	 * Associates a type with one or more column. This allows the parsed data to be converted automatically
	 * to the given type when reading data from a {@link Record}, e.g. {@link Record#toFieldObjectMap(String...)} will
	 * convert the selected field values to their respective types, and then set the result as the values in the map.
	 *
	 * @param type          the type to associate with a list of column
	 * @param columnIndexes the columns that will be associated with the given type.
	 */
	void setTypeOfColumns(Class<?> type, int... columnIndexes);


	/**
	 * Associates a default value with one or more columns, in case the values contained are {@code null}
	 *
	 * @param defaultValue the value to be used for the given column when the parsed result is {@code null}
	 * @param columns      the columns to be associated with a default value.
	 * @param <T>          type of the default value.
	 */
	<T> void setDefaultValueOfColumns(T defaultValue, Enum<?>... columns);

	/**
	 * Associates a default value with one or more columns, in case the values contained are {@code null}
	 *
	 * @param defaultValue the value to be used for the given column when the parsed result is {@code null}
	 * @param headerNames  the column names to be associated with a default value.
	 * @param <T>          type of the default value.
	 */
	<T> void setDefaultValueOfColumns(T defaultValue, String... headerNames);

	/**
	 * Associates a default value with one or more columns, in case the values contained are {@code null}
	 *
	 * @param defaultValue  the value to be used for the given column when the parsed result is {@code null}
	 * @param columnIndexes the column indexes to be associated with a default value.
	 * @param <T>           type of the default value.
	 */
	<T> void setDefaultValueOfColumns(T defaultValue, int... columnIndexes);

	/**
	 * Returns the default value associated with a column (defined using {@code setDefaultValueOf(Column, Object)})
	 *
	 * @param column the column whose default value will be returned
	 *
	 * @return the default value associated with the given column or {@code null}.
	 */
	Object defaultValueOf(Enum<?> column);

	/**
	 * Returns the default value associated with a column (defined using {@code setDefaultValueOf(Column, Object)})
	 *
	 * @param headerName the column name whose default value will be returned
	 *
	 * @return the default value associated with the given column or {@code null}.
	 */
	Object defaultValueOf(String headerName);

	/**
	 * Returns the default value associated with a column (defined using {@code setDefaultValueOf(Column, Object)})
	 *
	 * @param columnIndex the column index whose default value will be returned
	 *
	 * @return the default value associated with the given column or {@code null}.
	 */
	Object defaultValueOf(int columnIndex);


	/**
	 * Associates a sequence of {@link Conversion}s to fields of a given set of fields
	 *
	 * @param enumType    the type of the enumeration whose values represent headers in the input {@code Record}s
	 * @param conversions the sequence of conversions to apply
	 * @param <T>         the enumeration type
	 *
	 * @return (modifiable) set of fields to be selected and against which the given conversion sequence will be applied.
	 */
	@SuppressWarnings("rawtypes")
	<T extends Enum<T>> FieldSet<T> convertFields(Class<T> enumType, Conversion... conversions);

	/**
	 * Associates a sequence of {@link Conversion}s to fields of a given set of field names
	 *
	 * @param conversions the sequence of conversions to apply
	 *
	 * @return (modifiable) set of fields names to be selected and against which the given conversion sequence will be applied.
	 */
	@SuppressWarnings("rawtypes")
	FieldSet<String> convertFields(Conversion... conversions);

	/**
	 * Associates a sequence of {@link Conversion}s to fields of a given set of column indexes
	 *
	 * @param conversions the sequence of conversions to apply
	 *
	 * @return (modifiable) set of column indexes to be selected and against which the given conversion sequence will be applied.
	 */
	@SuppressWarnings("rawtypes")
	FieldSet<Integer> convertIndexes(Conversion... conversions);

	/**
	 * Returns the column names of the {@link Record}s parsed from the input.
	 *
	 * <p> If the headers are extracted from the input (i.e. {@link CommonParserSettings#isHeaderExtractionEnabled()} == true), then these values will be returned.
	 * <p> If no headers are extracted from the input, then the configured headers in {@link CommonSettings#getHeaders()} will be returned.
	 *
	 * @return the headers associated with the {@link Record}s parsed from the input
	 */
	String[] headers();

	/**
	 * Returns the sequence of headers that have been selected. If no selection has been made, all available headers
	 * will be returned, producing the same output as a call to method {@link #headers()}.
	 *
	 * @return the sequence of selected headers, or all headers if no selection has been made.
	 */
	String[] selectedHeaders();

	/**
	 * Queries whether a given header name exists in the  {@link Record}s parsed from the input
	 *
	 * @param headerName name of the header
	 *
	 * @return {@code true} if the given header name exists in the input records, otherwise {@code false}
	 */
	boolean containsColumn(String headerName);

}
