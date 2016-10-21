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
 * A simple {@link Processor} implementation that stores values of columns.
 * Values parsed in each row will be split into columns of Strings. Each column has its own list of values.
 *
 * <p> At the end of the process, the user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 *
 *
 * <p><b>Note:</b> Storing the values of all columns may be memory intensive. For large inputs, use a {@link AbstractBatchedColumnProcessor} instead</p>
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @see AbstractParser
 * @see Processor
 * @see ColumnReader
 */
public abstract class AbstractColumnProcessor<T extends Context> implements Processor<T>, ColumnReader<String> {

	private final ColumnSplitter<String> splitter;

	/**
	 * Constructs a column processor, pre-allocating room for 1000 rows.
	 */
	public AbstractColumnProcessor() {
		this(1000);
	}

	/**
	 * Constructs a column processor pre-allocating room for the expected number of rows to be processed
	 * @param expectedRowCount the expected number of rows to be processed
	 */
	public AbstractColumnProcessor(int expectedRowCount) {
		splitter = new ColumnSplitter<String>(expectedRowCount);
	}

	@Override
	public void processStarted(T context) {
		splitter.reset();
	}

	@Override
	public void rowProcessed(String[] row, T context) {
		splitter.addValuesToColumns(row, context);
	}

	@Override
	public void processEnded(T context) {
	}

	@Override
	public final String[] getHeaders() {
		return splitter.getHeaders();
	}

	@Override
	public final List<List<String>> getColumnValuesAsList() {
		return splitter.getColumnValues();
	}

	@Override
	public final void putColumnValuesInMapOfNames(Map<String, List<String>> map) {
		splitter.putColumnValuesInMapOfNames(map);
	}

	@Override
	public final void putColumnValuesInMapOfIndexes(Map<Integer, List<String>> map) {
		splitter.putColumnValuesInMapOfIndexes(map);
	}

	@Override
	public final Map<String, List<String>> getColumnValuesAsMapOfNames() {
		return splitter.getColumnValuesAsMapOfNames();
	}

	@Override
	public final Map<Integer, List<String>> getColumnValuesAsMapOfIndexes() {
		return splitter.getColumnValuesAsMapOfIndexes();
	}

	@Override
	public List<String> getColumn(String columnName) {
		return splitter.getColumnValues(columnName, String.class);
	}

	@Override
	public List<String> getColumn(int columnIndex) {
		return splitter.getColumnValues(columnIndex, String.class);
	}
}
