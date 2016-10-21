/*******************************************************************************
 * Copyright 2014 uniVocity Software Pty Ltd
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
package com.univocity.parsers.common.processor.core;

import com.univocity.parsers.common.*;

import java.util.*;

/**
 * A common interface for {@link Processor}s that collect the values parsed from each column in a row.
 * Namely: {@link AbstractColumnProcessor}, {@link AbstractObjectColumnProcessor}, {@link AbstractBatchedColumnProcessor} and {@link AbstractBatchedObjectColumnProcessor}.
 *
 * @see AbstractColumnProcessor
 * @see AbstractObjectColumnProcessor
 * @see AbstractBatchedColumnProcessor
 * @see AbstractBatchedObjectColumnProcessor
 * @see Processor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> the type of the data stored by the columns.
 */
interface ColumnReader<T> {

	/**
	 * Returns the column headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in
	 * the input when {@link CommonSettings#getHeaders()}  equals to {@code true}
	 * @return the headers of all column parsed.
	 */
	String[] getHeaders();

	/**
	 * Returns the values processed for each column
	 * @return a list of lists. The stored lists correspond to the position of the column processed from the input; Each list
	 * contains the corresponding values parsed for a column, across multiple rows.
	 */
	List<List<T>> getColumnValuesAsList();

	/**
	 * Fills a given map associating each column name to its list o values
	 * @param map the map to hold the values of each column
	 * @throws IllegalArgumentException if a column does not have a name associated to it. In this case, use {@link #putColumnValuesInMapOfIndexes(Map)} instead.
	 */
	void putColumnValuesInMapOfNames(Map<String, List<T>> map);

	/**
	 * Fills a given map associating each column index to its list of values
	 * @param map the map to hold the values of each column
	 */
	void putColumnValuesInMapOfIndexes(Map<Integer, List<T>> map);

	/**
	 * Returns a map of column names and their respective list of values parsed from the input.
	 * @return a map of column names and their respective list of values.
	 */
	Map<String, List<T>> getColumnValuesAsMapOfNames();

	/**
	 * Returns a map of column indexes and their respective list of values parsed from the input.
	 * @return a map of column indexes and their respective list of values.
	 */
	Map<Integer, List<T>> getColumnValuesAsMapOfIndexes();

	/**
	 * Returns the values of a given column.
	 * @param columnName the name of the column in the input.
	 * @return a list with all data  stored in the given column
	 */
	List<T> getColumn(String columnName);

	/**
	 * Returns the values of a given column.
	 * @param columnIndex the position of the column in the input (0-based).
	 * @return a list with all data  stored in the given column
	 */
	List<T> getColumn(int columnIndex);

}
