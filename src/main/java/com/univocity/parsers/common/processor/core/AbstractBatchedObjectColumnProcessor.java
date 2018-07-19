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
import com.univocity.parsers.conversions.*;

import java.util.*;

/**
 *
 * A {@link Processor} implementation for converting batches of rows extracted from any implementation of {@link AbstractParser} into columns of objects.
 * <p>This uses the value conversions provided by {@link Conversion} instances.</p>
 *
 * <p> For each row processed, a sequence of conversions will be executed to generate the appropriate object. Each resulting object will then be stored in
 * 	a list that contains the values of the corresponding column. </p>
 *
 * <p> During the execution of the process, the {@link #batchProcessed(int)} method will be invoked after a given number of rows has been processed.</p>
 * <p> The user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 * <p> After {@link #batchProcessed(int)} is invoked, all values will be discarded and the next batch of column values will be accumulated.
 * This process will repeat until there's no more rows in the input.
 *
 * @see AbstractParser
 * @see Processor
 * @see BatchedColumnReader
 * @see Conversion
 * @see AbstractObjectProcessor
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 */
public abstract class AbstractBatchedObjectColumnProcessor<T extends Context> extends AbstractObjectProcessor<T> implements Processor<T>, BatchedColumnReader<Object> {

	private final ColumnSplitter<Object> splitter;
	private final int rowsPerBatch;
	private int batchCount;
	private int batchesProcessed;

	/**
	 * Constructs a abstract batched column processor configured to invoke the {@link #batchesProcessed} method after a given number of rows has been processed.
	 * @param rowsPerBatch the number of rows to process in each batch.
	 */
	public AbstractBatchedObjectColumnProcessor(int rowsPerBatch) {
		splitter = new ColumnSplitter<Object>(rowsPerBatch);
		this.rowsPerBatch = rowsPerBatch;
	}

	@Override
	public void processStarted(T context) {
		super.processStarted(context);
		splitter.reset();
		batchCount = 0;
		batchesProcessed = 0;
	}

	@Override
	public void rowProcessed(Object[] row, T context) {
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
		super.processEnded(context);
		if (batchCount > 0) {
			batchProcessed(batchCount);
		}
	}

	@Override
	public final String[] getHeaders() {
		return splitter.getHeaders();
	}

	@Override
	public final List<List<Object>> getColumnValuesAsList() {
		return splitter.getColumnValues();
	}

	@Override
	public final void putColumnValuesInMapOfNames(Map<String, List<Object>> map) {
		splitter.putColumnValuesInMapOfNames(map);
	}

	@Override
	public final void putColumnValuesInMapOfIndexes(Map<Integer, List<Object>> map) {
		splitter.putColumnValuesInMapOfIndexes(map);
	}

	@Override
	public final Map<String, List<Object>> getColumnValuesAsMapOfNames() {
		return splitter.getColumnValuesAsMapOfNames();
	}

	@Override
	public final Map<Integer, List<Object>> getColumnValuesAsMapOfIndexes() {
		return splitter.getColumnValuesAsMapOfIndexes();
	}

	@Override
	public List<Object> getColumn(String columnName) {
		return splitter.getColumnValues(columnName, Object.class);
	}

	@Override
	public List<Object> getColumn(int columnIndex) {
		return splitter.getColumnValues(columnIndex, Object.class);
	}

	/**
	 * Returns the values of a given column.
	 * @param columnName the name of the column in the input.
	 * @param columnType the type of data in that column
	 * @param <V> the type of data in that column
	 * @return a list with all data  stored in the given column
	 */
	public <V> List<V> getColumn(String columnName, Class<V> columnType){
		return splitter.getColumnValues(columnName, columnType);
	}

	/**
	 * Returns the values of a given column.
	 * @param columnIndex the position of the column in the input (0-based).
	 * @param columnType the type of data in that column
	 * @param <V> the type of data in that column
	 * @return a list with all data  stored in the given column
	 */
	public <V> List<V> getColumn(int columnIndex, Class<V> columnType){
		return splitter.getColumnValues(columnIndex, columnType);
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
