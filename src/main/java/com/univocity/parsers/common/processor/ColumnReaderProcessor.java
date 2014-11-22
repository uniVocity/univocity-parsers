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
package com.univocity.parsers.common.processor;

import java.util.*;

import com.univocity.parsers.common.*;

/**
 * A common interface for {@link RowProcessor}s that collect the values parsed from each column in a row.
 * Namely: {@link ColumnProcessor}, {@link ObjectColumnProcessor}, {@link BatchedColumnProcessor} and {@link BatchedObjectColumnProcessor}.
 *
 * @see ColumnProcessor
 * @see ObjectColumnProcessor
 * @see BatchedColumnProcessor
 * @see BatchedObjectColumnProcessor
 * @see RowProcessor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> the type of the data stored by the columns.
 */
interface ColumnReaderProcessor<T> {

	/**
	 * Returns the column headers. This can be either the headers defined in {@link CommonSettings#getHeaders()} or the headers parsed in
	 * the input when {@link CommonSettings#getHeaders()}  equals to {@code true}
	 * @return the headers of all column parsed.
	 */
	public String[] getHeaders();

	/**
	 * Returns the values processed for each column
	 * @return a list of lists. The stored lists correspond to the position of the column processed from the input; Each list
	 * contains the corresponding values parsed for a column, across multiple rows.
	 */
	public List<List<T>> getColumnValuesAsList();

	/**
	 * Fills a given map associating each column name to its list o values
	 * @param map the map to hold the values of each column
	 * @throws IllegalArgumentException if a column does not have a name associated to it. In this case, use {@link #putColumnValuesInMapOfIndexes(Map)} instead.
	 */
	public void putColumnValuesInMapOfNames(Map<String, List<T>> map);

	/**
	 * Fills a given map associating each column index to its list of values
	 * @param map the map to hold the values of each column
	 */
	public void putColumnValuesInMapOfIndexes(Map<Integer, List<T>> map);

	/**
	 * Returns a map of column names and their respective list of values parsed from the input.
	 * @return a map of column names and their respective list of values.
	 */
	public Map<String, List<T>> getColumnValuesAsMapOfNames();

	/**
	 * Returns a map of column indexes and their respective list of values parsed from the input.
	 * @return a map of column indexes and their respective list of values.
	 */
	public Map<Integer, List<T>> getColumnValuesAsMapOfIndexes();

}
