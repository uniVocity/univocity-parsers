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

import com.univocity.parsers.common.AbstractParser;
import com.univocity.parsers.conversions.*;

import java.math.*;
import java.util.*;

/**
 * A record parsed from the input, with convenience methods for easier data manipulation.
 * Records are obtained from {@link com.univocity.parsers.common.AbstractParser} methods such as
 * <ul>
 * <li>{@link AbstractParser#parseAllRecords(java.io.Reader)}</li>
 * <li>{@link AbstractParser#parseNextRecord()} </li>
 * <li>{@link AbstractParser#parseRecord(String)}</li>
 * </ul>
 */
public interface Record {

	/**
	 * Returns the {@link RecordMetaData} associated with all records parsed from the input.
	 * The metadata allows associating types, conversions and default values to any column, which
	 * will be used when performing operations that can convert plain input strings into object instances.
	 * Methods such as {@link #toFieldMap(String...)}, {@link #fillFieldMap(Map, String...)} and any other
	 * method that returns Objects will use the metadata information to perform data conversions.
	 *
	 * @return the metadata object that provides information and basic data conversion controls
	 * over all records parsed from the input.
	 */
	RecordMetaData getMetaData();

	/**
	 * Returns the plain values obtained from a record parsed from the input.
	 *
	 * @return a {@code String} array with all values parsed from the input for this record.
	 */
	String[] getValues();

	/**
	 * Returns the value contained in the given column.
	 *
	 * @param headerName   the name of the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	<T> T getValue(String headerName, Class<T> expectedType);

	/**
	 * Returns the value contained in the given column.
	 *
	 * @param column       the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	<T> T getValue(Enum<?> column, Class<T> expectedType);

	/**
	 * Returns the value contained in the given column.
	 *
	 * @param columnIndex  the position of the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	<T> T getValue(int columnIndex, Class<T> expectedType);

	/**
	 * Returns the value contained in the given column, after applying a sequence of conversion over it.
	 *
	 * @param headerName   the name of the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(String headerName, Class<T> expectedType, Conversion... conversions);

	/**
	 * Returns the value contained in the given column, after applying a sequence of conversion over it.
	 *
	 * @param column       the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(Enum<?> column, Class<T> expectedType, Conversion... conversions);

	/**
	 * Returns the value contained in the given column, after applying a sequence of conversion over it.
	 *
	 * @param columnIndex  the index of the column whose value will be returned
	 * @param expectedType the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(int columnIndex, Class<T> expectedType, Conversion... conversions);


	/**
	 * Returns the value contained in the given column, or a default value if the column contains {@code null}
	 *
	 * @param headerName   the name of the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}. Its type will be used to derive
	 *                     the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column, or the default value in case the columns is {@code null}
	 */
	<T> T getValue(String headerName, T defaultValue);

	/**
	 * Returns the value contained in the given column, or a default value if the column contains {@code null}
	 *
	 * @param column       the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}. Its type will be used to derive
	 *                     the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column, or the default value in case the columns is {@code null}
	 */
	<T> T getValue(Enum<?> column, T defaultValue);

	/**
	 * Returns the value contained in the given column, or a default value if the column contains {@code null}
	 *
	 * @param columnIndex  index of the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}. Its type will be used to derive
	 *                     the expected type of the value. A conversion will be executed against the value
	 *                     to produce a result with the expected type.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column, or the default value in case the column is {@code null}
	 */
	<T> T getValue(int columnIndex, T defaultValue);

	/**
	 * Returns the value contained in a given column, after applying a sequence of conversions over it.
	 *
	 * @param headerName   the name of the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}.
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(String headerName, T defaultValue, Conversion... conversions);

	/**
	 * Returns the value contained in a given column, after applying a sequence of conversions over it.
	 *
	 * @param column       the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}.
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(Enum<?> column, T defaultValue, Conversion... conversions);

	/**
	 * Returns the value contained in a given column, after applying a sequence of conversions over it.
	 *
	 * @param columnIndex  the index of the column whose value will be returned
	 * @param defaultValue The default value to use if the column contains {@code null}.
	 * @param conversions  the sequence of {@link Conversion}s to apply over the column value.
	 * @param <T>          the expected value type
	 *
	 * @return the value this record holds at the given column
	 */
	@SuppressWarnings("rawtypes")
	<T> T getValue(int columnIndex, T defaultValue, Conversion... conversions);

	/**
	 * Returns the {@code String} value in the given column
	 *
	 * @param headerName the name of the column
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(String headerName);

	/**
	 * Returns the {@code String} value in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 *
	 * @param column the column
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(Enum<?> column);

	/**
	 * Returns the {@code String} value in the given column, truncating it to a given maximum length
	 *
	 * @param columnIndex the index of the column
	 * @param maxLength   the maximum number of characters to be returned.
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(int columnIndex, int maxLength);

	/**
	 * Returns the {@code String} value in the given column, truncating it to a given maximum length
	 *
	 * @param headerName the name of the column
	 * @param maxLength  the maximum number of characters to be returned.
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(String headerName, int maxLength);

	/**
	 * Returns the {@code String} value in the given column, truncating it to a given maximum length
	 *
	 * @param column    the column
	 * @param maxLength the maximum number of characters to be returned.
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(Enum<?> column, int maxLength);

	/**
	 * Returns the {@code String} value in the given column
	 *
	 * @param columnIndex the index of the column
	 *
	 * @return the value stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	String getString(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Float} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Float} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Float} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Float} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the name of the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Byte} and returns the result.
	 * The {@link ByteConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Byte} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Byte getByte(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Short} and returns the result.
	 * The {@link ShortConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Short} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Short getShort(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Integer} and returns the result.
	 * The {@link IntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Integer} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Integer getInt(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Long} and returns the result.
	 * The {@link LongConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Long} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Long getLong(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Float} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Float} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Float} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Float} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Float} and returns the result.
	 * The {@link FloatConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Float} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Float getFloat(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Double} and returns the result.
	 * The {@link DoubleConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code Double} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Double getDouble(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Character} and returns the result.
	 * The {@link CharacterConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Character} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Character getChar(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Character} and returns the result.
	 * The {@link CharacterConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Character} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Character getChar(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Character} and returns the result.
	 * The {@link CharacterConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the column index
	 *
	 * @return the {@code Character} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Character getChar(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Boolean} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Boolean} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the column index
	 *
	 * @return the {@code Boolean} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName  the column name
	 * @param trueString  a {@code String} that represents the {@code Boolean} value {@code true}
	 * @param falseString a {@code String} that represents the {@code Boolean} value {@code false}
	 *
	 * @return the {@code Boolean} stored in the given column if its original {@code String} value matches
	 * either the trueString or falseString, otherwise {@code null} or the default specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(String headerName, String trueString, String falseString);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param column      the column
	 * @param trueString  a {@code String} that represents the {@code Boolean} value {@code true}
	 * @param falseString a {@code String} that represents the {@code Boolean} value {@code false}
	 *
	 * @return the {@code Boolean} stored in the given column if its original {@code String} value matches
	 * either the trueString or falseString, otherwise {@code null} or the default specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(Enum<?> column, String trueString, String falseString);

	/**
	 * Converts the {@code String} value in the given column to a {@code Boolean} and returns the result.
	 * The {@link BooleanConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the column index
	 * @param trueString  a {@code String} that represents the {@code Boolean} value {@code true}
	 * @param falseString a {@code String} that represents the {@code Boolean} value {@code false}
	 *
	 * @return the {@code Boolean} stored in the given column if its original {@code String} value matches
	 * either the trueString or falseString, otherwise {@code null} or the default specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Boolean getBoolean(int columnIndex, String trueString, String falseString);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the column name
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the column name
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"decimalSeparator=,", "maximumFractionDigits=3"}
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigInteger} and returns the result.
	 * The {@link BigIntegerConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code BigInteger} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigInteger getBigInteger(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code BigDecimal} and returns the result.
	 * The {@link BigDecimalConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex the columnIndex
	 *
	 * @return the {@code BigDecimal} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	BigDecimal getBigDecimal(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the column name
	 * @param format        the numeric mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the date mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the date mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion will be used perform the transformation.
	 *
	 * @param headerName    the column name
	 * @param format        the date mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(String headerName, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion will be used perform the transformation.
	 *
	 * @param column        the column
	 * @param format        the date mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(Enum<?> column, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion will be used perform the transformation.
	 *
	 * @param columnIndex   the index of column
	 * @param format        the date mask to apply over the parsed content
	 * @param formatOptions a sequence of key-value pairs with options to configure the underlying formatter.
	 *                      Each element must be specified as {@code property_name=property_value},
	 *                      e.g. options={"lenient=true"}
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(int columnIndex, String format, String... formatOptions);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Date} and returns the result.
	 * The {@link DateConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param columnIndex the column index
	 *
	 * @return the {@code Date} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Date getDate(int columnIndex);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param headerName the column name
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(String headerName);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param column the column
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(Enum<?> column);

	/**
	 * Converts the {@code String} value in the given column to a {@code Calendar} and returns the result.
	 * The {@link CalendarConversion} conversion sequence registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} will be used perform the transformation.
	 *
	 * @param columnIndex the column index
	 *
	 * @return the {@code Calendar} stored in the given column, {@code null} or the
	 * default value specified in {@link RecordMetaData#defaultValueOf(String)}
	 */
	Calendar getCalendar(int columnIndex);

	/**
	 * Converts the record into a map of {@code String} values.
	 *
	 * @param selectedFields the header names to use as keys of the map. If no selection then all headers will be used.
	 *
	 * @return a map containing the selected (or all) header names as the keys, and their respective values.
	 */
	Map<String, String> toFieldMap(String... selectedFields);

	/**
	 * Converts the record into a map of {@code String} values.
	 *
	 * @param selectedIndexes the column indexes to use as keys of the map. If no selection then all indexes will be used.
	 *
	 * @return a map containing the selected (or all) column indexes as the keys, and their respective values.
	 */
	Map<Integer, String> toIndexMap(int... selectedIndexes);

	/**
	 * Converts the record into a map of {@code String} values.
	 *
	 * @param enumType        the enumeration type.
	 * @param selectedColumns the columns to use as keys of the map. If no selection then all values of the enumeration type will be used.
	 * @param <T>             the enumeration type
	 *
	 * @return a map containing the selected (or all) columns as the keys, and their respective values.
	 */
	<T extends Enum<T>> Map<T, String> toEnumMap(Class<T> enumType, T... selectedColumns);

	/**
	 * Fills a map with the {@code String} values of this record.
	 *
	 * @param map            the map that will receive the values
	 * @param selectedFields the header names to use as keys of the map
	 *
	 * @return the input map, containing the selected header names as the keys, and their respective values.
	 */
	Map<String, String> fillFieldMap(Map<String, String> map, String... selectedFields);

	/**
	 * Fills a map with the {@code String} values of this record.
	 *
	 * @param map             the map that will receive the values
	 * @param selectedIndexes the column indexes to use as keys of the map
	 *
	 * @return the input map, containing the selected column indexes as the keys, and their respective values.
	 */
	Map<Integer, String> fillIndexMap(Map<Integer, String> map, int... selectedIndexes);

	/**
	 * Fills a map with the {@code String} values of this record.
	 *
	 * @param map             the map that will receive the values
	 * @param selectedColumns the column to use as keys of the map
	 * @param <T>             the enumeration type
	 *
	 * @return the input map, containing the selected header names as the keys, and their respective values.
	 */
	<T extends Enum<T>> Map<T, String> fillEnumMap(Map<T, String> map, T... selectedColumns);

	/**
	 * Converts the record into a map of {@code Object} values. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param selectedFields the column names to use as keys of the map
	 *
	 * @return a map containing the selected column names as the keys, and their respective values.
	 */
	Map<String, Object> toFieldObjectMap(String... selectedFields);

	/**
	 * Converts the record into a map of {@code Object} values. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param selectedIndexes the column indexes to use as keys of the map
	 *
	 * @return a map containing the selected column indexes as the keys, and their respective values.
	 */
	Map<Integer, Object> toIndexObjectMap(int... selectedIndexes);

	/**
	 * Converts the record into a map of {@code Object} values. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param enumType        the enumeration type.
	 * @param selectedColumns the column to use as keys of the map
	 * @param <T>             the enumeration type
	 *
	 * @return a map containing the selected columns as the keys, and their respective values.
	 */
	<T extends Enum<T>> Map<T, Object> toEnumObjectMap(Class<T> enumType, T... selectedColumns);

	/**
	 * Fills a map with the converted {@code Object} values of this record. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param map            the map that will receive the values
	 * @param selectedFields the column names to use as keys of the map
	 *
	 * @return the input map, containing the selected columns as the keys, and their respective values.
	 */
	Map<String, Object> fillFieldObjectMap(Map<String, Object> map, String... selectedFields);

	/**
	 * Fills a map with the converted {@code Object} values of this record. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param map             the map that will receive the values
	 * @param selectedIndexes the column indexes to use as keys of the map
	 *
	 * @return the input map, containing the selected columns as the keys, and their respective values.
	 */
	Map<Integer, Object> fillIndexObjectMap(Map<Integer, Object> map, int... selectedIndexes);

	/**
	 * Fills a map with the converted {@code Object} values of this record. Conversions must be registered using
	 * {@link RecordMetaData#convertFields(Conversion[])} or {@link RecordMetaData#convertIndexes(Conversion[])} (Conversion[])}.
	 * Columns without a known conversion will have their values put into the map as plain {@code String}s.
	 *
	 * @param map             the map that will receive the values
	 * @param selectedColumns the column to use as keys of the map
	 * @param <T>             the enumeration type
	 *
	 * @return the input map, containing the selected columns as the keys, and their respective values.
	 */
	<T extends Enum<T>> Map<T, Object> fillEnumObjectMap(Map<T, Object> map, T... selectedColumns);
}
