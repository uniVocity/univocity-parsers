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

class ColumnSplitter<T> {

	private List<List<T>> columnValues;
	private String[] headers = null;
	private int initialRowSize = 1000;

	ColumnSplitter(int initialRowSize) {
		if (initialRowSize <= 0) {
			throw new IllegalArgumentException("Initial row size of ColumnProcessor must be positive");
		}
		this.initialRowSize = initialRowSize;
	}

	void clearValues() {
		this.columnValues = null;
	}

	void clearValuesAndHeaders() {
		this.columnValues = null;
		this.headers = null;
	}

	List<List<T>> getColumnValues() {
		return columnValues;
	}

	String[] getHeaders() {
		return headers;
	}

	private void initialize(ParsingContext context) {
		if (this.headers == null) {
			String[] allHeaders = context.headers();
			if (allHeaders == null) {
				headers = ArgumentUtils.EMPTY_STRING_ARRAY;
				return;
			}
			if (!context.columnsReordered()) {
				this.headers = allHeaders;
				return;
			}
			int[] selectedIndexes = context.extractedFieldIndexes();

			final int last = Math.min(allHeaders.length, selectedIndexes.length);
			this.headers = new String[selectedIndexes.length];
			for (int i = 0; i < last; i++) {
				headers[i] = allHeaders[selectedIndexes[i]];
			}
		}

		columnValues = new ArrayList<List<T>>(headers.length > 0 ? headers.length : 10);
	}

	String getHeader(int columnIndex) {
		if (headers.length < columnIndex) {
			return headers[columnIndex];
		}
		return null;
	}

	void addValuesToColumns(T[] row, ParsingContext context) {
		if (columnValues == null) {
			initialize(context);
		}

		if (columnValues.size() < row.length) {
			int columnsToAdd = row.length - columnValues.size();
			while (columnsToAdd-- > 0) {
				ArrayList<T> values;

				long records = context.currentRecord();
				if (records == 1L) {
					values = new ArrayList<T>(initialRowSize);
				} else {
					values = new ArrayList<T>(initialRowSize < records ? (int) records : initialRowSize);
					//adding nulls to the values of a new row with more columns than parsed before.
					//this ensures all columns will have the same number of values.
					while (--records > 0) {
						values.add(null);
					}
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
	}

	void putColumnValuesInMapOfNames(Map<String, List<T>> map) {
		if (columnValues == null) {
			return;
		}
		for (int i = 0; i < columnValues.size(); i++) {
			String header = getHeader(i);
			if (header == null) {
				throw new IllegalArgumentException("Parsed input does not have header for column at index '" + i + "'. Parsed header names: " + Arrays.toString(getHeaders()));
			}
			map.put(header, columnValues.get(i));
		}
	}

	void putColumnValuesInMapOfIndexes(Map<Integer, List<T>> map) {
		if (columnValues == null) {
			return;
		}
		for (int i = 0; i < columnValues.size(); i++) {
			map.put(i, columnValues.get(i));
		}
	}

	Map<String, List<T>> getColumnValuesAsMapOfNames() {
		Map<String, List<T>> map = new HashMap<String, List<T>>();
		putColumnValuesInMapOfNames(map);
		return map;
	}

	Map<Integer, List<T>> getColumnValuesAsMapOfIndexes() {
		Map<Integer, List<T>> map = new HashMap<Integer, List<T>>();
		putColumnValuesInMapOfIndexes(map);
		return map;
	}
}
