/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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
 * A utility class used split and store values columns parsed from each row in a {@link Processor}. Used to centralize common code used by implementations
 * of {@link ColumnReader}, namely:
 * {@link AbstractColumnProcessor}, {@link AbstractObjectColumnProcessor}, {@link AbstractBatchedColumnProcessor} and {@link AbstractBatchedObjectColumnProcessor}.
 *
 * @see ColumnReader
 * @see AbstractColumnProcessor
 * @see AbstractObjectColumnProcessor
 * @see AbstractBatchedColumnProcessor
 * @see AbstractBatchedObjectColumnProcessor
 * @see Processor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> the type of values stored in the columns.
 */
class ColumnSplitter<T> {

	private List<List<T>> columnValues;
	private String[] headers = null;
	private int expectedRowCount = 1000;
	private long rowCount;
	private long addNullsFrom;

	/**
	 * Creates a splitter allocating a space for a give number of expected rows to be read
	 * @param expectedRowCount the expected number of rows to be parsed
	 */
	ColumnSplitter(int expectedRowCount) {
		if (expectedRowCount <= 0) {
			throw new IllegalArgumentException("Expected row count must be positive");
		}
		this.expectedRowCount = expectedRowCount;
	}

	/**
	 * Removes any column values previously processed
	 */
	void clearValues() {
		addNullsFrom = rowCount;
		this.columnValues = null;
	}

	/**
	 * Prepares to execute a column splitting process from the beginning.
	 * Removes any column values previously processed, as well as information about headers in the input. Resets row count to 0.
	 */
	void reset() {
		this.columnValues = null;
		this.headers = null;
		addNullsFrom = 0L;
		rowCount = 0L;
	}

	/**
	 * Returns the values processed for each column
	 * @return a list of lists. The stored lists correspond to the position of the column processed from the input; Each list
	 * contains the corresponding values parsed for a column, across multiple rows.
	 */
	List<List<T>> getColumnValues() {
		return columnValues;
	}

	/**
	 * Returns the headers of the input. This can be either the headers defined in {@link CommonSettings#getHeaders()}
	 * or the headers parsed in the input when {@link CommonSettings#getHeaders()}  equals to {@code true}
	 * @return the headers of all records parsed.
	 */
	String[] getHeaders() {
		return headers;
	}

	/**
	 * Initializes the list of column values, the headers of each column and which columns to read if fields
	 * have been selected using {@link CommonSettings#selectFields(String...)} or {@link CommonSettings#selectIndexes(Integer...)}
	 * @param context the current active parsing context, which will be used to obtain information about headers and selected fields.
	 */
	private void initialize(Context context) {
		headers:
		if (this.headers == null) {
			String[] allHeaders = context.headers();
			if (allHeaders == null) {
				headers = ArgumentUtils.EMPTY_STRING_ARRAY;
				break headers;
			}
			if (!context.columnsReordered()) {
				this.headers = allHeaders;
				break headers;
			}
			int[] selectedIndexes = context.extractedFieldIndexes();

			final int last = Math.min(allHeaders.length, selectedIndexes.length);
			this.headers = new String[selectedIndexes.length];
			for (int i = 0; i < last; i++) {
				int idx = selectedIndexes[i];
				if(idx < allHeaders.length) {
					headers[i] = allHeaders[selectedIndexes[i]];
				}
			}
		}

		columnValues = new ArrayList<List<T>>(headers.length > 0 ? headers.length : 10);
	}

	/**
	 * Returns the header of a particular column
	 * @param columnIndex the index of the column whose header is to be obtained
	 * @return the name of the column at the given index, or null if there's no header defined for the given column index.
	 */
	String getHeader(int columnIndex) {
		if (columnIndex < headers.length) {
			return headers[columnIndex];
		}
		return null;
	}

	/**
	 * Splits the row and add stores the value of each column in its corresponding list in {@link #columnValues}
	 * @param row the row whose column values will be split
	 * @param context the current active parsing context.
	 */
	void addValuesToColumns(T[] row, Context context) {
		if (columnValues == null) {
			initialize(context);
		}

		if (columnValues.size() < row.length) {
			int columnsToAdd = row.length - columnValues.size();
			while (columnsToAdd-- > 0) {
				long records = context.currentRecord() - addNullsFrom;
				ArrayList<T> values = new ArrayList<T>(expectedRowCount < records ? (int) records : expectedRowCount);

				//adding nulls to the values of a new row with more columns than parsed before.
				//this ensures all columns will have the same number of values.
				while (--records > 0) {
					values.add(null);
				}

				columnValues.add(values);
			}
		}

		for (int i = 0; i < row.length; i++) {
			columnValues.get(i).add(row[i]);
		}

		//if we have more columns than what was parsed in the current row, we need to add nulls to the remaining columns.
		if (row.length < columnValues.size()) {
			for (int i = row.length; i < columnValues.size(); i++) {
				columnValues.get(i).add(null);
			}
		}
		rowCount++;
	}

	/**
	 * Fills a given map associating each column name to its list o values
	 * @param map the map to hold the values of each column
	 * @throws IllegalArgumentException if a column does not have a name associated to it. In this case, use {@link #putColumnValuesInMapOfIndexes(Map)} instead.
	 */
	void putColumnValuesInMapOfNames(Map<String, List<T>> map) {
		if (columnValues == null) {
			return;
		}
		for (int i = 0; i < columnValues.size(); i++) {
			String header = getHeader(i);
			if (header == null) {
				throw new DataProcessingException("Parsed input does not have header for column at index '" + i + "'. Parsed header names: " + Arrays.toString(getHeaders()), i);
			}
			map.put(header, columnValues.get(i));
		}
	}

	/**
	 * Returns the values of a given column.
	 * @param columnIndex the position of the column in the input (0-based).
	 * @param columnType the type of data in that column
	 * @param <V> the type of data in that column
	 * @return a list with all data  stored in the given column
	 */
	<V> List<V> getColumnValues(int columnIndex, Class<V> columnType){
		if(columnIndex < 0){
			throw new IllegalArgumentException("Column index must be positive");
		}
		if(columnIndex >= columnValues.size()){
			throw new IllegalArgumentException("Column index must be less than " + columnValues.size() +". Got " + columnIndex);
		}
		return (List<V>) columnValues.get(columnIndex);
	}

	/**
	 * Returns the values of a given column.
	 * @param columnName the name of the column in the input.
	 * @param columnType the type of data in that column
	 * @param <V> the type of data in that column
	 * @return a list with all data  stored in the given column
	 */
	<V> List<V> getColumnValues(String  columnName, Class<V> columnType){
		int index = ArgumentUtils.indexOf(headers, columnName);
		if(index == -1){
			throw new IllegalArgumentException("No column named '" + columnName +"' has been found. Available column headers: " + Arrays.toString(headers));
		}
		return getColumnValues(index, columnType);
	}

	/**
	 * Fills a given map associating each column index to its list of values
	 * @param map the map to hold the values of each column
	 */
	void putColumnValuesInMapOfIndexes(Map<Integer, List<T>> map) {
		if (columnValues == null) {
			return;
		}
		for (int i = 0; i < columnValues.size(); i++) {
			map.put(i, columnValues.get(i));
		}
	}

	/**
	 * Returns a map of column names and their respective list of values parsed from the input.
	 * @return a map of column names and their respective list of values.
	 */
	Map<String, List<T>> getColumnValuesAsMapOfNames() {
		Map<String, List<T>> map = new HashMap<String, List<T>>();
		putColumnValuesInMapOfNames(map);
		return map;
	}

	/**
	 * Returns a map of column indexes and their respective list of values parsed from the input.
	 * @return a map of column indexes and their respective list of values.
	 */
	Map<Integer, List<T>> getColumnValuesAsMapOfIndexes() {
		Map<Integer, List<T>> map = new HashMap<Integer, List<T>>();
		putColumnValuesInMapOfIndexes(map);
		return map;
	}
}
