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

/**
 * A common interface for {@link Processor}s that collect the values parsed from each column in a row and store values of columns in batches.
 * <p>Use implementations of this interface implementation in favor of {@link ColumnReader} when processing large inputs to avoid running out of memory.</p>
 *
 * <p> During the execution of the process, the {@link #batchProcessed(int)} method will be invoked after a given number of rows has been processed.</p>
 *
 * <p> The user can access the lists with values parsed for all columns using the methods {@link #getColumnValuesAsList()},
 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}. </p>
 * <p> After {@link #batchProcessed(int)} is invoked, all values will be discarded and the next batch of column values will be accumulated.
 * This process will repeat until there's no more rows in the input.
 *
 * @see AbstractBatchedColumnProcessor
 * @see AbstractBatchedObjectColumnProcessor
 * @see Processor
 *
 * @author uniVocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 *
 * @param <T> the type of the data stored by the columns.
 */
interface BatchedColumnReader<T> extends ColumnReader<T> {

	/**
	 * Returns the number of rows processed in each batch
	 * @return the number of rows per batch
	 */
	int getRowsPerBatch();

	/**
	 * Returns the number of batches already processed
	 * @return the number of batches already processed
	 */
	int getBatchesProcessed();

	/**
	 * Callback to the user, where the lists with values parsed for all columns can be accessed using the methods {@link #getColumnValuesAsList()},
	 * {@link #getColumnValuesAsMapOfIndexes()} and {@link #getColumnValuesAsMapOfNames()}.
	 * @param rowsInThisBatch the number of rows processed in the current batch. This corresponds to the number of elements of each list of each column.
	 */
	void batchProcessed(int rowsInThisBatch);
}
