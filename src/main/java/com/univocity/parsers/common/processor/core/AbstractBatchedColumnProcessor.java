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
 * A {@link Processor} implementation that stores values of columns in batches. Use this implementation in favor of {@link AbstractColumnProcessor}
 * when processing large inputs to avoid running out of memory.
 *
 * Values parsed in each row will be split into columns of Strings. Each column has its own list of values.
 *
 * <p> During the execution of the process, the {@link #batchProcessed(int)} method will be invoked after a given number of rows has been processed.</p>
 * <p> The user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 * <p> After {@link #batchProcessed(int)} is invoked, all values will be discarded and the next batch of column values will be accumulated.
 * This process will repeat until there's no more rows in the input.
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @see AbstractParser
 * @see BatchedColumnReader
 * @see Processor
 */
public abstract class AbstractBatchedColumnProcessor<T extends Context> implements Processor<T>, BatchedColumnReader<String> {

	private final ColumnSplitter<String> splitter;
	private final int rowsPerBatch;
	private int batchCount;
	private int batchesProcessed;

	/**
	 * Constructs a batched column processor configured to invoke the {@link #batchesProcessed} method after a given number of rows has been processed.
	 * @param rowsPerBatch the number of rows to process in each batch.
	 */
	public AbstractBatchedColumnProcessor(int rowsPerBatch) {
		splitter = new ColumnSplitter<String>(rowsPerBatch);
		this.rowsPerBatch = rowsPerBatch;
	}

	@Override
	public void processStarted(T context) {
		splitter.reset();
		batchCount = 0;
		batchesProcessed = 0;
	}

	@Override
	public void rowProcessed(String[] row, T context) {
		splitter.addValuesToColumns(row, context);
		batchCount++;

		if (batchCount >= rowsPerBatch) {
			batchProcessed(batchCount);
			batchCount = 0;
			splitter.clearValues();
			batchesProcessed++;
		}
	}

	@Override
	public void processEnded(T context) {
		if (batchCount > 0) {
			batchProcessed(batchCount);
		}
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

	@Override
	public int getRowsPerBatch() {
		return rowsPerBatch;
	}

	@Override
	public int getBatchesProcessed() {
		return batchesProcessed;
	}

	@Override
	public abstract void batchProcessed(int rowsInThisBatch);

}
